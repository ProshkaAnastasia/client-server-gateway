package communication.reader

import communication.Message
import communication.MessageType
import communication.MetaMessage
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class TcpChannelReader(private val channel: SocketChannel) : MessageReader() {
    override fun readMessage(): MetaMessage {
        var buffer = ByteBuffer.allocate(4)
        //channel.read(buffer)
        //buffer.flip()
        //val type = MessageType.values()[buffer.int]
        channel.read(buffer)
        buffer.flip()
        if (buffer.limit() == 0){
            throw IOException()
        }
        var data = ByteBuffer.allocate(buffer.int)
        channel.read(data)
        return MetaMessage(String(data.array()))
    }
}