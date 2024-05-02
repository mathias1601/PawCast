package no.uio.ifi.in2000.team19.prosjekt.ui.home

import android.graphics.Typeface
import android.text.Layout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.component.fixed
import com.patrykandpatrick.vico.compose.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.component.rememberTextComponent
import com.patrykandpatrick.vico.core.component.marker.MarkerComponent
import com.patrykandpatrick.vico.core.component.shape.cornered.CorneredShape
import com.patrykandpatrick.vico.core.component.shape.cornered.MarkerCorneredShape
import com.patrykandpatrick.vico.core.component.text.TextComponent
import com.patrykandpatrick.vico.core.marker.Marker


@Composable
internal fun rememberMarker(): Marker {

    val labelBackgroundShape = MarkerCorneredShape(CorneredShape())
    val labelBackground =
        rememberShapeComponent(labelBackgroundShape, MaterialTheme.colorScheme.surface)
            .setShadow(
                radius = 3f,
                dy = 1f,
                applyElevationOverlay = true,
            )

    val label =
        rememberTextComponent(
            color = MaterialTheme.colorScheme.onSurface,
            background = labelBackground,
            typeface = Typeface.MONOSPACE,
            textAlignment = Layout.Alignment.ALIGN_CENTER,
            minWidth = TextComponent.MinWidth.fixed(40.dp),
        )

    return remember(label) {
        object : MarkerComponent(
            label = label,
            labelPosition = LabelPosition.AbovePoint,
        ) {}
    }
}


