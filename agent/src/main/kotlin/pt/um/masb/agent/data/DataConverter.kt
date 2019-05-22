package pt.um.masb.agent.data

import kotlinx.io.ByteArrayInputStream
import kotlinx.io.ByteArrayOutputStream
import mu.KotlinLogging
import pt.um.masb.agent.messaging.block.ontology.concepts.JBlock
import pt.um.masb.agent.messaging.block.ontology.concepts.JBlockHeader
import pt.um.masb.agent.messaging.block.ontology.concepts.JCoinbase
import pt.um.masb.agent.messaging.block.ontology.concepts.JLedgerId
import pt.um.masb.agent.messaging.block.ontology.concepts.JMerkleTree
import pt.um.masb.agent.messaging.block.ontology.concepts.JTransactionOutput
import pt.um.masb.agent.messaging.transaction.ontology.concepts.JPhysicalData
import pt.um.masb.agent.messaging.transaction.ontology.concepts.JTransaction
import pt.um.masb.common.Hash
import pt.um.masb.common.data.BlockChainData
import pt.um.masb.common.misc.base64decode
import pt.um.masb.common.misc.base64encode
import pt.um.masb.common.misc.getStringFromKey
import pt.um.masb.common.misc.stringToPublicKey
import pt.um.masb.ledger.Block
import pt.um.masb.ledger.BlockHeader
import pt.um.masb.ledger.Coinbase
import pt.um.masb.ledger.Transaction
import pt.um.masb.ledger.TransactionOutput
import pt.um.masb.ledger.config.LedgerId
import pt.um.masb.ledger.data.MerkleTree
import pt.um.masb.ledger.data.PhysicalData
import java.io.InvalidClassException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.*


fun convertToJadeBlock(
    b: Block,
    clazz: Class<out BlockChainData>
): JBlock =
    JBlock(
        b.data
            .map { convertToJadeTransaction(it) }
            .toList(),
        convertToJadeCoinbase(b.coinbase),
        convertToJadeBlockHeader(b.header),
        convertToJadeMerkleTree(b.merkleTree),
        clazz.simpleName
    )

/*
fun <T : BlockChainData> convertToJadeBlock(
    b: Block,
    srl: SerializationStrategy<T>,
    clazz: Class<T>
): JBlock =
    JBlock(
        b.data
            .map { convertToJadeTransaction(it, srl) }
            .toList(),
        convertToJadeCoinbase<T>(b.coinbase),
        convertToJadeBlockHeader(b.header),
        convertToJadeMerkleTree(b.merkleTree),
        clazz.simpleName
    )
*/

fun convertToJadeMerkleTree(merkleTree: MerkleTree): JMerkleTree =
    JMerkleTree(
        merkleTree.collapsedTree.map { base64encode(it) },
        merkleTree.levelIndex
    )

fun convertToJadeBlockHeader(header: BlockHeader): JBlockHeader =
    JBlockHeader(
        base64encode(header.ledgerId),
        header.difficulty.toString(),
        header.blockheight,
        base64encode(header.hashId),
        base64encode(header.merkleRoot),
        base64encode(header.previousHash),
        header.params,
        header.timestamp.toString(),
        header.nonce
    )

fun convertToJadeBlockChainId(blid: LedgerId): JLedgerId =
    JLedgerId(
        blid.uuid.toString(),
        blid.timestamp.toString(),
        blid.params.copy(),
        blid.id,
        base64encode(blid.hashId)
    )


fun convertToJadeCoinbase(coinbase: Coinbase): JCoinbase =
    JCoinbase(
        null,
        coinbase.payoutTXO
            .map(::convertToJadeTransactionOutput)
            .toSet(),
        coinbase.coinbase.toString(),
        base64encode(coinbase.hashId)
    )

fun convertToJadeCoinbase(
    blid: LedgerId,
    coinbase: Coinbase
): JCoinbase =
    JCoinbase(
        convertToJadeBlockChainId(blid),
        coinbase.payoutTXO
            .map(::convertToJadeTransactionOutput)
            .toSet(),
        coinbase.coinbase.toString(),
        base64encode(coinbase.hashId)
    )

private fun convertToJadeTransactionOutput(txo: TransactionOutput): JTransactionOutput =
    JTransactionOutput(
        getStringFromKey(txo.publicKey),
        base64encode(txo.hashId),
        base64encode(txo.prevCoinbase),
        txo.payout.toString(),
        txo.tx.map { base64encode(it) }.toSet()
    )


fun convertToJadeTransaction(
    t: Transaction
): JTransaction =
    JTransaction(
        transactionId = base64encode(t.hashId),
        publicKey = getStringFromKey(t.publicKey),
        data = convertToJadePhysicalData(
            t.data
        ),
        signature = base64encode(t.signature)
    )

/*
fun <T : BlockChainData> convertToJadeTransaction(
    t: Transaction,
    srl: SerializationStrategy<T>
): JTransaction =
    JTransaction(
        null,
        base64encode(t.hashId),
        getStringFromKey(t.publicKey),
        convertToJadePhysicalData(t.data, srl),
        base64encode(t.signature)
    )
*/

fun convertToJadeTransaction(
    blid: LedgerId,
    t: Transaction
): JTransaction =
    JTransaction(
        ledgerId = convertToJadeBlockChainId(blid),
        transactionId = base64encode(t.hashId),
        publicKey = getStringFromKey(t.publicKey),
        data = convertToJadePhysicalData(t.data),
        signature = base64encode(t.signature)
    )

fun convertToJadeTransaction(
    blid: Hash,
    t: Transaction
): JTransaction =
    JTransaction(
        blockChainHash = base64encode(blid),
        transactionId = base64encode(t.hashId),
        publicKey = getStringFromKey(t.publicKey),
        data = convertToJadePhysicalData(t.data),
        signature = base64encode(t.signature)
    )


