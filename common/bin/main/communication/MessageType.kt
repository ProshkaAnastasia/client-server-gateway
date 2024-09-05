package communication

enum class MessageType(private var value: String) {
    REQUEST("Request"),
    RESPONSE("Response");
    companion object{
        fun getByName(name: String) : MessageType{
            return values().find{ it.value == name }!!
        }
    }
}