package com.example.masrufati

class Transportation : Validation {
    private var id: Int = -1
    private var name: TransportationName
    private var cost: Float

    constructor(id: Int?, name: TransportationName?, cost: Float?) {        // use when get info from database
        this.id = valid(id)     // check if id is valid or not
        this.name = valid(name)      // check if name is valid or not
        this.cost = valid(cost)     // check if cost is valid or not
    }

    constructor(name: TransportationName?, cost: Float?) {           // use when add new transportation
        this.name = valid(name)     // check if name is valid or not
        this.cost = valid(cost)     // check if cost is valid or not
    }

    fun id(): Int {
        return valid(this.id)       // check if id is -1 or not
    }

    fun transportationName(): TransportationName {
        return this.name
    }

    fun cost(): Float {
        return valid(this.cost)
    }

    fun remove(accessDatabase: AccessDatabase): Boolean {        // to remove transportation
        return try {
            accessDatabase.removeTransportation(valid(this.id))
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun isAdded(id:Int){
        this.id = id
    }

    fun update(newName: TransportationName?, accessDatabase: AccessDatabase): Boolean {      // to update name of transportation
        return try {
            val flag = accessDatabase.updateTransportationName(valid(newName), this.id)       // check if name is null or not and if not null return it
            if(flag) this.name = valid(newName)
            return flag
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun update(newCost: Float?, accessDatabase: AccessDatabase): Boolean {        // to update cost of transportation
        return try {
            val flag = accessDatabase.updateTransportationCost(valid(newCost), this.id)      // check if cost is valid or not and if valid return it
            if(flag) this.cost = valid(newCost)
            return flag
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    override fun valid(id: Int?): Int {         // check if id is not null and greater than 0 else throw NullPointerException
        if (id != null && id > 0) return id
        throw NullPointerException("invalid ID")
    }

    override fun valid(cost: Float?): Float {           // check if cost is not null and greater than 0 else throw NullPointerException
        if (cost != null && cost > 0.0) return cost
        throw NullPointerException("invalid cost")
    }

    override fun valid(transportationName: TransportationName?): TransportationName {          // check if transportationName is not null else throw NullPointerException
        if (transportationName != null) return transportationName
        throw NullPointerException("invalid transportationName")
    }

    companion object{
        fun convertTransportationNameToNum(transportationName: TransportationName): Int {
            return when (transportationName) {
                TransportationName.TAXI -> 1
                TransportationName.BUS -> 2
                TransportationName.MINIBUS -> 3
                TransportationName.TOKTOK -> 4
                TransportationName.FERRYBOAT -> 5
            }
        }

        fun convertNumToTransportationName(transportationName: Int): TransportationName {
            return when (transportationName) {
                1 -> TransportationName.TAXI
                2 -> TransportationName.BUS
                3 -> TransportationName.MINIBUS
                4 -> TransportationName.TOKTOK
                5 -> TransportationName.FERRYBOAT
                else -> throw Exception("invalid transportation name")
            }
        }
    }
}

enum class TransportationName {
    TAXI,
    BUS,
    MINIBUS,
    TOKTOK,
    FERRYBOAT
}