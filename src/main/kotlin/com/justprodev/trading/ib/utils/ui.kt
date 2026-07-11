/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.utils

import java.awt.Component
import java.awt.Container

/** Recursively traverses the Swing component tree.
 *  Lambda returns true to stop traversal early. */
fun visitComponents(comp: Component, visitor: (Component) -> Boolean): Boolean {
    if (visitor(comp)) return true
    if (comp is Container) {
        for (child in comp.components) {
            if (visitComponents(child, visitor)) return true
        }
    }
    return false
}