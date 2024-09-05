import reactor.core.publisher.Flux
import service.gateway.GatewayLBService
import service.gateway.TCPChannelGateway

fun main(){
    val gateway: GatewayLBService = TCPChannelGateway(2222)
    gateway.start()
}