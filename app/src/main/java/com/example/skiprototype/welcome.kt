package com.example.skiprototype

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

//Description of project quickly.
@Composable
fun WelcomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { /* No bottom bar on welcome screen */ } ,
        containerColor = Color(0xFFADDBF1)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFADDBF1),
                        Color(0xFF7BB9EE)
                    )))

                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                // Heading
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Adaptive Skiing App",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Description / disclaimer card
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(22.dp),
                    elevation = CardDefaults.cardElevation(6.dp),

                ) {
                    Column(modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally) {

                    Text(
                        text = "Disclaimer: This app is designed to improve the skiing experience for a variety of users, including those with disabilities that make it harder to see, move, or interpret surroundings. It can also enhance the experience for normal skiers.\n\n The system uses a pressure mat, temperature sensors, and camera recognition. Voice alerts and visual aids are provided for safety and accessibility. It can be used by both ski instructors and skiers themselves. All data collected by the Pi is anonymous and only used to improve accessibility. The app is modular so that parts can be used as required by the user.",
                        fontSize = 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)
                    )
                        Image(
                            painter = painterResource(id = R.drawable.sitskier),
                            contentDescription = "Skiing illustration",
                            modifier = Modifier.size(80.dp).padding(8.dp)
                        )
                        Text(
                            text = "Made by Caitlin C as part of a project with Edward Yeung supervised by Assoc. Prof Kirsten Ellis at Monash University.",
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 30.dp, vertical = 12.dp)
                        )
                }}

                Spacer(modifier = Modifier.height(30.dp))

                //Navigates user to home page.
                Button(
                    onClick = {

                        navController.navigate(BottomNavItem.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp, // shadow
                        disabledElevation = 0.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F74AE),
                        contentColor = Color.White
                    ),
                ) {
                    Text(
                        text = "Accept and Continue",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
