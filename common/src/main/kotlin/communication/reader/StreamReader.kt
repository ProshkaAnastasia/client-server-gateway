package communication.reader

import communication.Message
import communication.MessageType
import communication.MetaMessage
import java.io.DataInputStream
import java.net.Socket

class StreamReader(socket: Socket) : MessageReader() {
    private var dis: DataInputStream = DataInputStream(socket.getInputStream())
    override fun readMessage() : MetaMessage {
        val size = dis.readInt()
        val buffer = ByteArray(size)
        dis.read(buffer)
        return MetaMessage(String(buffer))
    }
}