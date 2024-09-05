package commands

import collection.CollectionHandler
import user.Status
import communication.action.AbstractAction
import communication.action.Output
import service.CommandAnswer
import user.User

class SumByHealthCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "sum_by_health"
    override val description: String = "выводит сумму значений поля health для всех элементов коллекциию"
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        val answer = CollectionHandler.collection.fold(0.toDouble()){
            a, b -> a + b.health
        }
        return CommandAnswer(true, foreignActions = arrayListOf(Output(true, answer)))
    }
}