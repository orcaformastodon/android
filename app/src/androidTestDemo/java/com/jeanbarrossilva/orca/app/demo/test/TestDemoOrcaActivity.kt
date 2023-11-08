package com.jeanbarrossilva.orca.app.demo.test

import com.jeanbarrossilva.orca.app.demo.DemoOrcaActivity
import com.jeanbarrossilva.orca.platform.ui.test.core.requestFocus

internal class TestDemoOrcaActivity : DemoOrcaActivity() {
  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    requestFocus(hasFocus)
  }
}
