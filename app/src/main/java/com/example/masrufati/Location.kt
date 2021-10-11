package com.example.masrufati

class Location(city: String?, address: String?) : Formatting, Validation {

    private var city: String
    private var address: String?

    init {
        this.city = valid(city)         // check if city is null or not and formatting it
        this.address =
            addressIsNullOrNotAndFormattingIt(address)             // check if city is null return it and if not null formatting it and return it
    }

    fun city(): String {                                   // to get id
        return this.city
    }

    fun address(): String? {                          // to get location name
        return this.address
    }

    fun updateCity(journeyId: Int, city: String?, accessDatabase: AccessDatabase): Boolean {           // to change city value and if it not valid will throw NullPointerException
        val flag = accessDatabase.updateBankCity(journeyId, valid(city))
        if (flag) this.city = valid(city)
        return flag
    }

    fun updateAddress(journeyId: Int, address: String?, accessDatabase: AccessDatabase): Boolean {     // to change address value and if it not valid will throw NullPointerException
        val flag = accessDatabase.updateBankAddress(journeyId, address)
        if(flag) this.address = address
        return flag
    }

    override fun valid(addressORcity: String?): String {        // to check value of city or address In terms of is it null or not
        if (!(addressORcity.isNullOrBlank() || addressORcity.isNullOrEmpty())) return sentence(
            addressORcity
        )
        throw NullPointerException("you may be enter invalid address or city")
    }

    private fun addressIsNullOrNotAndFormattingIt(address: String?): String? {        // to check if address is null or not and if it is not null formatting it    and it use in constructor
        if (address.isNullOrEmpty() || address.isNullOrBlank()) return address
        return sentence(address)
    }

    override fun sentence(sentence: String): String {           // to formatting sentence
        var newString = ""
        for (i in sentence) {
            if (i == ' ' && newString.isNotEmpty() && newString[newString.length - 1] != ' ') newString += i
            else if (i != ' ') newString += i
        }
        return newString
    }
}