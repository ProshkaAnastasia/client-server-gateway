package handler

import communication.ErrorResponse
import communication.Response
import communication.Request

abstract class AbstractHandler {
    abstract fun handleResponse(response: Response)
    abstract fun handleRequest(request: Request)
    abstract fun handleErrorResponse(errorResponse: ErrorResponse)
}