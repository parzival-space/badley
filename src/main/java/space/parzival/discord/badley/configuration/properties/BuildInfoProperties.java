package space.parzival.discord.badley.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.build-info")
@AllArgsConstructor
public class BuildInfoProperties {
    private String projectName = "N/A";
    private String projectVersion = "N/A";
    private String projectDescription = "N/A";
    private String projectUrl = "N/A";
    private String projectRepositoryUrl = "N/A";
    private String javaVersion = "N/A";
    private String springVersion = "N/A";
}
