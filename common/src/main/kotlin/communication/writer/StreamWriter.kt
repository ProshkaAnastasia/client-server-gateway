package communication.writer

import communication.Message
import communication.MetaMessage
import communication.reader.MessageReader
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer

class StreamWriter(socket: Socket) : MessageWriter(){
    private var dis: DataOutputStream = DataOutputStream(socket.getOutputStream())
    override fun sendMessage(message: MetaMessage){
        var out = ByteBuffer.allocate(message.buf.size + 4)
        out.putInt(message.buf.size)
        out.put(message.buf)
        out.flip()
        dis.write(out.array())
    }

    override fun stop() {
        dis.close();
    }
}