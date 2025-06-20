package space.parzival.discord.badley.ai.generic;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;
import space.parzival.discord.badley.configuration.properties.AiCharacterProperties;
import space.parzival.discord.badley.configuration.properties.BuildInfoProperties;

import java.util.Map;

@Slf4j
@Component
@AllArgsConstructor
@ConditionalOnProperty(value = "badley.ai.tools.self-identity.enabled", havingValue = "true")
public class SelfIdentityTools implements AiTools {
    private AiCharacterProperties aiCharacterProperties;
    private BuildInfoProperties buildInfoProperties;

    private static final String IDENTITY_TEMPLATE = """
        You are powered by the project "${project.name}" version ${project.version}: ${project.description}
        You were built with Spring Framework version ${spring.version} and Java version ${java.version}.
        The project is available at the following URLs:
        - Website: ${project.websiteUrl}
        - Repository: ${project.repositoryUrl}
        
        ---
        
        Information about yourself:
        Name: ${character.name}
        Description:
        ${character.description}
        """.stripIndent();

    @Tool(description = "Get information about yourself, including project details and character information. Use this if the user request information about you.")
    public String getSelfIdentityInformation() {
        log.debug("AI is requesting self-identity information");

        try {
            var result = StringSubstitutor.replace(IDENTITY_TEMPLATE, Map.of(
                "project.name", buildInfoProperties.getProjectName(),
                "project.version", buildInfoProperties.getProjectVersion(),
                "project.description", buildInfoProperties.getProjectDescription(),
                "spring.version", buildInfoProperties.getSpringVersion(),
                "java.version", buildInfoProperties.getJavaVersion(),
                "project.websiteUrl", buildInfoProperties.getProjectUrl(),
                "project.repositoryUrl", buildInfoProperties.getProjectRepositoryUrl(),
                "character.name", aiCharacterProperties.getName(),
                "character.description", aiCharacterProperties.getPersonality()
            ));

            log.debug("AI returned: {}", result);

            return result;
        } catch (Exception e) {
            log.error("Error while generating self-identity information", e);
            return "An error occurred while generating self-identity information.";
        }
    }
}
