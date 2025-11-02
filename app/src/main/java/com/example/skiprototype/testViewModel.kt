package com.example.skiprototype

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.*
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

//View Model that manages live data across all instances of the various modules. (As per
// MVVM architecture, managing everything separately increases ease of scaling and persistence).
object ApiClient {
    fun create(baseUrl: String): DataViewModel.SkiApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(DataViewModel.SkiApi::class.java)
    }
}

class DataViewModel : ViewModel() {

    interface SkiApi {
        @GET("data")
        suspend fun getData(): JsonObject
    }

    // Pi sends data to this url, so this can read it and extract the json.
    // Needs to be connected to the PiSkiConnect Network.
    private val api = ApiClient.create("http://10.42.0.1:5000/")

    private val _temperature = MutableLiveData<String>()
    val temperature: LiveData<String> = _temperature
    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> get() = _isFetching

    fun setFetching(value: Boolean) {
        _isFetching.value = value
    }
    private val _personStatus = MutableLiveData<String>()
    val personStatus: LiveData<String> = _personStatus

    //Internal state
    private var lastLeftDetectedTime = 0L
    private var lastRightDetectedTime = 0L
    private val detectionCooldownMs = 3000L // 3 seconds

    private var leftCount = 0
    private var rightCount = 0

    fun fetchData() {
        viewModelScope.launch {
            try {
                val data = api.getData()
                parseData(data)
            } catch (e: Exception) {
                Log.e("DataViewModel", "Error fetching data", e)
            }
        }
    }

    private fun parseData(data: JsonObject) {
        // Extract temperature
        val tempValue = data.get("temperature")?.asString ?: "N/A"
        _temperature.postValue(tempValue)

        // Extract person detection
        val personLine = data.get("person")?.asString
        personLine?.let { processPersonDetection(it) }
    }
    //Next functions focus on getting the data into a nice message for the text to display
    private fun processPersonDetection(detection: String) {
        val position = when {
            detection.contains("Left", ignoreCase = true) -> "Left"
            detection.contains("Right", ignoreCase = true) -> "Right"
            else -> null
        }

        val distance = when {
            detection.contains("Close", ignoreCase = true) -> "Close"
            detection.contains("Far", ignoreCase = true) -> "Far"
            else -> null
        }

        val currentTime = SystemClock.elapsedRealtime()
        var message: String? = null

        when (position) {
            "Left" -> {
                if (currentTime - lastLeftDetectedTime >= detectionCooldownMs) {
                    leftCount++
                    lastLeftDetectedTime = currentTime
                    message = if (leftCount > 1) "Another person on left" else "Person detected on left"
                } else {
                    Log.d("DataViewModel", "Left detection ignored due to cooldown")
                }
            }
            "Right" -> {
                if (currentTime - lastRightDetectedTime >= detectionCooldownMs) {
                    rightCount++
                    lastRightDetectedTime = currentTime
                    message = if (rightCount > 1) "Another person on right" else "Person detected on right"
                } else {
                    Log.d("DataViewModel", "Right detection ignored due to cooldown")
                }
            }
            else -> message = "No person detected"
        }

        message?.let {
            _personStatus.postValue("$it ($distance)")
        }
    }
}
