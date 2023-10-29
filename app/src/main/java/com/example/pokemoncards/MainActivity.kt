package com.example.pokemoncards

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.example.pokemoncards.ui.theme.PokemonCardsTheme


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
                    MyApp()
                }
            }
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

    LazyColumn{
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
