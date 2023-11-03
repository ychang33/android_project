package com.example.pokemoncards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.pokemoncards.ui.theme.PokemonCardsTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PokemonCardsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SearchScreen()
                    //MyAppPortrait()
                }
            }
        }
    }
}

@Composable
fun SearchScreen(){

    var cards by remember { mutableStateOf<List<Data>>(emptyList()) }
//    var mainquery by remember {
//        mutableStateOf("")
//    }

    Column {
        SearchBar(onSearch = { newCards -> cards = newCards as List<Data> })

        CardList(cards)
    }
//    LaunchedEffect(mainquery != ""){
//        val result = PokemonApi.getCard(mainquery)
//
//        if (result != null) {
//            cards = result.data
//        }
//    }
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
            card.rarity?.let { Text(text = it) }
            card.tcgplayer?.let { Text(text = it.updatedAt) }
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