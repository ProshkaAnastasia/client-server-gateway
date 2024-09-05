package communication.writer

import communication.Message
import communication.MetaMessage
import communication.writer.MessageWriter
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

class TcpChannelWriter(private val channel: SocketChannel) : MessageWriter(){
    override fun sendMessage(message: MetaMessage) {
        var buffer = ByteBuffer.allocate(message.buf.size + 4)
        buffer.putInt(message.buf.size)
        buffer.put(message.buf)
        buffer.flip()
        channel.write(buffer)
    }

    override fun stop() {
        TODO("Not yet implemented")
    }
}