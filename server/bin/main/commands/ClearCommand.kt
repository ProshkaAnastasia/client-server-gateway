package commands

import collection.CollectionHandler
import communication.DemandInfo
import communication.action.AbstractAction
import communication.action.Add
import communication.action.Clear
import communication.action.Output
import exceptions.DataBaseException
import service.CommandAnswer
import service.action.SendDemandInfo
import types.SpaceMarine
import user.Status
import user.User

class ClearCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN)
    override val name: String = "clear"
    override val description: String = "удаляет все элементы из коллекции"
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        return try{
            val number = CollectionHandler.clear(token)
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, "Коллекция очищена")),
                localActions = arrayListOf(SendDemandInfo(DemandInfo(number, arrayListOf(Clear())))))
        } catch (e : DataBaseException){
            CommandAnswer(false, e.message, arrayListOf(Output(Colors.RED, "Добавление элемента в коллекцию завершилось ошибкой.")))
        }
    }
}