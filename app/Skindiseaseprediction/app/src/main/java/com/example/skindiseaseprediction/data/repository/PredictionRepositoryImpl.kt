package com.example.skindiseaseprediction.data.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.skindiseaseprediction.data.remote.SkinDiseaseApi
import com.example.skindiseaseprediction.domain.model.Prediction
import com.example.skindiseaseprediction.domain.repository.PredictionRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PredictionRepositoryImpl(
    private val api: SkinDiseaseApi
) : PredictionRepository {
    override suspend fun predict(imageFile: File): Result<Prediction> {
        return try {
            val compressedFile = compressImage(imageFile)
            val requestFile = compressedFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", compressedFile.name, requestFile)
            val response = api.predict(body)
            Result.success(Prediction(label = response.label, confidence = response.confidence))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun compressImage(file: File): File {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        var quality = 100
        val stream = ByteArrayOutputStream()
        
        // Initial compression
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        
        // Iteratively reduce quality until size is below 200KB or quality is too low
        while (stream.toByteArray().size > 200 * 1024 && quality > 10) {
            stream.reset()
            quality -= 10
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }

        val compressedFile = File(file.parent, "compressed_" + file.name)
        val fos = FileOutputStream(compressedFile)
        fos.write(stream.toByteArray())
        fos.flush()
        fos.close()
        
        return compressedFile
    }
}
