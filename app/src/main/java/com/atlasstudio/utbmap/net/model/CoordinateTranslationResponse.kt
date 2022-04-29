package com.atlasstudio.utbmap.net.model

import com.google.gson.annotations.SerializedName

data class CoordinateTranslationResponse(@SerializedName("Coordinates") val coords: String?="")