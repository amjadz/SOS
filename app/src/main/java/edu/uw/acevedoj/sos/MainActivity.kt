package edu.uw.acevedoj.sos

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
<<<<<<< Updated upstream
import android.support.v7.app.AppCompatActivity
=======
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.Toast
>>>>>>> Stashed changes
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*

<<<<<<< Updated upstream
class MainActivity : AppCompatActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                message.setText(R.string.title_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                message.setText(R.string.title_dashboard)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                message.setText(R.string.title_notifications)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
=======
class  MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

>>>>>>> Stashed changes

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
<<<<<<< Updated upstream

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }
=======


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



>>>>>>> Stashed changes
}