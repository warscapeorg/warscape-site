import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.*
import org.gradle.util.GUtil.loadProperties
import org.hidetake.groovy.ssh.connection.AllowAnyHosts
import org.hidetake.groovy.ssh.core.Remote
import java.io.File
import java.util.*

class DeployPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(plugin = Deps.Plugins.Ssh.Id)
        target.apply(plugin = Deps.Plugins.Application.Id)
        target.apply(plugin = Deps.Plugins.Shadow.Id)

        val configuration = target.extensions.create<DeployExtension>(name = "deploy")

        target.afterEvaluate {
            configuration.apply {
                if (ignore) return@afterEvaluate
                host ?: error("`host` should be defined in `deploy`")
                deployPath ?: error("`deployPath` should be defined in `deploy`")
                mainClass ?: error("`mainClass` should be defined in `deploy`")
                serviceName ?: error("`serviceName` should be defined in `deploy`")
            }

            val webServer = Remote(
                mapOf(
                    "host" to configuration.host,
                    "user" to configuration.user,
                    "password" to configuration.password,
                    "knownHosts" to (configuration.knownHostsFile?.let(::File) ?: AllowAnyHosts.instance)
                )
            )

            target.extensions.create<SshSessionExtension>("sshSession", target, webServer)

            val webpackTask = tasks.getByName<org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpack>("frontendBrowserProductionWebpack")

            val jarTask = tasks.named<ShadowJar>("shadowJar") {
                dependsOn(webpackTask)
                archiveFileName.set("app.jar")
                manifest {
                    attributes(mapOf("Main-Class" to configuration.mainClass))
                }
                from(File(webpackTask.destinationDirectory, webpackTask.outputFileName))
            }

            target.task("deploy") {
                group = "deploy"

                dependsOn(jarTask)

                doLast {
                    target.the<SshSessionExtension>().invoke {
                        put(
                            hashMapOf(
                                "from" to jarTask.get().archiveFile.get().asFile,
                                "into" to configuration.deployPath
                            )
                        )
                        execute("systemctl restart ${configuration.serviceName}")
                    }
                }
            }
        }
    }

    private fun Project.properties(): Properties? =
        rootProject.file("deploy.properties")
            .takeIf(File::exists)
            ?.let(::loadProperties)
}