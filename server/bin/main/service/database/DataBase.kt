package service.database

import java.sql.*

class DataBase(val host: String, val port: Int, val db: String, val user: String, val password: String) {
    var connection: Connection = DriverManager.getConnection("jdbc:postgresql://$host:$port/$db", user, password)
    fun selectData(fields: ArrayList<String>, table: String, condition: String = "", joins: ArrayList<String>, order: String = "", extra: String = "") : ResultSet {
        var field = fields.reduce{a, b -> "$a, $b"}
        var join = joins.reduce{a, b -> "$a \n $b"}
        var st: PreparedStatement = connection.prepareStatement("""
            SELECT $field FROM $table 
            $condition 
            $join
            $order
            $extra
            """)
        var result = st.executeQuery()
        st.close()
        return result
    }
    fun insertData(table: String, fields: ArrayList<String>, values: ArrayList<Any>){
        var field = fields.reduce{a, b -> "$a, $b"}
        var value = unionArguments(values)
        var st = connection.prepareStatement("INSERT INTO $table ($field) VALUES ($value)")
        st.executeUpdate()
        st.close()
    }
    fun createTable(table: String, fields: ArrayList<FieldValue>, constraints: String = ""){
        var field = fields.map{it.toString()}.reduce{a, b -> "$a,\n$b"}
        var constraint = constraints
        if (constraint.isNotEmpty()){
            constraint = ",$constraints"
        }
        var st = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS $table (
                $field
                $constraints
            )
        """)
        st.execute()
        st.close()
    }
    private fun unionArguments(arguments: ArrayList<Any>) : String {
        return arguments.map{
            when(it){
                is Boolean, is Int, is Long, is Float, is Double -> "$it"
                else -> "'$it'"
            }
        }.reduce{a, b -> "$a, $b"}
    }
}