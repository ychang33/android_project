package com.example.pokemoncards

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalMaterial3Api::class)
@Destination
@Composable
fun CardDetail(
    id: Int,
    card: Data,
    destinationsNavigator: DestinationsNavigator
) {

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = card.name) },
                navigationIcon = {
                    IconButton(onClick = { destinationsNavigator.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
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
                }
                ElevatedCard(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "ID: ${card.id}")
                                Text(text = "Name: ${card.name}")
                                card.rarity?.let { Text(text = "Rarity: $it") }
                            }
                            if(PokemonCardsApp.isLoginSuccessful)
                                FavoriteIcon(card)
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, top = 5.dp, bottom = 5.dp)
                        )
                        {
                            Text(text = "Set: ${card.set.name}")
                            Text(text = "Series: ${card.set.series}")

                            card.tcgplayer?.let {
                                Spacer(modifier = Modifier.padding(5.dp))
                                Text(text = "Prices (USD)")
                                Text(text = "Updated At: ${it.updatedAt}")
                                Spacer(modifier = Modifier.padding(4.dp))

                                card.tcgplayer.prices?.let {
                                    val priceMap = card.tcgplayer.prices.toMap()
                                    for ((name, value) in priceMap) {
                                        value?.let {
                                            Text(text = name)
                                            val detail = value.toMap()
                                            if (detail != null) {
                                                for ((name, value) in detail) {
                                                    value?.let {
                                                        Text(text = "${name}: $$value")
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.padding(4.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


@Composable
fun FavoriteIcon(card: Data, modifier: Modifier = Modifier) {
    var isFavorite by remember { mutableStateOf(false) }
    val context = LocalContext.current
    // Retrieve and check card marked as favorite card
    val db = Firebase.firestore
    db.collection("favorites").document(card.id + PokemonCardsApp.currentUserId).get()
        .addOnSuccessListener{document->
            if (document != null){
                var cardid = document.data?.get("id")
                isFavorite = (cardid == card.id)
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
                    table = db.collection("favorites").document(card.id + PokemonCardsApp.currentUserId)
                    // Remove the record from database
                    if (isFavorite) {

                        table.delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Remove success", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                    exception -> Toast.makeText(context, "Remove failure", Toast.LENGTH_SHORT).show()
                            }
                    }else{
                        // Add a record to database
                       table.set(card)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Insert success", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener{
                                    exception -> Toast.makeText(context, "Insert failure", Toast.LENGTH_SHORT).show()
                            }
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
