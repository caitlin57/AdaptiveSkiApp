package com.example.skiprototype

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skiprototype.Settings.SettingsViewModel
import kotlinx.coroutines.delay
import java.util.Locale
//Main screen where users can fetch object detection data
@Composable
fun SkiStatusScreen(
    dataViewModel: DataViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController,
    backgroundColor: Color = Color(0xFFADDBF1),
    textColor: Color = Color.Black
) {
    val liveDataStarted by dataViewModel.isFetching.collectAsState()
    val showNotification = settingsViewModel.showNotification

    val context = LocalContext.current

    //ViewModel data
    val temperature by dataViewModel.temperature.observeAsState("Loading...")
    val personStatus by dataViewModel.personStatus.observeAsState("Waiting for detection...")

    //Track last announced status
    var lastAnnouncedStatus by remember { mutableStateOf("") }

    //Initialize text to speech
    val tts = remember {
        TextToSpeech(context) { ttsInstance ->
            if (ttsInstance == TextToSpeech.SUCCESS) {
                // The constructor lambda parameter is actually the status Int,
                // so need to set language after checking status doesnt need anything in here atm.
            }
        }
    }

    //Automatically navigate to temp if temperature < 10 so that alert will show if below set
    LaunchedEffect(temperature) {
        val tempValue = temperature.toFloatOrNull()
        if (tempValue != null && tempValue < 10f) {
            if (navController.currentDestination?.route != BottomNavItem.Record.route) {
                navController.navigate(BottomNavItem.Record.route)
            }
        }
    }

    //Voice announcement for personStatus changes
    LaunchedEffect(personStatus, settingsViewModel.voiceAnnouncementsEnabled) {
        if (liveDataStarted &&
            personStatus != lastAnnouncedStatus &&
            settingsViewModel.voiceAnnouncementsEnabled
        ) {
            lastAnnouncedStatus = personStatus

            //Haptic feedback
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))

            //Voice announcement
            tts.speak(personStatus, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFADDBF1),
                    Color(0xFF7BB9EE)
                )))
            .padding(30.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Live Ski Data",
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


            Spacer(modifier = Modifier.height(20.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FDFF)) ,
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Temperature", color = Color.DarkGray, fontSize = 18.sp, fontStyle = FontStyle.Italic)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (!liveDataStarted)
                            "Click below to get live data"
                        else
                            "$temperature",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 2.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF7FDFF)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Skier Detection", color = Color.DarkGray, fontSize = 18.sp,  fontStyle = FontStyle.Italic)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (!liveDataStarted)
                            "Not fetching"
                        else
                            personStatus,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {dataViewModel.setFetching(!liveDataStarted)},
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (liveDataStarted) Color.Red else Color(0xFF2F74AE),
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth().height(55.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp,
                    disabledElevation = 0.dp
                ),
            ) {
                Text(
                    text = if (liveDataStarted) "Stop Fetching" else "Fetch Data",
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "To get started, make sure you are connected to the PiSkiConnect network",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = textColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(95.dp))
            // Info Card
            if (showNotification) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        IconButton(
                            onClick = { settingsViewModel.toggleShowNotification() },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss notification",
                                tint = Color.Gray
                            )
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, end = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Live Data Streaming",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "You’ll get periodic updates. If temperature drops below safe levels, you’ll be redirected automatically!",
                                fontSize = 12.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            //Live fetch data loop
            if (liveDataStarted) {
                LaunchedEffect(liveDataStarted) {
                    while (liveDataStarted) {
                        dataViewModel.fetchData()
                        delay(1000)
                    }
                }
            }
        }
    }
}
