package edu.uw.acevedoj.sos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val button = findViewById<TextView>(R.id.welcome_next)
        button.setOnClickListener {
            val intent = Intent(this, WelcomeSettingActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.getBoolean("first_launch", true)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
