package collection.saver

import types.SpaceMarine

abstract class CollectionSaver {
    abstract fun load() : MutableCollection<SpaceMarine>
    abstract fun save(collection: MutableCollection<SpaceMarine>)
    abstract fun insert(element: SpaceMarine, token: String) : Int
    abstract fun remove(element: SpaceMarine, token: String) : Int
    abstract fun clear(token: String) : Int
    abstract fun update(last: SpaceMarine, new: SpaceMarine, token: String) : Int
}