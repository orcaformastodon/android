package com.jeanbarrossilva.mastodonte.feature.auth.activity.test

import com.jeanbarrossilva.mastodonte.core.auth.Authenticator
import com.jeanbarrossilva.mastodonte.core.auth.Authorizer
import com.jeanbarrossilva.mastodonte.core.sample.auth.SampleAuthenticator
import com.jeanbarrossilva.mastodonte.core.sample.auth.SampleAuthorizer
import org.koin.core.module.Module
import org.koin.dsl.module

@Suppress("TestFunctionName")
internal fun AuthModule(): Module {
    return module {
        single<Authorizer> { SampleAuthorizer }
        single<Authenticator> { SampleAuthenticator }
    }
}