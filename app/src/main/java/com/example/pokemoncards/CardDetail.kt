package com.example.pokemoncards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Destination
@Composable
fun CardDetail(
    id: Int,
    card: Data,
    destinationsNavigator: DestinationsNavigator
) {
    Column(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Centered AsyncImage
            AsyncImage(
                model = card.images.large,
                contentDescription = card.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
            )
            //.fillMaxSize()
            // FavoriteIcon in the top-right corner
/*            FavoriteIcon(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            )*/
        }

        // Rest of your content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "ID: ${card.id}")
                    Text(text = "Name: ${card.name}")
                    card.rarity?.let { Text(text = "Rarity: $it") }
                }
                FavoriteIcon(card)

            }

            Text(text = "Set: ${card.set.name}")
            Text(text = "Series: ${card.set.series}")

            card.tcgplayer?.let {
                Text(text = "Prices")
                Text(text = "Updated At: ${it.updatedAt}")
                Text(text = "")
            }
        }

        Button(onClick = { destinationsNavigator.popBackStack() }) {
            Text(text = "Go Back")
        }
    }
}



@Composable
fun FavoriteIcon(card: Data, modifier: Modifier = Modifier) {
    var isFavorite by remember { mutableStateOf(false) }
    // Retrieve and check card marked as favorite card
    val db = Firebase.firestore
    db.collection("favorites").document(card.id).get()
        .addOnSuccessListener{document->
            if (document != null){
                val cardData = document.toObject(Data::class.java)
                if (cardData != null) {
                    isFavorite = (cardData.id == card.id)
                }
            }
        }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = {

                    // Remove the record from database
                    if (isFavorite) {
                        db.collection("favorites").document("id").set(card)
                    }else{
                        // Add a record to database
                        db.collection("favorites").document(card.id).delete()
                    }
                    isFavorite = !isFavorite
                }
            ) {
                Icon(
                    painter = if (isFavorite) {
                        painterResource(id = R.drawable.baseline_favorite_24)
                    } else {
                        painterResource(id = R.drawable.baseline_favorite_border_24)
                    },
                    contentDescription = "Favorite Icon"
                )
            }
        }
    }
}
