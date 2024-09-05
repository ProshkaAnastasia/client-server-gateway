package service.gateway

import communication.*
import communication.action.Description
import communication.reader.TcpChannelReader
import communication.writer.TcpChannelWriter
import exceptions.BrokenPipeException
import reactor.core.publisher.FluxSink
import service.client_session.TCPChannelClient
import service.server_session.TCPChannelServer
import user.Status
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.nio.channels.spi.SelectorProvider
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.Future

class TCPChannelGateway(port: Int) : GatewayLBService(port) {
    var channel = ServerSocketChannel.open()
    val unknownUsersSelector = SelectorProvider.provider().openSelector()
    val clientSelector = SelectorProvider.provider().openSelector()
    val serverSelector = SelectorProvider.provider().openSelector()

    inner class ClientReadingTask(private val sink: FluxSink<Message>, val selKey: SelectionKey) : Callable<Boolean> {
        private val channel = selKey.channel() as SocketChannel
        private val key = "${channel.socket().inetAddress}:${channel.socket().port}"
        override fun call(): Boolean {
            try{
                sink.next(clients[key]!!.readMessage().setSender(key))
                println("New message recieved from client $key")
            } catch (e : BrokenPipeException){
                clients[key]?.close()
                servers.remove(key)
                selKey.cancel()
                println("Client $key disconnected")
            } catch (e : NullPointerException){
                println("Client $key does not exist")
            } catch (e : Exception) {
                println("Something went wrong (for example, message was not correctly deserialized)")
                println(e.message)
            }
            return true
        }
    }

    inner class ServerReadingTask(private val sink: FluxSink<Message>, val selKey: SelectionKey) : Callable<Boolean> {
        private val channel = selKey.channel() as SocketChannel
        private val key = "${channel.socket().inetAddress}:${channel.socket().port}"
        override fun call(): Boolean {
            try{
                val message = servers[key]!!.readMessage().setSender(key)
                if (message is Response){
                    println(message.number)
                    clients[message.receiver]?.getExchangerByKey(message.number)?.exchange(message)
                    servers[key]!!.occupancy--
                    return true
                }
                sink.next(message)
                println("New message received from server $key")
            } catch (e : BrokenPipeException){
                servers[key]?.close()
                servers.remove(key)
                selKey.cancel()
                println("Server $key disconnected")
            } catch (e : NullPointerException){
                println("Server $key does not exist")
            } catch (e : Exception) {
                println("Something went wrong (for example, message was not correctly deserialized)")
                println(e.message)
            }
            return true
        }
    }

    override fun makeConnection() {
        while(active){
            val user = channel.accept()
            if (user != null){
                user.configureBlocking(false)
                user.register(unknownUsersSelector, SelectionKey.OP_READ)
                println("New user connected on port $port")
            }
        }
    }

    override fun readClientMessage(sink: FluxSink<Message>){
        while (active){
            if (clientSelector.selectNow() == 0)
                continue
            val futures: ArrayList<Future<Boolean>> = arrayListOf()
            val iterator = clientSelector.selectedKeys().iterator()
            while(iterator.hasNext()){
                val selKey = iterator.next()
                iterator.remove()
                if (selKey.isReadable){
                    futures.add(readingExecutor.submit(ClientReadingTask(sink, selKey)))
                }
            }
            futures.forEach{it.get()}
        }
    }

    override fun readServerMessage(sink: FluxSink<Message>) {
        while (active){
            if (serverSelector.selectNow() == 0)
                continue
            val futures: ArrayList<Future<Boolean>> = arrayListOf()
            val iterator = serverSelector.selectedKeys().iterator()
            while(iterator.hasNext()){
                val selKey = iterator.next()
                iterator.remove()
                if (selKey.isReadable){
                    futures.add(readingExecutor.submit(ServerReadingTask(sink, selKey)))
                }
            }
            futures.forEach{it.get()}
        }
    }

    override fun listenToUnknown() {
        while(active) {
            unknownUsersSelector.selectedKeys().clear()
            if(unknownUsersSelector.selectNow() == 0)
                continue
            var iterator = unknownUsersSelector.selectedKeys().iterator()
            while (iterator.hasNext()) {
                var selKey = iterator.next()
                iterator.remove()
                if (selKey.isReadable) {
                    var user = selKey.channel() as SocketChannel
                    var reader = TcpChannelReader(user)
                    var key = "${user.socket().inetAddress}:${user.socket().port}"
                    var message: Message
                    try{
                        message = reader.readMessage().realMessage
                    } catch (e : IOException){
                        println("Connection with user $key was lost.")
                        selKey.cancel()
                        user.close()
                        continue
                    }
                    if (message !is GatewayRegistration){
                        var writer = TcpChannelWriter(user)
                        try{
                            if (message is Request){
                                writer.sendMessage(MetaMessage(Response(message.number, false, totalError = "Запрос не может быть обработан. Отправитель не определен.")))
                            } else {
                                writer.sendMessage(MetaMessage(ErrorResponse("Сообщение не может быть обработано. Отправитель не определен.")))
                            }
                        } catch (e : BrokenPipeException){
                            println("Connection with user $key was lost.")
                            selKey.cancel()
                            user.close()
                        }
                        continue
                    }
                    var registration = message
                    if (registration.senderType == SenderType.CLIENT) {
                        println("New client registered on address $key")
                        clients[key] = TCPChannelClient(user, key)
                        user.register(clientSelector, SelectionKey.OP_READ)
                    }
                    if (registration.senderType == SenderType.SERVER){
                        println("New server registered on address $key")
                        if (servers.isEmpty()){
                            updateNumber = registration.updateNumber
                        }
                        servers[key] = TCPChannelServer(user, key)
                        user.register(serverSelector, SelectionKey.OP_READ)
                    }
                    selKey.cancel()
                }
            }
        }
    }

    override fun tune() {
        channel.bind(InetSocketAddress(InetAddress.getLocalHost(), port))
        channel.configureBlocking(false)
        println("Gateway started on port $port")
    }


}