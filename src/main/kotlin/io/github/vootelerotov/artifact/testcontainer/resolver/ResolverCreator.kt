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
      config.getRemoteRepositories().forEach { repository ->
        when(repository) {
          is PublicRemoteRepository -> withRemoteRepo(repository.id, repository.url, DEFAULT)
          else -> { /* skip for now */ }
        }
      }
    }


}
