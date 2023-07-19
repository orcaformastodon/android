package com.jeanbarrossilva.mastodonte.platform.ui.component.stat

import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performClick
import com.jeanbarrossilva.mastodonte.platform.ui.component.onActivateableStatIcon
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class ActivateableStatIconTests {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun isUnselectedWhenInactive() {
        composeRule.setContent { TestActivateableStatIcon(isActive = false) }
        composeRule.onActivateableStatIcon().assertIsNotSelected()
    }

    @Test
    fun isSelectedWhenActive() {
        composeRule.setContent { TestActivateableStatIcon(isActive = true) }
        composeRule.onActivateableStatIcon().assertIsSelected()
    }

    @Test
    fun receivesInteractionWhenInteractive() {
        var hasBeenInteractedWith = false
        composeRule.setContent {
            TestActivateableStatIcon(
                interactiveness = ActivateableStatIconInteractiveness.Interactive {
                    hasBeenInteractedWith = true
                }
            )
        }
        composeRule.onActivateableStatIcon().performClick()
        assertTrue(hasBeenInteractedWith)
    }
}