package com.example.pokemoncards

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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

    Column(horizontalAlignment = Alignment.CenterHorizontally ){
        AsyncImage(model = card.images.small, contentDescription = card.name)

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

