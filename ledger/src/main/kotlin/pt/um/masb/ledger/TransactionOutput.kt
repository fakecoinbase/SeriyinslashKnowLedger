package pt.um.masb.ledger

import com.orientechnologies.orient.core.record.OElement
import com.squareup.moshi.JsonClass
import mu.KLogging
import org.openjdk.jol.info.GraphLayout
import pt.um.masb.common.Hash
import pt.um.masb.common.Hashable
import pt.um.masb.common.Hashed
import pt.um.masb.common.Payout
import pt.um.masb.common.Sizeable
import pt.um.masb.common.crypt.AvailableCrypters
import pt.um.masb.common.crypt.Crypter
import pt.um.masb.common.database.NewInstanceSession
import pt.um.masb.common.emptyHash
import pt.um.masb.common.misc.flattenBytes
import pt.um.masb.common.storage.adapters.Storable
import java.math.BigDecimal
import java.security.PublicKey

@JsonClass(generateAdapter = true)
data class TransactionOutput(
    val publicKey: PublicKey,
    val prevCoinbase: Hash,
    override var hashId: Hash,
    var payout: Payout,
    var tx: MutableSet<Hash>
) : Sizeable, Hashed, Hashable, Storable,
    LedgerContract {

    override val approximateSize: Long
        get() = GraphLayout
            .parseInstance(this)
            .totalSize()


    constructor(
        publicKey: PublicKey,
        prevCoinbase: Hash,
        cumUTXO: Payout,
        newT: Hash,
        prev: Hash
    ) : this(
        publicKey,
        prevCoinbase,
        emptyHash(),
        BigDecimal("0"),
        mutableSetOf<Hash>()
    ) {
        addToPayout(cumUTXO, newT, prev)
    }

    override fun store(
        session: NewInstanceSession
    ): OElement =
        session
            .newInstance("TransactionOutput")
            .apply {
                setProperty("publicKey", publicKey.encoded)
                setProperty("prevCoinbase", prevCoinbase)
                setProperty("hashId", hashId)
                setProperty("payout", payout)
                setProperty("txSet", tx)
            }

    fun addToPayout(
        payout: Payout,
        tx: Hash,
        prev: Hash
    ) {
        this.tx.add(prev + tx)
        this.payout = this.payout.add(payout)
        hashId = digest(crypter)
    }

    /**
     * {@inheritDoc}
     */
    override fun digest(c: Crypter): Hash =
        c.applyHash(
            flattenBytes(
                tx,
                publicKey.encoded,
                prevCoinbase,
                payout.unscaledValue().toByteArray()
            )
        )


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TransactionOutput)
            return false

        if (!publicKey.encoded!!.contentEquals(
                other.publicKey.encoded
            )
        )
            return false
        if (!prevCoinbase.contentEquals(
                other.prevCoinbase
            )
        )
            return false
        if (!hashId.contentEquals(other.hashId))
            return false
        if (payout != other.payout)
            return false
        if (!tx.containsAll(other.tx)) return false
        if (tx.size != other.tx.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = publicKey.hashCode()
        result = 31 * result + prevCoinbase.contentHashCode()
        result = 31 * result + hashId.contentHashCode()
        result = 31 * result + payout.hashCode()
        result = 31 * result + tx.hashCode()
        return result
    }

    companion object : KLogging() {
        val crypter = AvailableCrypters.SHA256Encrypter
    }
}