package com.example.oboepitch

import android.os.Bundle
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private external fun startListening()
    private external fun stopListening()

    var latestPitch: Float? = null
    var isListening: Boolean = false


    init {
        System.loadLibrary("oboepitch")
    }

    private lateinit var pitchTextView: TextView
    private lateinit var pitchNameTextView: TextView
    private lateinit var listeningButtonView: TextView
    private lateinit var circleRotator: FrameLayout
    private lateinit var circleNeedle: ImageView

    val pitchToAngle = mapOf(
        "C" to 0f,
        "C#" to 30f,
        "D" to 60f,
        "D#" to 90f,
        "E" to 120f,
        "F" to 150f,
        "F#" to 180f,
        "G" to 210f,
        "G#" to 240f,
        "A" to 270f,
        "A#" to 300f,
        "B" to 330f,
    )

    fun rotateNeedleToPitch(pitch: String) {
        val pitchToAngle = mapOf(
            "C" to 0f, "C#" to 30f, "D" to 60f, "D#" to 90f,
            "E" to 120f, "F" to 150f, "F#" to 180f,
            "G" to 210f, "G#" to 240f, "A" to 270f,
            "A#" to 300f, "B" to 330f
        )
        val angle = pitchToAngle[pitch] ?: 0f
        circleNeedle.animate().rotation(angle).setDuration(300).start()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pitchTextView = findViewById(R.id.pitchTextView)
        pitchNameTextView = findViewById(R.id.pitchNameTextView)
        listeningButtonView = findViewById(R.id.stopButton)
        circleRotator = findViewById(R.id.pointer)
        circleNeedle = findViewById(R.id.needle)
        listeningButtonView.text = "Start Listening"
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isListening) {
                listeningButtonView.text = "Start Listening"
                pitchNameTextView.visibility = INVISIBLE
                circleRotator.visibility = GONE
                updatePitch(latestPitch ?: 0f)
                pitchTextView.text =
                    "your pitch for \"Sa\" is : ${getNoteFromPitch((latestPitch ?: 0f).toDouble())}"
                stopListening()
                isListening = false
            } else {
                listeningButtonView.text = "Stop Listening"
                pitchTextView.text = "Sing SAAA..."
                pitchNameTextView.visibility = VISIBLE
                circleRotator.visibility = VISIBLE
                isListening = true
                startListening()
            }
        }

        pitchTextView.text = "Sing SAAA..."
    }

    @Keep
    fun onPitchDetected(pitch: Float) {
        runOnUiThread {
            if (isListening) {
                pitchTextView.text = "Pitch: %.2f Hz".format(pitch)
                latestPitch = pitch
                rotateNeedleToPitch(getNoteFromPitch(pitch.toDouble()))
                val note = getNoteFromPitch(pitch.toDouble())
                pitchNameTextView.text = note
            }
        }
    }

    private fun updatePitch(pitch: Float) {
        latestPitch = pitch
    }

    fun getNoteFromPitch(pitch: Double): String {
        val note_A = listOf(
            27.5,
            55.0,
            110.0,
            220.0,
            440.0,
            880.0,
            1760.0,
            3520.0,
        )
        val note_A_sharp = listOf(
            29.135,
            58.27,
            116.54,
            233.08,
            466.16,
            932.32,
            1864.64,
            3729.28
        )
        val note_B = listOf(
            30.8675,
            61.735,
            123.47,
            246.94,
            493.88,
            987.76,
            1975.52,
            3951.04
        )
        val note_C = listOf(
            16.351875,
            32.70375,
            65.4075,
            130.815,
            261.63,
            523.26,
            1046.52,
            2093.04
        )
        val note_C_sharp = listOf(
            17.32375,
            34.6475,
            69.295,
            138.59,
            277.18,
            554.36,
            1108.72,
            2217.44
        )
        val note_D = listOf(
            18.35375,
            36.7075,
            73.415,
            146.83,
            293.66,
            587.32,
            1174.64,
            2349.28
        )
        val note_D_sharp = listOf(
            19.445625,
            38.89125,
            77.7825,
            155.565,
            311.13,
            622.26,
            1244.52,
            2489.04
        )
        val note_E = listOf(
            20.601875,
            41.20375,
            82.4075,
            164.815,
            329.63,
            659.26,
            1318.52,
            2637.04
        )
        val note_F = listOf(
            21.826875,
            43.65375,
            87.3075,
            174.615,
            349.23,
            698.46,
            1396.92,
            2793.84
        )
        val note_F_sharp = listOf(
            23.124375,
            46.24875,
            92.4975,
            184.995,
            369.99,
            739.98,
            1479.96,
            2959.92
        )
        val note_G = listOf(
            24.5,
            49.0,
            98.0,
            196.0,
            392.0,
            784.0,
            1568.0,
            3136.0,
        )
        val note_G_sharp = listOf(
            25.95625,
            51.9125,
            103.825,
            207.65,
            415.3,
            830.6,
            1661.2,
            3322.4
        )
        val notes = listOf(
            Pair("C", note_C),
            Pair("C#", note_C_sharp),
            Pair("D", note_D),
            Pair("D#", note_D_sharp),
            Pair("E", note_E),
            Pair("F", note_F),
            Pair("F#", note_F_sharp),
            Pair("G", note_G),
            Pair("G#", note_G_sharp),
            Pair("A", note_A),
            Pair("A#", note_A_sharp),
            Pair("B", note_B),
        )

        val ranges = mutableListOf<Pair<ClosedFloatingPointRange<Double>, String>>()

        // Create ranges between each adjacent note frequency
        for (i in note_A.indices) {
            for (j in notes.indices) {
                val currentNote = notes[j]
                val nextNote = notes[(j + 1) % notes.size] // wrap around to start
                val currentFreq = currentNote.second[i]
                val nextFreq = nextNote.second[i]
                val lower = minOf(currentFreq, nextFreq)
                val upper = maxOf(currentFreq, nextFreq)
                ranges.add(lower..upper to nextNote.first)
            }
        }

        // Find the matching range
        for ((range, noteName) in ranges) {
            if (pitch in range) {
                return noteName
            }
        }

        return "Unknown"
    }
}