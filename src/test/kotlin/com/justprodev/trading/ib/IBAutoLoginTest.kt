/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib

import com.justprodev.trading.ib.model.IStorage
import io.mockk.clearAllMocks
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.awt.EventQueue
import javax.swing.JButton
import javax.swing.JDialog

class IBAutoLoginTest {

    private val storage: IStorage = mockk(relaxed = true)

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `constructor registers AWT event listener`() {
        val login = IBAutoLogin(storage)

        assertNotNull(login, "IBAutoLogin should be created without errors")
    }

    @Test
    fun `clears storage when Unrecognized Username dialog opens`() {
        IBAutoLogin(storage)

        // Simulate opening of the error dialog
        val errorDialog = JDialog()
        errorDialog.title = "Unrecognized Username or Password"
        errorDialog.isVisible = true
        EventQueue.invokeAndWait { /* pump */ }
        verify(atLeast = 1) { storage.clear() }
    }

    @Test
    fun `does not clear storage for non-error dialog`() {
        IBAutoLogin(storage)

        val normalDialog = JDialog()
        normalDialog.title = "Some Other Dialog"
        normalDialog.isVisible = true
        EventQueue.invokeAndWait { /* pump */ }
        verify(exactly = 0) { storage.clear() }
    }

    @Test
    fun `re-login dialog triggers re-login button click`() {
        IBAutoLogin(storage)

        val reLoginDialog = JDialog()
        val button = JButton("Re-login").apply {
            addActionListener {
                reLoginDialog.isVisible = false
            }
        }
        reLoginDialog.title = "U12345 Re-login Is Required"
        reLoginDialog.add(button)
        reLoginDialog.isVisible = true
        EventQueue.invokeAndWait { /* pump */ }
        assertFalse(reLoginDialog.isVisible)
    }

    @Test
    fun `exit session setting dialog triggers Ok button click`() {
        IBAutoLogin(storage)

        val exitSessionDialog = JDialog()
        val button = JButton("OK").apply {
            addActionListener {
                exitSessionDialog.isVisible = false
            }
        }
        exitSessionDialog.title = "U12345 Exit Session Setting"
        exitSessionDialog.add(button)
        exitSessionDialog.isVisible = true
        EventQueue.invokeAndWait { /* pump */ }
        assertFalse(exitSessionDialog.isVisible)
    }
}
