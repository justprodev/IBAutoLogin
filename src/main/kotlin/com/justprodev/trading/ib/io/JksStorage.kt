/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.io

import com.justprodev.trading.ib.model.Credentials
import com.justprodev.trading.ib.model.IStorage
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.attribute.BasicFileAttributes
import java.security.KeyStore
import javax.crypto.spec.SecretKeySpec

class JksStorage : IStorage {
    private val jksFile = File(JKS_FILE_NAME)

    @Synchronized
    override fun load(): Credentials? {
        if (!jksFile.exists()) return null

        return try {
            val ks = KeyStore.getInstance("PKCS12")
            FileInputStream(jksFile).use { ks.load(it, JKS_PASSWORD) }
            Credentials(
                username = readEntry(ks, ALIAS_USERNAME),
                password = readEntry(ks, ALIAS_PASSWORD)
            )
        } catch (e: Throwable) {
            logger.error("Error loading from JKS", e)
            null
        }
    }

    @Synchronized
    override fun save(credentials: Credentials) {
        try {
            val ks = KeyStore.getInstance("PKCS12")
            ks.load(null, JKS_PASSWORD)

            writeEntry(ks, ALIAS_USERNAME, credentials.username)
            writeEntry(ks, ALIAS_PASSWORD, credentials.password)

            FileOutputStream(jksFile).use { ks.store(it, JKS_PASSWORD) }
            logger.info("IBAutoLogin - Credentials saved to {}", JKS_FILE_NAME)
        } catch (e: Exception) {
            logger.error("IBAutoLogin - Error saving to JKS", e)
        }
    }

    @Synchronized
    override fun clear() {
        try {
            jksFile.delete()
            logger.info("IBAutoLogin - Credentials cleared from {}", JKS_FILE_NAME)
        } catch (e: Exception) {
            logger.error("IBAutoLogin - Error clearing JKS", e)
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(JksStorage::class.java)

        private const val JKS_FILE_NAME = "autologin.jks"
        private val JKS_PASSWORD = generatePassword()
        private const val ALIAS_USERNAME = "username"
        private const val ALIAS_PASSWORD = "password"

        private fun readEntry(ks: KeyStore, alias: String): String {
            val entry = ks.getEntry(alias, KeyStore.PasswordProtection(JKS_PASSWORD))
                    as? KeyStore.SecretKeyEntry ?: return ""
            return String(entry.secretKey.encoded)
        }

        private fun writeEntry(ks: KeyStore, alias: String, value: String) {
            val secretKey = SecretKeySpec(value.toByteArray(Charsets.UTF_8), "AES")
            ks.setEntry(
                alias, KeyStore.SecretKeyEntry(secretKey),
                KeyStore.PasswordProtection(JKS_PASSWORD)
            )
        }

        private fun generatePassword(): CharArray {
            val homePath = Paths.get(System.getProperty("user.home"))
            val attributes = Files.readAttributes(homePath, BasicFileAttributes::class.java)
            return attributes.creationTime().toMillis().toString().toCharArray()
        }
    }
}