/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.utils

import java.awt.Component
import java.awt.Container
import java.awt.Dialog
import java.awt.Frame
import java.awt.Window
import javax.swing.AbstractButton

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

fun Container.findButton(text: String): AbstractButton? {
    var result: AbstractButton? = null
    visitComponents(this) { comp ->
        if (comp is AbstractButton && comp.text == text) {
            result = comp
            true
        } else {
            false
        }
    }
    return result
}

val Window.title: String
    get() {
        return when (this) {
            is Frame -> this.title
            is Dialog -> this.title
            else -> ""
        }
    }