package communication

import actionModule
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import messageModule
import modules.deserializerModule
import typesModule
import java.net.InetAddress

class MetaMessage() {
    val json = Json{serializersModule = messageModule + actionModule + typesModule}
    lateinit var content: String
    lateinit var buf: ByteArray
    lateinit var realMessage: Message
    var port: Int? = null
    var address: InetAddress? = null
    constructor(message: Message) : this(){
        this.content = json.encodeToString(message)
        this.realMessage = message
        this.buf = content.toByteArray()
    }
    constructor(content: String) : this(){
        this.content = content
        this.realMessage = json.decodeFromString(content)
        this.buf = content.toByteArray()
    }
}