package communication.writer

import communication.Message
import communication.MetaMessage

abstract class MessageWriter() {
    abstract fun sendMessage(message: MetaMessage)
    abstract fun stop();
}