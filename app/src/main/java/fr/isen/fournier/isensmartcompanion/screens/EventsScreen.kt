package fr.isen.fournier.isensmartcompanion.screens

import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.isen.fournier.isensmartcompanion.models.Event
import fr.isen.fournier.isensmartcompanion.models.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import fr.isen.fournier.isensmartcompanion.R
import android.Manifest
import androidx.compose.ui.platform.LocalContext

@Composable
fun EventsScreen(innerPadding: PaddingValues) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    LaunchedEffect(Unit) {
        RetrofitInstance.api.getEvents().enqueue(object : Callback<List<Event>> {
            override fun onResponse(call: Call<List<Event>>, response: Response<List<Event>>) {
                if (response.isSuccessful) {
                    events = response.body() ?: emptyList()
                } else {
                    Log.e("EventsScreen", "Erreur API: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Event>>, t: Throwable) {
                Log.e("EventsScreen", "Erreur de connexion: ${t.message}")
            }
        })
    }

    Column(modifier = Modifier.padding(innerPadding)) {
        LazyColumn {
            items(events) { event ->
                if (selectedEvent != null && event == selectedEvent) {
                    EventDetail(event = event, innerPadding = innerPadding)
                } else {
                    EventRow(event = event) {
                        selectedEvent = event
                    }
                }
            }
        }
    }
}

@Composable
fun EventRow(event: Event, onEventClick: (Event) -> Unit) {
    Card(modifier = Modifier
        .padding(16.dp)
        .clickable {
            onEventClick(event)
        }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(event.title)
            Text(event.description)
        }
    }
}

@Composable
fun EventDetail(event: Event, innerPadding: PaddingValues) {
    Column(modifier = Modifier
        .padding(innerPadding)
        .padding(16.dp)
    ) {
        Text(text = event.title, style = MaterialTheme.typography.titleLarge)
        Text(text = "Date : ${event.date}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Catégorie : ${event.category}", style = MaterialTheme.typography.bodySmall)
        Text(text = "Lieu : ${event.location}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        ColorBox(event = event,
            Modifier
                .height(50.dp)
                .fillMaxWidth(0.5f)
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun ColorBox(event: Event,modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isNotificationPermissionGranted by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true)
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            isNotificationPermissionGranted = isGranted
        }
    )

    LaunchedEffect(key1 = Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val color = remember { mutableStateOf(Color.Red) }
    var previousColor by remember { mutableStateOf(Color.Red) }

    Box(
        modifier = modifier
            .background(color.value)
            .clickable {
                val newColor = if (color.value == Color.Red) Color.Green else Color.Red
                color.value = newColor

                if (previousColor == Color.Red && newColor == Color.Green && isNotificationPermissionGranted) {
                    val notificationManager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    val notification = NotificationCompat.Builder(context, "test_channel")
                        .setContentTitle(event.title)
                        .setContentText("Vous êtes maintenant abonnés à l'événement ${event.title} !")
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .build()
                    notificationManager.notify(1, notification)
                }
                previousColor = newColor
            }
    ) {
        Text(
            text = context.getString(R.string.notification_button_text),
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 50.dp)
        )
    }
}