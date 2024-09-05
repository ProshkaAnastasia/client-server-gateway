import communication.*
import communication.action.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import types.SpaceMarine
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm"))
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm").format(value))
    }

}

object IntAsObjectSerializer : KSerializer<Int> {

    @Serializable
    @SerialName("Int")
    data class IntSurrogate(val value: Int)

    override val descriptor: SerialDescriptor = IntSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Int) {
        IntSurrogate.serializer().serialize(encoder, IntSurrogate(value));
    }

    override fun deserialize(decoder: Decoder): Int {
        return decoder.decodeSerializableValue(IntSurrogate.serializer()).value
    }
}

object LongAsObjectSerializer : KSerializer<Long> {

    @Serializable
    @SerialName("Long")
    data class LongSurrogate(val value: Long)

    override val descriptor: SerialDescriptor = LongSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Long) {
        LongSurrogate.serializer().serialize(encoder, LongSurrogate(value));
    }

    override fun deserialize(decoder: Decoder): Long {
        return decoder.decodeSerializableValue(LongSurrogate.serializer()).value
    }
}

object StringAsObjectSerializer : KSerializer<String> {

    @Serializable
    @SerialName("String")
    data class StringSurrogate(val value: String)

    override val descriptor: SerialDescriptor = StringSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: String) {
        StringSurrogate.serializer().serialize(encoder, StringSurrogate(value));
    }

    override fun deserialize(decoder: Decoder): String {
        return decoder.decodeSerializableValue(StringSurrogate.serializer()).value
    }
}

object FloatAsObjectSerializer : KSerializer<Float> {

    @Serializable
    @SerialName("Float")
    data class FloatSurrogate(val value: Float)

    override val descriptor: SerialDescriptor = FloatSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Float) {
        FloatSurrogate.serializer().serialize(encoder, FloatSurrogate(value));
    }

    override fun deserialize(decoder: Decoder): Float {
        return decoder.decodeSerializableValue(FloatSurrogate.serializer()).value
    }
}

object DoubleAsObjectSerializer : KSerializer<Double> {

    @Serializable
    @SerialName("Double")
    data class DoubleSurrogate(val value: Double)

    override val descriptor: SerialDescriptor = DoubleSurrogate.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Double) {
        DoubleSurrogate.serializer().serialize(encoder, DoubleSurrogate(value));
    }

    override fun deserialize(decoder: Decoder): Double {
        return decoder.decodeSerializableValue(DoubleSurrogate.serializer()).value
    }
}

val messageModule = SerializersModule {
    polymorphic(Message::class, Request::class, Request.serializer())
    polymorphic(Message::class, Response::class, Response.serializer())
    polymorphic(Message::class, GatewayRegistration::class, GatewayRegistration.serializer())
    polymorphic(Message::class, ErrorResponse::class, ErrorResponse.serializer())
    polymorphic(Message::class, DemandInfo::class, DemandInfo.serializer())
}

val typesModule = SerializersModule {
    polymorphic(Any::class){
        subclass(SpaceMarine::class)
        subclass(AbstractCommand::class)
        subclass(Int::class, IntAsObjectSerializer)
        subclass(Long::class, LongAsObjectSerializer)
        subclass(String::class, StringAsObjectSerializer)
        subclass(Float::class, FloatAsObjectSerializer)
        subclass(Double::class, DoubleAsObjectSerializer)
    }
}

val actionModule = SerializersModule {
    polymorphic(AbstractAction::class, Execution::class, Execution.serializer())
    polymorphic(AbstractAction::class, Description::class, Description.serializer())
    polymorphic(AbstractAction::class, Registration::class, Registration.serializer())
    polymorphic(AbstractAction::class, Authorization::class, Authorization.serializer())
    polymorphic(AbstractAction::class, Output::class, Output.serializer())
    polymorphic(AbstractAction::class, Add::class, Add.serializer())
    polymorphic(AbstractAction::class, AddCommand::class, AddCommand.serializer())
    polymorphic(AbstractAction::class, Delete::class, Delete.serializer())
    polymorphic(AbstractAction::class, RenewCommands::class, RenewCommands.serializer())
    polymorphic(AbstractAction::class, Update::class, Update.serializer())
}

/*
object AbstractRequestSerializer : KSerializer<AbstractRequest>{

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AbstractRequest {
        return
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm").format(value))
    }

}
 */