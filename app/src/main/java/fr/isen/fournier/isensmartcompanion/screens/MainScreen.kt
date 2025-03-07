package fr.isen.fournier.isensmartcompanion.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import fr.isen.fournier.isensmartcompanion.R
import fr.isen.fournier.isensmartcompanion.models.HomeViewModel

@Composable
fun MainScreen(innerPadding: PaddingValues, viewModel: HomeViewModel) {
    val context = LocalContext.current
    var userInput by remember { mutableStateOf("") }

    val generatedTexts by viewModel.generatedTexts.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painterResource(R.drawable.isen),
            context.getString(R.string.isen_logo)
        )
        Text(context.getString(R.string.app_name), modifier = Modifier.padding(10.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            items(generatedTexts) { text ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                        .padding(12.dp)
                ) {
                    Text(text, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            TextField(
                value = userInput,
                onValueChange = { newValue -> userInput = newValue },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    errorContainerColor = Color.Transparent
                ),
                modifier = Modifier.weight(1f)
            )
            OutlinedButton(
                onClick = {
                    viewModel.generateText(userInput)
                    Toast.makeText(context, "User input : $userInput", Toast.LENGTH_LONG).show()
                },
                modifier = Modifier.background(Color.Red, shape = RoundedCornerShape(50))
            ) {
                Image(painterResource(R.drawable.send), "arrow send image")
            }
        }
    }
}