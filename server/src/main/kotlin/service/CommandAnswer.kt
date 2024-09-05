package service

import communication.action.AbstractAction
import service.action.LocalServerAction

data class CommandAnswer(val ok: Boolean, val error: String? = null, val foreignActions: ArrayList<AbstractAction> = arrayListOf(), val localActions: ArrayList<LocalServerAction> = arrayListOf())