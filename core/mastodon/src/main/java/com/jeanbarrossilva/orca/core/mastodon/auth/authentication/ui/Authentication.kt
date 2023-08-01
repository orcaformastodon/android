package com.jeanbarrossilva.orca.core.mastodon.auth.authentication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jeanbarrossilva.orca.platform.theme.OrcaTheme

@Composable
internal fun Authentication(modifier: Modifier = Modifier) {
    Surface(modifier, color = OrcaTheme.colorScheme.background) {
        Column(
            Modifier.fillMaxSize(),
            Arrangement.spacedBy(OrcaTheme.spacings.medium, Alignment.CenterVertically),
            Alignment.CenterHorizontally
        ) {
            Icon(OrcaTheme.Icons.Login, contentDescription = "Link", Modifier.size(64.dp))

            Text(
                "Authenticating...",
                textAlign = TextAlign.Center,
                style = OrcaTheme.typography.headlineLarge
            )
        }
    }
}

@Composable
@Preview
private fun AuthenticationPreview() {
    OrcaTheme {
        Authentication()
    }
}