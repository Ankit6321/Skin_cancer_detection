package com.example.skindiseaseprediction.presentation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skindiseaseprediction.domain.model.Prediction
import com.example.skindiseaseprediction.domain.repository.PredictionRepository
import kotlinx.coroutines.launch
import java.io.File

data class PredictionUiState(
    val isLoading: Boolean = false,
    val prediction: Prediction? = null,
    val error: String? = null,
    val capturedImageFile: File? = null
)

class PredictionViewModel(
    private val repository: PredictionRepository
) : ViewModel() {

    private val _state = mutableStateOf(PredictionUiState())
    val state: State<PredictionUiState> = _state

    fun onImageCaptured(file: File) {
        _state.value = _state.value.copy(
            capturedImageFile = file,
            prediction = null,
            error = null
        )
    }

    fun analyzeImage() {
        val file = _state.value.capturedImageFile ?: return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            val result = repository.predict(file)
            result.onSuccess { prediction ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    prediction = prediction
                )
            }.onFailure { exception ->
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = exception.message ?: "An unknown error occurred"
                )
            }
        }
    }
    
    fun reset() {
        _state.value = PredictionUiState()
    }
}
