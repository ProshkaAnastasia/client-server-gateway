package commands

import collection.CollectionHandler
import communication.action.AbstractAction
import communication.action.Output
import service.CommandAnswer
import user.Status
import user.User

class ShowCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "show"
    override val description: String = "выводит на экран все элементы коллекции."
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        return if (CollectionHandler.collection.isEmpty()){
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, "Коллекция пуста.")))
        } else {
            CommandAnswer(true, foreignActions = arrayListOf(Output(ArrayList(CollectionHandler.collection))))
        }
    }
}