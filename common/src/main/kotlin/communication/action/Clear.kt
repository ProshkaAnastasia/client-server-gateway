package communication.action

import handler.AbstractHandler
import handler.AbstractServerHandler

class Clear : AbstractAction() {
    override fun apply(handler: AbstractHandler, key: String): ActionResponse {
        if (handler is AbstractServerHandler){
            return handler.handleClear(this)
        }
        TODO()
    }
}