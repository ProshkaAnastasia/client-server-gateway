package collection

import collection.saver.CollectionSaver
import collection.saver.DataBaseSaver
import exceptions.DataBaseException
import exceptions.NotExistingElement
import koinApp
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import types.SpaceMarine
import java.sql.SQLException
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.BlockingDeque
import java.util.concurrent.CopyOnWriteArrayList

object CollectionHandler {
    var collection: MutableCollection<SpaceMarine> = LinkedList()
    var saver: CollectionSaver = DataBaseSaver("localhost", 5432, "anastasiapronina", "anastasiapronina", "89111984456Hp")
    private val initializationDate: LocalDateTime = LocalDateTime.now()
    init{
        try{
            collection = LinkedList(saver.load())
        } catch(e : Exception){
            println(e.message)
        }
    }
    fun getCollectionType() : String? {
        return collection::class.simpleName
    }
    fun changeCollectionType(type: String){
        collection = koinApp.koin.get(named(type)){ parametersOf(collection) }
    }
    fun getInfo() : ArrayList<Any> {
        val result = ArrayList<Any>()
        result.add("Тип коллекции - ${getCollectionType()}")
        result.add("Дата инициализации - $initializationDate")
        result.add("Размер коллекции - ${collection.size}")
        return result
    }
    fun add(element: SpaceMarine, token: String) : Int {
        try{
            val number = saver.insert(element, token)
            //collection.add(element)
            return number
        } catch (e : SQLException){
            throw DataBaseException(e.message)
        }
    }
    fun remove(element: SpaceMarine, token: String) : Int {
        try{
            val number = saver.remove(element, token)
            //collection.remove(element)
            return number
        } catch (e : SQLException){
            throw DataBaseException(e.message)
        }
    }
    fun clear(token: String) : Int{
        try{
            val number = saver.clear(token)
            //collection.clear()
            return number
        } catch (e : SQLException){
            throw DataBaseException(e.message)
        }
    }
    fun remove(function: (SpaceMarine) -> Boolean, token: String) : Int {
        val element = collection.find(function) ?: throw NotExistingElement()
        try{
            return remove(element, token)
        } catch (e : SQLException){
            throw DataBaseException(e.message)
        }
    }
    fun update(function: (SpaceMarine) -> Boolean, new: SpaceMarine, token: String) : Int {
        val last = collection.find(function) ?: throw NotExistingElement()
        return update(last, new, token)
    }
    @Synchronized
    fun update(last: SpaceMarine, new: SpaceMarine, token: String) : Int {
        try{
            val number = saver.update(last, new, token)
            //last.update(new)
            return number
        } catch (e : SQLException){
            throw DataBaseException(e.message)
        }
    }
    @Synchronized
    fun localAdd(element: SpaceMarine){
        collection.add(element)
    }
    @Synchronized
    fun localDelete(element: SpaceMarine){
        var e = collection.find{
            it.id == element.id
        }
        collection.remove(e)
    }
    @Synchronized
    fun localUpdate(element: SpaceMarine, new: SpaceMarine){
        val last = collection.find{ a -> a.id == element.id}
        last!!.update(new)
    }
    fun find(function: (SpaceMarine) -> Boolean) : SpaceMarine {
        return collection.find(function) ?: throw NotExistingElement()
    }
    fun sort(function: (SpaceMarine, SpaceMarine) -> Int) : List<SpaceMarine> {
        return collection.sortedWith(function)
    }
    val a = CopyOnWriteArrayList<String>()

}


//Lock-Free системы
//Корутины
//Java Memory Model