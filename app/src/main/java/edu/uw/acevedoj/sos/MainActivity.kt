package edu.uw.acevedoj.sos

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.BottomNavigationView

import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


class  MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val navigation: BottomNavigationView = findViewById(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(this)
    }

    private fun loadFragment(fragment: Fragment?): Boolean {
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container,fragment)
                .commit()
            return true

        }
        return false

    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkForSmsPermission()
        sendSOSSMS()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun sendSOSSMS(){
        val sentPI = PendingIntent.getBroadcast(this, 0, Intent(SENT), 0)
        val delivered = PendingIntent.getBroadcast(this,0, Intent(DELIVERED), 0)

        text_contact.setOnClickListener {
            checkForSmsPermission()

            val sms = SmsManager.getDefault()

            sms.sendTextMessage("5554",null, "I need help", sentPI, delivered)

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

    override fun onResume() {
        super.onResume()

        smsSent = object: BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                when(resultCode){

                    Activity.RESULT_OK ->
                        Toast.makeText(context, "SMS sent!", Toast.LENGTH_SHORT).show()

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                        Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show()

                    SmsManager.RESULT_ERROR_NO_SERVICE ->
                        Toast.makeText(context, "No Service!", Toast.LENGTH_SHORT).show()

                }
            }
        }

        smsDelivered = object: BroadcastReceiver(){
            override fun onReceive(context: Context, intent: Intent) {
                when(resultCode){

                    Activity.RESULT_OK ->
                        Toast.makeText(context, "SMS Delivered!", Toast.LENGTH_SHORT).show()

                    SmsManager.RESULT_ERROR_GENERIC_FAILURE ->
                        Toast.makeText(context, "SMS Not Delivered!", Toast.LENGTH_SHORT).show()

                }

            }

        }

        registerReceiver(smsSent, IntentFilter(SENT))
        registerReceiver(smsDelivered, IntentFilter(DELIVERED))

    }

    override  fun onPause() {
        super.onPause()

        unregisterReceiver(smsSent)
        unregisterReceiver(smsDelivered)
    }


}