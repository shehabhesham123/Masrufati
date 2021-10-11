package com.example.masrufati

import android.util.Log
import kotlinx.android.synthetic.main.bottom_sheet_layout.*

class Bank(name: String?, location: Location?) : Formatting, Validation {

    private var name: String
    private var location: Location

    init {
        this.name = valid(name)
        this.location = valid(location)
    }

    fun location(): Location {                          // to get location name
        return this.location
    }

    fun name(): String {                              // to get bank name
        return this.name
    }

    fun update(journeyId: Int, newName: String?, accessDatabase: AccessDatabase): Boolean {                // to update bank name
        return try {
            val flag = accessDatabase.updateBankName(journeyId, valid(newName))
            if(flag) this.name = valid(newName)
            return flag
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun updateCity(journeyId: Int, city: String?, accessDatabase: AccessDatabase): Boolean {          // to update bank address
        return try {
            this.location.updateCity(journeyId, valid(city), accessDatabase)
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun updateAddress(
        journeyId: Int,
        address: String?,
        accessDatabase: AccessDatabase
    ): Boolean {          // to update bank address
        return try {
            this.location.updateAddress(journeyId, address, accessDatabase)
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    override fun valid(name: String?): String {         // to check value of name In terms of is it null or not
        if (!(name.isNullOrBlank() || name.isNullOrEmpty())) return sentence(name)
        throw NullPointerException("invalid name")
    }

    override fun valid(location: Location?): Location {            // to check value of location In terms of is it null or not
        if (location != null) return location
        throw NullPointerException("invalid location")
    }

    override fun sentence(sentence: String): String {           // to formatting sentence
        var newString = ""
        for (i in sentence) {
            if (i == ' ' && newString.isNotEmpty() && newString[newString.length - 1] != ' ') newString += i
            else if (i != ' ') newString += i
        }
        return newString
    }

    companion object{
        fun getAddress(journey: Journey):String{
            var to = journey.bank().location().city() +' '
            if (!(journey.bank().location().address().isNullOrBlank() || journey.bank().location().address().isNullOrEmpty())) to += "${journey.bank().location().address()} \n "
            to+=journey.bank().name()
            return to
        }
    }
}