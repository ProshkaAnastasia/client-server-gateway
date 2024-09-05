package service

import communication.ErrorResponse
import communication.Request
import communication.Response
import communication.action.ActionResponse
import communication.action.AddCommand
import communication.action.Output
import communication.action.RenewCommands
import handler.AbstractClientHandler
import service.client.Client

class ClientMessageHandler(val client: Client) : AbstractClientHandler() {
    override fun handleOutput(request: Output) : ActionResponse {
        print(request.color)
        request.arguments.forEach(::println)
        print(Colors.RESET)
        return ActionResponse(true)
    }

    override fun handleAddCommand(request: AddCommand) : ActionResponse {
        CommandApproveMaker.addCommand(request.command)
        return ActionResponse(true)
    }

    override fun handleRenewCommands(request: RenewCommands) : ActionResponse {
        CommandApproveMaker.updateCommands(request.commands)
        return ActionResponse(true)
    }

    override fun handleResponse(response: Response){
        if (!response.ok){
            if (response.totalError != null){
                println(response.totalError)
            }
            response.errors?.forEach{println(it.message)}
        }
        var key: String = response.sender ?: "/localhost:2222"
        var warnings = response.actions?.map{it.apply(this, key)}?.filter{ !it.ok }
        if (!warnings.isNullOrEmpty()){
            //client.sendResponse(Response(false, response.key))
        }
    }

    override fun handleRequest(request: Request)  {
        TODO("Not yet implemented")
    }

    override fun handleErrorResponse(errorResponse: ErrorResponse) {
        var key: String = errorResponse.sender ?: "/localhost:2222"
        errorResponse.actions?.forEach{it.apply(this, key)}
    }
}