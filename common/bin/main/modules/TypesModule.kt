package modules

import org.koin.core.qualifier.named
import org.koin.dsl.module
import types.*

val typesModule = module{
    single{ Weapon.MISSILE_LAUNCHER }
    single{ MeleeWeapon.MANREAPER }
    single{ AstartesCategory.ASSAULT}
    single{ Chapter("Chapter", "Legion", 1, "World") }
    single{ Coordinates(5, 10f) }
    single{ SpaceMarine("SpaceMarine", get(), 6.0, get(), get(), get(), get()) }
}