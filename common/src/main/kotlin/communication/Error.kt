package communication

import communication.action.AbstractAction
import kotlinx.serialization.Serializable

@Serializable
data class Error(val message: String, val action: AbstractAction)