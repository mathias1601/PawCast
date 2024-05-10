package no.uio.ifi.in2000.team19.prosjekt.ui.extendedAdvice


import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel

@Composable
fun AdviceScreen(adviceId: Int, navController: NavController, viewModel: HomeScreenViewModel) {

    val advice: Advice = viewModel.collectAdviceById(adviceId)


    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.primaryContainer)
        ) {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(R.string.GoBackText)
                )
            }

        }

            Column(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxSize()
                    .verticalScroll(ScrollState(0))) {

                Text(
                    text = advice.title,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                    )

                val description: String = advice.description

                val paragraphs = description.split("\n")

                paragraphs.forEach { paragraph ->

                    val trimmedParagraph = paragraph.trim()

                    if (trimmedParagraph.isNotEmpty()) {

                        if (trimmedParagraph.startsWith("~")) {

                            Spacer(modifier = Modifier.padding(5.dp))
                            Text(
                                text = trimmedParagraph.substring(1),
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.padding(5.dp))
                        }
                        else {
                            Text(text = trimmedParagraph,
                                modifier = Modifier.padding(horizontal = 20.dp))
                        }
                    }

                }
            }

    }


}




