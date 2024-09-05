package service.action

import communication.DemandInfo
import service.server.AbstractServer

class SendDemandInfo(private val info : DemandInfo) : LocalServerAction() {
    override fun execute(server: AbstractServer) {
        println("Sending demand info")
        server.sendMessage(info)
    }
}