/*
fun <T : BlockChainData> convertToJadeTransaction(
    blid: ledgerHash,
    t: Transaction,
    srl: SerializationStrategy<T>
): JTransaction =
    JTransaction(
        convertToJadeBlockChainId(blid),
        base64encode(t.hashId),
        getStringFromKey(t.publicKey),
        convertToJadePhysicalData(t.data, srl),
        base64encode(t.signature)
    )
*/

fun convertToJadePhysicalData(data: PhysicalData): JPhysicalData {
    val byteStream = ByteArrayOutputStream(data.approximateSize.toInt())
    ObjectOutputStream(byteStream).use {
        it.writeObject(data.data)
    }
    return JPhysicalData(
        base64encode(byteStream.toByteArray()),
        data.instant.toString(),
        data.geoCoords?.latitude.toString(),
        data.geoCoords?.longitude.toString()
    )
}

/*
@Suppress("UNCHECKED_CAST")
fun <T : BlockChainData> convertToJadePhysicalData(
    data: PhysicalData,
    srl: SerializationStrategy<T>
): JPhysicalData =
    JPhysicalData(
        base64encode(CBOR.dump(srl, data.data as T)),
        data.instant.toString(),
        data.geoCoords?.latitude.toString(),
        data.geoCoords?.longitude.toString()
    )
*/

//Conversions from Jade Types

fun convertFromJadeBlock(
    b: JBlock,
    clazz: Class<out BlockChainData>
): Block =
    if (clazz.simpleName == b.clazz) {
        Block(b.data
                  .map { convertFromJadeTransaction(it) }
                  .toMutableList(),
              convertFromJadeCoinbase(b.coinbase),
              convertFromJadeBlockHeader(b.header),
              convertFromJadeMerkleTree(b.merkleTree))
    } else {
        val err = "Incompatible types on JBlock of type ${b.clazz} and Block of type ${clazz.simpleName}"
        KotlinLogging.logger {}
            .error { err }
        throw InvalidClassException(err)
    }

/*
fun <T : BlockChainData> convertFromJadeBlock(
    b: JBlock,
    srl: DeserializationStrategy<T>,
    clazz: Class<T>
): Block =
    if (clazz.simpleName == b.clazz) {
        Block(b.data
                  .map { convertFromJadeTransaction(it, srl) }
                  .toMutableList(),
              convertFromJadeCoinbase<T>(b.coinbase),
              convertFromJadeBlockHeader(b.header),
              convertFromJadeMerkleTree(b.merkleTree))
    } else {
        val err = "Incompatible types on JBlock of type ${b.clazz} and Block of type ${clazz.simpleName}"
        KotlinLogging.logger {}
            .error { err }
        throw InvalidClassException(err)
    }
*/

fun convertFromJadeMerkleTree(merkleTree: JMerkleTree): MerkleTree =
    MerkleTree(
        merkleTree.hashes.map { base64decode(it) },
        merkleTree.levelIndex
    )


fun convertFromJadeBlockHeader(header: JBlockHeader): BlockHeader =
    BlockHeader(
        base64decode(header.blid),
        BigInteger(header.difficulty),
        header.blockheight,
        base64decode(header.hash),
        base64decode(header.merkleRoot),
        base64decode(header.previousHash),
        header.params,
        Instant.parse(header.timeStamp),
        header.nonce
    )

fun convertFromJadeBlockChainId(blid: JLedgerId): LedgerId =
    LedgerId(
        blid.id,
        UUID.fromString(blid.uuid),
        Instant.parse(blid.timestamp),
        blid.params,
        base64decode(blid.hash)
    )


fun convertFromJadeCoinbase(
    coinbase: JCoinbase
): Coinbase =
    Coinbase(
        coinbase.payoutTXO
            .map(::convertFromJadeTransactionOutput)
            .toMutableSet(),
        BigDecimal(coinbase.coinbase),
        base64decode(coinbase.hashId)
    )

private fun convertFromJadeTransactionOutput(
    txo: JTransactionOutput
): TransactionOutput =
    TransactionOutput(
        stringToPublicKey(txo.pubkey),
        base64decode(txo.prevCoinbase),
        base64decode(txo.hashId),
        BigDecimal(txo.payout),
        txo.tx.map { base64decode(it) }.toMutableSet()
    )


fun convertFromJadeTransaction(
    t: JTransaction
): Transaction =
    Transaction(
        stringToPublicKey(t.publicKey),
        convertFromJadePhysicalData(t.data),
        base64decode(t.signature)
    )

/*
fun <T : BlockChainData> convertFromJadeTransaction(
    t: JTransaction,
    srl: DeserializationStrategy<T>
): Transaction =
    Transaction(
        stringToPublicKey(t.publicKey),
        convertFromJadePhysicalData(t.data, srl),
        base64decode(t.signature)
    )


fun <T : BlockChainData> convertFromJadePhysicalData(
    data: JPhysicalData,
    srl: DeserializationStrategy<T>
): PhysicalData =
    PhysicalData(
        Instant.parse(data.instant),
        BigDecimal(data.lat),
        BigDecimal(data.lng),
        CBOR.load(srl, base64decode(data.data))
    )
*/

fun convertFromJadePhysicalData(
    data: JPhysicalData
): PhysicalData {
    val b = ByteArrayInputStream(base64decode(data.data))
    val t = ObjectInputStream(b).use {
        it.readObject() as BlockChainData
    }

    return PhysicalData(
        Instant.parse(data.instant),
        BigDecimal(data.lat),
        BigDecimal(data.lng),
        t
    )
}


