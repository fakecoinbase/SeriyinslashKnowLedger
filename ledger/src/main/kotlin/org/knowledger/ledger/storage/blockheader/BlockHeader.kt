package org.knowledger.ledger.storage.blockheader

import org.knowledger.ledger.config.BlockParams
import org.knowledger.ledger.config.ChainId
import org.knowledger.ledger.crypto.Hash
import org.knowledger.ledger.serial.HashSerializable
import org.knowledger.ledger.storage.LedgerContract

interface BlockHeader : Cloneable,
                        HashSerializable,
                        LedgerContract {
    val chainId: ChainId
    val merkleRoot: Hash
    val previousHash: Hash
    val params: BlockParams
    val seconds: Long
    val nonce: Long

    public override fun clone(): BlockHeader
}
