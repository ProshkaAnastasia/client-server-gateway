package service.client_session

import java.util.concurrent.Exchanger
import communication.*
import communication.reader.MessageReader
import communication.reader.TcpChannelReader
import communication.writer.MessageWriter
import communication.writer.TcpChannelWriter
import exceptions.BrokenPipeException
import java.io.IOException
import java.util.NoSuchElementException
import java.util.stream.IntStream

abstract class ClientSession(private val address: String, private val threadPoolSize: Int = 4) {
    val exchanger: HashMap<Exchanger<Message>, Int?> = hashMapOf()
    init{
        IntStream.range(0, threadPoolSize).forEach{
            exchanger[Exchanger<Message>()] = null
        }
    }
    abstract val reader: MessageReader
    abstract val writer: MessageWriter
    fun sendMessage(message: Message){
        try{
            writer.sendMessage(MetaMessage(message))
        } catch (e: IOException){
            println("Client $address disconnected")
            throw BrokenPipeException(address)
        }
    }
    fun getFreeExchanger() : Exchanger<Message> {
        return exchanger.filter{it.value == null}.keys.first()
    }
    fun getExchangerByKey(key: Int) : Exchanger<Message>? {
        return try{
            exchanger.filterKeys { exchanger[it] == key }.keys.first()
        } catch (e : NoSuchElementException){
            null
        }
    }
    fun readMessage() : Message{
        try{
            return reader.readMessage().realMessage
        } catch (e: IOException){
            throw BrokenPipeException(address)
        }
    }
    abstract fun close()
}