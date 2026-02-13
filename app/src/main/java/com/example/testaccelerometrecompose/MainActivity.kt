package com.example.testaccelerometrecompose

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.testaccelerometrecompose.ui.theme.TestAccelerometreComposeTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var lastUpdate: Long = 0

    private var color : MutableState<Boolean> = mutableStateOf(false)

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
        // register this class as a listener for the accelerometer sensor
        lastUpdate = System.currentTimeMillis()

        enableEdgeToEdge()
        setContent {
            TestAccelerometreComposeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    SensorsInfo(color)
                }
            }
        }
    }

    override fun onSensorChanged(p0: SensorEvent) {
        getAccelerometer(p0)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        if (p0?.type == Sensor.TYPE_LIGHT) Toast.makeText(
            this,
            getString(R.string.changAcc, p1),
            Toast.LENGTH_SHORT
        ).show()

    }

    private fun getAccelerometer(event: SensorEvent) {
        // Adjusted threshold : 2.5 means an acceleration of 1.5 times Earth's gravity
        val accelerationThreshold = 1.5f
        val timeThreshold = 1000
        val values = event.values

        val x = values[0]
        val y = values[1]
        val z = values[2]

        // Calculation of the magnitude of the acceleration vector normalized by gravity
        val currentAcceleration = sqrt(x * x + y * y + z * z) / SensorManager.GRAVITY_EARTH

        val actualTime = System.currentTimeMillis()
        if (currentAcceleration >= accelerationThreshold) {
            if (actualTime - lastUpdate < timeThreshold) {
                return
            }
            lastUpdate = actualTime
            Toast.makeText(this, R.string.shuffed, Toast.LENGTH_SHORT).show()
            color.value = !(color.value)
        }
    }

    override fun onPause() {
        // unregister listener
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}

@Composable
fun SensorsInfo(color: MutableState<Boolean> ) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(if (color.value) Color.Red else Color.Green),
        shape = CardDefaults.shape,
        elevation = CardDefaults.cardElevation(),
        border = BorderStroke(10.dp, if (color.value) Color.Black else Color.LightGray)
    ) {
        Column {
            Text(text = "")
            Text(text = "")
            Row {
                Text(text = "            ")
                Text(text = stringResource(R.string.shake))
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true, widthDp = 360, heightDp = 640, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SensorsInfoPreview() {
    TestAccelerometreComposeTheme {
        SensorsInfo(mutableStateOf(false))
    }
}
