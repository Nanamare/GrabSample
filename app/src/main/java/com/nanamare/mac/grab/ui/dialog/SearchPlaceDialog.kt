package com.nanamare.mac.grab.ui.dialog

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.nanamare.mac.grab.BR
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.adapter.SearchPlaceAdapter
import com.nanamare.mac.grab.base.ui.BaseFullSheetDialogFragment
import com.nanamare.mac.grab.base.ui.SimpleRecyclerView
import com.nanamare.mac.grab.data.source.enums.SearchType
import com.nanamare.mac.grab.data.source.vo.LocationVO
import com.nanamare.mac.grab.databinding.RecentSearchPlaceItemBinding
import com.nanamare.mac.grab.databinding.SearchPlaceDialogBinding
import com.nanamare.mac.grab.network.NetworkState
import com.nanamare.mac.grab.network.response.PlaceResponse
import com.nanamare.mac.grab.vm.SearchLocationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchPlaceDialog :
    BaseFullSheetDialogFragment<SearchPlaceDialogBinding>(R.layout.search_place_dialog) {

    var onPlaceClickListener: ((LocationVO) -> Unit)? = null

    private val searchLocationViewModel by viewModel<SearchLocationViewModel>()

    private val searchAdapter by lazy {
        SearchPlaceAdapter(
            onPlaceClickListener = {
                searchLocationViewModel.saveLocations(it)
                onPlaceClickListener?.invoke(it)
                dismiss()
            })
    }

    // 더 커지면 분리하기
    private val recentAdapter by lazy {
        object :
            SimpleRecyclerView.Adapter<LocationVO, RecentSearchPlaceItemBinding>(
                R.layout.recent_search_place_item,
                BR.location
            ) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): SimpleRecyclerView.ViewHolder<RecentSearchPlaceItemBinding> {
                return super.onCreateViewHolder(parent, viewType).apply {
                    itemView.setOnClickListener {
                        val item =
                            searchLocationViewModel.liveLocalLocations.value?.get(adapterPosition)
                        item?.let {
                            onPlaceClickListener?.invoke(item)
                        } ?: error(getString(R.string.error_no_adapter_item))
                        dismiss()
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.run {
            searchPlaceVM = searchLocationViewModel
            searchPlaceAdapter = searchAdapter
            recentSearchAdapter = recentAdapter

            when (arguments?.getSerializable(REQUEST_SEARCH_TYPE)) {
                SearchType.SOURCE -> tvToolbarTitle.text = getString(R.string.setting_departure)
                SearchType.DESTINATION -> tvToolbarTitle.text = getString(R.string.setting_destination)
            }

            rvContentSearch.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager(requireContext()).orientation
                )
            )
            rvContentRecent.addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    LinearLayoutManager(requireContext()).orientation
                )
            )

            rvContentRecent.post {
                searchLocationViewModel.loadLocalLocations()
            }

            etKeyword.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchLocationViewModel.onSearchClick()
                }
                true
            }

            ivExit.setOnClickListener { onCloseClick() }
        }

        searchLocationViewModel.livePlaceState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is NetworkState.Init -> hideLoadingPopup()
                is NetworkState.Loading -> showLoadingPopup()
                is NetworkState.Success -> hideLoadingPopup()
                is NetworkState.Error -> {
                    hideLoadingPopup()
                    Log.e(TAG, it.throwable.toString())
                }
            }
        })

        searchLocationViewModel.liveSearchItems.observe(viewLifecycleOwner, Observer {
            it?.observe(viewLifecycleOwner, Observer {
                searchAdapter.submitList(it)
            }) ?: run {
                searchAdapter.submitList(null)
            }
        })


    }


    companion object {
        private const val REQUEST_SEARCH_TYPE = "REQUEST_SEARCH_TYPE"

        val POST_COMPARATOR = object : DiffUtil.ItemCallback<PlaceResponse.Result>() {
            override fun areContentsTheSame(
                oldItem: PlaceResponse.Result,
                newItem: PlaceResponse.Result
            ): Boolean = oldItem == newItem

            override fun areItemsTheSame(
                oldItem: PlaceResponse.Result,
                newItem: PlaceResponse.Result
            ): Boolean = oldItem === newItem
        }

        @JvmStatic
        val TAG: String = SearchPlaceDialog::class.java.simpleName

        fun getInstance(
            searchType: SearchType,
            onPlaceClickListener: ((LocationVO) -> Unit)?
        ) = SearchPlaceDialog().apply {
            arguments = Bundle().apply {
                putSerializable(REQUEST_SEARCH_TYPE, searchType)
            }
            this.onPlaceClickListener = onPlaceClickListener
        }
    }


}