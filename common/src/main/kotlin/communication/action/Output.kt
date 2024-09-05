package communication.action

import Colors
import handler.AbstractClientHandler
import handler.AbstractHandler
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("output")
class Output(val arguments: ArrayList<@Polymorphic Any> = arrayListOf(), val color: Colors = Colors.RESET) : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractClientHandler){
            return handler.handleOutput(this)
        }
        TODO()
    }
    //constructor(array : ArrayList<Any>) : this(array, Colors.RESET)
    constructor(color: Colors, vararg arguments: Any) : this(ArrayList(arguments.toMutableList()), color)
    constructor(args: Boolean, vararg arguments: Any) : this(ArrayList(arguments.toMutableList()))
}