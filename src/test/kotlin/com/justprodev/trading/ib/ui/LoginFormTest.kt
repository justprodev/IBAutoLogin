/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.ui

import com.justprodev.trading.ib.model.Credentials
import com.justprodev.trading.ib.model.IStorage
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Window
import javax.swing.*

class LoginFormTest {

    private val storage: IStorage = mockk(relaxed = true)

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    // ── isLoginWindow ──────────────────────────────────────────────

    @Test
    fun `isLoginWindow returns true when both fields present`() {
        val window = createWindowWith(JTextField(), JPasswordField())

        assertTrue(LoginForm.isLoginWindow(window))
    }

    @Test
    fun `isLoginWindow returns false for empty window`() {
        val window = JDialog()

        assertFalse(LoginForm.isLoginWindow(window))
    }

    @Test
    fun `isLoginWindow returns false when only text field present`() {
        val window = createWindowWith(JTextField())

        assertFalse(LoginForm.isLoginWindow(window))
    }

    @Test
    fun `isLoginWindow returns false when only password field present`() {
        val window = createWindowWith(JPasswordField())

        assertFalse(LoginForm.isLoginWindow(window))
    }

    @Test
    fun `isLoginWindow finds fields in nested panels`() {
        val inner = JPanel()
        inner.add(JTextField())
        inner.add(JPasswordField())
        val outer = JPanel()
        outer.add(inner)
        val window = JDialog()
        window.add(outer)

        assertTrue(LoginForm.isLoginWindow(window))
    }

    // ── Constructor: error cases ───────────────────────────────────

    @Test
    fun `constructor throws when no username field found`() {
        val window = createWindowWith(JPasswordField(), JButton("Log In"))

        val ex = assertThrows(IllegalArgumentException::class.java) {
            LoginForm(window, storage)
        }
        assertTrue(ex.message!!.contains("username"), "Error should mention username")
    }

    @Test
    fun `constructor throws when no password field found`() {
        val window = createWindowWith(JTextField(), JButton("Log In"))

        val ex = assertThrows(IllegalArgumentException::class.java) {
            LoginForm(window, storage)
        }
        assertTrue(ex.message!!.contains("password"), "Error should mention password")
    }

    @Test
    fun `constructor throws when no login button found`() {
        val window = createWindowWith(JTextField(), JPasswordField())

        val ex = assertThrows(IllegalArgumentException::class.java) {
            LoginForm(window, storage)
        }
        assertTrue(ex.message!!.contains("login button"), "Error should mention login button")
    }

    // ── Auto-fill ──────────────────────────────────────────────────

    @Test
    fun `fills username and password when credentials available`() {
        val usernameField = JTextField()
        val passwordField = JPasswordField()
        val window = createWindowWith(usernameField, passwordField, JButton("Log In"))

        every { storage.load() } returns Credentials("savedUser", "savedPass")

        LoginForm(window, storage)

        assertEquals("savedUser", usernameField.text)
        assertEquals("savedPass", String(passwordField.password))
    }

    @Test
    fun `does not fill fields when no credentials`() {
        val usernameField = JTextField()
        val passwordField = JPasswordField()
        val window = createWindowWith(usernameField, passwordField, JButton("Log In"))

        every { storage.load() } returns null

        LoginForm(window, storage)

        assertEquals("", usernameField.text)
        assertEquals(0, passwordField.password.size)
    }

    // ── Save on login click ────────────────────────────────────────

    @Test
    fun `saves credentials when login button clicked`() {
        val usernameField = JTextField()
        val passwordField = JPasswordField()
        val loginButton = JButton("Log In")
        val window = createWindowWith(usernameField, passwordField, loginButton)

        every { storage.load() } returns null

        LoginForm(window, storage)

        usernameField.text = "newUser"
        passwordField.text = "newPass"
        loginButton.doClick()

        verify(exactly = 1) {
            storage.save(match { it.username == "newUser" && it.password == "newPass" })
        }
    }

    @Test
    fun `auto-fill does not re-save already stored credentials`() {
        val usernameField = JTextField()
        val passwordField = JPasswordField()
        val window = createWindowWith(usernameField, passwordField, JButton("Log In"))

        every { storage.load() } returns Credentials("u", "p")

        LoginForm(window, storage)

        // Auto-fill clicks the button BEFORE the save listener is registered —
        // intentional: no need to re-save credentials already in storage
        verify(exactly = 0) {
            storage.save(any())
        }
    }

    // ── Helpers ────────────────────────────────────────────────────

    private fun createWindowWith(vararg components: JComponent): Window {
        val dialog = JDialog()
        dialog.isVisible = true // required for visitComponents (Container.getComponents does not filter)
        val panel = JPanel()
        components.forEach { panel.add(it) }
        dialog.add(panel)
        return dialog
    }
}
