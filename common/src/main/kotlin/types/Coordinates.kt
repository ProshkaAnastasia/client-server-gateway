package types

import Stream
import exceptions.IllegalScriptFileException
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.valiktor.functions.isLessThanOrEqualTo
import org.valiktor.functions.isNotNull
import org.valiktor.validate
import java.io.BufferedReader

@Serializable
class Coordinates (var x: Long, var y: Float) : ProgramTypes {

    override fun toString(): String {
        var result: String = "\n\t"
        result += String.format("%-21s", "x: ") + x.toString() + "\n\t"
        result += String.format("%-21s", "y: ") + y.toString()
        return result
    }

    override fun createSerializedInstance(br: BufferedReader, stream: Stream): JsonElement {
        @Serializable
        class Serializer() {
            var x: Long? = 0
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::x).isNotNull().isLessThanOrEqualTo(864)
                    }
                }
            var y: Float? = 0.0f
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::y).isLessThanOrEqualTo(798F)
                    }
                }
        }
        //val br = BufferedReader(istream)
        val s = Serializer()
        if (stream != Stream.FILE)
            println("Запущен процесс ввода элемента Coordinates.")
        while (true){
            try{
                if (stream == Stream.CONSOLE)
                    print("Введите значение поля x (тип Long): ")
                val str = br.readLine()
                s.x = if (str.isEmpty()) null else str.toLong()
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля x. Пожалуйста, повторите попытку.")
                continue
            }
        }
        while (true){
            try{
                if (stream == Stream.CONSOLE)
                    print("Введите значение поля y (тип Float): ")
                val str = br.readLine()
                s.y = if (str.isEmpty()) null else str.toFloat()
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля y. Пожалуйста, повторите попытку.")
                continue
            }
        }
        return Json.encodeToJsonElement(s)
    }

    override fun instance(): ProgramTypes {
        x = 5
        y = 6.0f
        return this
    }

}