package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class Instant (

  @SerializedName("details" ) var details : Details? = Details()

)