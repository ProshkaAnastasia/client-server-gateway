package modules

import deserializer.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val typeDeserializerModule = module{
    single<Deserializer>(named("String")){ StringDeserializer() }
    single<Deserializer>(named("SpaceMarine")){ SpaceMarineDeserializer() }
    single<Deserializer>(named("Int")){ IntDeserializer() }
    single<Deserializer>(named("Long")){ LongDeserializer() }
    single<Deserializer>(named("Double")){ DoubleDeserializer() }
};