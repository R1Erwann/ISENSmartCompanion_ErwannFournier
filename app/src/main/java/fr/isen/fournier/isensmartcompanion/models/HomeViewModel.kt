package fr.isen.fournier.isensmartcompanion.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fr.isen.fournier.isensmartcompanion.data.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val apiKey = "AIzaSyAKtq3yDns3fm1_dMBZgrYIYQzZqeKZDps"
    private val repository = GeminiRepository(apiKey = apiKey)

    private val _generatedTexts = MutableStateFlow<List<String>>(emptyList())
    val generatedTexts: StateFlow<List<String>> = _generatedTexts

    fun generateText(prompt: String) {
        viewModelScope.launch {
            val response = repository.generateText(prompt)
            _generatedTexts.update { it + response }
        }
    }
}