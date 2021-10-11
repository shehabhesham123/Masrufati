package com.example.masrufati

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.masrufati.Note.Companion.toColor
import com.example.masrufati.Transportation.Companion.convertNumToTransportationName
import com.example.masrufati.Transportation.Companion.convertTransportationNameToNum
import java.text.SimpleDateFormat
import java.util.*


private const val DATABASE_NAME = "masrufati_database"
private const val version: Int = 2
private const val JOURNEY_TABLE_NAME = "journey_table"
private const val JOURNEY_ID_COLUMN_NAME_INT = "journey_id"
private const val JOURNEY_START_DATE_COLUMN_NAME_STRING = "journey_start_date"
private const val JOURNEY_END_DATE_COLUMN_NAME_STRING = "journey_end_date"
private const val JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING = "journey_initial_address"
private const val JOURNEY_BANK_NAME_COLUMN_NAME_STRING = "journey_bank_name"
private const val JOURNEY_BANK_CITY_COLUMN_NAME_STRING = "journey_bank_city"
private const val JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING = "journey_bank_address"
private const val JOURNEY_NOTE_TABLE_NAME = "note"
private const val JOURNEY_NOTE_ID_COLUMN_NAME_INT = "id"
private const val JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT = "background"
private const val JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING = "title"
private const val JOURNEY_NOTE_BODY_COLUMN_NAME_STRING = "body"
private const val JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT = "journey_id"
private const val TRANSPORTATION_TABLE_NAME = "transportation_table"
private const val TRANSPORTATION_ID_COLUMN_NAME_INT = "transportation_id"
private const val TRANSPORTATION_NAME_COLUMN_NAME_INT = "transportation_name"
private const val TRANSPORTATION_COST_COLUMN_NAME_FLOAT = "transportation_cost"
private const val TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT = "transportation_journey_id"

abstract class Database(mContext: Context) :
    SQLiteOpenHelper(mContext, DATABASE_NAME, null, version) {
    override fun onCreate(p0: SQLiteDatabase?) {
        checkUpdate(p0!!, 0)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        checkUpdate(p0!!, p1)
    }

    private fun checkUpdate(sqLiteDatabase: SQLiteDatabase, lastVersion: Int) {
        if (lastVersion < 1) {
            sqLiteDatabase.execSQL(
                "create table $JOURNEY_TABLE_NAME (" +
                        "$JOURNEY_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null," +
                        "$JOURNEY_START_DATE_COLUMN_NAME_STRING  text not null," +
                        "$JOURNEY_END_DATE_COLUMN_NAME_STRING  text null," +
                        "$JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING  text not null," +
                        "$JOURNEY_BANK_NAME_COLUMN_NAME_STRING  text not null," +
                        "$JOURNEY_BANK_CITY_COLUMN_NAME_STRING text not null," +
                        "$JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING text)"
            )

            sqLiteDatabase.execSQL(
                "create table $TRANSPORTATION_TABLE_NAME (" +
                        "$TRANSPORTATION_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null," +
                        "$TRANSPORTATION_NAME_COLUMN_NAME_INT integer not null," +
                        "$TRANSPORTATION_COST_COLUMN_NAME_FLOAT real not null," +
                        "$TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT integer not null," +
                        "FOREIGN KEY($TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT) REFERENCES $JOURNEY_TABLE_NAME($JOURNEY_ID_COLUMN_NAME_INT))"
            )
        }

        if (lastVersion < 2) {
            sqLiteDatabase.execSQL(
                "create table $JOURNEY_NOTE_TABLE_NAME (" +
                        "$JOURNEY_NOTE_ID_COLUMN_NAME_INT integer primary key AUTOINCREMENT not null ," +
                        "$JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING text null ," +
                        "$JOURNEY_NOTE_BODY_COLUMN_NAME_STRING text null ," +
                        "$JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT integer ," +
                        "$JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT integer, " +
                        "FOREIGN KEY($JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT) REFERENCES $JOURNEY_TABLE_NAME($JOURNEY_ID_COLUMN_NAME_INT))"
            )
        }
    }
}

class AccessDatabase private constructor(mContext: Context) : Database(mContext) {

    private lateinit var sqLiteDatabase: SQLiteDatabase

    private fun openDB() {
        sqLiteDatabase = writableDatabase
    }

    private fun closeDB() {
        sqLiteDatabase.close()
    }

