package no.uio.ifi.in2000.team19.prosjekt.examples.RoomDataBase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.testifiproxy.ui.theme.TestIfiProxyTheme
import kotlinx.coroutines.flow.Flow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            CordsDatabase::class.java, "database-name"
        ).build()
        setContent {
            TestIfiProxyTheme {
                // A surface container using the 'background' color from the theme

                    val cordsDao = db.dao()

                    CordsScreen(cordsDao)

                    }
            }
        }
    }






