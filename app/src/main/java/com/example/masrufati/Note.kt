package com.example.masrufati

class Note : Validation {
    private var id = -1
    private var title: String? = null
    private var body: String? = null
    private var background :Color = Color.WHITE

    constructor(id: Int?, title: String?, body: String?,color:Int) {
        this.id = valid(id)
        this.title = title
        this.body = body
        this.background = toColor(color)
    }

    constructor(title: String?, body: String?,color:Color) {
        this.title = title
        this.body = body
        this.background = color
    }

    fun setBackground(color: Int){
        this.background = toColor(color)
    }

    fun getBackground():Color{
        return this.background
    }

    fun getId(): Int {
        return valid(this.id)
    }

    fun setId(id: Int?) {
        this.id = valid(id)
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun getTitle(): String? {
        return this.title
    }

    fun setBody(body: String?) {
        this.body = body
    }

    fun getBody(): String? {
        return this.body
    }

    private fun sentence(sentence: String?): String? {
        return try {
            val newSentence = validNote(sentence)
            var newString = ""
            for (i in newSentence) {
                if (i == ' ' && newString.isNotEmpty() && newString[newString.length - 1] != ' ') newString += i
                else if (i != ' ') newString += i
            }
            newString
        } catch (e: Exception) {
            null
        }
    }

    override fun valid(id: Int?): Int {
        if (id != null && id > 0) return id
        throw NullPointerException("invalid ID")
    }

    override fun validNote(title_body: String?): String {
        if (!(title_body.isNullOrBlank() || title_body.isNullOrEmpty())) return title_body
        throw NullPointerException()
    }

    enum class Color{
        WHITE,
        BLUE,
        PURPLE,
        GREY
    }

    companion object{
        fun toInt(color: Color):Int{
            return when(color){
                Color.WHITE -> 1
                Color.BLUE -> 2
                Color.GREY -> 3
                else -> 4
            }
        }

        fun toColor(color:Int):Color{
            return when(color){
                1 -> Color.WHITE
                2 -> Color.BLUE
                3 -> Color.GREY
                else -> Color.PURPLE
            }
        }
    }
}