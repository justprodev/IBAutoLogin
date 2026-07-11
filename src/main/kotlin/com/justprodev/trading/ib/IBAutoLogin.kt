/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib

import com.justprodev.trading.ib.model.IStorage
import com.justprodev.trading.ib.ui.LoginForm
import org.slf4j.LoggerFactory
import java.awt.AWTEvent
import java.awt.Dialog
import java.awt.Toolkit
import java.awt.Window
import java.awt.event.WindowEvent

class IBAutoLogin(private val storage: IStorage) {
    private val logger = LoggerFactory.getLogger(IBAutoLoginAgent::class.java)

    init {
        Toolkit.getDefaultToolkit().addAWTEventListener(
            {
                val window = it.source as? Window ?: return@addAWTEventListener
                when (it.id) {
                    WindowEvent.WINDOW_OPENED -> onWindowOpened(window)
                }
            },
            AWTEvent.WINDOW_EVENT_MASK
        )
    }

    private fun onWindowOpened(window: Window) {
        logger.info("IBAutoLogin - Window opened: $window")

        when {
            LoginForm.isLoginWindow(window) -> try {
                LoginForm(window, storage)
                logger.info("IBAutoLogin - LoginForm bound to $window")
            } catch (e: Exception) {
                logger.error("IBAutoLogin - Error occurred while binding LoginForm to $window", e)
            }

           window is Dialog && window.title.startsWith("Unrecognized Username") -> {
               logger.info("IBAutoLogin - Unrecognized Username dialog detected")
               storage.clear()
           }
        }

    }
}