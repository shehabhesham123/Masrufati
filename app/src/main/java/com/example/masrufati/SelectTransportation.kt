package com.example.masrufati

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText

class SelectTransportation : DialogFragment() {

    private lateinit var listener: TransportationListener
    private lateinit var journey: Journey
    private var isEdit = false

    interface TransportationListener {
        fun onSelectTransportation(journey: Journey, transportation: Transportation)
        fun onEditJourneyAndAddTransportation(journey: Journey, transportation: Transportation)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is TransportationListener) listener = context
        else throw Exception("you don't implements from TransportationListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            try {
                journey = BottomSheet.toJourney(it)
                isEdit = it.getBoolean("ISEDIT")
            } catch (e: NullPointerException) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.select_transportation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val done = view.findViewById<Button>(R.id.SelectTransportation_Button_Done)
        val spinner =
            view.findViewById<Spinner>(R.id.SelectTransportation_Spinner_SelectTransportation)
        val cost =
            view.findViewById<TextInputEditText>(R.id.SelectTransportation_TextInputEditText_Cost)
        spinner.adapter = SpinnerAdapter()

        done.setOnClickListener {
            val name = when (spinner.selectedItem) {
                0 -> TransportationName.MINIBUS
                1 -> TransportationName.TOKTOK
                2 -> TransportationName.BUS
                3 -> TransportationName.FERRYBOAT
                else -> TransportationName.TAXI
            }

            if (!(cost.text.toString().isNullOrEmpty() || cost.text.toString()
                    .isNullOrBlank()) && cost.text.toString().toFloat() != 0f
            ) {

                if (isEdit) listener.onEditJourneyAndAddTransportation(
                    journey,
                    Transportation(name, cost.text.toString().toFloat())
                )
                else listener.onSelectTransportation(
                    journey,
                    Transportation(name, cost.text.toString().toFloat())
                )
                dismiss()
            } else {
                cost.error = "You must enter the cost"
            }
        }

    }

    companion object {
        fun newInstance(journey: Journey, isEdit: Boolean): SelectTransportation {
            val fragment = SelectTransportation()
            try {
                val bundle = BottomSheet.toBundle(journey)
                bundle.putBoolean("ISEDIT", isEdit)
                fragment.arguments = bundle
            } catch (e: NullPointerException) {
                Log.i("NullPointerException", "${e.message}")
            } catch (e: Exception) {
                Log.i("Exception", "${e.message}")
            }
            return fragment
        }
    }

}

class SpinnerAdapter() : BaseAdapter() {

    private val transportation = arrayOf(
        R.drawable.ic_minibus,
        R.drawable.ic_tuk_tuk,
        R.drawable.ic_bus,
        R.drawable.ic_boat,
        R.drawable.ic_taxi
    )

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        var view = p1
        if (view == null)
            view =
                LayoutInflater.from(p2!!.context).inflate(R.layout.transportation_layout, p2, false)
        try {
            val image = view!!.findViewById<ImageView>(R.id.Transportation_ImageView_Image)
            val name = view.findViewById<TextView>(R.id.Transportation_TextView_Name)
            image.setImageResource(transportation[p0])
            val n = when (p0) {
                0 -> "Minibus"
                1 -> "Tuktuk"
                2 -> "Bus"
                3 -> "Boat"
                else -> "Taxi"
            }
            name.text = n

        } catch (e: Exception) {
            Log.i("asd", "")
            throw Exception("getView")
        }

        return view
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return transportation.size
    }

}