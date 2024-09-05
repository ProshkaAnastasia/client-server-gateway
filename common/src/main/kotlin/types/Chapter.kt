package types

import Stream
import exceptions.IllegalScriptFileException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.valiktor.functions.isBetween
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import java.io.BufferedReader
import java.time.format.DateTimeFormatter

@Serializable
class Chapter(
    var name : String,
    var parentLegion: String? = null,
    var marinesCount: Long = 0,
    var world: String = "") : ProgramTypes{
    override fun toString(): String {
        var result: String = "\n\t"
        result += String.format("%-21s", "Name: ") + name + "\n\t"
        result += String.format("%-21s", "Parent Legion: ") + parentLegion + "\n\t"
        result += String.format("%-21s", "Marines count: ") + marinesCount.toString() + "\n\t"
        result += String.format("%-21s", "World: ") + world
        return result
    }

    override fun createSerializedInstance(br: BufferedReader, stream: Stream): JsonElement {
        @Serializable
        class Serializer() {
            var name: String? = null
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::name).isNotNull().isNotBlank()
                    }
                }
            var parentLegion: String? = null
            var marinesCount: Long? = null
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::marinesCount).isBetween(1, 1000)
                    }
                }
            var world: String? = null
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::world).isNotNull()
                    }
                }
        }
        //val br = BufferedReader(istream)
        val s = Serializer()
        if (stream != Stream.FILE)
            println("Запущен процесс ввода элемента Chapter.")
        while (true){
            try{
                if (stream == Stream.CONSOLE)
                    print("Введите значение поля name (тип String): ")
                val str = br.readLine()
                s.name = str.ifEmpty { null }
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля name. Пожалуйста, повторите попытку.")
                continue
            }
        }
        while (true){
            try{
                if (stream == Stream.CONSOLE)
                    print("Введите значение поля parentLegion (тип String): ")
                val str = br.readLine()
                s.parentLegion = str.ifEmpty { null }
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля parentLegion. Пожалуйста, повторите попытку.")
                continue
            }
        }
        while (true){
            try{
                if (stream == Stream.CONSOLE)
                    print("Введите значение поля marinesCount (тип Long): ")
                val str = br.readLine()
                s.marinesCount = if (str.isEmpty()) null else str.toLong()
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля marinesCount. Пожалуйста, повторите попытку.")
                continue
            }
        }
        while (true){
            try{
                if (stream == Stream.CONSOLE)
                    print("Введите значение поля world (тип String): ")
                val str = br.readLine()
                s.world = str.ifEmpty { null }
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля world. Пожалуйста, повторите попытку.")
                continue
            }
        }
        return Json.encodeToJsonElement(s)
    }

    override fun instance(): ProgramTypes {
        name = "name"
        parentLegion = "legion"
        marinesCount = 10
        world = "world"
        return this
    }

}