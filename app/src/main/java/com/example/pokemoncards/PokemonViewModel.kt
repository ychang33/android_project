package com.example.pokemoncards


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
//

class PokemonViewModel: ViewModel(){
    //var cards by mutableStateOf<List<Data>?>(emptyList())
    var cards by mutableStateOf<List<Data>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun Favourites(){

        if (PokemonCardsApp.isLoginSuccessful) {
            val db = Firebase.firestore
            db.collection("favorites").get()
                .addOnSuccessListener { documents ->
                    val favoriteList = mutableListOf<Data>()
                    for (document in documents) {
                        if (document.id.substring(
                                document.id.length - PokemonCardsApp.currentUserId.length) ==
                            PokemonCardsApp.currentUserId) {
                            val favoriteCard = document.toObject(Data::class.java)
                            favoriteList.add(favoriteCard)
                        }
                    }
                    cards = favoriteList
                }
        }
    }
}
