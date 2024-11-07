import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView() = App()

@Preview
@Composable
fun AppPreview() {
    App()
}
