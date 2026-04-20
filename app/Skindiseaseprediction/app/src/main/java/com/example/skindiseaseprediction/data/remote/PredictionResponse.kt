package com.example.skindiseaseprediction.data.remote

import com.google.gson.annotations.SerializedName

data class PredictionResponse(
    @SerializedName("class")
    val label: String,
    @SerializedName("confidence")
    val confidence: Double
)
