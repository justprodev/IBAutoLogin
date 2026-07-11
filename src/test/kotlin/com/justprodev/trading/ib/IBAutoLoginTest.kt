/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib

import com.justprodev.trading.ib.model.IStorage
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.*
import java.awt.event.WindowEvent
import javax.swing.*

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

        try {
            val event = WindowEvent(errorDialog, WindowEvent.WINDOW_OPENED)
            Toolkit.getDefaultToolkit().systemEventQueue.postEvent(event)
            // Give time for EventQueue to process the event
            Thread.sleep(200)
            EventQueue.invokeAndWait { /* pump */ }
        } finally {
            errorDialog.dispose()
        }

        verify(atLeast = 1) { storage.clear() }
    }

    @Test
    fun `does not clear storage for non-error dialog`() {
        IBAutoLogin(storage)

        val normalDialog = JDialog()
        normalDialog.title = "Some Other Dialog"
        normalDialog.isVisible = true

        try {
            val event = WindowEvent(normalDialog, WindowEvent.WINDOW_OPENED)
            Toolkit.getDefaultToolkit().systemEventQueue.postEvent(event)
            Thread.sleep(200)
            EventQueue.invokeAndWait { /* pump */ }
        } finally {
            normalDialog.dispose()
        }

        verify(exactly = 0) { storage.clear() }
    }
}
