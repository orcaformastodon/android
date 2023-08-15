package com.jeanbarrossilva.orca.platform.theme.kit.scaffold.bar.snack

import androidx.compose.material3.SnackbarData

/**
 * [visuals][SnackbarData.visuals] cast to [OrcaSnackbarVisuals].
 *
 * @throws ClassCastException If [visuals][SnackbarData.visuals] is not an [OrcaSnackbarVisuals].
 **/
internal val SnackbarData.orcaVisuals
    get() = visuals as OrcaSnackbarVisuals