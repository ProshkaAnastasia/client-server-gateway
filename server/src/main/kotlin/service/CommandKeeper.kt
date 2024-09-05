package service

import AbstractCommand
import commands.Command
import user.Status
import kotlin.reflect.full.createInstance

object CommandKeeper {
    private var commands: HashMap<String, AbstractCommand> = HashMap()

    init {
        val classes = Command::class.sealedSubclasses
        for (command in classes){
            val instance = command.createInstance()
            commands[instance.name] = AbstractCommand(instance.name, instance.name, instance.description, instance.callStatus, instance.primitiveTypes, instance.referenceTypes)
        }
    }

    fun getRealName(name: String) : String{
        return commands[name]!!.realName
    }

    fun getCommands(status: Status) : List<AbstractCommand> {
        println(status)
        return commands.filter{it.value.status.contains(status)}.map{it.value}
    }

    fun print(){
        commands.forEach { println(it) }
    }

    fun add(command: AbstractCommand){
        commands[command.name] = command
    }
}