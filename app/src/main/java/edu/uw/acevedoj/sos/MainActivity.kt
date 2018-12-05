package edu.uw.acevedoj.sos

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 3
    private  val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForCallPermission()
        checkForSmsPermission()


        val navigation: BottomNavigationView = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(this)

        if (intent.getStringExtra("alarm_tab") == null) {
            loadFragment(DashboardFragment())

        } else {
            val mNavigationView = findViewById<BottomNavigationView>(R.id.navigation)
            val menuNav = mNavigationView.menu
            onNavigationItemSelected(menuNav.findItem(R.id.navigation_dashboard).setChecked(true))
        }

    }

    // Loads a given fragment passed as a parameter
    // and then loads it
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

    // Checks to see what navigation tab was clicked and
    // then loads the fragment for that tab item
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {

        var fragment:Fragment? = null

        when(p0.itemId) {
            R.id.navigation_home -> {
                fragment = DashboardFragment()

            }

            R.id.navigation_dashboard -> {
                fragment = AlarmFragment()

            }

            R.id.navigation_notifications -> {

                fragment = OtherFragment()

            }

        }
        return loadFragment(fragment)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkForSmsPermission(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_SEND_SMS
                )
            }
        }
    }

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