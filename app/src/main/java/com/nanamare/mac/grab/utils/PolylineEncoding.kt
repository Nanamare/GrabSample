package com.nanamare.mac.grab.utils

import com.google.android.gms.maps.model.LatLng
import java.util.*


// Google sample code 참고
object PolylineEncoding {
    /**
     * Decodes an encoded path string into a sequence of LatLngs.
     */
    fun decode(encodedPath: String): List<LatLng> {
        val len = encodedPath.length
        val path: MutableList<LatLng> = ArrayList(len / 2)
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPath[index++].toInt() - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }

    /**
     * Encodes a sequence of LatLngs into an encoded path string.
     */
    fun encode(path: List<LatLng>): String {
        var lastLat: Long = 0
        var lastLng: Long = 0
        val result = StringBuilder()
        for (point in path) {
            val lat = Math.round(point.latitude * 1e5)
            val lng = Math.round(point.longitude * 1e5)
            val dLat = lat - lastLat
            val dLng = lng - lastLng
            encode(dLat, result)
            encode(dLng, result)
            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encode(v: Long, result: StringBuilder) {
        var v = v
        v = if (v < 0) (v shl 1).inv() else v shl 1
        while (v >= 0x20) {
            result.append(Character.toChars(((0x20 or (v and 0x1f).toInt()) + 63)))
            v = v shr 5
        }
        result.append(Character.toChars((v + 63).toInt()))
    }

    /**
     * Encodes an array of LatLngs into an encoded path string.
     */
    fun encodeResult(path: List<LatLng>): String {
        return encode(path)
    }
}