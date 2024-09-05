package commands

import collection.CollectionHandler
import communication.DemandInfo
import user.Status
import communication.action.AbstractAction
import communication.action.Add
import communication.action.Delete
import communication.action.Output
import exceptions.DataBaseException
import exceptions.NotExistingElement
import service.CommandAnswer
import service.action.SendDemandInfo
import types.SpaceMarine
import user.User

class RemoveByIdCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "remove_by_id"
    override val description: String = "удаляет элемент из коллекции по его id."
    override val primitiveTypes: ArrayList<String> = arrayListOf("Long")
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        println("Start")
        val id = arguments[0] as Long
        println("Deserialized")
        return try{
            val element = CollectionHandler.find{
                it.id == id
            }
            println("Found")
            val number = CollectionHandler.remove(element, token)
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, "Элемент с id = $id удален из коллекции")),
                localActions = arrayListOf(SendDemandInfo(DemandInfo(number, arrayListOf(Delete(element))))))
        } catch (e : DataBaseException){
            CommandAnswer(false, e.message, arrayListOf(Output(Colors.RED, "Удаление элемента из коллекции завершилось ошибкой.", e.message!!)))
        } catch (e : NotExistingElement){
            CommandAnswer(false, e.message, arrayListOf(Output(Colors.RED, "Удаление элемента из коллекции завершилось ошибкой.", e.message!!)))
        } catch (e : NullPointerException){
            CommandAnswer(false, e.message, arrayListOf(Output(Colors.RED, "Удаление элемента из коллекции завершилось ошибкой.", e.message!!)))
        }
    }
}