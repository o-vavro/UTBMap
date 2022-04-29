package com.atlasstudio.utbmap.data

data class JTSKLocation(val lat: Double, val lng: Double) {
    constructor(lat: String, lng: String): this(lat.toDouble(), lng.toDouble())
    override fun toString(): String = "$lat,$lng"
}