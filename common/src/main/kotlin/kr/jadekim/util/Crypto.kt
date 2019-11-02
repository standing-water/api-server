package kr.jadekim.util

import kr.jadekim.exception.CryptoException
import kr.jadekim.ext.toHex
import java.io.ByteArrayOutputStream
import java.security.KeyFactory
import java.security.MessageDigest
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

enum class Crypto {
    AES {

        override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
            return try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

                cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(key))

                cipher.doFinal(data)
            } catch (e: Exception) {
                throw CryptoException(e)
            }
        }

        override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
            return try {
                val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")

                cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(key, "AES"), IvParameterSpec(key))

                cipher.doFinal(data)
            } catch (e: Exception) {
                throw CryptoException(e)
            }
        }
    },
    RSA_2048 {

        @Suppress("BlockingMethodInNonBlockingContext")
        override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
            return try {
                val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")

                val keyFactory = KeyFactory.getInstance("RSA");

                val publicKeySpec = X509EncodedKeySpec(key);
                val publicKey = keyFactory.generatePublic(publicKeySpec);

                cipher.init(Cipher.ENCRYPT_MODE, publicKey)

                val input = data.inputStream()
                val output = ByteArrayOutputStream()

                val inputBuffer = ByteArray(245)

                var len: Int = input.read(inputBuffer)
                while (len != -1) {
                    output.write(cipher.doFinal(inputBuffer, 0, len))

                    len = input.read(inputBuffer)
                }

                output.toByteArray()
            } catch (e: Exception) {
                throw CryptoException(e)
            }
        }

        @Suppress("BlockingMethodInNonBlockingContext")
        override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
            return try {
                val cipher = Cipher.getInstance("RSA")

                val keyFactory = KeyFactory.getInstance("RSA");

                val privateKeySpec = PKCS8EncodedKeySpec(key)
                val privateKey = keyFactory.generatePrivate(privateKeySpec)

                cipher.init(Cipher.DECRYPT_MODE, privateKey)

                val input = data.inputStream()
                val output = ByteArrayOutputStream()

                val inputBuffer = ByteArray(256)

                var len: Int = input.read(inputBuffer)
                while (len != -1) {
                    output.write(cipher.doFinal(inputBuffer, 0, len))

                    len = input.read(inputBuffer)
                }

                output.toByteArray()
            } catch (e: Exception) {
                throw CryptoException(e)
            }
        }
    },
    MD5 {

        override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
            val md = MessageDigest.getInstance("MD5")

            md.update(data)

            return md.digest().toHex().toUpperCase().toByteArray()
        }

        override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
            throw IllegalAccessException("Not Support decrypt : MD5")
        }
    },
    HMAC_SHA_512 {

        override suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray {
            val mac = Mac.getInstance("HmacSHA512")

            val keySpec = SecretKeySpec(key, "HmacSHA512")
            mac.init(keySpec)

            return mac.doFinal(data).toHex().toUpperCase().toByteArray()
        }

        override suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray {
            throw IllegalAccessException("Not Support decrypt : HMAC_SHA_512")
        }
    };

    abstract suspend fun encrypt(data: ByteArray, key: ByteArray): ByteArray

    abstract suspend fun decrypt(data: ByteArray, key: ByteArray): ByteArray
}