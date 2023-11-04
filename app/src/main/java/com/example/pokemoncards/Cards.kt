package com.example.pokemoncards

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Main(
    val data: List<Data>,
    val page: Int,
    val pageSize: Int,
    val count: Int,
    val totalCount: Int
)

@Serializable
data class Data(
    val id: String,
    val name: String,
    val supertype: String,
    val set: Set,
    val number: String,
    val artist: String? = null,
    val rarity: String? = null,
    val flavorText: String? = null,
    val images: Images,
    val tcgplayer: Tcgplayer? = null,
)

@Serializable
data class Set(
    val id: String,
    val name: String,
    val series: String,
    val printedTotal: Int,
    val total: Int,
    val releaseDate: String,
    val updatedAt: String,
)

@Serializable
data class Images(
    val small: String,
    val large: String
)

@Serializable
data class Tcgplayer(
    val url: String,
    val updatedAt: String,
    val prices: Prices? = null
)

@Serializable
data class Prices(
    val normal: PriceDetail? = null,
    val holofoil: PriceDetail? = null,
    val reverseHolofoil: PriceDetail? = null,
    @SerialName("1stEditionHolofoil") val firstHolofoil: PriceDetail? = null,
    @SerialName("1stEditionNormal") val firstNormal: PriceDetail? = null,
)

@Serializable
data class PriceDetail(
    val low: Float? = null,
    val mid: Float? = null,
    val high: Float? = null,
    val market: Float? = null,
    val directLow: Float? = null
)
