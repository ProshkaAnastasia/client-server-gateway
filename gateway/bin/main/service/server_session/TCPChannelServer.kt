package service.server_session

import communication.reader.MessageReader
import communication.reader.TcpChannelReader
import communication.writer.MessageWriter
import communication.writer.TcpChannelWriter
import java.nio.channels.SocketChannel

class TCPChannelServer(val channel: SocketChannel, address: String) : ServerSession(address){
    override val reader: MessageReader = TcpChannelReader(channel)
    override val writer: MessageWriter = TcpChannelWriter(channel)
    override fun close() {
        channel.close()
    }
}