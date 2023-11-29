package uk.ac.tees.mad.q2252114

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.*

class SplasScreen : AppCompatActivity() {

//    private lateinit var logo: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

//        logo = findViewById(R.id.logo)
//
//        // Start the animation on the logo
//        val animation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation)
//        logo.startAnimation(animation)

        // Start a timer to finish the splash screen activity after 3 seconds
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                startActivity(Intent(this@SplasScreen, MainActivity::class.java))
                finish()
            }
        }, 3000)
    }
}