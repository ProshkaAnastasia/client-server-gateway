package service

import AbstractCommand
import collection.CollectionHandler
import communication.action.*
import handler.AbstractServerHandler
import commands.*
import communication.*
import communication.action.RenewCommands
import koinApp
import org.koin.core.qualifier.named
import service.action.LocalServerAction
import service.database.UserHandler
import service.server.AbstractServer
import java.util.stream.Collectors

class ServerMessageHandler(val server: AbstractServer) : AbstractServerHandler() {
    override fun handleExecution(action: Execution, key: String): ActionResponse {
        val answer = koinApp.koin.get<Command>(named(action.command)).execute(key, action.arguments)
        answer.localActions.forEach{
            server.executor.execute { (it.execute(server)) }
        }
        val error = if (answer.error == null) null else Error(answer.error, action)
        return ActionResponse(answer.ok, error, answer.foreignActions)
    }

    override fun handleDescription(action: Description): ActionResponse {
        val status = action.status
        val array = CommandKeeper.getCommands(status).map{it.name to it}.toMap()
        val actions: ArrayList<AbstractAction> = arrayListOf(RenewCommands(HashMap(array)))
        return ActionResponse(true, null, actions)
    }

    override fun handleAuthorization(action: Authorization, key: String): ActionResponse {
        return if (UserHandler.authorizeUser(action.user, key))
            ActionResponse(true, actions = arrayListOf(Output(Colors.GREEN, "Авторизация успешно завершена!")))
        else
            ActionResponse(false, error = Error("Ошибка авторизации.", action), actions = arrayListOf(Output(Colors.RED, "Неверное имя пользователя или пароль.")))
    }

    override fun handleRegistration(action: Registration): ActionResponse {
        UserHandler.registerUser(action.user)
        return ActionResponse(true)
    }

    override fun handleAdd(action: Add): ActionResponse {
        CollectionHandler.localAdd(action.element)
        return ActionResponse(true)
    }

    override fun handleDelete(action: Delete): ActionResponse {
        CollectionHandler.localDelete(action.element)
        return ActionResponse(true)
    }

    override fun handleUpdate(action: Update): ActionResponse {
        CollectionHandler.localUpdate(action.last, action.new)
        return ActionResponse(true)
    }

    override fun handleClear(action: Clear): ActionResponse {
        TODO("Not yet implemented")
    }

    override fun handleFullUpdate(action: FullUpdate): ActionResponse {
        TODO("Not yet implemented")
    }

    override fun handleDemandInfo(info: DemandInfo) {
        println(server.freeThreads)
        while (server.freeThreads < 3){
            //
        }
        info.actions.forEach{it.apply(this, info.sender!!)}
    }

    override fun handleResponse(response: Response) {
        if (response.ok) return
        // Я зашьюсь, если в таком случае серверу нужно будет что-то делать еще
        response.errors?.forEach{println("${it.action::class.simpleName}: ${it.message} from ${response.sender}")}     //Потенциально логируем ошибки
        val actions = response.errors?.map{it.action}?.toCollection(ArrayList()) //Получаем все невыполненные действия
        val request = Request(actions!!).setReceiver(response.sender).setSender(response.receiver).setAttempt(response.attempt + 1) //Создаем запрос на их повторное выполнение
        if (request.attempt <= 3)
            server.sendMessage(request)
    }

    override fun handleRequest(request: Request) {
        val answers = request.actions.map{it.apply(this, request.sender!!)}
        val ok = answers.fold(true){a, b -> a and b.ok}
        var errors: ArrayList<Error>? = answers.filter{!it.ok}.map{it.error!!}.toCollection(ArrayList())
        val actions = answers.filter{!it.actions.isNullOrEmpty()}.map{it.actions!!}.fold(ArrayList<AbstractAction>()){
            a, b -> a.addAll(b); a
        }
        server.sendMessage(Response(request.number, ok, errors = errors, actions = actions).setSender(request.receiver).setReceiver(request.sender))
    }

    override fun handleErrorResponse(errorResponse: ErrorResponse) {
        if (errorResponse.totalError != null){
            println(errorResponse.totalError)
        }
        val actions = errorResponse.errors!!.fold(ArrayList<AbstractAction>()){
            a, b -> a.add(b.action)
            a
        }
        val request = Request(actions, false).setReceiver(errorResponse.sender).setSender(errorResponse.receiver).setAttempt(errorResponse.attempt + 1)
        if (request.attempt <= 3){
            server.sendMessage(request)
        }
    }
}