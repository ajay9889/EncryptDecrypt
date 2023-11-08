package com.ajay.encryptdecrypt

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import java.io.InputStream
import java.io.OutputStream
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

@RequiresApi(Build.VERSION_CODES.M)
class CryptoManager {
    // getting instance of keystore

    private val keyStore =KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }


    // cypher tell to the app how it will encrypt or decrypt
    private val encryptCipher = Cipher.getInstance(TRANSFORMATIONS).apply {
        init(Cipher.ENCRYPT_MODE ,getKey() )
    }
    private fun getDecryptCipherForIv(iv: ByteArray): Cipher{
        return Cipher.getInstance(TRANSFORMATIONS).apply {
            init(Cipher.DECRYPT_MODE, getKey(), IvParameterSpec(iv))
        }
    }

    fun encrypt(byte: ByteArray, outputStream: OutputStream): ByteArray{
        val encryptBytes = encryptCipher.doFinal(byte)
        outputStream.use {
            it.write(encryptCipher.iv.size)
            it.write(encryptCipher.iv)
            it.write(encryptBytes.size)
            it.write(encryptBytes)
        }
        return encryptBytes;
    }

     fun decrypt(inputStream: InputStream):ByteArray{
       return inputStream.use {
            val ivSize = it.read()
            val iv = ByteArray(ivSize)
            it.read(iv)

            val encryptByteSize = it.read()
            val encryptBytes = ByteArray(encryptByteSize)
            it.read(encryptBytes)

            getDecryptCipherForIv(iv).doFinal(encryptBytes)
        }
    }
    private fun getKey(): SecretKey{
        val existingKey = keyStore.getEntry("secret" , null) as? KeyStore.SecretKeyEntry
        return existingKey?.secretKey?:createScretKey()
    }
    private fun createScretKey(): SecretKey{
       return KeyGenerator.getInstance(ALGORITHM).apply {
           init(
               KeyGenParameterSpec.Builder(
                   "secret" , KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
               )
               .setBlockModes(BLOCK_MODE)
               .setEncryptionPaddings(PADDING)
               .setUserAuthenticationRequired(false)
               .setRandomizedEncryptionRequired(true).build()
           )
       }.generateKey()
    }

    companion object
    {
        private const val ALGORITHM= KeyProperties.KEY_ALGORITHM_AES
        private const val BLOCK_MODE= KeyProperties.BLOCK_MODE_CBC
        private const val PADDING= KeyProperties.ENCRYPTION_PADDING_PKCS7
        private const val TRANSFORMATIONS= "$ALGORITHM/$BLOCK_MODE/$PADDING"


    }
}