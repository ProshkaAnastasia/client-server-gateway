package modules

import org.koin.core.qualifier.named
import org.koin.dsl.module
import types.*

val programTypesModule = module{
    single<ProgramTypes>(named("Weapon")){ Weapon.MISSILE_LAUNCHER }
    single<ProgramTypes>(named("MeleeWeapon")){ MeleeWeapon.MANREAPER }
    single<ProgramTypes>(named("AstartesCategory")){ AstartesCategory.ASSAULT}
    single<ProgramTypes>(named("Chapter")){ Chapter("Chapter", "Legion", 1, "World") }
    single<ProgramTypes>(named("Coordinates")){ Coordinates(5, 10f) }
    single<ProgramTypes>(named("SpaceMarine")){ SpaceMarine("SpaceMarine", get(), 6.0, get(), get(), get(), get()) }
}
