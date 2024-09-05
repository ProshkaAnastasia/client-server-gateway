package types

import Stream
import exceptions.IllegalScriptFileException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import types.ProgramTypes
import java.io.BufferedReader

@Serializable
enum class AstartesCategory : ProgramTypes {
    ASSAULT,
    SUPPRESSOR,
    LIBRARIAN,
    CHAPLAIN,
    APOTHECARY;

    override fun createSerializedInstance(br: BufferedReader, stream: Stream): JsonElement {
        //val br = BufferedReader(istream)
        if (stream != Stream.FILE) {
            println("Запущен процесс ввода элемента типа AstartesCategory.")
        }
        while (true){
            if (stream != Stream.FILE) {
                print("Возможные значения: ")
                values().forEach { print("$it ") }
            }
            val str = br.readLine()
            try{
                if (values().find{it == valueOf(str) } != null)
                    return Json.encodeToJsonElement(valueOf(str))
            } catch(e: Exception){
                if (stream == Stream.FILE)
                    throw IllegalScriptFileException()
                println("Недопустимое значение элемента. Пожалуйста, попробуйте снова.")
            }
        }
    }

    override fun instance(): ProgramTypes {
        return CHAPLAIN
    }
}