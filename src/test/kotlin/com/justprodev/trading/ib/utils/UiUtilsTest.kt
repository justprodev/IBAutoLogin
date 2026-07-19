/*
 * IBAutoLogin — Java Agent for automating IBGateway login
 * Copyright (c) 2026 justprodev
 * Licensed under the MIT License. See LICENSE file in the project root.
 */
package com.justprodev.trading.ib.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import javax.swing.*

class UiUtilsTest {

    @Test
    fun `finds component in flat container`() {
        val button = JButton("Click")
        val panel = JPanel()
        panel.add(button)

        var found: JButton? = null
        visitComponents(panel) { comp ->
            if (comp is JButton) {
                found = comp
                return@visitComponents true
            }
            false
        }

        assertSame(button, found)
    }

    @Test
    fun `finds component in nested hierarchy`() {
        val label = JLabel("deep")
        val inner = JPanel()
        inner.add(label)
        val outer = JPanel()
        outer.add(inner)

        var found: JLabel? = null
        visitComponents(outer) { comp ->
            if (comp is JLabel) {
                found = comp
                return@visitComponents true
            }
            false
        }

        assertSame(label, found)
    }

    @Test
    fun `visits root component first`() {
        val panel = JPanel()

        val visited = mutableListOf<JComponent>()
        visitComponents(panel) { comp ->
            visited.add(comp as JComponent)
            false
        }

        assertSame(panel, visited.first(), "Root component should be visited first")
    }

    @Test
    fun `early termination stops traversal`() {
        val btn1 = JButton("one")
        val btn2 = JButton("two")
        val panel = JPanel()
        panel.add(btn1)
        panel.add(btn2)

        var found: JButton? = null
        visitComponents(panel) { comp ->
            if (comp is JButton) {
                found = comp
                true // stop after first button
            } else {
                false
            }
        }

        assertSame(btn1, found, "Should stop at first button found")
    }

    @Test
    fun `returns false when no component matches`() {
        val panel = JPanel()

        val result = visitComponents(panel) { false }

        assertFalse(result)
    }

    @Test
    fun `returns true when visitor returns true`() {
        val panel = JPanel()

        val result = visitComponents(panel) { true }

        assertTrue(result)
    }

    @Test
    fun `traverses all children when no early termination`() {
        val panel = JPanel()
        panel.add(JButton("1"))
        panel.add(JTextField("2"))
        panel.add(JLabel("3"))

        val visited = mutableListOf<JComponent>()
        visitComponents(panel) { comp ->
            visited.add(comp as JComponent)
            false
        }

        assertEquals(4, visited.size, "Should visit panel + 3 children")
    }

    @Test
    fun `find button`() {
        val button1 = JButton("button1")
        val button2 = JButton("button2")
        val inner = JPanel()
        inner.add(button1)
        inner.add(button2)
        val outer = JPanel()
        outer.add(inner)

        assertSame(button1, outer.findButton("button1"))
        assertSame(button2, outer.findButton("button2"))
    }
}
