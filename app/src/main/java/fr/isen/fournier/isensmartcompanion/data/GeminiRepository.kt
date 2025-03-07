package fr.isen.fournier.isensmartcompanion.data

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository(apiKey: String) {
    val config = generationConfig {
        temperature = 0.7f
    }
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyAKtq3yDns3fm1_dMBZgrYIYQzZqeKZDps",
        generationConfig = config
    )

    suspend fun generateText(prompt: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val response = generativeModel.generateContent(prompt)
                response.text ?: "Aucune réponse générée."
            } catch (e: Exception) {
                "Erreur : ${e.localizedMessage}"
            }
        }
    }
}