package ph.gcash.marites

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import ph.gcash.marites.databinding.ActivityMain2Binding
import java.util.*

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMain2Binding
    private lateinit var tts: TextToSpeech
    private lateinit var b1: Button
    private lateinit var e1: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMain2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        b1 = binding.btnSpeak
        e1 = binding.editText

        b1.setOnClickListener{ speakOut(e1) }

    }

    private fun speakOut(e1: EditText) {
        textToSpeechEngine.speak(
            e1.text.toString(),
            TextToSpeech.QUEUE_FLUSH,
            null,
            "tts1"
        )
    }


    private val textToSpeechEngine: TextToSpeech by lazy {
        // Pass in context and the listener.
        TextToSpeech(this,
            TextToSpeech.OnInitListener { status ->
                // set our locale only if init was success.
                if (status == TextToSpeech.SUCCESS) {
                    textToSpeechEngine.language = Locale.CANADA
                }
            })
    }


}