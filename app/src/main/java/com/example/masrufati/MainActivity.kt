package com.example.masrufati

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), CalenderFragment.CalenderListener,
    NewJourneyFragment.JourneyListener, BottomSheet.BottomSheetListener,
    AdapterOneJourney.AdapterListener, SelectTransportation.TransportationListener,
    NoteFragment.NoteListener, TransportationAdapter.TransportationListener {
    private lateinit var accessDatabase: AccessDatabase
    private var isStartDateClicked = 0          // 1 to startDate    2 to endDate
    private var allJourney: MutableList<Journey> = mutableListOf()

    //private var allJourney2: MutableList<Journey> = mutableListOf()
    private var totalCost = 0f
    private var numOfJourney: Int = 0
    private lateinit var lastJourney: Journey
    private lateinit var adapter: AdapterOneJourney
    private lateinit var bottomSheet: BottomSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessDatabase = AccessDatabase.newInstance(baseContext)

        getAllJourney()

        setAdapter()

        showJourneys()

        getLastJourney()

        defaultDate()

        MainActivity_TextView_StartDate.setOnClickListener {
            try {
                isStartDateClicked = 1
                showCalenderFragment()
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }

        }

        MainActivity_TextView_EndDate.setOnClickListener {
            try {
                isStartDateClicked = 2
                showCalenderFragment()
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        MainActivity_ImageView_NewJourney.setOnClickListener {
            try {
                val new = NewJourneyFragment()
                new.show(supportFragmentManager, null)
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        MainActivity_ProgressBar_ThereIsJourney.setOnClickListener {
            try {
                showBottomSheet(lastJourney)
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        MainActivity_ImageView_Search.setOnClickListener {
            try {
                searchOrNot(true)
                getAllJourneyBetweenTwoDates(
                    MainActivity_TextView_StartDate.text.toString(),
                    MainActivity_TextView_EndDate.text.toString()
                )
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        MainActivity_ImageView_close.setOnClickListener {
            getAllJourney()
            showJourneys(allJourney)
        }
    }

    private fun setAdapter() {
        try {
            adapter = AdapterOneJourney(allJourney, baseContext)
            MainActivity_RecyclerView_AllJourney.setHasFixedSize(true)
            MainActivity_RecyclerView_AllJourney.layoutManager = LinearLayoutManager(baseContext)
            MainActivity_RecyclerView_AllJourney.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showBottomSheet(journey: Journey) {
        bottomSheet = BottomSheet.newInstance(journey)
        bottomSheet.show(supportFragmentManager, null)
    }

    private fun showCalenderFragment() {
        val dialog = CalenderFragment()
        dialog.show(supportFragmentManager, null)
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun getAllJourney() {
        try {
            searchOrNot(false)
            allJourney = accessDatabase.getAllJourney()
        } catch (e: NullPointerException) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchOrNot(search: Boolean) {
        if (search) {
            MainActivity_ImageView_close.visibility = View.VISIBLE
            MainActivity_ImageView_Search.visibility = View.GONE
        } else {
            MainActivity_ImageView_close.visibility = View.GONE
            MainActivity_ImageView_Search.visibility = View.VISIBLE
        }
    }

    private fun thereIsJourney(isFound: Boolean) {
        if (isFound) {
            MainActivity_LinearLayout_NoJourney.visibility = View.GONE
            MainActivity_RecyclerView_AllJourney.visibility = View.VISIBLE
        } else {
            MainActivity_LinearLayout_NoJourney.visibility = View.VISIBLE
            MainActivity_RecyclerView_AllJourney.visibility = View.GONE
        }
    }

    private fun showJourneys(list: MutableList<Journey>) {
        allJourney = list
        adapter.changeList(allJourney)
        numOfJourney = allJourney.size
        totalCost = 0f
        for (i in allJourney) {
            for (j in i.transportation())
                totalCost += j.cost()
        }
        if (allJourney.size == 0) {
            thereIsJourney(false)
        } else {
            thereIsJourney(true)
        }
        allCost()
        numOfJourney()
    }

    private fun showJourneys() {
        numOfJourney = allJourney.size
        totalCost = 0f
        for (i in allJourney) {
            for (j in i.transportation())
                totalCost += j.cost()
        }
        if (allJourney.size == 0) {
            thereIsJourney(false)
        } else {
            thereIsJourney(true)
        }
        allCost()
        numOfJourney()
    }


    @SuppressLint("SetTextI18n")
    private fun allCost() {
        MainActivity_TextView_TotalCost.text = "$totalCost EGP"
    }

    @SuppressLint("SetTextI18n")
    private fun numOfJourney() {
        MainActivity_TextView_NumOfJourney.text = "$numOfJourney J"
    }

    private fun getLastJourney() {
        try {
            lastJourney =
                accessDatabase.getLastJourney()           // throw Exception if there no last journey
            thereIsLastJourney(true)
        } catch (e: Exception) {       // there is not last journey
            thereIsLastJourney(false)
        }
    }

    private fun thereIsLastJourney(isFound: Boolean) {
        if (isFound) {
            MainActivity_ImageView_NewJourney.visibility = View.GONE
            MainActivity_ProgressBar_ThereIsJourney.visibility = View.VISIBLE
        } else {
            MainActivity_ImageView_NewJourney.visibility = View.VISIBLE
            MainActivity_ProgressBar_ThereIsJourney.visibility = View.GONE
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun defaultDate() {
        val currentDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
        MainActivity_TextView_StartDate.text = currentDate
        MainActivity_TextView_EndDate.text = currentDate
    }

    override fun onDateSelected(date: String) {
        when (isStartDateClicked) {
            1 -> MainActivity_TextView_StartDate.text = date
            2 -> MainActivity_TextView_EndDate.text = date
            else -> {
                Toast.makeText(baseContext, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStartJourneyClick(
        from: String,
        bankName: String,
        city: String,
        address: String
    ) {
        try {
            val journey =
                Journey.newJourney(from, Bank(bankName, Location(city, address)), accessDatabase)
            lastJourney = journey
            thereIsLastJourney(true)
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
        //getLastJourney()
    }

    override fun onEndJourneyClick(journey: Journey) {
        if (journey.transportation().size == 0) {
            Toast.makeText(
                baseContext,
                "You didn't any transportation to end journey",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            try {
                if (MainActivity_ImageView_close.visibility == View.VISIBLE) {
                    searchOrNot(false)
                    getAllJourney()
                    showJourneys(allJourney)
                }
                bottomSheet.dismiss()
                journey.endJourney(accessDatabase)
                getLastJourney()
                numOfJourney++
                numOfJourney()
                totalCost += getAllCostForOneJourney(journey.transportation())
                allCost()
                allJourney.add(journey)
                thereIsJourney(true)
                adapter.newJourneyIsAdded()
                adapter.shifts.add(0)
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAllCostForOneJourney(listOfTransportation: List<Transportation>): Float {
        var total = 0f
        for (i in listOfTransportation) {
            total += i.cost()
        }
        return total
    }

    override fun onCancelJourneyClick(journey: Journey) {
        try {
            journey.removeJourney(accessDatabase)
            //getAllJourney()
            getLastJourney()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onReturnJourneyClick(journey: Journey) {
        try {
            accessDatabase.getLastJourney()
            throw InterruptedException("You must finish the running journey")
        } catch (e: NullPointerException) {
            lastJourney =
                Journey.newJourney(journey.initialAddress(), journey.bank(), accessDatabase)
            thereIsLastJourney(true)
        } catch (e: InterruptedException) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEdit(journey: Journey, bank: Bank, initialAddress:String) {
        var valid = true
        valid = valid && journey.updateBankName(bank.name(), accessDatabase)
        valid = valid && journey.updateBankCity(bank.location().city(), accessDatabase)
        valid = valid && journey.updateBankAddress(bank.location().address(), accessDatabase)

        valid = valid && journey.updateInitialAddress(initialAddress,accessDatabase)
        if (valid) {
            var b = true
            for (i in 0 until allJourney.size) {
                if (allJourney[i].id() == journey.id()) {
                    allJourney[i] = journey
                    adapter.notifyItemChanged(i)
                    b = false
                    break
                }
            }
            if(b && lastJourney.id() == journey.id()){
                lastJourney = journey
            }
            bottomSheet.bankEdit(journey)
        }
        else{
            Toast.makeText(baseContext,"Error",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClickOnJourney(journey: Journey) {
        try {
            showBottomSheet(journey)
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDeleteJourney(journeyIdx: Int, journey: Journey, view: View) {
        val dialog = ConfirmDelete {
            if (journey.removeJourney(accessDatabase)) {
                val anim = AnimationUtils.loadAnimation(baseContext, R.anim.delete)
                anim.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationRepeat(p0: Animation?) {}
                    override fun onAnimationStart(p0: Animation?) {}
                    override fun onAnimationEnd(p0: Animation?) {
                        view.visibility = View.GONE
                        allJourney.removeAt(journeyIdx)
                        adapter.notifyItemRemoved(journeyIdx)
                        showJourneys()
                    }
                })
                view.startAnimation(anim)
                adapter.isDeleted(journeyIdx)
                Snackbar.make(
                    MainActivity_ImageView_Search,
                    "Journey deleted successfully",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    MainActivity_ImageView_Search,
                    "Maybe an error occurred while deleting the journey",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        dialog.show(supportFragmentManager, null)
    }

    private fun getAllJourneyBetweenTwoDates(startDate: String, endDate: String) {
        try {
            val list = accessDatabase.getAllJourney(startDate, endDate)
            showJourneys(list)
        } catch (e: NullPointerException) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSelectTransportation(journey: Journey, transportation: Transportation) {
        try {
            journey.addTransportation(transportation, accessDatabase)
            bottomSheet.addTransportation(transportation)
            lastJourney.transportation().add(transportation)
        } catch (e: NullPointerException) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onEditJourneyAndAddTransportation(
        journey: Journey,
        transportation: Transportation
    ) {
        try {
            journey.addTransportation(transportation, accessDatabase)
            bottomSheet.addTransportation(transportation)
            for(i in 0 until allJourney.size){
                if(allJourney[i].id() == journey.id()){
                    allJourney[i].transportation().add(transportation)
                    adapter.notifyItemChanged(i)
                    break
                }
            }
            showJourneys()
        } catch (e: NullPointerException) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onNoteDoneClick(note: Note, journey: Journey) {
        try {
            note.getId()        // if id == -1  throw Exception that mean that make new note else mean that update this note
            journey.updateNote(note, accessDatabase).toString()
        } catch (e: Exception) {
            journey.newNote(note, accessDatabase).toString()
        }
        try {
            bottomSheet.addNote(journey.getNote()!!)
        } catch (e: Exception) {
            Toast.makeText(baseContext, e.message, Toast.LENGTH_SHORT).show()
        }
        for (i in 0 until allJourney.size) {
            if (allJourney[i].id() == journey.id()) {
                allJourney[i] = journey
                break
            }
        }
        try {
            if (lastJourney.id() == journey.id()) lastJourney = journey
        } catch (e: Exception) {
        }

    }

    override fun onDeleteTransportation(transportation: Transportation) {
        val journey = bottomSheet.getJourney()
        val transportations = journey.transportation()
        if(transportations.size == 1){
            Toast.makeText(baseContext,"This journey will be with no transportation",Toast.LENGTH_SHORT).show()
        }
        else {
            for (i in transportations) {
                if (i.id() == transportation.id()) {
                    i.remove(accessDatabase)
                    bottomSheet.deleteTransportation(transportation)
                    break
                }
            }
            for(i in 0 until allJourney.size){
                if(allJourney[i].id() == journey.id()){
                    for (j in allJourney[i].transportation()) {
                        if (j.id() == transportation.id()) {
                            allJourney[i].transportation().remove(j)
                            adapter.notifyItemChanged(i)
                            break
                        }
                    }
                }
            }
        }
    }
}