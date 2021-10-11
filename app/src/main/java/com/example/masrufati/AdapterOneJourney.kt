package com.example.masrufati

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import javax.security.auth.login.LoginException

class AdapterOneJourney(
    private var listOfJourney: MutableList<Journey>,
    private val mContext: Context
) : RecyclerView.Adapter<AdapterOneJourney.VH>() {
    var shifts =
        MutableList(listOfJourney.size) { 0 }          // because when delete one the position don't change -> (index out of bounds exception)
    // when delete one the items after it shift to up (position-1)

    private lateinit var listener: AdapterListener

    interface AdapterListener {
        fun onClickOnJourney(journey: Journey)
        fun onDeleteJourney(journeyIdx: Int, journey: Journey,view:View)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (recyclerView.context is AdapterListener) listener =
            recyclerView.context as AdapterListener
        else throw Exception("you not implements from AdapterListener")
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.one_journey_layout, parent, false)
        return VH(view)
    }

    override fun getItemCount(): Int {
        return listOfJourney.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val view = holder.itemView
        view.visibility =
            View.VISIBLE      // because when delete one the view go to Gone after animation

        view.setOnClickListener {
            listener.onClickOnJourney(listOfJourney[position - shifts[position]])
        }

        view.setOnLongClickListener {
            //listener.onDeleteJourney(position - shifts[position], listOfJourney[position - shifts[position]],view)
            true
        }

        view.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim))
        val date = view.findViewById<TextView>(R.id.OneJourneyLayout_TextView_Date)
        val from = view.findViewById<TextView>(R.id.OneJourneyLayout_TextView_From)
        val to = view.findViewById<TextView>(R.id.OneJourneyLayout_TextView_To)
        val totalCost = view.findViewById<TextView>(R.id.OneJourneyLayout_TextView_TotalCost)
        val bus = view.findViewById<ImageView>(R.id.OneJourneyLayout_ImageView_Bus_True)
        val minibus = view.findViewById<ImageView>(R.id.OneJourneyLayout_ImageView_MiniBus_True)
        val taxi = view.findViewById<ImageView>(R.id.OneJourneyLayout_ImageView_Taxi_True)
        val boat = view.findViewById<ImageView>(R.id.OneJourneyLayout_ImageView_Boat_true)
        val tuktuk = view.findViewById<ImageView>(R.id.OneJourneyLayout_ImageView_Tuktuk_True)

        date.text = listOfJourney[position].startDate()
        from.text = listOfJourney[position].initialAddress()

        var totalCostV = 0f     // to calc total cost

        val setOfTransportation =
            mutableSetOf<TransportationName>()        // to know what transportation taken

        for (i in listOfJourney[position].transportation()) {
            setOfTransportation.add(i.transportationName())
            totalCostV += i.cost()
        }

        totalCost.text = "$totalCostV EGP"

        invisible(bus, minibus, taxi, boat, tuktuk)     // to make sure that all true deleted

        for (i in setOfTransportation) {
            when (i) {
                TransportationName.TAXI -> taxi.visibility = View.VISIBLE
                TransportationName.BUS -> bus.visibility = View.VISIBLE
                TransportationName.TOKTOK -> tuktuk.visibility = View.VISIBLE
                TransportationName.MINIBUS -> minibus.visibility = View.VISIBLE
                TransportationName.FERRYBOAT -> boat.visibility = View.VISIBLE
            }
        }

        to.text = Bank.getAddress(listOfJourney[position])
    }

    fun newJourneyIsAdded() {
        notifyItemInserted(listOfJourney.size - 1)
    }

    fun changeList(list: MutableList<Journey>) {
        this.listOfJourney = list
        notifyDataSetChanged()
    }

    fun isDeleted(position: Int){
        if(position-shifts[position] == listOfJourney.size)     // because the item is delete (size-1)
        {
            for(i in listOfJourney.size until shifts.size){
                shifts[i]=0
            }
        }
        else{
            for (i in position + 1 until shifts.size) {
                shifts[i]++
            }
        }

    }

    private fun invisible(vararg icons: ImageView) {
        for (i in icons)
            i.visibility = View.INVISIBLE
    }
}