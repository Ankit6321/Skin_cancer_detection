package com.example.skindiseaseprediction

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.skindiseaseprediction.data.remote.SkinDiseaseApi
import com.example.skindiseaseprediction.data.repository.PredictionRepositoryImpl
import com.example.skindiseaseprediction.presentation.PredictionViewModel
import com.example.skindiseaseprediction.presentation.PredictionViewModelFactory
import com.example.skindiseaseprediction.ui.theme.SkinDiseasePredictionTheme
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val api = Retrofit.Builder()
            .baseUrl(SkinDiseaseApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(SkinDiseaseApi::class.java)

        val repository = PredictionRepositoryImpl(api)
        val viewModelFactory = PredictionViewModelFactory(repository)

        setContent {
            SkinDiseasePredictionTheme {
                val viewModel: PredictionViewModel = viewModel(factory = viewModelFactory)
                val state by viewModel.state
                val context = LocalContext.current

                var tempImageUri by remember { mutableStateOf<Uri?>(null) }
                var tempImageFile by remember { mutableStateOf<File?>(null) }

                val cameraLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicture()
                ) { success ->
                    if (success) {
                        tempImageFile?.let { viewModel.onImageCaptured(it) }
                    }
                }

                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                        tempImageFile = file
                        tempImageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file
                        )
                        cameraLauncher.launch(tempImageUri!!)
                    }
                }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(title = { Text("Skin Cancer Prediction") })
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (state.capturedImageFile != null) {
                            Image(
                                painter = rememberAsyncImagePainter(state.capturedImageFile),
                                contentDescription = "Captured Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentScale = ContentScale.Crop
                            )

                            if (state.isLoading) {
                                CircularProgressIndicator()
                            } else if (state.prediction != null) {
                                Card(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                            text = "Prediction: ${state.prediction?.label}",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        Text(
                                            text = "Confidence: ${(state.prediction?.confidence?.times(100))?.format(2)}%",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }

                            if (!state.isLoading) {
                                Button(
                                    onClick = { viewModel.analyzeImage() },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Analyze")
                                }
                            }
                        }

                        Button(
                            onClick = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) -> {
                                        val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                                        tempImageFile = file
                                        tempImageUri = FileProvider.getUriForFile(
                                            context,
                                            "${context.packageName}.fileprovider",
                                            file
                                        )
                                        cameraLauncher.launch(tempImageUri!!)
                                    }
                                    else -> {
                                        permissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isLoading
                        ) {
                            Text(if (state.capturedImageFile == null) "Take Photo" else "Retake Photo")
                        }

                        if (state.error != null) {
                            Text(
                                text = "Error: ${state.error}",
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                            )
                        }
                    }
                }
            }
        }
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
