package org.knowledger.common.misc

import org.knowledger.common.config.LedgerConfiguration

@Suppress("NOTHING_TO_INLINE")
@UseExperimental(ExperimentalStdlibApi::class)
inline fun String.encodeStringToUTF8(): ByteArray =
    this.encodeToByteArray()

@Suppress("NOTHING_TO_INLINE")
@UseExperimental(ExperimentalStdlibApi::class)
inline fun ByteArray.decodeUTF8ToString(): String =
    this.decodeToString()

fun <T> Class<T>.extractIdFromClass(): String =
    LedgerConfiguration.DEFAULT_CRYPTER.applyHash(
        toGenericString()
    ).base64Encode()
