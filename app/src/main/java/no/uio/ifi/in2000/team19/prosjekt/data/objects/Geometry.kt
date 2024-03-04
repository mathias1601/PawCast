package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class Geometry (

  @SerializedName("type"        ) var type        : String?        = null,
  @SerializedName("coordinates" ) var coordinates : ArrayList<Int> = arrayListOf()

)