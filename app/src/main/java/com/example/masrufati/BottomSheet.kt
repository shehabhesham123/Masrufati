package com.example.masrufati

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.masrufati.Note.Companion.toInt
import com.example.masrufati.Transportation.Companion.convertNumToTransportationName
import com.example.masrufati.Transportation.Companion.convertTransportationNameToNum
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*

private var size = 0
private const val JOURNEY_ID = "id"
private const val JOURNEY_END_DATE = "end_date"
private const val JOURNEY_START_DATE = "start_date"
private const val JOURNEY_INITIAL_ADDRESS = "initial_address"
private const val JOURNEY_BANK_NAME = "bank_name"
private const val JOURNEY_CITY = "city"
private const val JOURNEY_ADDRESS = "address"
private const val JOURNEY_TRANSPORTATION_NAME = "transportation_name"
private const val JOURNEY_TRANSPORTATION_COST = "transportation_cost"
private const val JOURNEY_TRANSPORTATION_ID = "transportation_id"
private const val JOURNEY_NOTE_ID = "note_id"
private const val JOURNEY_NOTE_TITLE = "note_title"
private const val JOURNEY_NOTE_BODY = "note_body"
private const val JOURNEY_NOTE_BACKGROUND = "note_background"

class BottomSheet : BottomSheetDialogFragment() {

    private lateinit var journey: Journey
    private lateinit var listener: BottomSheetListener
    private lateinit var adapter: TransportationAdapter

