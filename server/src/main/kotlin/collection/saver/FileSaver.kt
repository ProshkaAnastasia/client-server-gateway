package collection.saver

import exceptions.NotExistingFile
import exceptions.NotReadableFile
import koinApp
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.koin.core.qualifier.named
import types.SpaceMarine
import java.io.*
import java.util.*

class FileSaver(var fileName: String, var format: String = "Csv"): CollectionSaver() {
    override fun load(): MutableCollection<SpaceMarine> {
        val file = File(fileName)
        var collection: MutableCollection<SpaceMarine> = arrayListOf()
        val f = koinApp.koin.get<StringFormat>(named(format))
        var ifstream: InputStreamReader
        try{
            ifstream = InputStreamReader(FileInputStream(file))
        } catch (e: FileNotFoundException){
            throw NotExistingFile()
        } catch (e: SecurityException){
            throw NotReadableFile()
        }
        var text = ifstream.readText()
        if (text.isNotEmpty())
            collection = LinkedList(f.decodeFromString<ArrayList<SpaceMarine>>(text))
        ifstream.close()
        return collection
    }

    override fun save(collection: MutableCollection<SpaceMarine>) {
        var file = File(fileName)
        var ofstream: OutputStreamWriter
        try{
            ofstream = OutputStreamWriter(FileOutputStream(file))
        } catch (e: FileNotFoundException){
            file = File(file.nameWithoutExtension + "." + format.lowercase())
            ofstream = OutputStreamWriter(FileOutputStream(file))
        } catch (e: SecurityException){
            file = File(file.nameWithoutExtension + "." + format.lowercase())
            ofstream = OutputStreamWriter(FileOutputStream(file))
        }
        val f = koinApp.koin.get<StringFormat>(named(format))
        ofstream.write(f.encodeToString(ArrayList(collection)))
        ofstream.close()
    }

    override fun insert(element: SpaceMarine, token: String): Int {
        TODO("Not yet implemented")
    }

    override fun remove(element: SpaceMarine, token: String): Int {
        TODO("Not yet implemented")
    }

    override fun clear(token: String): Int {
        TODO("Not yet implemented")
    }

    override fun update(last: SpaceMarine, new: SpaceMarine, token: String): Int {
        TODO("Not yet implemented")
    }


}