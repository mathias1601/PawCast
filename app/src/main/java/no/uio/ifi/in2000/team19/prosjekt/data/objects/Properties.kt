package no.uio.ifi.in2000.team19.prosjekt.data.objects

import com.google.gson.annotations.SerializedName


data class Properties (

    @SerializedName("meta"       ) var meta       : Meta?                 = Meta(),
    @SerializedName("timeseries" ) var timeseries : ArrayList<Timeseries> = arrayListOf()

)