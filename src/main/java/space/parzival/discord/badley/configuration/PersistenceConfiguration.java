package space.parzival.discord.badley.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "space.parzival.discord.badley.persistence")
public class PersistenceConfiguration {}
