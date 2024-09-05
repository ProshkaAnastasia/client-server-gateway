package commands

import user.Status
import communication.action.AbstractAction
import service.CommandAnswer
import user.User

class PrintCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf()
    override val name: String = "print"
    override val description: String = "печатает свои аргументы."
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        TODO("Not yet implemented")
    }
}