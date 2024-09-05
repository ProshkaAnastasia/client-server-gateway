package types

import Stream
import kotlinx.serialization.json.JsonElement
import modules.deserializerModule
import modules.programTypesModule
import modules.typesModule
import org.koin.dsl.koinApplication
import java.io.BufferedReader

sealed interface ProgramTypes{
    companion object{
        val commonApp = koinApplication{
            modules(programTypesModule, deserializerModule, typesModule)
        }
    }
    fun createSerializedInstance(br: BufferedReader, stream: Stream = Stream.CONSOLE) : JsonElement
    fun instance() : ProgramTypes
}