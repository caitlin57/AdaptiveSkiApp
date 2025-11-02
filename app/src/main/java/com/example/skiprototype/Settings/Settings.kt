package com.example.skiprototype

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skiprototype.Settings.SettingsViewModel
import com.example.skiprototype.Settings.SettingsViewModelFactory
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel(factory = SettingsViewModelFactory())) {
    val scrollState = rememberScrollState()
    var showTempInfoDialog by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFADDBF1), //light blue
                    Color(0xFF7BB9EE)  //darker
                )))
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Settings",
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
        //voice announcements card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Voice Announcements",
                    fontSize = 16.sp,
                    color = if (viewModel.darkModeEnabled) Color.White else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = viewModel.voiceAnnouncementsEnabled,
                    onCheckedChange = { viewModel.voiceAnnouncementsEnabled = it }
                )
            }
        }

        // Voice button card
//        Card(
//            shape = RoundedCornerShape(16.dp),
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(
//                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
//            )
//        ) {
//            Button(
//                onClick = { viewModel.voiceOption = "Changed Voice" },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(16.dp),
//                shape = RoundedCornerShape(12.dp),
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = Color(0xFF0288D1),
//                    contentColor = Color.White
//                )
//            ) {
//                Text("Change Voice (Current: ${viewModel.voiceOption})", fontSize = 16.sp)
//            }
//        }
        // Temperature Threshold slider card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Temperature Threshold: ${viewModel.temperatureThreshold.toInt()}°C",
                        fontSize = 16.sp,
                        color = if (viewModel.darkModeEnabled) Color.White else Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showTempInfoDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Info",
                            tint = if (viewModel.darkModeEnabled) Color.White else Color.Black
                        )
                    }
                }
                Slider(
                    value = viewModel.temperatureThreshold,
                    onValueChange = { viewModel.updateTemperatureThreshold(it) },
                    valueRange = -10f..15f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFF0288D1),
                        activeTrackColor = Color(0xFF0288D1)
                    )
                )
            }
        }
        if (showTempInfoDialog) {
            AlertDialog(
                onDismissRequest = { showTempInfoDialog = false },
                title = { Text("Temperature Threshold Info") },
                text = {
                    Text(
                        "Recommended value is 10°C. Be careful if you change it. " +
                                "If the temperature falls below this threshold, you will receive notifications."
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showTempInfoDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        // Keep screen on toggle card
//        Card(
//            shape = RoundedCornerShape(16.dp),
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(
//                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
//            )
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "Keep Screen On",
//                    fontSize = 16.sp,
//                    color = if (viewModel.darkModeEnabled) Color.White else Color.Black,
//                    modifier = Modifier.weight(1f)
//                )
//                Switch(
//                    checked = viewModel.keepScreenOn,
//                    onCheckedChange = { viewModel.keepScreenOn = it }
//                )
//            }
//        }


        // Temperature sensing toggle card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Temperature Sensing",
                    fontSize = 16.sp,
                    color = if (viewModel.darkModeEnabled) Color.White else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = viewModel.temperatureSensing,
                    onCheckedChange = { viewModel.temperatureSensing = it }
                )
            }
        }

        // Pi camera toggle card
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Pi Camera Enabled",
                    fontSize = 16.sp,
                    color = if (viewModel.darkModeEnabled) Color.White else Color.Black,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = viewModel.piCameraEnabled,
                    onCheckedChange = { viewModel.piCameraEnabled = it }
                )
            }
        }
        Button(
            onClick = {


            },
            modifier = Modifier.fillMaxWidth().height(55.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 8.dp,  // shadow when enabled
                pressedElevation = 12.dp, // shadow when pressed
                disabledElevation = 0.dp
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2F74AE),
                contentColor = Color.White
            ),
        ) {
            Text(
                text = "Learn more about this project",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Dark Mode toggle card
//        Card(
//            shape = RoundedCornerShape(16.dp),
//            modifier = Modifier.fillMaxWidth(),
//            colors = CardDefaults.cardColors(
//                containerColor = if (viewModel.darkModeEnabled) Color(0xFF37474F) else Color(0xFFE1F5FE)
//            )
//        ) {
//            Row(
//                modifier = Modifier
//                    .padding(16.dp)
//                    .fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Text(
//                    "Dark Mode",
//                    fontSize = 16.sp,
//                    color = if (viewModel.darkModeEnabled) Color.White else Color.Black,
//                    modifier = Modifier.weight(1f)
//                )
//                Switch(
//                    checked = viewModel.darkModeEnabled,
//                    onCheckedChange = { viewModel.darkModeEnabled = it }
//                )
//            }
//        }
    }
}
