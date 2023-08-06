package com.jeanbarrossilva.orca.platform.ui.component.scaffold.bar.text

import android.content.res.Configuration
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.jeanbarrossilva.orca.platform.theme.OrcaTheme
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.bar.text.size.AutoSizeRange
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.bar.text.size.AutoSizer
import com.jeanbarrossilva.orca.platform.ui.component.scaffold.bar.text.size.rememberAutoSizeRange

/**
 * [Text] that is sized automatically based on the specified [range].
 *
 * @param text [String] to be displayed.
 * @param modifier [Modifier] to be applied to the underlying [Text].
 * @param style [TextStyle] with which it will be styled.
 * @param range [AutoSizeRange] within which the font size should be.
 **/
@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    range: AutoSizeRange = rememberAutoSizeRange(style.fontSize)
) {
    val density = LocalDensity.current
    var canBeDrawn by remember { mutableStateOf(false) }
    val sizer = remember(density, range) { AutoSizer(density, range) }

    Text(
        text,
        modifier.drawWithContent {
            if (canBeDrawn) {
                drawContent()
            }
        },
        fontSize = sizer.size,
        onTextLayout = {
            sizer.autoSize(it, canBeDrawn) {
                canBeDrawn = true
            }
        },
        maxLines = 1,
        style = style
    )
}

/** Preview of an [AutoSizeText]. **/
@Composable
@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
private fun AutoSizeTextPreview() {
    OrcaTheme {
        Surface(color = OrcaTheme.colorScheme.background) {
            AutoSizeText("Text")
        }
    }
}