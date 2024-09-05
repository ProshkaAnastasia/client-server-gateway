package service.gateway

import communication.*
import communication.action.Output
import exceptions.BrokenPipeException
import exceptions.NoActiveServerException
import reactor.core.publisher.BaseSubscriber
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import service.client_session.ClientSession
import service.server_session.ServerSession
import java.util.*
import java.util.concurrent.*
import kotlin.properties.Delegates

abstract class GatewayLBService(val port: Int) {
    protected var updateNumber by Delegates.notNull<Int>()
    private var queueExchanger = Exchanger<DemandInfo>()
    protected var active: Boolean = true
    protected val clients: HashMap<String, ClientSession> = HashMap()
    protected val servers: LinkedHashMap<String, ServerSession> = LinkedHashMap()
    protected var readingExecutor = Executors.newFixedThreadPool(4)
    private var clientMessageExecutor = Executors.newFixedThreadPool(4)
    private var serverMessageExecutor = Executors.newFixedThreadPool(4)
    protected var demandInfoQueue = PriorityQueue<DemandInfo>()
    val notifier: Notifier = Notifier()
    abstract fun makeConnection()
    abstract fun listenToUnknown()
    abstract fun tune()
    abstract fun readClientMessage(sink: FluxSink<Message>)
    abstract fun readServerMessage(sink: FluxSink<Message>)

    inner class DemandInfoSubscriber : BaseSubscriber<DemandInfo>() {
        override fun hookOnNext(value: DemandInfo) {
            println("on next demand ${value.number}")
            updateNumber++
            servers.forEach{ it.value.sendMessage(value)}
            println("Сервера проинформированы")
            if (!demandInfoQueue.isEmpty()){
                if (demandInfoQueue.peek().number != updateNumber){
                    synchronized(notifier){
                        println("Бужу потоки исполнения")
                        notifier.state = true
                        (notifier as Object).notifyAll()
                    }
                }
            } else{
                synchronized(notifier){
                    println("Бужу потоки исполнения")
                    notifier.state = true
                    (notifier as Object).notifyAll()
                }
            }
        }
    }

    inner class Notifier {
        var state = true
    }

    fun queueBuilder(sink: FluxSink<DemandInfo>) {
        while (active) {
            if (!demandInfoQueue.isEmpty()){
                if (demandInfoQueue.peek().number == updateNumber){
                    notifier.state = false
                    sink.next(demandInfoQueue.poll())
                    continue
                }
            }
            var demandInfo = queueExchanger.exchange(DemandInfo(1, arrayListOf()))
            println(demandInfo)
            if (demandInfo.number == updateNumber){
                notifier.state = false
                sink.next(demandInfo)
            } else{
                println("not next number ${demandInfo.number}")
                demandInfoQueue.add(demandInfo)
            }
        }
    }

