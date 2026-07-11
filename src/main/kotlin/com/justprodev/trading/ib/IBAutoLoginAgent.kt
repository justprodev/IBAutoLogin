/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib

import com.justprodev.trading.ib.io.JksStorage
import java.lang.instrument.Instrumentation

object IBAutoLoginAgent {
    @JvmStatic
    fun premain(agentArgs: String?, inst: Instrumentation) {
        IBAutoLogin(JksStorage())
    }
}