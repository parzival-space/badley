package space.parzival.discord.badley.configuration.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "badley.build-info")
@AllArgsConstructor
public class BuildInfoProperties {
    public String projectName = "N/A";
    public String projectVersion = "N/A";
    public String projectDescription = "N/A";
    public String projectUrl = "N/A";
    public String projectRepositoryUrl = "N/A";
    public String javaVersion = "N/A";
    public String springVersion = "N/A";
}
