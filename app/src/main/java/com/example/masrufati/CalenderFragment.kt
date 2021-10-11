package com.example.masrufati

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*


class CalenderFragment : DialogFragment() {

    lateinit var listener: CalenderListener

    interface CalenderListener {
        fun onDateSelected(date: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CalenderListener) listener = context
        else throw Exception("You not implementation from CalenderListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calender, container, false)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val done = view.findViewById<Button>(R.id.CalenderFragment_Button_Done)
        val calender = view.findViewById<CalendarView>(R.id.CalenderFragment_CalenderView)
        var lastDate = SimpleDateFormat("yyyy/MM/dd").format(Date())

        calender.setOnDateChangeListener { _, i, i2, i3 ->
            lastDate = "$i/${String.format("%02.0f", i2 + 1f)}/${String.format("%02.0f", i3 + 0f)}"
        }

        done.setOnClickListener {
            listener.onDateSelected(lastDate)
            dismiss()
        }
    }

}