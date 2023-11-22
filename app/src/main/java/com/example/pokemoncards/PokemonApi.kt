package com.example.pokemoncards

import androidx.compose.runtime.Composable
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class PokemonApi {

    companion object{
        private val client = HttpClient(Android){
            install(ContentNegotiation){
                json(Json{
                    ignoreUnknownKeys = true
                    isLenient = true
                    prettyPrint = true
                    allowStructuredMapKeys = true
                    encodeDefaults = true
                })

            }
        }

        suspend fun getCard(search: String): Main? {

            val response: HttpResponse =
                client.get("https://api.pokemontcg.io/v2/cards?q=name:$search") {
                    url{
                        //appendPathSegments("v2", "cards")
                        //parameters.append("name", "Pikachu")
                    }
                }
            return if (response.status.value in 200..299) {
                response.body()
            } else{
                null
            }
        }

        suspend fun getCardById(id: String): Main? {

            val response: HttpResponse =
                client.get("https://api.pokemontcg.io/v2/cards?q=id:$id") {
                    url{
                    }
                }
            return if (response.status.value in 200..299) {
                response.body()
            } else{
                null
            }
        }
    }

}