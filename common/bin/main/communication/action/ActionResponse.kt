package communication.action

import communication.Error

class ActionResponse(var ok: Boolean, var error: Error? = null, var actions: ArrayList<AbstractAction>? = null) {

}