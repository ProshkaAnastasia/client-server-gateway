package commands

import Colors
import collection.CollectionHandler
import communication.DemandInfo
import communication.action.Add
import communication.action.Output
import exceptions.DataBaseException
import service.CommandAnswer
import service.action.SendDemandInfo
import types.SpaceMarine
import user.Status
import user.User

class AddCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "add"
    override val description: String = "добавляет новый элемент в коллекцию."
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf("SpaceMarine")
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        return try{
            val number = CollectionHandler.add(arguments[0] as SpaceMarine, token)
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, "Элемент добавлен в коллекцию")),
                localActions = arrayListOf(SendDemandInfo(DemandInfo(number, arrayListOf(Add(arguments[0] as SpaceMarine))))))
        } catch (e : DataBaseException){
            CommandAnswer(false, e.message, arrayListOf(Output(Colors.RED, "Добавление элемента в коллекцию завершилось ошибкой.")))
        }
    }
}