    fun newJourney(journey: Journey): Int {
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_START_DATE_COLUMN_NAME_STRING, journey.startDate())
        contentValues.put(JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING, journey.initialAddress())
        contentValues.put(JOURNEY_BANK_NAME_COLUMN_NAME_STRING, journey.bank().name())
        contentValues.put(JOURNEY_BANK_CITY_COLUMN_NAME_STRING, journey.bank().location().city())
        contentValues.put(
            JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING,
            journey.bank().location().address()
        )
        openDB()
        val id = sqLiteDatabase.insert(JOURNEY_TABLE_NAME, null, contentValues)
        closeDB()
        return id.toInt()
    }

    @SuppressLint("SimpleDateFormat")
    fun endJourney(journeyId: Int): Pair<Boolean, String> {
        openDB()
        val endDate = SimpleDateFormat("yyyy/MM/dd").format(Date())
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_END_DATE_COLUMN_NAME_STRING, endDate)
        val flag = sqLiteDatabase.update(
            JOURNEY_TABLE_NAME,
            contentValues,
            "$JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        closeDB()
        return Pair(flag != 0, endDate)
    }

    fun updateInitialAddress(journeyId: Int, newInitialAddress: String): Boolean {
        openDB()
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING, newInitialAddress)
        val flag = sqLiteDatabase.update(
            JOURNEY_TABLE_NAME,
            contentValues,
            "$JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun updateBankName(journeyId: Int, newBankName: String): Boolean {
        openDB()
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_BANK_NAME_COLUMN_NAME_STRING, newBankName)
        val flag = sqLiteDatabase.update(
            JOURNEY_TABLE_NAME,
            contentValues,
            "$JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun updateBankCity(journeyId: Int, newBankCity: String): Boolean {
        openDB()
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_BANK_CITY_COLUMN_NAME_STRING, newBankCity)
        val flag = sqLiteDatabase.update(
            JOURNEY_TABLE_NAME,
            contentValues,
            "$JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun updateBankAddress(journeyId: Int, newBankAddress: String?): Boolean {
        openDB()
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING, newBankAddress)
        val flag = sqLiteDatabase.update(
            JOURNEY_TABLE_NAME,
            contentValues,
            "$JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun removeJourney(journeyId: Int): Boolean {
        openDB()
        val flag = sqLiteDatabase.delete(
            JOURNEY_TABLE_NAME,
            "$JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun removeTransportation(transportationId: Int): Boolean {
        openDB()
        val flag = sqLiteDatabase.delete(
            TRANSPORTATION_TABLE_NAME,
            "$TRANSPORTATION_ID_COLUMN_NAME_INT = ?",
            arrayOf(transportationId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun addTransportation(transportation: Transportation, journeyId: Int): Int {
        val contentValues = ContentValues()
        contentValues.put(TRANSPORTATION_COST_COLUMN_NAME_FLOAT, transportation.cost())
        contentValues.put(TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT, journeyId)
        contentValues.put(
            TRANSPORTATION_NAME_COLUMN_NAME_INT,
            convertTransportationNameToNum(transportation.transportationName())
        )
        openDB()
        val id = sqLiteDatabase.insert(TRANSPORTATION_TABLE_NAME, null, contentValues)
        closeDB()
        return id.toInt()
    }

    fun updateTransportationCost(newCost: Float, transportationId: Int): Boolean {
        val contentValues = ContentValues()
        contentValues.put(TRANSPORTATION_COST_COLUMN_NAME_FLOAT, newCost)
        openDB()
        val flag = sqLiteDatabase.update(
            TRANSPORTATION_TABLE_NAME,
            contentValues,
            "$TRANSPORTATION_ID_COLUMN_NAME_INT = ?",
            arrayOf(transportationId.toString())
        )
        closeDB()
        return flag != 0
    }

    fun updateTransportationName(
        newTransportationName: TransportationName,
        transportationId: Int
    ): Boolean {
        val contentValues = ContentValues()
        contentValues.put(
            TRANSPORTATION_COST_COLUMN_NAME_FLOAT,
            convertTransportationNameToNum(newTransportationName)
        )
        openDB()
        val flag = sqLiteDatabase.update(
            TRANSPORTATION_TABLE_NAME,
            contentValues,
            "$TRANSPORTATION_ID_COLUMN_NAME_INT = ?",
            arrayOf(transportationId.toString())
        )
        closeDB()
        return flag != 0
    }

    @SuppressLint("Recycle")
    fun getJourney(journeyId: Int): Journey {
        openDB()
        var journey: Journey? = null
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $JOURNEY_TABLE_NAME where $JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_ID_COLUMN_NAME_INT))
            val startDate =
                cursor.getString(cursor.getColumnIndex(JOURNEY_START_DATE_COLUMN_NAME_STRING))
            val endDate =
                cursor.getString(cursor.getColumnIndex(JOURNEY_END_DATE_COLUMN_NAME_STRING))
            val initialAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING))
            val bankName =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_NAME_COLUMN_NAME_STRING))
            val bankCity =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_CITY_COLUMN_NAME_STRING))
            val bankAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING))
            val listOfTransportation = getAllTransportation(id)
            val note = getNoteByJourneyID(id)
            closeDB()
            journey = Journey(
                id,
                startDate,
                endDate,
                initialAddress,
                Bank(bankName, Location(bankCity, bankAddress)),
                listOfTransportation,
                note
            )
        }
        return journey!!
    }

    @SuppressLint("Recycle")
    private fun getAllTransportation(journeyId: Int): MutableList<Transportation> {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $TRANSPORTATION_TABLE_NAME where $TRANSPORTATION_JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        val listOfTransportation: MutableList<Transportation> = mutableListOf()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(TRANSPORTATION_ID_COLUMN_NAME_INT))
            val name = convertNumToTransportationName(
                cursor.getInt(
                    cursor.getColumnIndex(TRANSPORTATION_NAME_COLUMN_NAME_INT)
                )
            )
            val cost = cursor.getFloat(cursor.getColumnIndex(TRANSPORTATION_COST_COLUMN_NAME_FLOAT))
            listOfTransportation.add(Transportation(id, name, cost))
        }
        closeDB()
        return listOfTransportation
    }

    @SuppressLint("Recycle")
    fun getAllJourney(): MutableList<Journey> {
        openDB()
        val cursor = sqLiteDatabase.rawQuery("select * from $JOURNEY_TABLE_NAME", arrayOf())
        val listOfJourney = mutableListOf<Journey>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_ID_COLUMN_NAME_INT))
            val startDate =
                cursor.getString(cursor.getColumnIndex(JOURNEY_START_DATE_COLUMN_NAME_STRING))
            val endDate =
                cursor.getString(cursor.getColumnIndex(JOURNEY_END_DATE_COLUMN_NAME_STRING))
            val initialAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING))
            val bankName =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_NAME_COLUMN_NAME_STRING))
            val bankCity =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_CITY_COLUMN_NAME_STRING))
            val bankAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING))
            if (endDate != null)
                listOfJourney.add(
                    Journey(
                        id,
                        startDate,
                        endDate,
                        initialAddress,
                        Bank(bankName, Location(bankCity, bankAddress)),
                        mutableListOf(),
                        null
                    )
                )
        }
        closeDB()
        for (i in listOfJourney) {
            i.transportation(getAllTransportation(i.id()))
            i.setNote(getNoteByJourneyID(i.id()))
        }
        return listOfJourney
    }

    @SuppressLint("Recycle")
    fun getAllJourney(startDate: String, endDate: String): MutableList<Journey> {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $JOURNEY_TABLE_NAME where $JOURNEY_START_DATE_COLUMN_NAME_STRING >= ? and $JOURNEY_START_DATE_COLUMN_NAME_STRING <= ? and $JOURNEY_END_DATE_COLUMN_NAME_STRING is not null",
            arrayOf(startDate, endDate)
        )
        val listOfJourney = mutableListOf<Journey>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_ID_COLUMN_NAME_INT))
            val initialAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING))
            val bankName =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_NAME_COLUMN_NAME_STRING))
            val bankCity =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_CITY_COLUMN_NAME_STRING))
            val bankAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING))
            val jStartDate = cursor.getString(cursor.getColumnIndex(JOURNEY_START_DATE_COLUMN_NAME_STRING))
            val jEndDate = cursor.getString(cursor.getColumnIndex(JOURNEY_END_DATE_COLUMN_NAME_STRING))
            listOfJourney.add(
                Journey(
                    id,
                    jStartDate,
                    jEndDate,
                    initialAddress,
                    Bank(bankName, Location(bankCity, bankAddress)),
                    mutableListOf(),
                    null
                )
            )
        }
        closeDB()
        for (i in listOfJourney) {
            i.transportation(getAllTransportation(i.id()))
            i.setNote(getNoteByJourneyID(i.id()))
        }
        return listOfJourney
    }

    @SuppressLint("Recycle")
    fun getLastJourney(): Journey {
        openDB()
        var journey: Journey? = null
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $JOURNEY_TABLE_NAME where $JOURNEY_END_DATE_COLUMN_NAME_STRING is NULL",
            arrayOf()
        )
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_ID_COLUMN_NAME_INT))
            val startDate =
                cursor.getString(cursor.getColumnIndex(JOURNEY_START_DATE_COLUMN_NAME_STRING))
            val endDate =
                cursor.getString(cursor.getColumnIndex(JOURNEY_END_DATE_COLUMN_NAME_STRING))
            val initialAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_INITIAL_ADDRESS_COLUMN_NAME_STRING))
            val bankName =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_NAME_COLUMN_NAME_STRING))
            val bankCity =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_CITY_COLUMN_NAME_STRING))
            val bankAddress =
                cursor.getString(cursor.getColumnIndex(JOURNEY_BANK_ADDRESS_COLUMN_NAME_STRING))
            val listOfTransportation = getAllTransportation(id)
            val note = getNoteByJourneyID(id)
            closeDB()
            journey = Journey(
                id,
                startDate,
                endDate,
                initialAddress,
                Bank(bankName, Location(bankCity, bankAddress)),
                listOfTransportation,
                note
            )
        }
        if(journey != null) return journey
        else throw NullPointerException()
        //return journey!!
    }

    fun newNote(journeyId: Int, note: Note): Int {
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING, note.getTitle())
        contentValues.put(JOURNEY_NOTE_BODY_COLUMN_NAME_STRING, note.getBody())
        contentValues.put(JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT, journeyId)
        contentValues.put(JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT, Note.toInt(note.getBackground()))
        openDB()
        val id = sqLiteDatabase.insert(JOURNEY_NOTE_TABLE_NAME, null, contentValues)
        closeDB()
        return id.toInt()
    }

    fun updateNote(note: Note): Boolean {
        val contentValues = ContentValues()
        contentValues.put(JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING, note.getTitle())
        contentValues.put(JOURNEY_NOTE_BODY_COLUMN_NAME_STRING, note.getBody())
        contentValues.put(JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT, Note.toInt(note.getBackground()))
        openDB()
        val flag = sqLiteDatabase.update(
            JOURNEY_NOTE_TABLE_NAME, contentValues, "$JOURNEY_NOTE_ID_COLUMN_NAME_INT = ?",
            arrayOf(note.getId().toString())
        )
        closeDB()
        return flag != 0
    }

    fun getNote(noteId: Int): Note {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $JOURNEY_NOTE_TABLE_NAME where $JOURNEY_NOTE_ID_COLUMN_NAME_INT = ?",
            arrayOf(noteId.toString())
        )
        var note: Note? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_NOTE_ID_COLUMN_NAME_INT))
            val title =
                cursor.getString(cursor.getColumnIndex(JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING))
            val body =
                cursor.getString((cursor.getColumnIndex(JOURNEY_NOTE_BODY_COLUMN_NAME_STRING)))
            val background = cursor.getInt(cursor.getColumnIndex(
                JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT))
            note = Note(id, title, body,background)
        }
        return note!!
    }

    fun getNotes(title:String):MutableList<Note>{
        openDB()
        val newTitle = "%$title%"
        val cursor = sqLiteDatabase.rawQuery("select * from $JOURNEY_NOTE_TABLE_NAME where $JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING like ?",
            arrayOf(title))
        val listOfNotes = mutableListOf<Note>()
        while(cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_NOTE_ID_COLUMN_NAME_INT))
            val background = cursor.getInt(cursor.getColumnIndex(JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT))
            val title = cursor.getString(cursor.getColumnIndex(JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING))
            val body = cursor.getString(cursor.getColumnIndex(JOURNEY_NOTE_BODY_COLUMN_NAME_STRING))
            listOfNotes.add(Note(id,title,body,background))
        }
        return listOfNotes
    }

    @SuppressLint("Recycle")
    fun getNoteByJourneyID(journeyId: Int): Note? {
        openDB()
        val cursor = sqLiteDatabase.rawQuery(
            "select * from $JOURNEY_NOTE_TABLE_NAME where $JOURNEY_NOTE_JOURNEY_ID_COLUMN_NAME_INT = ?",
            arrayOf(journeyId.toString())
        )
        var note: Note? = null
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(JOURNEY_NOTE_ID_COLUMN_NAME_INT))
            val title =
                cursor.getString(cursor.getColumnIndex(JOURNEY_NOTE_TITLE_COLUMN_NAME_STRING))
            val body =
                cursor.getString((cursor.getColumnIndex(JOURNEY_NOTE_BODY_COLUMN_NAME_STRING)))
            val background = cursor.getInt(cursor.getColumnIndex(
                JOURNEY_NOTE_BACKGROUND_COLUMN_NAME_INT))
            note = Note(id, title, body,background)
        }
        return note
    }

    companion object {
        fun newInstance(mContext: Context): AccessDatabase {
            return AccessDatabase(mContext)
        }
    }

}