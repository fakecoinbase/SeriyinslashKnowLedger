package org.knowledger.ledger.core.serial

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.PrimitiveDescriptor
import kotlinx.serialization.PrimitiveKind
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializer
import kotlinx.serialization.builtins.ByteArraySerializer
import java.math.BigInteger

@Serializer(forClass = BigInteger::class)
object BigIntegerSerializer : KSerializer<BigInteger> {
    override val descriptor: SerialDescriptor =
        PrimitiveDescriptor("BigInteger", PrimitiveKind.STRING)

    private val byteArraySerializer = ByteArraySerializer()

    override fun deserialize(decoder: Decoder): BigInteger =
        BigInteger(
            decoder.decodeSerializableValue(
                byteArraySerializer
            )
        )


    override fun serialize(encoder: Encoder, value: BigInteger) {
        encoder.encodeSerializableValue(
            byteArraySerializer, value.toByteArray()
        )
    }
}