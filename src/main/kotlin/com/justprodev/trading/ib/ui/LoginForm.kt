/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.ui

import com.justprodev.trading.ib.model.Credentials
import com.justprodev.trading.ib.model.IStorage
import com.justprodev.trading.ib.utils.visitComponents
import org.slf4j.LoggerFactory
import java.awt.Window
import javax.swing.JButton
import javax.swing.JPasswordField
import javax.swing.JTextField

class LoginForm(loginWindow: Window, storage: IStorage) {
    init {
        var usernameField: JTextField? = null
        var passwordField: JPasswordField? = null
        var loginButton: JButton? = null

        visitComponents(loginWindow) { comp ->
            when (comp) {
                is JPasswordField if(passwordField == null) -> passwordField = comp
                is JTextField if(usernameField == null) -> usernameField = comp
                is JButton if comp.text.endsWith("Log In") -> loginButton = comp
            }
            usernameField != null && passwordField != null && loginButton != null
        }

        if (usernameField == null) throw IllegalArgumentException("Can't bind LoginForm - username field not found")
        if (passwordField == null) throw IllegalArgumentException("Can't bind LoginForm - password field not found")
        if (loginButton == null) throw IllegalArgumentException("Can't bind LoginForm - login button not found")

        LoggerFactory.getLogger(LoginForm::class.java).info("username: $usernameField")
        LoggerFactory.getLogger(LoginForm::class.java).info("password: $passwordField")
        LoggerFactory.getLogger(LoginForm::class.java).info("loginButton: $loginButton")

        storage.load()?.let { credentials ->
            usernameField.text = credentials.username
            passwordField.text = credentials.password
            loginButton.doClick()
        }

        loginButton.addActionListener {
            storage.save(
                Credentials(usernameField.text, String(passwordField.password))
            )
        }
    }

    companion object {
        fun isLoginWindow(window: Window): Boolean {
            var hasLoginField = false
            var hasPasswordField = false
            visitComponents(window) { comp ->
                when (comp) {
                    is JPasswordField -> hasPasswordField = true
                    is JTextField -> hasLoginField = true
                }
                hasLoginField && hasPasswordField
            }
            return hasLoginField && hasPasswordField
        }
    }
}