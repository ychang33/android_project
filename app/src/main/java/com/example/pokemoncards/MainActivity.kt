package com.example.pokemoncards

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.pokemoncards.destinations.CardDetailDestination
import com.example.pokemoncards.destinations.LoginScreenDestination
import com.example.pokemoncards.destinations.SearchScreenDestination
import com.example.pokemoncards.ui.theme.PokemonCardsTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContent {
                PokemonCardsTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        DestinationsNavHost(navGraph = NavGraphs.root)
                    }
                }
            }
        }
    }


@RootNavGraph(start = true)
@Destination
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator
){
    //navigator.navigate(LoginScreenDestination)
    navigator.navigate(SearchScreenDestination)
}


@Destination
@Composable
fun SearchScreen(
    destinationsNavigator: DestinationsNavigator
){
    val background = if(PokemonCardsApp.isLoginSuccessful)
                    MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.background

    Scaffold (
        topBar = {SearchBar()},
        bottomBar = { Bottom_bar(destinationsNavigator = destinationsNavigator)}
    ){ innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .background(color = background)){
            CardList( destinationsNavigator = destinationsNavigator)}
     }
}

@Composable
fun CardList(destinationsNavigator: DestinationsNavigator){

    val viewModel = viewModel{ PokemonViewModel() }

    if (viewModel.isLoading)
        {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    else {
        if (viewModel.cards.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
                contentAlignment = Alignment.Center) {
                Text(text = "No Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                    )
            }
        }
        else{
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
            //contentPadding = PaddingValues(10.dp)
        )
        {
            items(viewModel.cards) { card ->
                Card (
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier.fillMaxSize(),
                ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            AsyncImage(
                                model = card.images.small,
                                modifier = Modifier
                                    //.weight(0.8f)
                                    .padding(4.dp)
                                    .clickable() {
                                        destinationsNavigator.navigate(
                                            CardDetailDestination(
                                                1,
                                                card
                                            )
                                        )
                                    },
                                contentDescription = card.id,
                                alignment = Alignment.Center
                            )
                            Text(card.set.name,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis)
                            if(PokemonCardsApp.isLoginSuccessful)
                                FavoriteIcon(card, modifier = Modifier.padding(3.dp))
                        }
                    }
                }
            }
        }
    }
   }


@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val viewModel = viewModel{ PokemonViewModel() }
    var query by remember { mutableStateOf("") }
    val coroutine = rememberCoroutineScope()

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
            Text("Search Cards by Name")
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions (onSearch = {
            viewModel.isLoading = true
            coroutine.launch{
                val result = PokemonApi.getCard(query)
                if (result != null)
                    viewModel.cards = result.data
                else
                    viewModel.cards = emptyList()
                viewModel.isLoading = false
            }
            focusManager.clearFocus()
        }),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Composable
fun Bottom_bar(destinationsNavigator: DestinationsNavigator){
    val viewModel = viewModel{ PokemonViewModel() }
    val context = LocalContext.current

    BottomAppBar {
        if(!PokemonCardsApp.isLoginSuccessful)
            IconButton(onClick = { destinationsNavigator.navigate(LoginScreenDestination) }) {
                    Icon(imageVector = Icons.Default.Login, contentDescription = stringResource(id = R.string.desc_login))
            }
        else
        {

            IconButton(onClick = {destinationsNavigator.navigate(LoginScreenDestination)}) {
                Icon(Icons.Filled.Search, contentDescription = stringResource(id = R.string.desc_search))
            }
            IconButton(onClick = { viewModel.Favourites() })
            {
                Icon(Icons.Filled.Favorite, contentDescription = stringResource(id = R.string.desc_favorities))
            }
            IconButton(onClick = {
                Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                PokemonCardsApp.isLoginSuccessful = false
                destinationsNavigator.navigate(SearchScreenDestination)
            })
            {
                Icon(Icons.Default.Logout, contentDescription = stringResource(id = R.string.desc_logout))
            }
        }
    }
}
