package com.example.pokemoncards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.example.pokemoncards.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import com.example.pokemoncards.navigation.Screen.SignInScreen

@AndroidEntryPoint
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<MainViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
/*            PokemonCardsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //MyApp()
                    MyAppPortrait()
                }*/
                navController = rememberNavController()
                NavGraph(
                    navController = navController
                )
                AuthState()
        }
    }

    @Composable
    private fun AuthState() {
        val isUserSignedOut = viewModel.getAuthState().collectAsState().value
        if (isUserSignedOut) {
            NavigateToSignInScreen()
        } else {
            if (viewModel.isEmailVerified) {

                //NavigateToProfileScreen()
            } else {
                //NavigateToVerifyEmailScreen()

            }
        }
    }

    @Composable
    private fun NavigateToSignInScreen() = navController.navigate(SignInScreen.route) {
        popUpTo(navController.graph.id) {
            inclusive = true
        }
    }
}


@Composable
fun MyApp(){

    var cards by remember { mutableStateOf<List<Data>>(emptyList()) }

    CardList(cards)

    LaunchedEffect(true){
        val result = PokemonApi.getCard()

        if (result != null) {
            cards = result.data
        }
    }


}
@Composable
fun CardList(cards: List<Data>){

    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally)
    {
        items(cards) { card ->
            SubcomposeAsyncImage(
                model = card.images.small,
                loading = {
                    CircularProgressIndicator()
                },
                contentDescription = null
            )
            Text(text = card.id)
            Text(text = card.name)
            Text(text = card.rarity)
            Text(text = card.tcgplayer.updatedAt)
        }
    }
}

