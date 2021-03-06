package org.knowledger.ledger.service.handles.builder

import kotlinx.serialization.Serializable
import org.knowledger.ledger.config.CoinbaseParams
import org.knowledger.ledger.config.LedgerId
import org.knowledger.ledger.config.LedgerParams

/**
 *
 */
@Serializable
internal data class LedgerConfig(
    val ledgerId: LedgerId,
    val ledgerParams: LedgerParams,
    val coinbaseParams: CoinbaseParams
)

