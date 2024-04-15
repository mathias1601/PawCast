package no.uio.ifi.in2000.team19.prosjekt.ui.setup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

@Composable
fun SetupManager(
    viewModel: SetupScreenViewModel
) {

    val setupIndex = viewModel.setupIndex.collectAsState().value

    Scaffold {innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
        ) {
            Row () {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Left")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Right")
                }
            }

        }
    }

}

@Composable
fun SetupScreen(setupScreenViewModel: SetupScreenViewModel) {

}
