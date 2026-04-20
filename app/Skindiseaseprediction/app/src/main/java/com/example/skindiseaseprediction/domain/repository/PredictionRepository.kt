package com.example.skindiseaseprediction.domain.repository

import com.example.skindiseaseprediction.domain.model.Prediction
import java.io.File

interface PredictionRepository {
    suspend fun predict(imageFile: File): Result<Prediction>
}
