package edu.uw.acevedoj.sos

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

private val MY_PERMISSIONS_REQUEST_READ_SMS = 3
val SENT: String = "SMS_Sent"
val DELIVERED: String = "SMS_DELIVERED"
lateinit var smsSent: BroadcastReceiver
lateinit var smsDelivered: BroadcastReceiver

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation: BottomNavigationView = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(this)
    }

    private fun loadFragment(fragment: Fragment?): Boolean{

        if(fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()

            return true
        }

        return  false

    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        var fragment:Fragment? = null

        when(p0.itemId) {
            R.id.navigation_home -> {
                fragment = SMSFragment()

            }

            R.id.navigation_dashboard -> {
                fragment = OtherFragment()

            }

            R.id.navigation_notifications -> {
                fragment = DashboardFragment()


            }


        }
        return loadFragment(fragment)
    }


}