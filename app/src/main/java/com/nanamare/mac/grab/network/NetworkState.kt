package com.nanamare.mac.grab.network


sealed class NetworkState<out T> {
    object Init : NetworkState<Nothing>()
    object Loading : NetworkState<Nothing>()
    class Success<out T>(val item: T) : NetworkState<T>()
    class Error(val throwable: Throwable) : NetworkState<Nothing>()

    override fun toString(): String {
        return when (this) {
            Init -> "Init"
            Loading -> "Loading"
            is Success -> "Success[data=$item]"
            is Error -> "Error[exception=$throwable]"
        }
    }

    companion object {
        fun init() = Init
        fun loading() = Loading
        fun <T> success(data: T) = Success(data)
        fun error(throwable: Throwable) = Error(throwable)
    }

}