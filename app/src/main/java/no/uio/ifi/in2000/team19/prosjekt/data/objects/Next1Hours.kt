package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class Next1Hours (

  @SerializedName("summary" ) var summary : Summary? = Summary(),
  @SerializedName("details" ) var details : Details? = Details()

)