package handler

import communication.DemandInfo
import communication.action.*

abstract class AbstractServerHandler : AbstractHandler() {
    abstract fun handleExecution(action: Execution, key: String) : ActionResponse
    abstract fun handleDescription(action: Description) : ActionResponse
    abstract fun handleAuthorization(action: Authorization, key: String) : ActionResponse
    abstract fun handleRegistration(action: Registration) : ActionResponse
    abstract fun handleAdd(action: Add) : ActionResponse
    abstract fun handleDelete(action: Delete) : ActionResponse
    abstract fun handleUpdate(action: Update) : ActionResponse
    abstract fun handleClear(action: Clear) : ActionResponse
    abstract fun handleFullUpdate(action: FullUpdate) : ActionResponse
    abstract fun handleDemandInfo(info: DemandInfo)
}