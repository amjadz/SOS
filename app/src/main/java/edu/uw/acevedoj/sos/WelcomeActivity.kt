package edu.uw.acevedoj.sos

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.TextView

class WelcomeActivity : AppCompatActivity() {

    private  val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val button = findViewById<TextView>(R.id.welcome_next)
        checkForCallPermission()
        button.setOnClickListener {
            val intent = Intent(this, WelcomeSettingActivity::class.java)
            startActivity(intent)
        }
    }

    // Sees if this is the first time the user has started the app
    // if it isn't activity won't be shown.
    override fun onStart() {
        super.onStart()
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        if (!prefs.getBoolean("first_launch", true)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    // Sets the permissions at runtime
    private fun checkForCallPermission() {

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.CALL_PHONE)){

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CALL_PHONE, android.Manifest.permission.SEND_SMS, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )
            }

        }

    }
}
