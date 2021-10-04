import io.kvision.Application
import io.kvision.form.text.text
import io.kvision.panel.root
import io.kvision.panel.vPanel

class WarscapeApplication : Application() {
    override fun start() {
        super.start()
        root("root") {
            vPanel {
                text(value = "Test")
            }
        }
    }
}