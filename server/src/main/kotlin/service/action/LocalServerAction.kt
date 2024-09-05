package service.action

import service.server.AbstractServer

abstract class LocalServerAction()  {
    abstract fun execute(server: AbstractServer)
}