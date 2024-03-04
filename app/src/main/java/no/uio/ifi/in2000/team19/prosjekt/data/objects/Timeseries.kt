package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class Timeseries (

  @SerializedName("time" ) var time : String? = null,
  @SerializedName("data" ) var data : Data?   = Data()

)