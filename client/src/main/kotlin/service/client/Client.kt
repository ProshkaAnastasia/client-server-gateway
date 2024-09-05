package service.client

import Stream
import communication.*
import communication.action.Authorization
import communication.action.Description
import communication.action.Execution
import communication.action.Registration
import communication.reader.MessageReader
import communication.writer.MessageWriter
import deserializer.Deserializer
import exceptions.IllegalScriptFileException
import koinApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import service.ClientMessageHandler
import service.CommandApproveMaker
import types.ProgramTypes
import user.Status
import user.User
import java.io.BufferedReader
import java.io.EOFException
import java.io.FileInputStream
import java.io.InputStreamReader
import java.lang.RuntimeException
import java.net.InetAddress
import java.util.concurrent.Exchanger
import java.util.regex.Pattern


abstract class Client(val host: InetAddress, val port: Int) {
    abstract val reader: MessageReader
    abstract val writer: MessageWriter
    lateinit var token: String
    protected val exchanger: Exchanger<Message> = Exchanger()
    protected lateinit var handler: ClientMessageHandler
    protected var active: Boolean = true
    protected var user: User? = null
    protected var status: Status? = null
    abstract fun tune()
    abstract fun start()
    abstract fun stop()
    fun registration(){
        while (true){
            val user = getConsoleUser()
            val response = sendRequest(Request(true, Registration(user)))
            response.apply(handler)
            if (response.ok){
                break
            }
        }
    }
    fun authorization(){
        val istream = BufferedReader(InputStreamReader(System.`in`))
        while (true){
            val user = getConsoleUser()
            println("Хотите ли Вы войти в учетную запись администратора?")
            print("Если нет, оставьте поле пустым.  ")
            val admin = istream.readLine().isNotEmpty()
            var key: String? = null
            var status = Status.STANDARD
            if (admin){
                print(String.format("%-20s", "Ключ подключения:"))
                status = Status.ADMIN
                key = istream.readLine()
            }
            var response = sendRequest(Request(true, Authorization(user, status, key)))
            response.apply(handler)
            if (response.ok){
                this.user = user
                this.status = status
                break
            }
        }
    }
    fun getConsoleUser() : User{
        val istream = BufferedReader(InputStreamReader(System.`in`))
        print(String.format("%-20s", "Имя пользователя:"))
        val login = istream.readLine()
        print(String.format("%-20s", "Пароль:"))
        val password = istream.readLine()
        return User(login, password)
    }
    fun preparation(){
        while (true){
            val istream = BufferedReader(InputStreamReader(System.`in`))
            println("Хотите ли Вы зарегистрироваться в системе (1) или войти в свою учетную запись(2)?")
            var ind = istream.readLine()
            while (ind != "1" && ind != "2"){
                println("Неожиданный ответ на вопрос. Пожалуйста, попробуйте снова.")
                ind = istream.readLine()
            }
            if (ind == "1"){
                registration()
            } else {
                authorization()
                break
            }
        }
        var r = sendRequest(Request(true, Description(status!!)))
        r.apply(handler)
    }
    fun run() {
        while (active){
            try{
                start()
                Thread(::readMessages).start()
                sendMessage(GatewayRegistration(SenderType.CLIENT))
                preparation()
            } catch (e: Exception){
                println(e.message)
                println("Попытка переподключения...")
            }
            var response1 = sendRequest(Request(true, Execution("djhjdjhd","info")))
            response1.apply(handler)
            //var r = sendRequest(Request(true, Description(Status.STANDARD)))
            //r.apply(handler)
            //token = "dlkhfkjdhf"
            logicStart()
        }
        //sendMessage(Response(1, true))
        //var response = sendRequest(Request(true, Execution("djhjdjhd","info")))
        //response.apply(handler)
    }
    fun readMessages(){
        while(true) {
            try{
                val message = reader.readMessage().realMessage
                if (message is Response) {
                    exchanger.exchange(message)
                    continue
                }
                message.apply(handler)
            } catch (e: EOFException){
                println("Соединение с сервером потеряно. Попытка переподключения...")
            }
        }
    }
    fun sendMessage(message: Message){
        var data = MetaMessage(message)
        writer.sendMessage(data)
    }
    fun sendRequest(request: Request) : Response{
        sendMessage(request)
        return exchanger.exchange(request) as Response
    }

    fun logicStart(){
        val istream = BufferedReader(InputStreamReader(System.`in`))
        while (true){
            print("Ожидается ввод новой команды...\t")
            val commandLine = istream.readLine()
            val arguments = commandLine.split(Pattern.compile("\\s+"))
            if (arguments.isNotEmpty() && arguments[0] == "exit"){
                active = false;
                break;
            }
            val fullArguments: ArrayList<Any> = arrayListOf()
            val referenceTypes: ArrayList<String>
            try{
                referenceTypes = CommandApproveMaker.approveCommand(arguments, fullArguments)
            } catch(e : Exception){
                println("${Colors.RED}" + e.message + "${Colors.RESET}")
                continue
            }
            if (arguments[0] == "help"){
                CommandApproveMaker.print()
                continue
            }
            if (arguments[0] == "execute_script"){
                val newStream: BufferedReader
                try{
                    newStream = BufferedReader(InputStreamReader(FileInputStream(arguments[1])))
                    readFile(newStream, Stream.FILE, hashSetOf(arguments[1]))
                } catch(e: Exception){
                    println("${Colors.RED}" + e.message + "${Colors.RESET}")
                }
                continue
            }
            for (type in referenceTypes){
                val dto = Json.encodeToString(koinApp.koin.get<ProgramTypes>(named(type)).createSerializedInstance(istream))
                val obj = koinApp.koin.get<Deserializer>(named(type)).decodeFromString(dto)
                println(dto)
                fullArguments.add(obj)
            }
            val execution = Execution(user!!.login, arguments[0], fullArguments)
            val request = Request(true, execution)
            val response = sendRequest(request)
            response.apply(handler)
        }
    }

    private fun readFile(istream: BufferedReader, stream: Stream, script: HashSet<String> = hashSetOf()){
        println("${Colors.CYAN}Запущено выполнение скриптового файла...${Colors.RESET}")
        while (istream.ready()){
            val commandLine = istream.readLine()
            val arguments = commandLine.split(Pattern.compile("\\s+"))
            val fullArguments: ArrayList<Any> = arrayListOf()
            val referenceTypes: ArrayList<String>
            try{
                referenceTypes = CommandApproveMaker.approveCommand(arguments, fullArguments)
            } catch(e : Exception){
                println("${Colors.RED}" + e.message + "${Colors.RESET}")
                throw IllegalScriptFileException()
            }
            if (arguments[0] == "help"){
                CommandApproveMaker.print()
                continue
            }
            if (arguments[0] == "execute_script"){
                if (script.contains(arguments[1])){
                    continue
                }
                val newStream: BufferedReader
                try{
                    newStream = BufferedReader(InputStreamReader(FileInputStream(arguments[1])))
                    script.add(arguments[1])
                    readFile(newStream, stream, script)
                } catch(e: Exception){
                    println("${Colors.RED}" + e.message + "${Colors.RESET}")
                }
                continue
            }
            for (type in referenceTypes){
                val dto = Json.encodeToString(koinApp.koin.get<ProgramTypes>(named(type)).createSerializedInstance(istream, stream))
                fullArguments.add(dto)
            }
            val execution = Execution(user!!.login, arguments[0], fullArguments)
            val request = Request(true, execution)
            val response = sendRequest(request)
            response.apply(handler)

        }
    }
}