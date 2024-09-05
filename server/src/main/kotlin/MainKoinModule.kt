import commands.*
import deserializer.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.StringFormat
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import types.SpaceMarine
import java.util.*

@OptIn(ExperimentalSerializationApi::class)
val formatModule = module{
    single<StringFormat>(named("Json")){ Json }
    single<StringFormat>(named("Csv")){ Csv{hasHeaderRecord = true} }
}

val commandModule = module{
    factory<Command>(named("add")){ AddCommand() }
    factory<Command>(named("AddCommand")){ AddCommand() }
    factory<Command>(named("info")){ InfoCommand() }
    factory<Command>(named("InfoCommand")){ InfoCommand() }
    factory<Command>(named("show")){ ShowCommand() }
    factory<Command>(named("ShowCommand")){ ShowCommand() }
    factory<Command>(named("update")){ UpdateCommand() }
    factory<Command>(named("UpdateCommand")){ UpdateCommand() }
    factory<Command>(named("remove_by_id")){ RemoveByIdCommand() }
    factory<Command>(named("RemoveByIdCommand")){ RemoveByIdCommand() }
    factory<Command>(named("add_new_command")){ AddNewCommand() }
    factory<Command>(named("print_command")){ PrintCommand() }
    factory<Command>(named("min_by_melee_weapon")){ MinByMeleeWeaponCommand() }
    factory<Command>(named("sum_by_health")){ SumByHealthCommand() }
    factory<Command>(named("clear")){ ClearCommand() }
}

val MutableCollectionModule = module{
    factory<MutableCollection<SpaceMarine>>(named("ArrayList")){ (collection: MutableCollection<SpaceMarine>) -> ArrayList(collection) }
    factory<MutableCollection<SpaceMarine>>(named("LinkedList")){ (collection: MutableCollection<SpaceMarine>) -> LinkedList(collection) }
    factory<MutableCollection<SpaceMarine>>(named("HashSet")){ (collection: MutableCollection<SpaceMarine>) -> HashSet(collection) }
}

val koinApp = koinApplication{
    modules(formatModule, MutableCollectionModule, commandModule, modules.typesModule)
}