package pt.um.masb.ledger.config.adapters

import pt.um.masb.common.database.NewInstanceSession
import pt.um.masb.common.database.StorageElement
import pt.um.masb.common.database.StorageType
import pt.um.masb.common.hash.Hash
import pt.um.masb.common.results.Outcome
import pt.um.masb.ledger.config.LedgerId
import pt.um.masb.ledger.results.tryOrLoadUnknownFailure
import pt.um.masb.ledger.service.results.LoadFailure
import pt.um.masb.ledger.storage.adapters.LedgerStorageAdapter

object LedgerIdStorageAdapter : LedgerStorageAdapter<LedgerId> {
    override val id: String
        get() = "LedgerId"

    override val properties: Map<String, StorageType>
        get() = mapOf(
            "tag" to StorageType.STRING,
            "hashId" to StorageType.HASH
        )

    override fun store(
        toStore: LedgerId, session: NewInstanceSession
    ): StorageElement =
        session.newInstance(id).apply {
            setStorageProperty("tag", toStore.tag)
            setHashProperty("hashId", toStore.hashId)
        }


    override fun load(
        ledgerHash: Hash,
        element: StorageElement
    ): Outcome<LedgerId, LoadFailure> =
        tryOrLoadUnknownFailure {
            val hash =
                element.getHashProperty("hashId")

            assert(hash.contentEquals(ledgerHash))


            Outcome.Ok(
                LedgerId(
                    element.getStorageProperty("tag"),
                    ledgerHash
                )
            )
        }
}