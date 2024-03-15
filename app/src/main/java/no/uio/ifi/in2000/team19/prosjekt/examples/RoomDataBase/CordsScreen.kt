package no.uio.ifi.in2000.team19.prosjekt.examples.RoomDataBase

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CordsScreen(
    cordsDao: DataAccessObjectDao,
    cordsViewModel: CordsViewModel = viewModel()
) {

    val cordsUiState: CordsState by cordsViewModel.cordsState.collectAsState()

    Column {
        Button(onClick = {
            cordsViewModel.insertArbitraryCords(cordsDao)
            cordsViewModel.loadCords(cordsDao)
        }) {
            Text(text = "Insert test coordinates")
        }


        Button(onClick = {
            cordsViewModel.loadCords(cordsDao)
        }) {
            Text(text = "Load test coordinates")
        }

        Button(onClick = {
            cordsViewModel.deleteCords(cordsDao)
            cordsViewModel.loadCords(cordsDao)
        }) {
            Text(text = "Delete test coordinates")
        }



        LazyColumn(){
            items(cordsUiState.cords) {cords ->
                Text(text = "Longtitude: " + cords.longitude + ", Latitude: " + cords.latitude)

            }
        }


    }

}
