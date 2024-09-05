package commands

import collection.CollectionHandler
import user.Status
import communication.action.AbstractAction
import communication.action.Output
import service.CommandAnswer
import user.User

class InfoCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "info"
    override val description: String = "выводит информацию о коллекции."
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        return CommandAnswer(true, foreignActions = arrayListOf(Output(CollectionHandler.getInfo(), Colors.BLUE)))
    }
}