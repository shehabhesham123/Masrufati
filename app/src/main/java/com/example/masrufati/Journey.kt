package com.example.masrufati

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class Journey : Validation, Formatting {
    private var id: Int = -1
    private var startDate: String = ""
    private var endDate: String? = null
    private var initialAddress: String
    private var bank: Bank
    private var transportation: MutableList<Transportation> = mutableListOf()
    private var note: Note? = null

    @SuppressLint("SimpleDateFormat")
    private constructor(initialAddress: String?, bank: Bank?, accessDatabase: AccessDatabase) {
        this.bank = valid(bank)
        this.initialAddress = valid(initialAddress)
        this.startDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
        this.id = valid(accessDatabase.newJourney(this))
    }

    constructor(
        id: Int?,
        startDate: String,
        endDate: String?,
        initialAddress: String?,
        bank: Bank?,
        transportation: MutableList<Transportation>,
        note: Note?
    ) {
        this.id = valid(id)
        this.startDate = validDate(startDate)
        this.endDate = endDate
        this.initialAddress = valid(initialAddress)
        this.bank = valid(bank)
        this.transportation = transportation
        this.note = note
    }

    fun getNote(): Note? {
        return this.note
    }

    fun setNote(note: Note?) {
        this.note = note
    }

    fun newNote(note: Note?, accessDatabase: AccessDatabase): Boolean {
        val noteId = accessDatabase.newNote(this.id, valid(note))
        if (noteId != -1) {
            val newNote = valid(note)
            newNote.setId(noteId)
            this.note = newNote
            return true
        }
        return false
    }

    fun updateNote(note: Note?, accessDatabase: AccessDatabase): Boolean {
        val flag = accessDatabase.updateNote(valid(note))
        if (flag) {
            this.note = valid(note)
            return true
        }
        return false
    }

    fun id(): Int {
        return valid(this.id)       // if the user make new journey and it with no id and want to new the id ... the id will be -1 and it is invalid
    }

    fun startDate(): String {
        return validDate(this.startDate)
    }

    fun endDate(): String? {
        return this.endDate
    }

    fun initialAddress(): String {
        return valid(this.initialAddress)
    }

    fun updateInitialAddress(initialAddress: String,accessDatabase: AccessDatabase):Boolean{
        return try {
            val flag = accessDatabase.updateInitialAddress(this.id, valid(initialAddress))
            if(flag) this.initialAddress = valid(initialAddress)
            return flag
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun bank(): Bank {
        return valid(this.bank)
    }

    fun transportation(): MutableList<Transportation> {
        return this.transportation
    }

    fun transportation(listOfTransportation: MutableList<Transportation>) {
        this.transportation = listOfTransportation
    }

    fun endJourney(accessDatabase: AccessDatabase): Boolean {
        return try {
            val pair = accessDatabase.endJourney(valid(this.id))
            if (pair.first) endDate = pair.second
            pair.first
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun removeJourney(accessDatabase: AccessDatabase): Boolean {
        return try {
            accessDatabase.removeJourney(valid(this.id))
        } catch (e: NullPointerException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun addTransportation(transportation: Transportation, accessDatabase: AccessDatabase): Boolean {
        val id = accessDatabase.addTransportation(transportation, this.id)
        if (id != -1) {
            transportation.isAdded(id)
            this.transportation.add(transportation)
            return true
        }
        return false
    }

    fun updateBankName(newName: String, accessDatabase: AccessDatabase): Boolean {
        return this.bank.update(this.id, newName, accessDatabase)
    }

    fun updateBankCity(newCity: String, accessDatabase: AccessDatabase): Boolean {
        return this.bank.updateCity(this.id, newCity, accessDatabase)
    }

    fun updateBankAddress(newAddress: String?, accessDatabase: AccessDatabase): Boolean {
        return this.bank.updateAddress(this.id, newAddress, accessDatabase)
    }

    override fun valid(bank: Bank?): Bank {
        if (bank != null) return bank
        throw NullPointerException("invalid bank info")
    }

    override fun valid(initialAddress: String?): String {
        if (!(initialAddress.isNullOrBlank() || initialAddress.isNullOrEmpty())) return sentence(
            initialAddress
        )
        throw NullPointerException("invalid initial address")
    }

    override fun valid(id: Int?): Int {
        if (id != null && id > 0) return id
        throw NullPointerException("invalid ID")
    }

    override fun valid(note: Note?): Note {
        if (note != null) return note
        throw NullPointerException("invalid Note")
    }

    companion object {
        fun newJourney(from: String, bank: Bank, accessDatabase: AccessDatabase): Journey {
            return Journey(from, bank, accessDatabase)
        }
    }

    override fun validDate(date: String?): String {
        if (!(date.isNullOrEmpty() || date.isNullOrBlank()) && date.length == 10) return date
        throw NullPointerException("invalid date")
    }

    override fun sentence(sentence: String): String {
        var newString = ""
        for (i in sentence) {
            if (i == ' ' && newString.isNotEmpty() && newString[newString.length - 1] != ' ') newString += i
            else if (i != ' ') newString += i
        }
        return newString
    }
}