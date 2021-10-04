import io.kvision.Application
import io.kvision.form.text.text
import io.kvision.html.h1
import io.kvision.panel.root
import io.kvision.panel.vPanel
import io.kvision.startApplication

class WarscapeApplication : Application() {
    override fun start() {
        root("root") {
            vPanel {
                h1 {
                    text {
                        content = "Test"
                    }
                }
            }
        }
    }
}

fun main() = startApplication(::WarscapeApplication)