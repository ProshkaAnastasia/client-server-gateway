package service

import AbstractCommand
import ArgDTO
import deserializer.Deserializer
import exceptions.IllegalArgumentsException
import exceptions.IllegalCommandException
import exceptions.IllegalStatusException
import koinApp
import kotlinx.serialization.SerializationException
import org.koin.core.qualifier.named
import types.ProgramTypes
import user.Status

object CommandApproveMaker {
    private var commands: HashMap<String, AbstractCommand> = HashMap()
    private var localCommands: HashMap<String, AbstractCommand> = HashMap()
    private var accessibleTypes: ArrayList<String?> = ProgramTypes::class.sealedSubclasses.map{it.simpleName} as ArrayList

    init{
        accessibleTypes.addAll(0, arrayListOf("String", "Int", "Byte", "Char", "Long", "Short", "Float", "Double"))
        localCommands["execute_script"] = AbstractCommand("execute_script",
                    "execute_script",
            "cчитывает и исполняет скрипт из указанного файла.",
                        hashSetOf(Status.ADMIN, Status.STANDARD),
                        arrayListOf("String"), arrayListOf())
        localCommands["help"] = AbstractCommand("help",
            "help",
            "выводит список доступных команд с их описанием и аргументами.",
            hashSetOf(Status.ADMIN, Status.STANDARD),
            arrayListOf(), arrayListOf())
        localCommands.forEach{commands[it.key] = it.value}
    }

    fun print(){
        commands.forEach{println(it.value)}
    }

    fun approveCommand(arguments: List<String>, primitiveArguments: ArrayList<Any>) : ArrayList<String> {
        val name: String
        val primitiveTypes: ArrayList<String>
        try{
            name = arguments[0]
            primitiveTypes = ArrayList(commands[name]!!.primitiveTypes)
            if (arguments.size - 1 < primitiveTypes.size){
                throw IllegalArgumentsException()
            }
            //6 -> 5
            //4 -> 3
            if (primitiveTypes.isNotEmpty() && primitiveTypes.last().startsWith("ArrayList")){
                val last = primitiveTypes.last()
                val type = "(?<=\\<).+(?=\\>)".toRegex().find(last)
                val size = arguments.size - primitiveTypes.size
                primitiveTypes.removeLast()
                val array = Array(size) { type!!.value }
                primitiveTypes.addAll(array)
            }
            if (primitiveTypes.size < arguments.size - 1){
                throw IllegalArgumentsException()
            }
            for (i in 0 until primitiveTypes.size){
                val type = primitiveTypes[i]
                val deserializer = koinApp.koin.get<Deserializer>(named(type))
                deserializer.decodeFromString(arguments[i + 1])
                if (i > 0 && name == "add_new_command" && !accessibleTypes.contains(arguments[i + 1])){
                    throw IllegalArgumentsException()
                }
                primitiveArguments.add(deserializer.decodeFromString(arguments[i + 1]))
            }
        } catch(e : Exception){
            when(e){
                is ArrayIndexOutOfBoundsException, is IndexOutOfBoundsException, is NullPointerException -> throw IllegalCommandException()
                is NumberFormatException, is SerializationException, is IllegalArgumentException -> throw IllegalArgumentsException()
                else -> throw e
            }
        }
        return commands[name]!!.referenceTypes
    }

    fun isClientCommand(command: String) : Boolean{
        return localCommands.keys.contains(command)
    }

    @Synchronized
    fun addCommand(command: AbstractCommand){
        commands[command.name] = command
    }
    @Synchronized
    fun updateCommands(newArray: HashMap<String, AbstractCommand>){
        commands = newArray
        localCommands.forEach{ commands[it.key] = it.value }
    }
    @Synchronized
    fun deleteCommand(command: AbstractCommand){
        commands.remove(command.name)
    }
}