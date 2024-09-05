package communication

import handler.AbstractHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class GatewayRegistration(val senderType: SenderType, val updateNumber: Int = 0) : Message() {
    override var sender: String? = null
    override var receiver: String? = null
    override fun apply(handler: AbstractHandler) {
        //
    }

    override var attempt: Int = 1
}