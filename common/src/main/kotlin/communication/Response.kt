package communication

import communication.action.AbstractAction
import handler.AbstractHandler
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("response")
class Response(val number: Int, var ok: Boolean, var totalError: String? = null, var errors: ArrayList<Error>? = null,
               var actions: ArrayList<AbstractAction>? = null) : Message() {
    override var sender: String? = null
    override var receiver: String? = null
    override fun apply(handler: AbstractHandler) {
        handler.handleResponse(this)
    }

    override var attempt: Int = 1
}