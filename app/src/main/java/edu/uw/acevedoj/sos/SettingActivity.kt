package edu.uw.acevedoj.sos

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceFragment

class SettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        //action bar "back"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        //the FM is the guy who moves fragments around
        fragmentManager.beginTransaction()
            .replace(android.R.id.content, SettingsFragment())
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragment() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            addPreferencesFromResource(R.xml.preferences)

        }


    }

}
