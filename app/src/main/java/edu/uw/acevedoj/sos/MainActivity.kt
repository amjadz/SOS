package edu.uw.acevedoj.sos

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

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        var fragment:Fragment? = null

        when(p0.itemId){
            R.id.navigation_home -> {
                fragment = SMSFragment()

            }

            R.id.navigation_notifications -> {
                fragment = DashboardFragment()

            }

            R.id.navigation_dashboard -> {
                fragment = OtherFragment()

            }

        }


        return loadFragment(fragment)
    }

}