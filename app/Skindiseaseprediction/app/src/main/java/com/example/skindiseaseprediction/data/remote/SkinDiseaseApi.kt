package com.example.skindiseaseprediction.data.remote

import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface SkinDiseaseApi {
    @Multipart
    @POST("predict")
    suspend fun predict(
        @Part file: MultipartBody.Part
    ): PredictionResponse

    companion object {
        const val BASE_URL = "https://skin-disease-api-onsr.onrender.com/"
    }
}
