package no.uio.ifi.in2000.team19.prosjekt.examples.RoomDataBase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CordsState(
    val cords: List<Cords> = emptyList()
)


class CordsViewModel (): ViewModel() {

    private val _state = MutableStateFlow(CordsState())
    val cordsState: StateFlow<CordsState> =_state.asStateFlow()

    fun loadCords(dao: DataAccessObjectDao) {

        viewModelScope.launch (Dispatchers.IO) {

            _state.update {
                val cords = dao.getCords()
                it.copy(cords = cords)
            }
        }
    }

    fun insertArbitraryCords(dao: DataAccessObjectDao) {

        viewModelScope.launch (Dispatchers.IO) {
            val cords = Cords(0, "100", "50")
            dao.insertCords(cords)
        }

    }

    fun deleteCords(dao: DataAccessObjectDao) {

        viewModelScope.launch (Dispatchers.IO) {
            dao.deleteCords(dao.getCords()[0])
        }

    }

}

