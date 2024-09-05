package service.client

import communication.GatewayRegistration
import communication.Request
import communication.Response
import communication.SenderType
import communication.action.Description
import communication.reader.MessageReader
import communication.reader.StreamReader
import communication.writer.MessageWriter
import communication.writer.StreamWriter
import service.ClientMessageHandler
import user.Status
import java.net.InetAddress
import java.net.Socket

class TcpStreamClient(host: InetAddress, port: Int) : Client(host, port) {
    override lateinit var reader: MessageReader
    override lateinit var writer: MessageWriter
    override fun tune() {
        TODO("Not yet implemented")
    }

    private lateinit var socket: Socket
    override fun start() {
        socket = Socket(host, port)
        reader = StreamReader(socket)
        writer = StreamWriter(socket)
        handler = ClientMessageHandler(this)
    }

    override fun stop() {
        TODO("Not yet implemented")
    }



}