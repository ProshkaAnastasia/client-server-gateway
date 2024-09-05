package commands

import user.Status
import communication.action.AbstractAction
import service.CommandAnswer
import user.User

sealed class Command {
    abstract val name: String
    abstract val description: String
    abstract val primitiveTypes: ArrayList<String>
    abstract val referenceTypes: ArrayList<String>
    abstract val callStatus: HashSet<Status>
    abstract fun execute(token: String, arguments: ArrayList<Any>) : CommandAnswer
}