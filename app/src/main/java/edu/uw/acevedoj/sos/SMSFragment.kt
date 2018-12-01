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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_sms.*


class SMSFragment: Fragment() {

    private val MY_PERMISSIONS_REQUEST_SEND_SMS = 3
    private  val MY_PERMISSIONS_REQUEST_CALL_PHONE = 4

    val SENT: String = "SMS_Sent"
    val DELIVERED: String = "SMS_DELIVERED"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate( R.layout.fragment_sms, container, false)

        val sentPI = PendingIntent.getBroadcast(requireContext(), 0, Intent(SENT), 0)
        val delivered = PendingIntent.getBroadcast(requireContext(),0, Intent(DELIVERED), 0)


        checkForSmsPermission()
        val textContact = root.findViewById<View>(R.id.text_contact)
        textContact?.setOnClickListener {
            val sms = SmsManager.getDefault()

            sms.sendTextMessage("5554",null, "I need help", sentPI, delivered)

        }

        val intent = Intent(Intent.ACTION_CALL)

        checkForCallPermission()
        val callContact = root.findViewById<View>(R.id.call_contact)
        callContact?.setOnClickListener {

            intent.setData(Uri.parse("tel:0377778888"))
            startActivity(intent)

        }


        return root

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

        if(ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this.requireActivity(), android.Manifest.permission.CALL_PHONE)){


            } else {
                ActivityCompat.requestPermissions(
                    this.requireActivity(),
                    arrayOf(android.Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )
            }

        }

    }

}