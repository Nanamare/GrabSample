package com.nanamare.mac.grab.ext

import com.google.gson.Gson

inline fun Any.toJsonString(): String = Gson().toJson(this)