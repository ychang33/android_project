package com.example.pokemoncards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil.compose.AsyncImage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CardDetail(
    id: Int,
    card: Data,
    destinationsNavigator: DestinationsNavigator
){

    Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        AsyncImage(model = card.images.large, contentDescription = card.name)

        Box() {
            Column {
                Text(text = "ID: ${card.id}")
                Text(text = "Name: ${card.name}")
                card.rarity?.let { Text(text = "Rarity: $it") }

                Text(text = "Set: ${card.set.name}")
                Text(text = "Series: ${card.set.series}")

                card.tcgplayer?.let {
                    Text(text = "Prices")
                    Text(text = "Updated At: ${it.updatedAt}")
                    Text(text = "")
                }

            }
        }
        Button(onClick = { destinationsNavigator.popBackStack()}) {
            Text(text = "Go Back")
        }
    }
}

