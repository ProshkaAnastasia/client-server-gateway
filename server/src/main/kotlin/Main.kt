import collection.CollectionHandler
import collection.saver.DataBaseSaver
import org.koin.core.qualifier.named
import service.CommandKeeper
import service.database.DataBase
import service.database.FieldValue
import service.server.TCPStreamServer
import types.ProgramTypes
import types.SpaceMarine
import java.net.InetAddress
import service.database.UserHandler

fun main(){
    val server = TCPStreamServer(InetAddress.getLocalHost(), 2222)
    server.run()
}