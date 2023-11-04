package com.jeanbarrossilva.orca.core.http

import com.jeanbarrossilva.orca.core.auth.actor.Actor
import com.jeanbarrossilva.orca.core.feed.profile.toot.content.TermMuter
import com.jeanbarrossilva.orca.core.instance.Instance
import com.jeanbarrossilva.orca.core.instance.InstanceProvider
import com.jeanbarrossilva.orca.core.module.CoreModule
import com.jeanbarrossilva.orca.std.injector.module.Inject
import com.jeanbarrossilva.orca.std.injector.module.Module

/**
 * [CoreModule] into which core HTTP structures are injected.
 *
 * @param termMuter [TermMuter] by which terms will be muted.
 * @param instanceProvider [InstanceProvider] that will provide the [Instance] in which the
 *   currently [authenticated][Actor.Authenticated] [Actor] is.
 */
open class HttpModule(
  @Inject internal val instanceProvider: Module.() -> InstanceProvider,
  @Inject internal val termMuter: Module.() -> TermMuter
) : CoreModule(instanceProvider, termMuter)
