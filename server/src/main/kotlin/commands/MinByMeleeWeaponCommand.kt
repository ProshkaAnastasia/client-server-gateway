package commands

import collection.CollectionHandler
import user.Status
import communication.action.AbstractAction
import communication.action.Output
import service.CommandAnswer
import types.SpaceMarine
import user.User

class MinByMeleeWeaponCommand : Command() {
    override val callStatus: HashSet<Status> = hashSetOf(Status.ADMIN, Status.STANDARD)
    override val name: String = "min_by_melee_weapon"
    override val description: String = "выводит элемент коллекции, значение поля meleeWeapon которого является минимальным."
    override val primitiveTypes: ArrayList<String> = arrayListOf()
    override val referenceTypes: ArrayList<String> = arrayListOf()
    override fun execute(token: String, arguments: ArrayList<Any>): CommandAnswer {
        val list = CollectionHandler.sort(SpaceMarine::mwCompare)
        return if (list.isEmpty()){
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, "Коллекция пуста.")))
        } else {
            CommandAnswer(true, foreignActions = arrayListOf(Output(true, list.first())))
        }
    }
}