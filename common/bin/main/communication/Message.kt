package communication

import handler.AbstractHandler
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.net.InetAddress

@Serializable
abstract class Message() {
    abstract var attempt: Int
    abstract var sender: String?
    abstract var receiver: String?
    fun setSender(key: String?) : Message {
        sender = key
        return this
    }
    fun setReceiver(key: String?) : Message {
        receiver = key
        return this
    }
    fun setAttempt(value: Int) : Message {
        attempt = value
        return this
    }
    abstract fun apply(handler: AbstractHandler)
    @Transient
    var port: Int? = null
    @Transient
    var address: InetAddress? = null
}