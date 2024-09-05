package service.database

data class FieldValue(val name: String, val type: String, val constraints: String = ""){
    override fun toString() : String{
        return "$name $type $constraints"
    }
}