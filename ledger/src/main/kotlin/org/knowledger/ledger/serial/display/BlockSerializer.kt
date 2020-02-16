package org.knowledger.ledger.serial.display

import kotlinx.serialization.CompositeDecoder
import kotlinx.serialization.CompositeEncoder
import org.knowledger.ledger.serial.internal.AbstractBlockSerializer
import org.knowledger.ledger.storage.BlockHeader
import org.knowledger.ledger.storage.Coinbase
import org.knowledger.ledger.storage.MerkleTree

internal object BlockSerializer : AbstractBlockSerializer(TransactionSerializer) {
    override fun CompositeEncoder.encodeCoinbase(
        index: Int, coinbase: Coinbase
    ) {
        encodeSerializableElement(
            descriptor, index, CoinbaseSerializer,
            coinbase
        )
    }

    override fun CompositeDecoder.decodeCoinbase(
        index: Int
    ): Coinbase =
        decodeSerializableElement(
            descriptor, index, CoinbaseSerializer
        )

    override fun CompositeEncoder.encodeBlockHeader(
        index: Int, header: BlockHeader
    ) {
        encodeSerializableElement(
            descriptor, index, BlockHeaderSerializer,
            header
        )
    }

    override fun CompositeDecoder.decodeBlockHeader(index: Int): BlockHeader =
        decodeSerializableElement(
            descriptor, index, BlockHeaderSerializer
        )

    override fun CompositeEncoder.encodeMerkleTree(
        index: Int, merkleTree: MerkleTree
    ) {
        encodeSerializableElement(
            descriptor, index, MerkleTreeSerializer,
            merkleTree
        )
    }

    override fun CompositeDecoder.decodeMerkleTree(index: Int): MerkleTree =
        decodeSerializableElement(
            descriptor, index, MerkleTreeSerializer
        )

}