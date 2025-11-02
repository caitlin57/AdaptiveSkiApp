package com.example.skiprototype

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
@Composable
fun PressureScreen(navController: NavController) {

    //storess feedback text coming from the page
    var feedback by remember { mutableStateOf("Waiting for feedback...") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFADDBF1), Color(0xFF7BB9EE))
                )
            )
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "Pressure Mat",
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

            Spacer(modifier = Modifier.height(16.dp))

            //Description card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Text(
                    text = "This pressure mat helps you align your seating posture to prevent falling over, lopsidedness, and maintain balance.",
                    fontSize = 16.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = feedback,
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            //The way I did this is a bit dodgy but it works surprisingly well! Essentially, it puts the
            // webpage straight into the app, embedding it in the below card once it fetches the
            // grid off it. Edward has documentation about how to set up the webpage.
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE

                            //Bridge from JS -> Android
                            addJavascriptInterface(
                                object {
                                    @JavascriptInterface
                                    fun sendFeedback(data: String?) {
                                        Handler(Looper.getMainLooper()).post {
                                            feedback = data ?: ""
                                        }
                                    }
                                },
                                "AndroidBridge"
                            )

                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    //once page fully loads inject js to make it pretty and get the table that Edward edited.
                                    view?.evaluateJavascript(
                                        """
                                        (function() {
                                            try {
                                                const grid = document.querySelector('.grid');
                                                const centroids = document.querySelector('.centroids');
                                                const lines = document.querySelector('.lines');
                                                const feedbackElem = document.getElementById('feedback');
                                    
                                                // Hide everything except grid, centroids, lines, feedback
                                                Array.from(document.body.children).forEach(el => {
                                                    if (el !== grid && el !== centroids && el !== lines && el !== feedbackElem) {
                                                        el.style.display = 'none';
                                                    }
                                                });
                                    
                                                // Optional: move grid, centroids, lines to top of body for layout
                                                if (grid) document.body.prepend(grid);
                                                if (centroids) document.body.appendChild(centroids);
                                                if (lines) document.body.appendChild(lines);
                                    
                                                function sendFeedback() {
                                                    const f = document.getElementById('feedback');
                                                    if (f) AndroidBridge.sendFeedback(f.textContent || '');
                                                }
                                    
                                                // Send initial feedback
                                                sendFeedback();
                                    
                                                // Watch for updates
                                                const fNode = document.getElementById('feedback');
                                                if (fNode) {
                                                    const obs = new MutationObserver(sendFeedback);
                                                    obs.observe(fNode, { childList: true, subtree: true });
                                                }
                                    
                                                // Safety polling
                                                if (!window.__android_feedback_poll) {
                                                    window.__android_feedback_poll = setInterval(sendFeedback, 500);
                                                }
                                            } catch(e) {
                                                console.error(e);
                                            }
                                        })();
                                        """.trimIndent(),
                                        null
                                    )

                                }
                            }

                            //working url"http://192.168.0.110:8999" ->
                            // will be updated to Pi once I get the right IP address when have access to physical mat.
                            loadUrl("http://192.168.0.110:8999")
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))


        }
    }
}
