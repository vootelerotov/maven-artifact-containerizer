package io.github.vootelerotov.artifact.testcontainer.resolver

import io.github.vootelerotov.artifact.testcontainer.RepositoryConfig
import io.github.vootelerotov.artifact.testcontainer.RepositoryConfig.PublicRemoteRepository
import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem
import org.jboss.shrinkwrap.resolver.api.maven.Maven


class ResolverCreator {

  private companion object {
    private const val DEFAULT = "default"
  }

  internal fun createResolver(config: RepositoryConfig): ConfigurableMavenResolverSystem =
    Maven.configureResolver().apply {
      fromFile(SettingsXmlWriter().writeSettingsXml(config).toFile())
      config.getRemoteRepositories().forEach { repository ->
        withRemoteRepo(repository.id, repository.url, DEFAULT)
      }
    }

}
