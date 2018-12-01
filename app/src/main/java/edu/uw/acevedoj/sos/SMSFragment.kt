package edu.uw.acevedoj.sos

import android.app.Activity
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.AppComponentFactory
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_sms.*


class SMSFragment: Fragment() {

    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 3
    val SENT: String = "SMS_Sent"
    val DELIVERED: String = "SMS_DELIVERED"
    lateinit var smsSent: BroadcastReceiver
    lateinit var smsDelivered: BroadcastReceiver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        sendSOSSMS()
        sendPhoneCall()

        return inflater.inflate(R.layout.fragment_sms, null)

    }

    private fun sendSOSSMS(){
        val sentPI = PendingIntent.getBroadcast(requireContext(), 0, Intent(SENT), 0)
        val delivered = PendingIntent.getBroadcast(requireContext(),0, Intent(DELIVERED), 0)
        checkForSmsPermission()

        text_contact.setOnClickListener {
            val sms = SmsManager.getDefault()

            sms.sendTextMessage("5554",null, "I need help", sentPI, delivered)

        }

    }

    private fun sendPhoneCall() {
        val intent = Intent(Intent.ACTION_CALL)

        checkForCallPermission()

        call_contact.setOnClickListener {

            intent.setData(Uri.parse("tel:0377778888"))
            startActivity(intent)

        }

    }

    private fun checkForSmsPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.SEND_SMS)) {

            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.SEND_SMS),
                    MY_PERMISSIONS_REQUEST_SEND_SMS
                )
            }
        }
    }

    private fun checkForCallPermission() {

        if(ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
            return

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
//
//        registerReceiver(smsSent, IntentFilter(SENT))
//        registerReceiver(smsDelivered, IntentFilter(DELIVERED))

    }

    override  fun onPause() {
        super.onPause()

/*        unregisterReceiver(smsSent)
        unregisterReceiver(smsDelivered)*/
    }


}