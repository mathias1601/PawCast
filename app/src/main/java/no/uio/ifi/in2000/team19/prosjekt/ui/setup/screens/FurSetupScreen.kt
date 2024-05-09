package no.uio.ifi.in2000.team19.prosjekt.ui.setup.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import no.uio.ifi.in2000.team19.prosjekt.R
import no.uio.ifi.in2000.team19.prosjekt.ui.setup.SetupScreenViewModel


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FurSetupScreen(viewModel: SetupScreenViewModel, navController: NavHostController) {

    val userInfo = viewModel.userInfo.collectAsState().value
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Image(
                painter = painterResource(id = R.drawable.dog_normal),
                contentDescription = "Avatar",
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.height(125.dp)
            )
        }

        Column (
            modifier = Modifier
                .weight(2f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text="Hva slags pels har hunden din?",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.padding(10.dp))


            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp),
            ) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 10.dp,
                            bottom = 10.dp,
                        ),
                    maxItemsInEachRow = 3,
                    horizontalArrangement = Arrangement.Center
                ) {

                    FilterChip(text = "Tynn", categoryName = "tynnPels", viewModel, userInfo.isThinHaired)
                    FilterChip(text = "Tykk", categoryName = "tykkPels", viewModel, userInfo.isThickHaired)
                    FilterChip(text = "Lang", categoryName = "langPels", viewModel, userInfo.isLongHaired)
                    FilterChip(text = "Kort", categoryName = "kortPels", viewModel, userInfo.isShortHaired)
                    FilterChip(text = "Lys", categoryName = "lysPels", viewModel, userInfo.isLightHaired)
                    FilterChip(text = "Mørk", categoryName = "moerkPels", viewModel, userInfo.isDarkHaired)

                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Text(
                text= stringResource(R.string.chooseDogCategoryBottomScreenTip),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

        }
        Column (
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            

            Button(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    viewModel.saveUserInfo() // save user info to DB
                    viewModel.saveSetupState(isCompleted = true) // store info that setup is completed so next app launch doesnt ask for setup.
                    // navController.popBackStack() // removes history from backstack. Stops user from being able to click back, navigating the user back to setup 👎
                    navController.navigate("home")
                }
            ) {
                Text (text="Fullfør")
            }
        }
    }
}


@Composable
fun FilterChip(
    text: String,
    categoryName: String,
    viewModel: SetupScreenViewModel,
    selected: Boolean
) {

    var isSelected by remember {
        mutableStateOf(selected)
    }

    androidx.compose.material3.FilterChip(
        modifier = Modifier
            .padding(8.dp),
        onClick = {
            isSelected = !isSelected
            viewModel.updateFilterCategories(categoryName, isSelected)
        },
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        selected = isSelected,
        leadingIcon = if (isSelected) {
            {
                Icon(
                    imageVector = Icons.Filled.Done,
                    contentDescription = "Done icon",
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            }
        } else {
            null
        },
    )
}


