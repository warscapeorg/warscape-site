import org.gradle.util.GUtil.loadProperties

plugins {
    id(Deps.Plugins.Configuration.Kotlin.Mpp)
    id(Deps.Plugins.Deploy.Id)
    application
}

group = AppInfo.PACKAGE
version = AppInfo.VERSION

kotlin {
    js("frontend", IR) {
        browser {
            runTask {
                outputFileName = "static/main.bundle.js"
                sourceMaps = false
                cssSupport.enabled = true
            }
            webpackTask {
                sourceMaps = false
                cssSupport.enabled = true
                outputFileName = "static/main.bundle.js"
            }
        }
        binaries.executable()
    }
    jvm("backend") {
        withJava()
    }

    sourceSets {
        val backendMain by getting {
            dependencies {
                implementation(Deps.Libs.Ktor.Server.Core)
                implementation(Deps.Libs.Ktor.Server.Cio)
                implementation(Deps.Libs.Ktor.Server.Core)
                implementation(Deps.Libs.Ktor.Server.Html)
            }
        }
        val frontendMain by getting {
            dependencies {
                implementation(Deps.Libs.KVision.Core)
                implementation(Deps.Libs.KVision.Bootstrap.Core)
                implementation(Deps.Libs.KVision.Bootstrap.Css)
                implementation(Deps.Libs.KVision.Bootstrap.DateTime)
                implementation(Deps.Libs.KVision.Bootstrap.Spinner)
                implementation(Deps.Libs.KVision.Bootstrap.Dialog)
                implementation(Deps.Libs.KVision.Bootstrap.Typeahead)
            }
        }
    }
}

val service = "warscape"

val propertiesFile = rootProject.file("deploy.properties")

deploy {
    if (propertiesFile.exists()) {
        ignore = false
        val properties = loadProperties(propertiesFile)

        host = properties.getProperty("host")
        user = properties.getProperty("user")
        password = properties.getProperty("password")
        deployPath = properties.getProperty("deployPath")
        knownHostsFile = properties.getProperty("knownHosts")

        mainClass = "ServerKt"
        serviceName = service
    } else {
        ignore = true
    }
}

application {
    mainClass.set("ServerKt")
}

application {
    mainClass.set("ServerKt")
}