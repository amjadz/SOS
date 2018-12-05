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
import android.preference.PreferenceManager
import android.content.SharedPreferences




class SMSFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val root = inflater.inflate( R.layout.fragment_sms, container, false)

        val textContact = root.findViewById<View>(R.id.text_contact)
        textContact?.setOnClickListener {
            sendText(requireContext())
        }

        val callContact = root.findViewById<View>(R.id.call_contact)
        callContact?.setOnClickListener {
            makeCall(requireContext())
        }


        return root

    }

    companion object {
        private val TAG = "SMSFragment"
        val SENT: String = "SMS_Sent"
        val DELIVERED: String = "SMS_DELIVERED"

        fun makeCall(ctx: Context) {

            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)
            val intent = Intent(Intent.ACTION_CALL)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_FROM_BACKGROUND)

            val callPreferences = prefs.getString("call_preference", "1")
            if (callPreferences == "1") {
                intent.setData(Uri.parse("tel:911"))
            } else {
                intent.setData(Uri.parse("tel:${prefs.getString("contact_text_1", "911")}"))
            }
            ctx.startActivity(intent)
        }

        fun sendText(ctx: Context) {
            val sentPI = PendingIntent.getBroadcast(ctx, 0, Intent(SENT), 0)
            val delivered = PendingIntent.getBroadcast(ctx,0, Intent(DELIVERED), 0)

            val prefs = PreferenceManager.getDefaultSharedPreferences(ctx)

            val sms = SmsManager.getDefault()
            val textPreferences = prefs.getString("text_preference", "1")
            val primaryContact = prefs.getString("contact_text_1", "911")
            val msgContent = prefs.getString("text_message_content", "I need help!")
            sms.sendTextMessage(primaryContact,null, msgContent, sentPI, delivered)
            if (textPreferences == "2") {
                for (i in 2..3) {
                    val contactNumber = prefs.getString("contact_text_$i", " ")
                    if (contactNumber != " ") {
                        sms.sendTextMessage(contactNumber, null, msgContent, sentPI, delivered)
                    }
                }
            }
        }
    }


}