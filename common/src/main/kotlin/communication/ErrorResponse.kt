package communication

import communication.action.AbstractAction
import handler.AbstractHandler
import kotlinx.serialization.Serializable

@Serializable
class ErrorResponse(val errors: ArrayList<Error>? = null, val totalError: String? = null, val actions: ArrayList<AbstractAction>? = null) : Message() {
    override var sender: String? = null
    override var receiver: String? = null
    override fun apply(handler: AbstractHandler) {
        handler.handleErrorResponse(this)
    }

    override var attempt: Int = 1
    constructor(totalError: String) : this(null, totalError)
    constructor(actions: ArrayList<AbstractAction>) : this(null, null, actions)
}