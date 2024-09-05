package service.server_session

import communication.Message
import communication.MetaMessage
import communication.reader.MessageReader
import communication.writer.MessageWriter
import exceptions.BrokenPipeException
import java.io.IOException
import java.util.concurrent.Exchanger

abstract class ServerSession(val address: String) {
    var occupancy: Int = 0
    abstract val reader: MessageReader
    abstract val writer: MessageWriter
    abstract fun close()
    fun sendMessage(message: Message){
        try{
            writer.sendMessage(MetaMessage(message))
        } catch (e: IOException){
            println("Server $address disconnected")
            throw BrokenPipeException(address)
        }
    }
    fun readMessage() : Message {
        try{
            return reader.readMessage().realMessage
        } catch (e: IOException){
            throw BrokenPipeException(address)
        }
    }
}