    interface BottomSheetListener {
        fun onEndJourneyClick(journey: Journey)
        fun onCancelJourneyClick(journey: Journey)
        fun onReturnJourneyClick(journey: Journey)
        fun onEdit(journey: Journey, bank: Bank, initialAddress:String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomSheetListener) listener = context
        else throw Exception("you don't implements from BottomSheetListener")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            try {
                journey = toJourney(it)
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
        return inflater.inflate(R.layout.bottom_sheet_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (journey.endDate() == null) {        // mean that the user start journey and not end it
            BottomSheetLayout_TextView_Date.visibility = View.GONE
            BottomSheetLayout_ProgressBar_ThereIsJourney.visibility = View.VISIBLE
            BottomSheetLayout_Button_Return.visibility = View.GONE
            BottomSheetLayout_Button_End.visibility = View.VISIBLE
            BottomSheetLayout_Button_Cancel.visibility = View.VISIBLE
            BottomSheetLayout_Button_AddTransportation.visibility = View.VISIBLE
            BottomSheetLayout_ImageView_Add.visibility = View.GONE
        } else {
            BottomSheetLayout_TextView_Date.visibility = View.VISIBLE
            BottomSheetLayout_ProgressBar_ThereIsJourney.visibility = View.GONE
            BottomSheetLayout_Button_Return.visibility = View.VISIBLE
            BottomSheetLayout_Button_End.visibility = View.GONE
            BottomSheetLayout_Button_Cancel.visibility = View.GONE
            BottomSheetLayout_Button_AddTransportation.visibility = View.GONE
            BottomSheetLayout_ImageView_Add.visibility = View.VISIBLE
        }

        BottomSheetLayout_TextView_Date.text = journey.startDate()

        BottomSheetLayout_EditText_From.setText(journey.initialAddress())

        BottomSheetLayout_TextView_To.text = Bank.getAddress(journey)

        BottomSheetLayout_Button_End.setOnClickListener {
            listener.onEndJourneyClick(journey)
        }

        BottomSheetLayout_Button_Cancel.setOnClickListener {
            listener.onCancelJourneyClick(journey)
            dismiss()
        }

        BottomSheetLayout_Button_Return.setOnClickListener {
            listener.onReturnJourneyClick(journey)
            dismiss()
        }

        BottomSheetLayout_Button_AddTransportation.setOnClickListener {
            val dialog = SelectTransportation.newInstance(journey,false)
            dialog.show(fragmentManager!!, null)
        }

        BottomSheetLayout_ImageView_Add.setOnClickListener {
            val dialog = SelectTransportation.newInstance(journey,true)
            dialog.show(fragmentManager!!, null)
        }

        BottomSheetLayout_ImageView_Note.setOnClickListener {
            val note = NoteFragment.newInstance(journey.getNote(),journey)
            note.show(fragmentManager!!,null)
        }

        BottomSheetLayout_ImageView_Edit.setOnClickListener {
            BottomSheetLayout_EditText_BankName.setText(journey.bank().name())
            BottomSheetLayout_EditText_City.setText(journey.bank().location().city())
            BottomSheetLayout_EditText_Address.setText(journey.bank().location().address())
            BottomSheetLayout_EditText_From.isEnabled = true
            BottomSheetLayout_ConstraintLayout_EditTo.visibility = View.VISIBLE
            BottomSheetLayout_TextView_To.visibility = View.GONE
            BottomSheetLayout_ImageView_Done.visibility = View.VISIBLE
            BottomSheetLayout_ImageView_Edit.visibility = View.GONE
        }

        BottomSheetLayout_ImageView_Done.setOnClickListener {
            val city = BottomSheetLayout_EditText_City.text.toString()
            val bankName = BottomSheetLayout_EditText_BankName.text.toString()
            val address = BottomSheetLayout_EditText_Address.text.toString()
            val from = BottomSheetLayout_EditText_From.text.toString()
            BottomSheetLayout_EditText_From.isEnabled = false
            var valid = true
            if(city.isEmpty()) valid = false
            if(bankName.isEmpty()) valid = false
            if(from.isEmpty()) valid = false
            if(valid){
                listener.onEdit(journey, Bank(bankName, Location(city,address)),from)
            }else{
                Toast.makeText(context,"You must fill in all fields",Toast.LENGTH_SHORT).show()
            }
        }

        BottomSheetLayout_RecyclerView_AllTransportation.setHasFixedSize(true)
        BottomSheetLayout_RecyclerView_AllTransportation.layoutManager =
            GridLayoutManager(context, 3)
        adapter = TransportationAdapter(this.journey.transportation(),context!!)
        BottomSheetLayout_RecyclerView_AllTransportation.adapter = adapter
    }

    fun bankEdit(journey: Journey){
        this.journey = journey
        BottomSheetLayout_TextView_To.text = Bank.getAddress(journey)
        BottomSheetLayout_ConstraintLayout_EditTo.visibility = View.GONE
        BottomSheetLayout_TextView_To.visibility = View.VISIBLE
        BottomSheetLayout_ImageView_Done.visibility = View.GONE
        BottomSheetLayout_ImageView_Edit.visibility = View.VISIBLE
    }

    fun addTransportation(transportation: Transportation) {
        this.journey.transportation().add(transportation)
        this.adapter.notifyDataSetChanged()
    }

    fun addNote(note:Note){
        this.journey.setNote(note)
    }

    fun getJourney():Journey{
        return this.journey
    }

    fun deleteTransportation(transportation: Transportation){
        val trans = this.journey.transportation()
        for(i in 0 until trans.size) {
            if (trans[i].id() == transportation.id()) {
                trans.removeAt(i)
                break
            }
        }
        this.adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(journey: Journey): BottomSheet {
            val fragment = BottomSheet()
            try {
                fragment.arguments = toBundle(journey)
            } catch (e: NullPointerException) {
                Log.i("NullPointerException", "${e.message}")
            } catch (e: Exception) {
                Log.i("Exception", "${e.message}")
            }
            return fragment
        }

        fun toBundle(journey: Journey): Bundle {
            val bundle = Bundle()
            bundle.putInt(JOURNEY_ID, journey.id())
            bundle.putString(JOURNEY_END_DATE, journey.endDate())
            bundle.putString(JOURNEY_START_DATE, journey.startDate())
            bundle.putString(JOURNEY_INITIAL_ADDRESS, journey.initialAddress())
            bundle.putString(JOURNEY_BANK_NAME, journey.bank().name())
            bundle.putString(JOURNEY_CITY, journey.bank().location().city())
            bundle.putString(JOURNEY_ADDRESS, journey.bank().location().address())
            journey.getNote()?.let {
                bundle.putInt(JOURNEY_NOTE_ID, it.getId())
                bundle.putString(JOURNEY_NOTE_TITLE, it.getTitle())
                bundle.putString(JOURNEY_NOTE_BODY, it.getBody())
                bundle.putInt(JOURNEY_NOTE_BACKGROUND,toInt(it.getBackground()))
            }
            size = journey.transportation().size
            for ((i, j) in journey.transportation().withIndex()) {
                bundle.putInt("$JOURNEY_TRANSPORTATION_ID$i", j.id())
                bundle.putInt(
                    "$JOURNEY_TRANSPORTATION_NAME$i",
                    convertTransportationNameToNum(j.transportationName())
                )
                bundle.putFloat("$JOURNEY_TRANSPORTATION_COST$i", j.cost())
            }
            return bundle
        }

        fun toJourney(bundle: Bundle): Journey {
            val id = bundle.getInt(JOURNEY_ID)
            val endDate = bundle.getString(JOURNEY_END_DATE)
            val startDate = bundle.getString(JOURNEY_START_DATE)
            val initialAddress = bundle.getString(JOURNEY_INITIAL_ADDRESS)
            val bankName = bundle.getString(JOURNEY_BANK_NAME)
            val city = bundle.getString(JOURNEY_CITY)
            val address = bundle.getString(JOURNEY_ADDRESS)
            val list = mutableListOf<Transportation>()
            val noteId = bundle.getInt(JOURNEY_NOTE_ID, -1)
            val noteTitle = bundle.getString(JOURNEY_NOTE_TITLE)
            val noteBody = bundle.getString(JOURNEY_NOTE_BODY)
            val noteBackground = bundle.getInt(JOURNEY_NOTE_BACKGROUND)
            var note: Note? = null
            if (noteId != -1) note = Note(noteId, noteTitle, noteBody, noteBackground)
            for (i in 0 until size) {
                val transportationId = bundle.getInt("$JOURNEY_TRANSPORTATION_ID$i")
                val name =
                    convertNumToTransportationName(bundle.getInt("$JOURNEY_TRANSPORTATION_NAME$i"))
                val cost = bundle.getFloat("$JOURNEY_TRANSPORTATION_COST$i")
                list.add(Transportation(transportationId, name, cost))
            }
            try {
                return Journey(
                    id, startDate!!, endDate, initialAddress,
                    Bank(bankName, Location(city, address)), list, note
                )
            } catch (e: Exception) {
                throw Exception("null startDate")
            }
        }

    }
}

class TransportationAdapter(private val list: MutableList<Transportation>,private val mContext:Context) :
    RecyclerView.Adapter<TransportationAdapter.VH>() {

    private val images = arrayOf(
        R.drawable.ic_minibus,
        R.drawable.ic_tuk_tuk,
        R.drawable.ic_bus,
        R.drawable.ic_boat,
        R.drawable.ic_taxi
    )

    interface TransportationListener{
        fun onDeleteTransportation( transportation: Transportation)
    }
    private lateinit var listener:TransportationListener

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        if(mContext is TransportationListener) listener = mContext
        else{throw Exception("You don't implements from TransportationListener")}
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.show_tranportation, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val view = holder.itemView
        val image = view.findViewById<ImageView>(R.id.ShowTransportation_ImageView_Image)
        val name = view.findViewById<TextView>(R.id.ShowTransportation_TextView_Name)
        val cost = view.findViewById<TextView>(R.id.ShowTransportation_TextView_Cost)

        view.setOnLongClickListener {
            listener.onDeleteTransportation(list[position])
            true
        }

        var nameT = ""
        var imageT = 0

        when (list[position].transportationName()) {
            TransportationName.TAXI -> {
                nameT = "Taxi"
                imageT = images[4]
            }
            TransportationName.FERRYBOAT -> {
                nameT = "Boat"
                imageT = images[3]
            }
            TransportationName.BUS -> {
                nameT = "Bus"
                imageT = images[2]
            }
            TransportationName.TOKTOK -> {
                nameT = "TukTuk"
                imageT = images[1]
            }
            TransportationName.MINIBUS -> {
                nameT = "Minibus"
                imageT = images[0]
            }
        }
        image.setImageResource(imageT)
        name.text = nameT
        cost.text = "${list[position].cost()} EGP"
    }
}