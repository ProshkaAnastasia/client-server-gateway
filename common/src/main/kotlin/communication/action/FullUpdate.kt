package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler

class FullUpdate : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleFullUpdate(this)
        }
        TODO()
    }
}