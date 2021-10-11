package com.example.masrufati

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText


class NewJourneyFragment : DialogFragment() {

    private lateinit var listener: JourneyListener

    interface JourneyListener {
        fun onStartJourneyClick(from: String, bankName: String, city: String, address: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is JourneyListener) listener = context
        else throw Exception("You not implements from JourneyListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment_in
        return inflater.inflate(R.layout.new_journey, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val start = view.findViewById<Button>(R.id.NewJourney_Button_Start)
        val from = view.findViewById<TextInputEditText>(R.id.NewJourney_TextInputEditText_From)
        val bankName = view.findViewById<TextInputEditText>(R.id.NewJourney_TextInputEditText_BankName)
        val city = view.findViewById<TextInputEditText>(R.id.NewJourney_TextInputEditText_City)
        val address = view.findViewById<TextInputEditText>(R.id.NewJourney_TextInputEditText_Address)

        start.setOnClickListener {
            val fromT = from.text.toString()
            val bankNameT = bankName.text.toString()
            val cityT = city.text.toString()
            val addressT = address.text.toString()

            var valid = true
            if (fromT.isEmpty()) {
                from.error = "You must write the first place for your trip"
                valid = false
            }

            if (bankNameT.isEmpty()) {
                bankName.error = "You must write the name of the bank"
                valid = false
            }

            if (cityT.isEmpty()) {
                city.error = "You must write the city"
                valid = false
            }

            if (valid) {
                listener.onStartJourneyClick(fromT, bankNameT, cityT, addressT)
                dismiss()
            }
        }
    }
}