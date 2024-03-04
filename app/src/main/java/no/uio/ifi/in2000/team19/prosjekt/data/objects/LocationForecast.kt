package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class LocationForecast (

  @SerializedName("type"       ) var type       : String?     = null,
  @SerializedName("geometry"   ) var geometry   : Geometry?   = Geometry(),
  @SerializedName("properties" ) var properties : Properties? = Properties()

)