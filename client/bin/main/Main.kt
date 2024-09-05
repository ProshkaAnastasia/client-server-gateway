import communication.Message
import communication.MetaMessage
import communication.Request
import communication.action.Description
import communication.action.Execution
import reactor.core.publisher.Flux
import service.client.Client
import service.client.TcpStreamClient
import types.SpaceMarine
import user.Status
import user.User
import java.net.InetAddress

fun main(){
    val client: Client = TcpStreamClient(InetAddress.getLocalHost(), 2222)
    client.run()
    val request: Message = Request(Description(Status.STANDARD), Execution("hjdjkh",
        "remove", SpaceMarine(), 1, 5.4, "hello"))
    val m = MetaMessage(request)
    var str = m.content
    val mm = MetaMessage(m.content)
}