package service.server

import communication.*
import communication.reader.MessageReader
import communication.writer.MessageWriter
import exceptions.ConnectionLostException
import handler.AbstractServerHandler
import service.ServerMessageHandler
import service.database.UserHandler
import sun.awt.Mutex
import java.io.EOFException
import java.net.InetAddress
import java.net.SocketException
import java.util.concurrent.Executors
import java.util.concurrent.locks.ReentrantLock

abstract class AbstractServer(val host: InetAddress, val port: Int) {
    abstract val handler: AbstractServerHandler
    protected var active = true
    var freeThreads = 4
    var executor = Executors.newFixedThreadPool(freeThreads)
    protected val rLock = ReentrantLock()
    abstract var reader: MessageReader
    abstract var writer: MessageWriter
    abstract fun start()
    abstract fun stop()
    fun notifyEveryone(message: Message){
        UserHandler.getAllActiveUsers().forEach{message.receiver = it; sendMessage(message)}
    }
    fun notifySpecial(message: Message, key: String){
        message.receiver = key
        sendMessage(message)
    }
    fun notifyFilter(message: Message, keys: ArrayList<String>, function: (key: String) -> Boolean = { true }){
        keys.filter(function).forEach{message.receiver = it; sendMessage(message)}
    }
    fun sendMessage(message: Message){
        writer.sendMessage(MetaMessage(message))
    }
    fun readMessages() {
        while (active){
            try{
                var message = reader.readMessage().realMessage
                println("Message received: $message")
                if (message is DemandInfo){
                    message.apply(handler)
                } else{
                    executor.execute{
                        rLock.lock()
                        freeThreads--
                        rLock.unlock()
                        message.apply(handler)
                        rLock.lock()
                        freeThreads++
                        rLock.unlock()
                    }
                }
            } catch (e : EOFException){
                throw ConnectionLostException()
            }
        }
    }
    fun run(){
        while(active){
            try{
                start()
                readMessages()
            } catch (e : ConnectionLostException){
                println(e.message)
                continue
            } catch (e : EOFException){
                println(e.message)
                continue
            } catch (e : SocketException){
                println(e.message)
                continue
            } catch (e : Exception){
                println("Something went wrong...")
                println(e.message)
            }
        }
        stop()
    }

}