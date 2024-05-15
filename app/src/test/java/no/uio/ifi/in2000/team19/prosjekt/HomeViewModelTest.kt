package no.uio.ifi.in2000.team19.prosjekt

import androidx.hilt.navigation.compose.hiltViewModel
import no.uio.ifi.in2000.team19.prosjekt.data.LocationForecastRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.SettingsRepository
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.cords.LocationDao
import no.uio.ifi.in2000.team19.prosjekt.data.settingsDatabase.userInfo.UserInfoDao
import no.uio.ifi.in2000.team19.prosjekt.ui.home.HomeScreenViewModel
import org.junit.Before

class HomeViewModelTest {

    @Before

    val viewModel = HomeScreenViewModel(
        SettingsRepository(LocationDao, UserInfoDao()), LocationForecastRepository())

}