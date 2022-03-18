package io.github.vootelerotov.artifact.testcontainer.resolver

import io.github.vootelerotov.artifact.testcontainer.RepositoryConfig
import org.redundent.kotlin.xml.xml
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolutePathString

internal class SettingsXmlWriter {

  fun writeSettingsXml(repositoryConfig: RepositoryConfig) : Path {
    val tempSettingsXmlFile = Files.createTempFile("settings", ".xml")
    val settingsXmlContent = createSettingsXml(repositoryConfig)
    return Files.write(tempSettingsXmlFile, settingsXmlContent.toByteArray() )
  }

  private fun createSettingsXml(repositoryConfig: RepositoryConfig): String =
    xml("settings") {
      xmlns = "http://maven.apache.org/SETTINGS/1.0.0"
      attributes(
        "xmlns:xsi" to "http://www.w3.org/2001/XMLSchema-instance",
        "xsi:schemaLocation"  to "http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd"
      )

      repositoryConfig.localRepositoryPath?.let {
        "localRepository" {
          - it.absolutePathString()
        }
      }

      "servers" {
        repositoryConfig.getRemoteRepositories().forEach {
          when(it) {
            is RepositoryConfig.PrivateRemoteRepository -> {
              "server" {
                "id" {
                  - it.id
                }
                "username" {
                  - it.username
                }
                "password" {
                  - it.password
                }
              }
            }
            else -> {/* do nothing */ }
          }
        }
      }

    }.toString()


}


