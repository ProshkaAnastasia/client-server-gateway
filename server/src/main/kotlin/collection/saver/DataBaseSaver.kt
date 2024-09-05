package collection.saver

import exceptions.DataBaseException
import org.apache.commons.dbcp.BasicDataSource
import service.database.UserHandler
import types.*
import java.sql.*
import javax.xml.crypto.Data

class DataBaseSaver(host: String, port: Int, db: String, user: String, password: String): CollectionSaver() {
    private val pool = BasicDataSource()
    //var connection: Connection = DriverManager.getConnection("jdbc:postgresql://$host:$port/$db", user, password)

    init{
        DriverManager.registerDriver(org.postgresql.Driver())
        pool.url = "jdbc:postgresql://$host:$port/$db"
        pool.username = "anastasiapronina"
        pool.password = "89111984456Hp"
        pool.maxActive = 32
        pool.defaultAutoCommit = false
        var connection = getConnection()
        //setAutoCommitMode(false)
        connection.transactionIsolation = Connection.TRANSACTION_SERIALIZABLE
        var st = connection.prepareStatement("""
            DO $$
            BEGIN
                IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'astartes_category') THEN
                    CREATE TYPE astartes_category AS ENUM ('ASSAULT', 'SUPPRESSOR', 'LIBRARIAN', 'CHAPLAIN', 'APOTHECARY');
                END IF;
            END $$;
        """)
        st.execute()
        st = connection.prepareStatement("""
            DO $$
            BEGIN
                IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'weapon') THEN
                    CREATE TYPE weapon AS ENUM ('BOLTGUN', 'COMBI_FLAMER', 'GRAV_GUN', 'HEAVY_FLAMER', 'MISSILE_LAUNCHER');
                END IF;
            END $$;
        """)
        st.execute()
        st = connection.prepareStatement("""
            DO $$
            BEGIN
                IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'melee_weapon') THEN
                    CREATE TYPE melee_weapon AS ENUM ('CHAIN_SWORD', 'POWER_SWORD', 'MANREAPER', 'POWER_BLADE', 'POWER_FIST');
                END IF;
            END $$;
        """)
        st.execute()
        st = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS proga.users(
                id bigserial PRIMARY KEY,
                nickname varchar(100) UNIQUE NOT NULL,
                pwd text NOT NULL,
                current_key text 
            )
        """)
        st.execute()
        st = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS proga.coordinates(
                id bigserial PRIMARY KEY,
                x bigint NOT NULL CHECK (x < 865),
                y double precision CHECK (y < 799)
            )
        """)
        st.execute()
        st = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS proga.chapter(
                id bigserial PRIMARY KEY,
                name text NOT NULL,
                parent_legion text,
                marines_count bigint NOT NULL CHECK (marines_count > 0),
                world text NOT NULL
            )
        """)
        st.execute()
        st = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS proga.space_marine(
                id bigserial PRIMARY KEY,
                user_id bigserial REFERENCES proga.users (id) ON DELETE CASCADE,
                name text NOT NULL CHECK (name <> ''),
                coordinates bigint NOT NULL REFERENCES proga.coordinates (id) ON DELETE RESTRICT,
                creation_date timestamp DEFAULT NOW(),
                health double precision NOT NULL CHECK (health > 0),
                category astartes_category NOT NULL,
                weapon_type weapon NOT NULL,
                melee_weapon melee_weapon NOT NULL,
                chapter bigint NOT NULL REFERENCES proga.chapter (id) ON DELETE RESTRICT
            )
        """)
        st.execute()
        st = connection.prepareStatement("""
            CREATE TABLE IF NOT EXISTS proga.collection_info(
                update_number integer DEFAULT 0
            )
        """.trimIndent())
        connection.commit()
        st.close()
    }

    fun setAutoCommitMode(connection: Connection, status: Boolean){
        connection.autoCommit = status
    }

    fun incUpdateNumber(connection: Connection): ResultSet {
        var st = connection.prepareStatement("""
                    UPDATE proga.collection_info 
                    SET update_number = MOD(update_number + 1, 10000)
                """.trimIndent())
        st.execute()
        st = connection.prepareStatement("""
                    SELECT update_number FROM proga.collection_info LIMIT 1
                """.trimIndent()
        )
        return st.executeQuery()
    }

    fun getConnection() : Connection {
        return pool.connection
    }

    override fun load(): MutableCollection<SpaceMarine> {
        var collection: MutableCollection<SpaceMarine> = ArrayList()
        var connection = getConnection()
        var st = connection.prepareStatement("""
            SELECT space_marine.id, space_marine.user_id, space_marine.name, coordinates.x, coordinates.y, space_marine.creation_date, 
            space_marine.health, space_marine.category, space_marine.weapon_type, space_marine.melee_weapon, chapter.name as chapter_name,
            chapter.parent_legion, chapter.marines_count, chapter.world, users.nickname FROM proga.space_marine as space_marine
            JOIN proga.chapter as chapter ON chapter.id = space_marine.chapter
            JOIN proga.coordinates as coordinates ON coordinates.id = space_marine.coordinates
            JOIN proga.users as users ON users.id = space_marine.user_id
        """, Statement.RETURN_GENERATED_KEYS)
        var result = st.executeQuery()
        while (result.next()){
            var id = result.getLong("id")
            var name = result.getString("name")
            var coordinates = Coordinates(result.getLong("x"), result.getFloat("y"))
            var time = result.getTimestamp("creation_date").toLocalDateTime()
            var health = result.getDouble("health")
            var category = AstartesCategory.valueOf(result.getString("category"))
            var weapon_type = Weapon.valueOf(result.getString("weapon_type"))
            var melee_weapon = MeleeWeapon.valueOf(result.getString("melee_weapon"))
            var chapter = Chapter(result.getString("chapter_name"), result.getString("parent_legion"), result.getLong("marines_count"), result.getString("world"))
            var user = result.getString("nickname")
            collection.add(SpaceMarine(id, name, coordinates, time, health, category, weapon_type, melee_weapon, chapter).addUserName(user))
        }
        return collection
    }

    override fun save(collection: MutableCollection<SpaceMarine>) {

    }

    override fun insert(element: SpaceMarine, token: String) : Int {
        val connection = getConnection()
        val savePoint = connection.setSavepoint("BeforeInsert")
        try{
            var ch_id = getIdByChapter(connection, element.chapter)
            var co_id = getIdByCoordinates(connection, element.coordinates)
            var u_id: Long? = UserHandler.findUserIdByToken(token)
                ?: throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой (Пользователь не имеет прав на добавление элмента в коллекцию)")
            if (ch_id == null){
                insertChapter(connection, element.chapter)
                ch_id = getIdByChapter(connection, element.chapter)
            }
            if (co_id == null){
                insertCoordinates(connection, element.coordinates)
                co_id = getIdByCoordinates(connection, element.coordinates)
            }
            if ((ch_id == null) or (co_id == null)){
                throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой")
            }
            var st = connection.prepareStatement("""
                INSERT INTO proga.space_marine (name, coordinates, health, category, weapon_type, melee_weapon, chapter, user_id)
                VALUES ('${element.name}', $co_id, ${element.health}, '${element.category}', '${element.weaponType}', '${element.meleeWeapon}', ${ch_id}, ${u_id})
            """)
            if (st.executeUpdate() == 0){
                throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой")
            }
            var row = incUpdateNumber(connection)
            if (row.next())
                connection.commit()
            else throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой")
            element.user = UserHandler.findUserById(u_id!!)!!
            st = connection.prepareStatement("""
                SELECT max(id) FROM proga.space_marine
            """.trimIndent())
            var r = st.executeQuery()
            r.next()
            element.id = r.getLong(1)
            return row.getInt(1)
        } catch (e : SQLException){
            connection.rollback(savePoint)
            throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой (${e.message})")
        } finally{
            connection.commit()
            connection.close()
        }
    }

    override fun remove(element: SpaceMarine, token: String): Int {
        val connection = getConnection()
        val savePoint = connection.setSavepoint("BeforeRemoving")
        try {
            val uId = UserHandler.findUserIdByToken(token)
                ?: throw DataBaseException("Удаление элемента из коллекции завершилось ошибкой")
            var st = connection.prepareStatement(
                """
                DELETE FROM proga.space_marine
                WHERE id = ${element.id} AND user_id = $uId
            """.trimIndent()
            )
            if (st.executeUpdate() == 0) {
                throw DataBaseException("Удаление элемента из коллекции завершилось ошибкой. Элемента не существует или у пользователя недостаточно прав для совершения операции")
            }
            var row = incUpdateNumber(connection)
            if (row.next())
                connection.commit()
            else throw DataBaseException("Удаление элемента из коллекции завершилось ошибкой")
            return row.getInt(1)
        } catch (e : SQLException){
            connection.rollback(savePoint)
            throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой (${e.message})")
        } finally{
            connection.commit()
            connection.close()
        }
    }

    override fun clear(token: String): Int {
        val connection = getConnection()
        setAutoCommitMode(connection, false)
        val savePoint = connection.setSavepoint("BeforeClearing")
        try{
            var st = connection.prepareStatement("""
                DELETE FROM proga.space_marine;
                DELETE FROM proga.coordinates;
                DELETE FROM proga.chapter;
        """)
            st.execute()
            st = connection.prepareStatement("""
                    UPDATE proga.collection_info 
                    SET update_number = (SELECT update_number FROM proga.collection_info LIMIT 1) + 1;
                    SELECT update_number FROM proga.collection_info LIMIT 1
                """.trimIndent())
            var row = st.executeQuery()
            if (row.next())
                connection.commit()
            else throw DataBaseException("Добавление элемента в коллекцию завершилось ошибкой")
            return row.getInt(1)
        } catch (e : SQLException){
            connection.rollback(savePoint)
            throw DataBaseException(e.message)
        }
    }

    fun insertChapter(connection: Connection, chapter: Chapter) : Int {
        var st = connection.prepareStatement("""
                INSERT INTO proga.chapter (name, parent_legion, marines_count, world)
                VALUES (?, ?, ?, ?)
            """)
        st.setString(1, chapter.name)
        if (chapter.parentLegion != null){
            st.setString(2, chapter.parentLegion)
        } else {
            st.setNull(2, Types.VARCHAR)
        }
        st.setLong(3, chapter.marinesCount)
        st.setString(4, chapter.world)
        try{
            return st.executeUpdate()
        }catch (e : SQLException){
            println("Element can not be added")
            throw DataBaseException("")
        }
    }

    fun insertCoordinates(connection: Connection, coordinates: Coordinates) : Int {
        var st = connection.prepareStatement("""
                INSERT INTO proga.coordinates (x, y)
                VALUES (${coordinates.x}, ${coordinates.y})
            """)
        try {
            return st.executeUpdate()
        } catch (e : SQLException){
            println("Element can not be added: ${e.message}")
            throw DataBaseException("")
        }
    }

    fun getIdByChapter(connection: Connection, chapter: Chapter) : Long? {
        val parentLegion = if (chapter.parentLegion == null) {
            "parent_legion IS NULL"
        } else {
            "parent_legion = '${chapter.parentLegion}'"
        }
        setAutoCommitMode(connection, false)
        var st = connection.prepareStatement("""
                SELECT id FROM proga.chapter 
                WHERE name = ? AND $parentLegion AND marines_count = ? AND world = ?
            """.trimIndent())
        st.setString(1, chapter.name)
        st.setLong(2, chapter.marinesCount)
        st.setString(3, chapter.world)
        var row = st.executeQuery()
        return if (row.next()){
            row.getLong(1)
        } else{
            null
        }
    }

    fun getIdByCoordinates(connection: Connection, coordinates: Coordinates) : Long? {
        var st = connection.prepareStatement("""
                SELECT id FROM proga.coordinates 
                WHERE x = ${coordinates.x} AND y = ${coordinates.y}
            """)
        var row = st.executeQuery()
        return if (row.next()){
            row.getLong(1)
        } else{
            null
        }
    }

    override fun update(last: SpaceMarine, new: SpaceMarine, token: String): Int {
        TODO("Not yet implemented")
    }

}