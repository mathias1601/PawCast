package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Start
import androidx.compose.material.icons.outlined.FastForward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.theme.Measurements


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(onDone: () -> Unit, onSkip: () -> Unit){


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = Measurements.HorizontalPadding.measurement + 40.dp)
        ,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally


    ) {


        Image(painter = painterResource(id = R.drawable.dog_normal_white_sticker), contentDescription = stringResource(id = R.string.dog_normal_description))

        Text(
            text = "Velkommen!",
            style = MaterialTheme.typography.displaySmall
        )
        Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

        Text(
            text = "Dette er en vær app for hundeeiere, som gir klima-relaterte anbefalinger basert på din hund.",
            style = MaterialTheme.typography.bodyLarge,
            )

        Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))

        Text(
            text = "Før vi begynner trenger vi litt info om deg og hunden din for å personalisere appen til dere",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

        Button(onClick = onDone) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Konfigurer appen")
                Icon(imageVector = Icons.Filled.Start, contentDescription = "Hundepote")
            }
        }


        var showBottomSkipModal by remember { mutableStateOf(false) }
        TextButton(onClick = { showBottomSkipModal = true }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = "Hopp over konfigurasjon")
                Icon(imageVector = Icons.Outlined.FastForward, contentDescription = "Symbol for hopp over")

            }
        }



        if (showBottomSkipModal){
            ModalBottomSheet(onDismissRequest = { showBottomSkipModal = false }) {

                Column(
                    modifier = Modifier.padding(Measurements.HorizontalPadding.measurement)
                ) {
                    Text(
                        text = "Før du hopper over!",
                        style = MaterialTheme.typography.titleMedium)
                    Text(
                        text = "Dersom du velger å ikke oppgi infomasjon om hunden din, velger vi å gi deg varsler for alle hunde typer for å gi best utbytte av appen. Likevel anbefaler vi deg sterkt til å konfigurere appen.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.padding(Measurements.WithinSectionVerticalGap.measurement))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(
                            onClick = onSkip,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = "Bare konfigurer lokasjon", textAlign = TextAlign.Center)
                        }
                        Button(onClick = onDone, modifier = Modifier.weight(1f).height(IntrinsicSize.Max)) {
                            Text(text = "Konfigurer alt", textAlign = TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.padding(Measurements.BetweenSectionVerticalGap.measurement))
                }



            }
        }
    }

}

