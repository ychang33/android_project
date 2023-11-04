package com.example.pokemoncards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface

import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.example.pokemoncards.navigation.NavGraph
import dagger.hilt.android.AndroidEntryPoint
import com.example.pokemoncards.navigation.Screen.SignInScreen

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.pokemoncards.ui.theme.PokemonCardsTheme
import kotlinx.coroutines.launch


@AndroidEntryPoint
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
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

                    SearchScreen()
                    //MyAppPortrait()
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
fun SearchScreen(){

    var cards by remember { mutableStateOf<List<Data>>(emptyList()) }

    Column {
        SearchBar(onSearch = { newCards -> cards = newCards as List<Data> })

        CardList(cards)
    }
}

@Composable
fun CardList(cards: List<Data>){

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(10.dp)
    )
    {
        items(cards) { card ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                SubcomposeAsyncImage(
                    model = card.images.small,
                    loading = {
                        CircularProgressIndicator()
                    },
                    modifier = Modifier.clickable(){
                        },
                    contentDescription = card.id
                )
                Spacer(Modifier.size(8.dp))
                Text(text="Series: ${card.set.series}")
            }

        }
    }
}

@Composable
fun SearchBar(
    onSearch: (Any?) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current

    var query by remember { mutableStateOf("") }

    val corountine = rememberCoroutineScope()

    TextField(
        value = query,
        onValueChange = {query = it},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.placeholder_search))
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions (onSearch = {
            corountine.launch{
                val result = PokemonApi.getCard(query)

                if (result != null) {
                    onSearch(result.data)
                }
            }
            focusManager.clearFocus()
        }),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}
