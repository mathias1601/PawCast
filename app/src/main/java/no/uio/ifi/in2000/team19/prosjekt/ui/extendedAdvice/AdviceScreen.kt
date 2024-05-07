package no.uio.ifi.in2000.team19.prosjekt.ui.extendedAdvice


import android.os.Build
import android.widget.Space
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(adviceId: Int, navController: NavController, viewModel: HomeScreenViewModel) {

    val advice: Advice = viewModel.collectAdviceById(adviceId)


    Box {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon( //Er ikke Material Design 3
                        imageVector = Icons.Filled.ArrowBackIosNew,
                        contentDescription = "Tilbake"
                    )
                }

            }

                Column(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .verticalScroll(ScrollState(0))) {

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(topStart = 25.dp, topEnd = 25.dp)
                            ),
                        text = advice.title,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        textAlign = TextAlign.Center,

                        )

                    var description: String = advice.description

                    val paragraphs = description.split("\n")

                    paragraphs.forEach { paragraph ->

                        val trimmedParagraph = paragraph.trim()

                        if (trimmedParagraph.isNotEmpty()) {

                            if (trimmedParagraph.startsWith("~")) {

                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(
                                    text = trimmedParagraph.substring(1),
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier
                                        .background(
                                        color = MaterialTheme.colorScheme.primaryContainer))
                                Spacer(modifier = Modifier.padding(5.dp))
                            }
                            else {
                                Text(text = trimmedParagraph)
                            }
                        }

                    }
                }

        }
    }

}




