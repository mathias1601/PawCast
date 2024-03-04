package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class Meta (

  @SerializedName("updated_at" ) var updatedAt : String? = null,
  @SerializedName("units"      ) var units     : Units?  = Units()

)