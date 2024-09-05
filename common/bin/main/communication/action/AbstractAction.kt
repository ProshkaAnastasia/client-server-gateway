package communication.action

import handler.AbstractHandler
import kotlinx.serialization.Serializable

@Serializable
abstract class AbstractAction {
    abstract fun apply(handler: AbstractHandler, key: String) : ActionResponse
}