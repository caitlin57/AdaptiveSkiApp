package com.example.skiprototype

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skiprototype.Settings.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun TempScreen(
    navController: NavController,
    dataViewModel: DataViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val threshold by remember { derivedStateOf { settingsViewModel.temperatureThreshold } }
    val temperature by dataViewModel.temperature.observeAsState("0°C")
    val tempValue = temperature.replace("°C", "").toFloatOrNull()
    var showAlertDialog by remember { mutableStateOf(false) }

    //text to speech set up the same way as home screen
    val tts = remember {
        TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // tts.language = java.util.Locale.US
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    // --- Continuous monitoring loop ---
    LaunchedEffect(Unit) {
        while (isActive) {
            //Remove °C and parse float safely

            val belowThreshold = tempValue != null && tempValue < threshold


            if (belowThreshold) {
                showAlertDialog = true

                //Haptic feedback
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))

                //Voice alert when below the threshold measured.
                if (settingsViewModel.voiceAnnouncementsEnabled) {
                    tts.speak(
                        "Warning! Temperature dropped below safe level. Please warm up immediately.",
                        TextToSpeech.QUEUE_FLUSH,
                        null,
                        "temp_alert"
                    )
                }
            } else {
                showAlertDialog = false
                tts.stop()
            }

            delay(5000) // check every 5 seconds
        }
    }
    Text(text = "$temperature, $tempValue, $threshold")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFADDBF1), Color(0xFF7BB9EE))))
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Title
            Text(
                text = "Temperature Alert Settings",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                thickness = 1.8.dp,
                color = Color(0xFF3070A7)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Threshold slider
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Set Alert Threshold (°C)", fontSize = 18.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Slider(
                        value = settingsViewModel.temperatureThreshold,
                        onValueChange = { settingsViewModel.updateTemperatureThreshold(it) },
                        valueRange = -10f..15f,
                        steps = 25,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF0288D1),
                            activeTrackColor = Color(0xFF0288D1)
                        )
                    )
                    Text(
                        text = "Current threshold: ${threshold.toInt()}°C",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Status card: changes full screen once low temp detected..
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FDFF)),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val tempValDisplay = temperature.replace("°C", "").toFloatOrNull()
                    if (tempValDisplay != null && tempValDisplay < threshold) {
                        Text(
                            text = "Warning: Temperature is below safe level!",
                            color = Color.Red,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Current Temperature: $tempValDisplay°C\nPlease warm up immediately.",
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.sitskier),
                            contentDescription = "Skiing illustration",
                            modifier = Modifier.size(200.dp).padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "All good! Temperature is safe.\nEnjoy skiing",
                            fontSize = 18.sp,
                            color = Color(0xFF3866A1),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Current temperature card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Current Temperature",
                        color = Color.DarkGray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = temperature,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF000000)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Alert Dialog
    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Temperature Alert!") },
            text = { Text("⚠Temperature dropped below threshold! Warm up immediately.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAlertDialog = false
                        tts.stop()
                    }
                ) {
                    Text("Understood")
                }
            }
        )
    }
}
