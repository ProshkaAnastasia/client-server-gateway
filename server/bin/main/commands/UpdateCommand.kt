package commands

import collection.CollectionHandler
import communication.DemandInfo
import communication.action.Output
import communication.action.Update
import exceptions.DataBaseException
import exceptions.NotExistingElement
import service.CommandAnswer
import service.action.SendDemandInfo
import types.SpaceMarine
import user.Status
import user.User

class UpdateCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "update"
    override val description: String = "обновляет элемент коллекции, id которого соответствует заданному."
    override val primitiveTypes: ArrayList<String> = arrayListOf("Long")
    override val referenceTypes: ArrayList<String> = arrayListOf("SpaceMarine")
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        val id = arguments[0] as Long
        val newSpaceMarine = arguments[1] as SpaceMarine
        return try{
            val lastSpaceMarine = CollectionHandler.find{
                it.id == id
            }
            val updateNumber = CollectionHandler.update(lastSpaceMarine, newSpaceMarine, token)
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, "Элемент c id = $id успешно обновлен.")),
                localActions = arrayListOf(SendDemandInfo(DemandInfo(updateNumber, arrayListOf(Update(lastSpaceMarine, newSpaceMarine))))))
        } catch (e : DataBaseException){
            CommandAnswer(false, e.message, arrayListOf(Output(true, e.message!!)))
        } catch (e : NotExistingElement){
            CommandAnswer(false, e.message, arrayListOf(Output(true, e.message!!)))
        }
    }
}