    fun start() {
        tune()
        Thread(::makeConnection).start()
        Thread(::listenToUnknown).start()

        val clientMessagePublisher: Flux<Message> = Flux.create(::readClientMessage).share()

        val clientRequestPublisher = clientMessagePublisher.filter{it is Request}.filter{(it as Request).answer}.map{it as Request}
        val clientUsualMessagePublisher = clientMessagePublisher.filter{
            if (it !is Request){
                return@filter true
            } else {
                return@filter !it.answer
            }
        }

        Thread{clientRequestPublisher.subscribe {
            println(it)
            synchronized(notifier){
                if (!notifier.state){
                    println("I am sleeping...")
                    (notifier as Object).wait()
                    println("I am woke up")
                }
            }
            clientMessageExecutor.submit {
                println("Обработка запроса")
                val exchanger = clients[it.sender]!!.getFreeExchanger()
                val response: Message = try {
                    redirectClientMessage(it)
                    clients[it.sender]!!.exchanger[exchanger] = it.number
                    exchanger.exchange(it, 10, TimeUnit.SECONDS)
                } catch (e : TimeoutException) {
                    println("Время ожидания ответа на запрос истекло.")
                    Response(it.number, false, actions = arrayListOf(Output(Colors.RED, "Время ожидания ответа на запрос истекло.")))
                } catch (e : NoActiveServerException){
                    println("Нет доступных серверов для обработки сообщения")
                    Response(it.number, false, actions = arrayListOf(Output(Colors.RED, "Нет доступных серверов для обработки сообщения")))
                } catch (e : NullPointerException){
                    println("Указанный клиент больше не существует!")
                    return@submit
                } finally{
                    clients[it.sender]?.exchanger?.set(exchanger, null)
                }
                try {
                    println("Запрос обработан")
                    clients[it.sender]!!.sendMessage(response)
                } catch (e: BrokenPipeException) {
                    println("Соединение с клиентом потеряно!")
                    clients[it.sender]?.close()
                } catch (e: NullPointerException) {
                    println("Указанный клиент больше не существует!")
                }
            }
        }}.start()

        Thread{clientUsualMessagePublisher.subscribe {
            println(it)
            synchronized(notifier){
                if (!notifier.state){
                    (notifier as Object).wait()
                }
            }
            serverMessageExecutor.submit{
                try {
                    redirectClientMessage(it)
                } catch (e : NoActiveServerException){
                    clients[it.sender]?.sendMessage(ErrorResponse(totalError = e.message!!, actions = arrayListOf(Output(Colors.RED, e.message!!))))
                } catch (e : Exception){
                    println(e.message)
                }
            }
        }}.start()

        val serverMessagePublisher: Flux<Message> = Flux.create(::readServerMessage).share()

        val demandInfoPublisher = serverMessagePublisher.filter{ it is DemandInfo }.map{ it as DemandInfo }

        Thread{demandInfoPublisher.subscribe{
            println("received demand info")
            queueExchanger.exchange(it)
        }}.start()

        val queuePublisher: Flux<DemandInfo> = Flux.create(::queueBuilder).share()

        queuePublisher.subscribe(DemandInfoSubscriber())
        /*
        Thread{ clientRequestPublisher.subscribe(object : BaseSubscriber<Request>(){
            override fun hookOnNext(value: Request) {
                println(value)
                clientMessageExecutor.submit {
                    // Блок synchronized в будущем
                    val exchanger = clients[value.sender]!!.getFreeExchanger()
                    val response: Message = try {
                        redirectClientMessage(value)
                        clients[value.sender]!!.exchanger[exchanger] = value.number
                        exchanger.exchange(value, 10, TimeUnit.SECONDS)
                    } catch (e: TimeoutException) {
                        Response(value.number, false, "Время ожидания ответа на запрос истекло.")
                    } catch (e: NoActiveServerException){
                        Response(value.number, false, e.message)
                    } catch (e : NullPointerException){
                        println("Указанный клиент больше не существует!")
                        return@submit
                    } finally{
                        clients[value.sender]?.exchanger?.set(exchanger, null)
                    }
                    try {
                        clients[value.sender]!!.sendMessage(response)
                    } catch (e: BrokenPipeException) {
                        println("Соединение с клиентом потеряно!")
                        // Здесь будет код закрытия клиентской сессии
                    } catch (e: NullPointerException) {
                        println("Указанный клиент больше не существует!")
                    }
                }
            }
        }) }.start()

        clientUsualMessagePublisher.subscribe(object : BaseSubscriber<Message>(){
            override fun hookOnNext(value: Message) {
                println(value)
                clientMessageExecutor.submit{
                    // Блок synchronized в будущем. А он нужен?
                    redirectClientMessage(value)
                }
            }
        })


        var serverMessagePublisher: Flux<Message> = Flux.create{sink ->
            while (active)
                readServerMessage().forEach(sink::next)
            sink.complete()
        }

        var serverResponsePublisher = serverMessagePublisher.filter{it is Response}.map{it as Response}
        var serverDemandPublisher = serverMessagePublisher.filter{it is DemandInfo}
        var serverUsualMessagePublisher = serverMessagePublisher.filter{(it !is DemandInfo) and (it !is Response) and ((it !is Request) or !(it as Request).answer) }

        serverResponsePublisher.subscribe(object : BaseSubscriber<Response>(){
            override fun hookOnNext(value: Response) {
                val exchanger = clients[value.reciever]?.getExchangerByKey(value.number)
                try {
                    var response: Message = exchanger!!.exchange(value)
                } catch (e : NullPointerException){
                    println("Ответ на запрос получен слишком поздно.")
                }
            }
        })

        serverUsualMessagePublisher.subscribe(object : BaseSubscriber<Message>(){
            override fun hookOnNext(value: Message) {
                try {
                    clients[value.reciever]!!.sendMessage(value)
                } catch (e : BrokenPipeException){
                    println("Соединение с клиентом потеряно!")
                    // Здесь будет код закрытия клиентской сессии
                } catch (e : NullPointerException){
                    println("Указанный клиент больше не существует!")
                }
            }
        })
        //var clientRequest = clientMessagePublisher.filter{(it is Request) and (it as Request).answer}
        //var serverRequest = serverMessagePublisher.filter{(it is Request) and (it as Request).answer}
        //При получении от клиента запроса, который предполагает ответ, создается поток ответов на основе обработки каждого запроса.
        //Далее некоторый подписчик будет обрабатывать эти ответы, пересылая их нужному клиенту.

        /*
        var serverResponse = clientMessagePublisher.filter{(it is Request) and (it as Request).answer}.map{
            var future = clientMessageExecutor.submit(Callable {
                try{
                    redirectClientMessage(it)
                    clients[it.sender]!!.exchanger.exchange(it) as Response
                } catch (e : TimeoutException){
                    var errors = (it as Request).actions.map{Error(e.message!!, it)}.toCollection(ArrayList())
                    var response = Response(it.number, false, errors = errors)
                    response.reciever = it.sender
                    response
                }
            })
            future.get()
        }
        Thread{ serverResponse.subscribe(object : BaseSubscriber<Response>() {
            override fun hookOnNext(value: Response) {
                try{
                    clients[value.reciever]!!.sendMessage(value)
                } catch (e: BrokenPipeException){
                    clients[value.reciever]!!.stop()
                    clients.remove(value.reciever)
                }
            }
        }) }.start()

         */


        //При получении от какого-либо сервера запроса, который предполагает ответ, создается поток ответов на основе обработки каждого запроса.
        //Далее в отдельном потоке некоторый подписчик при получении нового ответа будет перенаправлять его адресату.
        var clientResponse = serverMessagePublisher.filter{(it is Request) and (it as Request).answer}.map{
            var future = clientMessageExecutor.submit(Callable {
                try{
                    clients[it.reciever]!!.sendMessage(it)
                    servers[it.sender]!!.exchanger.exchange(it, 10, TimeUnit.SECONDS) as Response
                } catch (e : TimeoutException){
                    var errors = (it as Request).actions.map{Error(e.message!!, it)}.toCollection(ArrayList())
                    Response(it.number, false, errors = errors)
                }
            })
            future.get()
        }
        var serverDemandInfo = serverMessagePublisher.filter{it is Request}
        clientMessagePublisher.subscribe(::println)


         */

    }
    fun redirectClientMessage(message: Message){
        println(message.sender)
        if (servers.isEmpty()){
            throw NoActiveServerException()
        }
        val server = servers.values.sortedBy(ServerSession::occupancy).first()
        println(server.address)
        if (message is Request){
            server.occupancy++
        }
        server.sendMessage(message.setReceiver(server.address))
    }
}