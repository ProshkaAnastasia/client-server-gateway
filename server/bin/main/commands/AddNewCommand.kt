package commands

import AbstractCommand
import communication.DemandInfo
import communication.Request
import user.Status
import communication.action.AbstractAction
import communication.action.AddCommand
import communication.action.Output
import service.CommandAnswer
import service.CommandKeeper
import service.action.DispatchAll
import service.action.SendDemandInfo
import types.ProgramTypes
import user.User

class AddNewCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN)
    override val name: String = "add_new_command"
    override val description: String = "добавляет новую команду на сервер."
    override val primitiveTypes: ArrayList<String> = arrayListOf("String", "ArrayList<String>")
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        val name = arguments[0] as String
        arguments.removeFirst()
        val primitiveTypes: ArrayList<String> = arrayListOf()
        val referenceTypes: ArrayList<String> = arrayListOf()
        val sealed = ProgramTypes::class.sealedSubclasses
        arguments.forEach{ if (sealed.map{ e -> e.simpleName}.contains(it as String)) referenceTypes.add(it) else primitiveTypes.add(it)}
        val command = AbstractCommand(name, "print_command", "печатает свои аргументы.", hashSetOf(Status.STANDARD, Status.ADMIN), primitiveTypes, referenceTypes)
        CommandKeeper.add(command)
        Request(AddCommand(command))
        return CommandAnswer(ok = false, foreignActions = arrayListOf(Output(Colors.GREEN, "Команда добавлена на сервер")), localActions = arrayListOf(DispatchAll(Request(AddCommand(command)))))
        TODO("Not yet implemented")
    }
}