package com.noemi.imageloader.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class ZipoImage(

    @SerialName("id")
    val id: Int,

    @SerialName("imageUrl")
    val imageUrl: String
)