package no.uio.ifi.in2000.team19.prosjekt.ui.searchBox

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Preview(showBackground = true)
@Composable
fun SearchLocationTextField(
    // modifier: Modifier
){

    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            onValueChange = { query -> handleTextFieldValueUpdate(query)},
            label = {Text("Enter Location")},
        )
    }
}

fun handleTextFieldValueUpdate(newQuery:String) {

}

