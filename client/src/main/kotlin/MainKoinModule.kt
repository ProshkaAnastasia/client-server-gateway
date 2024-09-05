
import modules.programTypesModule
import modules.typeDeserializerModule
import modules.typesModule
import org.koin.core.context.startKoin

val koinApp = startKoin{
    modules(typeDeserializerModule, programTypesModule, typesModule)
}
