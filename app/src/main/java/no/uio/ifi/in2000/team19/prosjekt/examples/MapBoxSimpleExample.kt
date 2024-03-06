package no.uio.ifi.in2000.team19.prosjekt.examples

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.MapViewportState


@OptIn(MapboxExperimental::class)
@Composable
fun MapBoxSimpleExample(){

    MapboxMap(
        Modifier
            .fillMaxWidth()
            .height(10.dp),

        mapViewportState = MapViewportState().apply {
            setCameraOptions {
                zoom(10.0)
                center(Point.fromLngLat(10.0, 60.0))
                pitch(0.0)
                bearing(0.0)
            }
        },
    )
}