package communication

import communication.action.AbstractAction
import handler.AbstractHandler
import handler.AbstractServerHandler
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("demand_info")
class DemandInfo(val number: Int, val actions: ArrayList<AbstractAction>) : Message(), Comparable<DemandInfo> {
    override var sender: String? = null
    override var receiver: String? = null
    override fun apply(handler: AbstractHandler) {
        if (handler is AbstractServerHandler){
            handler.handleDemandInfo(this)
        }
    }

    override var attempt: Int = 1
    override fun compareTo(other: DemandInfo): Int {
        return if (this.number > other.number){
            1
        } else if (this.number == other.number){
            0
        } else {
            -1
        }
    }
}