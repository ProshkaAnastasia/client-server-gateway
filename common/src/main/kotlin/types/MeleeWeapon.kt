package types

import Stream
import exceptions.IllegalScriptFileException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import java.io.BufferedReader

@Serializable
enum class MeleeWeapon : ProgramTypes{
    CHAIN_SWORD,
    POWER_SWORD,
    MANREAPER,
    POWER_BLADE,
    POWER_FIST;

    override fun createSerializedInstance(br: BufferedReader, stream: Stream): JsonElement {
        if (stream != Stream.FILE) {
            println("Запущен процесс ввода элемента типа MeleeWeapon.")
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
        return MANREAPER
    }
}