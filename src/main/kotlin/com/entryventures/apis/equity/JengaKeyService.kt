package com.entryventures.apis.equity

import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.EncodedKeySpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*

@Service
class JengaKeyService {

    private val privateKey: PrivateKey by lazy {
        KeyFactory.getInstance("RSA").generatePrivate(getKeySpec(Path.of("./jenga-key-store/jenga-private-key.pem"), -1))
    }

    private val publicKey: PublicKey by lazy {
        KeyFactory.getInstance("RSA").generatePublic(getKeySpec(Path.of("./jenga-key-store/jenga-public-key.pem"), 1))
    }

    private fun getKeySpec(filePath: Path, key: Int): EncodedKeySpec {
        val keyString = Files.readString(filePath)

        val keyPair = when(key >= 0) {
            true -> "PUBLIC"
            false -> "PRIVATE"
        }

        val keyContent = keyString
            .replace("-----BEGIN $keyPair KEY-----", "")
            .replace("-----END $keyPair KEY-----", "")
            .replace("\\s+".toRegex(), "")

        val decodedKey = Base64.getDecoder().decode(keyContent)

        return when(key >= 0 ) {
            true -> X509EncodedKeySpec(decodedKey)
            false -> PKCS8EncodedKeySpec(decodedKey)
        }
    }

    fun signaturePayload(vararg tokens :String): ByteArray = tokens.toList().joinToString("").toByteArray()

    fun generateSignature(digest: ByteArray): ByteArray {

        // Sign payload
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(digest)
        val signedPayload = signature.sign()

        return signedPayload
    }

    fun verifySignature(messageBytes: ByteArray, digitalSignature: ByteArray): Boolean {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(messageBytes)
        return signature.verify(digitalSignature)
    }
}