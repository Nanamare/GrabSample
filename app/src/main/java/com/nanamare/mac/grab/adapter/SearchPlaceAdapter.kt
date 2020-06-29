package com.nanamare.mac.grab.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.nanamare.mac.grab.R
import com.nanamare.mac.grab.base.ui.BaseViewHolder
import com.nanamare.mac.grab.data.source.vo.LocationVO
import com.nanamare.mac.grab.databinding.SearchPlaceItemBinding
import com.nanamare.mac.grab.network.response.PlaceResponse
import com.nanamare.mac.grab.ui.dialog.SearchPlaceDialog

class SearchPlaceAdapter(
    private val onPlaceClickListener: ((LocationVO) -> Unit)? = null
) : PagedListAdapter<PlaceResponse.Result, RecyclerView.ViewHolder>(
    SearchPlaceDialog.POST_COMPARATOR
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return object : BaseViewHolder<PlaceResponse.Result, SearchPlaceItemBinding>(
            R.layout.search_place_item, parent
        ) {
            init {
                itemView.setOnClickListener {
                    val item = getItem(adapterPosition)
                    val location = item?.geometry?.location
                    location?.let {
                        val locationVO =
                            LocationVO(LatLng(it.lat, it.lng), item.formattedAddress, item.name)
                        onPlaceClickListener?.invoke(locationVO)
                    }
                }
            }

            override fun onViewCreated(item: PlaceResponse.Result?) {
                binding.run {
                    tvName.text = item?.name
                    tvAddress.text = item?.formattedAddress
                }
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? BaseViewHolder<*, *>)?.onBindViewHolder(getItem(position))
    }
}

