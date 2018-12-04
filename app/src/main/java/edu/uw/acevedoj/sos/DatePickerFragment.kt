package edu.uw.acevedoj.sos

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView


class DatePickerFragment : Fragment() {




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_date_picker, container, false)
        val button = rootView.findViewById<TextView>(R.id.date_picker_button)
        val datePicker = rootView.findViewById<DatePicker>(R.id.date_picker)
        button.setOnClickListener {
            val args = Bundle().apply {
                putInt("year", datePicker.year)
                putInt("month",datePicker.month)
                putInt("day", datePicker.dayOfMonth)
            }
            val fragment = TimePickerFragment().apply {
                arguments = args
            }

            activity!!.supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        return rootView
    }



}
