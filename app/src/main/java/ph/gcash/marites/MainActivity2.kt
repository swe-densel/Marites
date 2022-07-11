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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        var b1 = findViewById<Button>(R.id.btn_Speak)
        var e1 = findViewById<EditText>(R.id.edit_text)

        b1.setOnClickListener{
            tts = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
                if(it==TextToSpeech.SUCCESS) {

                    tts.language = Locale.US
                    tts.setSpeechRate(1.0f)
                    tts.speak(e1.text.toString(), TextToSpeech.QUEUE_ADD, null)
                }

                 })
             }

        }



}