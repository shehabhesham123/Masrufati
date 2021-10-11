package com.example.masrufati

interface Validation {
    fun valid(id: Int?): Int {
        throw Exception("Not not implementation this function")
    }

    fun valid(sentence: String?): String {
        throw Exception("Not not implementation this function")
    }

    fun valid(location: Location?): Location {
        throw Exception("Not not implementation this function")
    }

    fun valid(cost: Float?): Float {
        throw Exception("Not not implementation this function")
    }

    fun valid(transportationName: TransportationName?): TransportationName {
        throw Exception("Not not implementation this function")
    }

    fun valid(bank: Bank?): Bank {
        throw Exception("Not not implementation this function")
    }

    fun validDate(date: String?): String {
        throw Exception("Not not implementation this function")
    }

    fun validNote(title_body:String?):String{
        throw Exception("Not not implementation this function")
    }

    fun valid(note: Note?):Note{
        throw Exception("Not not implementation this function")
    }
}