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
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.jar.Manifest

private val MY_PERMISSIONS_REQUEST_READ_SMS = 3
val SENT: String = "SMS_Sent"
val DELIVERED: String = "SMS_DELIVERED"
lateinit var smsSent: BroadcastReceiver
lateinit var smsDelivered: BroadcastReceiver

class  MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                text_contact.setOnClickListener {
                    val message = "I am in trouble, please help me"

                    val sms = SmsManager.getDefault()

                    sms.sendTextMessage("5554", null, message, null, null)

                }

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkForSmsReadPermission()
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    // My (Zubair) code from YAMA Assignment
    private fun checkForSmsReadPermission(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED){


        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_SMS),
                MY_PERMISSIONS_REQUEST_READ_SMS)
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