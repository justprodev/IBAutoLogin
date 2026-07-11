/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.io

import com.justprodev.trading.ib.model.Credentials
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class JksStorageTest {

    private val storage = JksStorage()
    private val jksFile = File("autologin.jks")

    @AfterEach
    fun cleanup() {
        jksFile.delete()
    }

    @Test
    fun `load returns null when JKS file does not exist`() {
        assertNull(storage.load())
    }

    @Test
    fun `save creates JKS file on disk`() {
        storage.save(Credentials("user", "pass"))

        assertTrue(jksFile.exists(), "JKS file should be created after save")
    }

    @Test
    fun `load returns saved credentials after save`() {
        val original = Credentials("testuser", "testpass")
        storage.save(original)

        val loaded = storage.load()

        assertNotNull(loaded, "Credentials should be loaded after save")
        assertEquals(original.username, loaded!!.username)
        assertEquals(original.password, loaded.password)
    }

    @Test
    fun `roundtrip preserves credentials exactly`() {
        val original = Credentials("admin", "s3cr3t!")

        storage.save(original)
        val loaded = storage.load()

        assertEquals(original.username, loaded?.username)
        assertEquals(original.password, loaded?.password)
    }

    @Test
    fun `load returns null after clear`() {
        storage.save(Credentials("user", "pass"))
        storage.clear()

        assertNull(storage.load(), "load should return null after clear")
    }

    @Test
    fun `clear deletes JKS file from disk`() {
        storage.save(Credentials("user", "pass"))
        assertTrue(jksFile.exists(), "JKS file should exist before clear")

        storage.clear()

        assertFalse(jksFile.exists(), "JKS file should be deleted after clear")
    }

    @Test
    fun `save overwrites previous credentials`() {
        storage.save(Credentials("old", "oldpass"))
        storage.save(Credentials("new", "newpass"))

        val loaded = storage.load()

        assertEquals("new", loaded?.username)
        assertEquals("newpass", loaded?.password)
    }

    @Test
    fun `clear on non-existent file does not throw`() {
        assertDoesNotThrow {
            storage.clear()
        }
    }
}
