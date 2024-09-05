package handler

import communication.action.ActionResponse
import communication.action.AddCommand
import communication.action.Output
import communication.action.RenewCommands

abstract class AbstractClientHandler : AbstractHandler() {
    abstract fun handleOutput(request: Output) : ActionResponse
    abstract fun handleAddCommand(request: AddCommand) : ActionResponse
    abstract fun handleRenewCommands(request: RenewCommands) : ActionResponse
}