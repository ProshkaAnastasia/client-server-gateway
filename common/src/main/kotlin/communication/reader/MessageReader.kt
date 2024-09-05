package communication.reader

import communication.Message
import communication.MetaMessage

abstract class MessageReader() {
    abstract fun readMessage() : MetaMessage
}