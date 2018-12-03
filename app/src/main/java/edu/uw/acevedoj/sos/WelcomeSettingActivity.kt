package edu.uw.acevedoj.sos

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.R.id.edit
import android.provider.MediaStore
import android.widget.EditText
import android.widget.RadioButton


class WelcomeSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome_setting)
    }

    fun onCompleteButtonClicked(view: View) {
        val primaryContact = findViewById<EditText>(R.id.contact_1)
        val secondaryContact = findViewById<EditText>(R.id.contact_2)
        val tertiaryContact = findViewById<EditText>(R.id.contact_3)
        val callPriority = findViewById<RadioButton>(R.id.radio_call_1)
        val textPriority = findViewById<RadioButton>(R.id.radio_text_1)

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putBoolean("first_launch", false)
        editor.putString("contact_text_1", "${primaryContact.text}")
        editor.putString("contact_text_2", "${secondaryContact.text}")
        editor.putString("contact_text_3","${tertiaryContact.text}")
        if (callPriority.isChecked) {
            editor.putString("call_preference", "1")
        } else {
            editor.putString("call_preference", "2")
        }
        if (textPriority.isChecked) {
            editor.putString("text_preference", "1")
        } else {
            editor.putString("text_preference", "2")
        }
        editor.commit();

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
