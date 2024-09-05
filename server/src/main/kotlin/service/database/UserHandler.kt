package service.database

import org.apache.commons.dbcp.BasicDataSource
import user.User
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object UserHandler {
    var adminKey = "key"
    val pool = BasicDataSource()
    init{
        DriverManager.registerDriver(org.postgresql.Driver())
        pool.url = "jdbc:postgresql://localhost:5432/anastasiapronina"
        pool.username = "anastasiapronina"
        pool.password = "89111984456Hp"
        pool.defaultAutoCommit = false
        pool.maxActive = 32
    }
    fun getConnection() : Connection {
        return pool.connection
    }
    //val connection: Connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/anastasiapronina", "anastasiapronina", "89111984456Hp")
    fun findUser(user: User) : Boolean{
        val connection = getConnection()
        var st = connection.prepareStatement("""
            SELECT count(*) FROM proga.users
            WHERE nickname = ${user.login}
        """)
        var res = st.executeQuery()
        st.close()
        connection.commit()
        connection.close()
        return res.fetchSize != 0
    }
    fun registerUser(user: User) : Boolean{
        val connection = getConnection()
        var inserted_rows: Int = 0
        var st = connection.prepareStatement("""
            INSERT INTO proga.users (nickname, pwd)
            VALUES (?, ?)
        """)
        st.setString(1, user.login)
        st.setString(2, hashPassword(user.password))
        try {
            inserted_rows = st.executeUpdate()
        } finally{
            st.close()
        }
        connection.commit()
        connection.close()
        return inserted_rows == 1
    }
    fun authorizeUser(user: User, key: String) : Boolean{
        val connection = getConnection()
        var updated_rows: Int
        println(key)
        var st = connection.prepareStatement("""
            UPDATE proga.users
            SET current_key = '$key'
            WHERE nickname = '${user.login}' AND pwd = '${user.password}'
        """)
        try {
            updated_rows = st.executeUpdate()
        } finally{
            st.close()
        }
        connection.commit()
        connection.close()
        return updated_rows == 1
    }
    fun deactivateUser(key: String) : Boolean {
        val connection = getConnection()
        var updated_rows: Int
        var st = connection.prepareStatement("""
            UPDATE proga.users
            SET current_key = NULL
            WHERE current_key = ?
        """)
        st.setString(1, key)
        try {
            updated_rows = st.executeUpdate()
        } finally{
            st.close()
        }
        connection.commit()
        connection.close()
        return updated_rows == 1
    }
    fun hashPassword(pwd: String) : String{
        return pwd
    }
    fun getAllActiveUsers() : ArrayList<String> {
        val connection = getConnection()
        var answer = ArrayList<String>()
        var st = connection.prepareStatement("""
            SELECT current_key FROM proga.users
            WHERE current_key IS NOT NULL
        """)
        var result = st.executeQuery()
        while (result.next()){
            answer.add(result.getString(1))
        }
        return answer
    }
    fun findUserIdByToken(token: String) : Long? {
        val connection = getConnection()
        var st = connection.prepareStatement("""
            SELECT id FROM proga.users
            WHERE current_key = '$token'
        """)
        var res = st.executeQuery()
        res.next()
        return try {
            res.getLong("id")
        } catch (e : SQLException){
            null
        }
    }
    fun findUserById(id: Long) : String? {
        val connection = getConnection()
        var st = connection.prepareStatement("""
            SELECT nickname FROM proga.users
            WHERE id = $id
        """)
        var res = st.executeQuery()
        res.next()
        return try {
            res.getString("nickname")
        } catch (e : SQLException){
            null
        } finally{
            connection.close()
        }
    }
    fun getUpdateNumber() : Int {
        val connection = getConnection()
        var st = connection.prepareStatement("""
            SELECT update_number FROM proga.collection_info LIMIT 1
        """)
        var res = st.executeQuery()
        res.next()
        //connection.close()
        try{
            return res.getInt("update_number") + 1
        } finally{
            connection.close()
        }
    }
}