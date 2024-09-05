package types

import LocalDateTimeSerializer
import Stream
import exceptions.IllegalScriptFileException
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.encodeToJsonElement
import org.koin.core.qualifier.named
import org.valiktor.functions.isNotBlank
import org.valiktor.functions.isNotNull
import org.valiktor.functions.isPositive
import org.valiktor.validate
import java.io.BufferedReader
import java.time.LocalDateTime

@Serializable
class SpaceMarine(var name: String,
                  var coordinates: Coordinates,
                  var health: Double,
                  var category: AstartesCategory,
                  var weaponType: Weapon,
                  var meleeWeapon: MeleeWeapon,
                  var chapter: Chapter
) : ProgramTypes, Comparable<SpaceMarine> {
    var user: String = ""
    var id: Long = 1
    constructor (id: Long, name: String, coordinates: Coordinates, time: LocalDateTime, health: Double, category: AstartesCategory,
                    weaponType: Weapon, meleeWeapon: MeleeWeapon, chapter: Chapter) : this(name, coordinates, health, category,
                                                                                            weaponType, meleeWeapon, chapter){
                        this.id = id
                        this.creationDate = time
                    }
    @Serializable(with = LocalDateTimeSerializer::class)
    var creationDate: LocalDateTime? = LocalDateTime.now()
    constructor() : this("Name", Coordinates(1, 0.0f), 56.0, AstartesCategory.ASSAULT, Weapon.BOLTGUN, MeleeWeapon.MANREAPER, Chapter("Chapter", "Legion", 45, "World"))

    fun hasId(id: Long) : Boolean{
        return id == this.id
    }

    override fun compareTo(other: SpaceMarine): Int {
        return if(this.id > other.id){
            1
        } else if (this.id == other.id){
            0
        } else{
            -1
        }
    }

    fun update(other: SpaceMarine) {
        this.name = other.name
        this.coordinates = other.coordinates
        this.category = other.category
        this.health = other.health
        this.meleeWeapon = other.meleeWeapon
        this.chapter = other.chapter
        this.weaponType = other.weaponType
    }

    fun mwCompare(other: SpaceMarine): Int {
        return this.meleeWeapon.compareTo(other.meleeWeapon)
    }

    fun addUserName(user: String?) : SpaceMarine {
        if (user != null){
            this.user = user
        }
        return this
    }

    override fun toString(): String {
        var result: String = "\n"
        result += String.format("%-25s", "Id: ") + id + "\n"
        result += String.format("%-25s", "Creator: ") + user + "\n"
        result += String.format("%-25s", "Creation date: ") + creationDate + "\n"
        result += String.format("%-25s", "Name: ") + name + "\n"
        result += String.format("%-25s", "Coordinates: ") + coordinates.toString() + "\n"
        result += String.format("%-25s", "Health: ") + health.toString() + "\n"
        result += String.format("%-25s", "Category: ") + category.toString() + "\n"
        result += String.format("%-25s", "Weapon Type: ") + weaponType.toString() + "\n"
        result += String.format("%-25s", "Melee Weapon: ") + meleeWeapon.toString() + "\n"
        result += String.format("%-25s", "Chapter: ") + chapter.toString() + "\n"
        return result
    }

    override fun createSerializedInstance(br: BufferedReader, stream: Stream): JsonElement {
        @Serializable
        class Serializer(){
            var name: String? = null
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::name).isNotNull().isNotBlank()
                    }
                }
            var health: Double? = null
                set(value){
                    field = value
                    validate(this){
                        validate(Serializer::health).isNotNull().isPositive()
                    }
                }
            lateinit var coordinates: JsonElement
            lateinit var category: JsonElement
            lateinit var weaponType: JsonElement
            lateinit var meleeWeapon: JsonElement
            lateinit var chapter: JsonElement
        }
        val s = Serializer()
        if (stream != Stream.FILE)
            println("Запущен процесс ввода элемента SpaceMarine.")
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
                    print("Введите значение поля health (тип Double): ")
                val str = br.readLine()
                s.health = if (str.isEmpty()) null else str.toDouble()
                break
            } catch(e: Exception){
                if (stream == Stream.FILE){
                    throw IllegalScriptFileException()
                }
                println("Недопустимое значение поля health. Пожалуйста, повторите попытку.")
                continue
            }
        }
        s.coordinates = ProgramTypes.commonApp.koin.get<ProgramTypes>(named("Coordinates")).createSerializedInstance(br, stream)
        s.category = ProgramTypes.commonApp.koin.get<ProgramTypes>(named("AstartesCategory")).createSerializedInstance(br, stream)
        s.weaponType = ProgramTypes.commonApp.koin.get<ProgramTypes>(named("Weapon")).createSerializedInstance(br, stream)
        s.meleeWeapon = ProgramTypes.commonApp.koin.get<ProgramTypes>(named("MeleeWeapon")).createSerializedInstance(br, stream)
        s.chapter = ProgramTypes.commonApp.koin.get<ProgramTypes>(named("Chapter")).createSerializedInstance(br, stream)
        return Json.encodeToJsonElement(s)
    }

    override fun instance() : ProgramTypes{
        name = "name"
        coordinates = ProgramTypes.commonApp.koin.get<Coordinates>().instance() as Coordinates
        category = ProgramTypes.commonApp.koin.get<AstartesCategory>().instance() as AstartesCategory
        weaponType = ProgramTypes.commonApp.koin.get<Weapon>().instance() as Weapon
        meleeWeapon = ProgramTypes.commonApp.koin.get<MeleeWeapon>().instance() as MeleeWeapon
        chapter = ProgramTypes.commonApp.koin.get<Chapter>().instance() as Chapter
        return this
    }
}