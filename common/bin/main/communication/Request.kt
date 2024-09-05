package communication

import communication.action.AbstractAction
import handler.AbstractHandler
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("request")
class Request(var actions: ArrayList<AbstractAction>, val answer: Boolean = false) : Message() {
    var number: Int = -1
    init{
        if (number == -1){
            number = totalNumber
            totalNumber = (totalNumber + 1) % 100
        }
    }
    override var sender: String? = null
    override var receiver: String? = null
    override fun apply(handler: AbstractHandler) {
        handler.handleRequest(this)
    }

    override var attempt: Int = 1
    companion object{
        var totalNumber: Int = 1
    }

    constructor(answer: Boolean, vararg action: AbstractAction) : this(ArrayList(action.toMutableList()), answer)
    constructor(vararg action: AbstractAction) : this(ArrayList(action.toMutableList()))
}