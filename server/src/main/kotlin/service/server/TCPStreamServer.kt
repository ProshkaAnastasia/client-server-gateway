package service.server

import communication.DemandInfo
import communication.GatewayRegistration
import communication.SenderType
import communication.reader.MessageReader
import communication.reader.StreamReader
import communication.writer.MessageWriter
import communication.writer.StreamWriter
import service.ServerMessageHandler
import service.database.UserHandler
import java.net.InetAddress
import java.net.Socket

class TCPStreamServer(host: InetAddress, port: Int) : AbstractServer(host, port){
    override lateinit var reader: MessageReader
    override lateinit var writer: MessageWriter
    override val handler = ServerMessageHandler(this)
    private lateinit var socket: Socket
    override fun start() {
        socket = Socket(host, port)
        reader = StreamReader(socket)
        writer = StreamWriter(socket)
        sendMessage(GatewayRegistration(SenderType.SERVER, UserHandler.getUpdateNumber()))
    }

    override fun stop() {
        socket.close()
    }
}