package no.uio.ifi.in2000.team19.prosjekt.ui.extendedAdvice


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team19.prosjekt.model.DTO.Advice
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(adviceId: Int, navController: NavController, viewModel: HomeScreenViewModel) {

    val advice: Advice = viewModel.collectAdviceById(adviceId)

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Text(
                        text = "CocoMilo"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                            Icon( //Er ikke Material Design 3
                                imageVector = Icons.Filled.ArrowBackIosNew,
                                contentDescription = "Tilbake"
                            )
                        }
                }
            )
        }
    ) {innerpadding->
        Box(modifier = Modifier.padding(innerpadding)) {

            Text(advice.title, fontWeight = FontWeight.Bold, modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp))


            LazyColumn(
                modifier = Modifier
                    .padding(30.dp)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())) {

                val description: String = advice.description

                val paragraphs = description.split("\n")


                items(paragraphs) { paragraph ->
                    if (paragraph[0].toString() == "~") {
                        Text(text = paragraph.substring(1), style = MaterialTheme.typography.headlineSmall)
                    }
                    else {
                        Text(text = paragraph)
                    }
                }
            }


        }

    }

}

/*
@Composable
fun SingleStringLayout(stringFromXml: String) {
    val paragraphs = stringFromXml.split("\n")

        LazyColumn {
            items(paragraphs) { paragraph ->
                if (paragraph[0].toString() == "~") {
                    Text(text = paragraph.substring(1), style = MaterialTheme.typography.headlineSmall)
                }
                else {
                    Text(text = paragraph)
                }
            }
        }
}


                    if (it[0].toString() == "~"){
                        Text(it.substring(1), style = MaterialTheme.typography.bodyLarge)
                    }
                    else {

 */