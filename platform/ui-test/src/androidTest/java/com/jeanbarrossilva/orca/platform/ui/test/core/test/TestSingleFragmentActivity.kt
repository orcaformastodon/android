package com.jeanbarrossilva.orca.platform.ui.test.core.test

import androidx.test.core.app.launchActivity
import com.jeanbarrossilva.orca.platform.ui.test.core.SingleFragmentActivity
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals

internal abstract class TestSingleFragmentActivity : SingleFragmentActivity() {
    var runNavGraphIntegrityCallback: NavGraphIntegrityCallback? = null
        private set

    enum class NavGraphIntegrityCallback {
        NO_DESTINATION,
        INEQUIVALENT_DESTINATION_ROUTE,
        NON_FRAGMENT_DESTINATION,
        MULTIPLE_DESTINATIONS
    }

    override fun onNoDestination() {
        runNavGraphIntegrityCallback = NavGraphIntegrityCallback.NO_DESTINATION
        cancelNavGraphIntegrityInsuranceJob()
    }

    override fun onInequivalentDestinationRoute() {
        runNavGraphIntegrityCallback = NavGraphIntegrityCallback.INEQUIVALENT_DESTINATION_ROUTE
        cancelNavGraphIntegrityInsuranceJob()
    }

    override fun onNonFragmentDestination() {
        runNavGraphIntegrityCallback = NavGraphIntegrityCallback.NON_FRAGMENT_DESTINATION
    }

    override fun onMultipleDestinations() {
        runNavGraphIntegrityCallback = NavGraphIntegrityCallback.MULTIPLE_DESTINATIONS
    }

    private fun cancelNavGraphIntegrityInsuranceJob() {
        runTest {
            navGraphIntegrityInsuranceJob?.cancelAndJoin()
        }
    }

    companion object {
        /**
         * Asserts that [expected] is the [NavGraphIntegrityCallback] to get run when this
         * [TestSingleFragmentActivity] is launched.
         *
         * @param expected [NavGraphIntegrityCallback] that's expected to be run.
         **/
        inline fun <reified T : TestSingleFragmentActivity> assertRunNavGraphCallbackEquals(
            expected: NavGraphIntegrityCallback
        ) {
            launchActivity<T>().use { scenario ->
                scenario.onActivity { activity ->
                    waitUntil { activity.runNavGraphIntegrityCallback != null }
                    assertEquals(expected, activity.runNavGraphIntegrityCallback)
                }
            }
        }
    }
}