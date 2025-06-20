package space.parzival.discord.badley.ai.generic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.configuration.properties.AiCharacterProperties;
import space.parzival.discord.badley.configuration.properties.BuildInfoProperties;
import space.parzival.discord.badley.configuration.properties.tools.SelfIdentityProperties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SelfIdentityToolsTest {
    private AiCharacterProperties aiCharacterProperties;
    private BuildInfoProperties buildInfoProperties;

    private SelfIdentityTools selfIdentityTools;

    @BeforeEach
    void setUp() {
        aiCharacterProperties = mock(AiCharacterProperties.class);
        when(aiCharacterProperties.getName()).thenReturn("Badley");
        when(aiCharacterProperties.getPersonality()).thenReturn("A helpful AI assistant for Discord.");

        buildInfoProperties = mock(BuildInfoProperties.class);
        when(buildInfoProperties.getProjectName()).thenReturn("Badley AI");
        when(buildInfoProperties.getProjectVersion()).thenReturn("1.0.0");
        when(buildInfoProperties.getProjectDescription()).thenReturn("An AI assistant for Discord.");
        when(buildInfoProperties.getProjectUrl()).thenReturn("https://badley.ai");
        when(buildInfoProperties.getProjectRepositoryUrl()).thenReturn("https://badley.repo");
        when(buildInfoProperties.getSpringVersion()).thenReturn("5.3.9");
        when(buildInfoProperties.getJavaVersion()).thenReturn("17");

        selfIdentityTools = new SelfIdentityTools(aiCharacterProperties, buildInfoProperties);
    }

    @Test
    void getSelfIdentityInformation() {
        String identityInfo = selfIdentityTools.getSelfIdentityInformation();

        assertNotNull(identityInfo);
        assertTrue(identityInfo.contains("Badley AI"));
        assertTrue(identityInfo.contains("1.0.0"));
        assertTrue(identityInfo.contains("An AI assistant for Discord."));
        assertTrue(identityInfo.contains("A helpful AI assistant for Discord."));
        assertTrue(identityInfo.contains("https://badley.ai"));
        assertTrue(identityInfo.contains("https://badley.repo"));
        assertTrue(identityInfo.contains("Badley"));
        assertTrue(identityInfo.contains("A helpful AI assistant for Discord."));
    }

    @Test
    void getSelfIdentityInformation_withNulls() {
        // Test with null properties
        SelfIdentityTools toolsWithNulls = new SelfIdentityTools(null, null);
        String identityInfo = toolsWithNulls.getSelfIdentityInformation();

        assertNotNull(identityInfo);
        assertTrue(identityInfo.contains("An error occurred while generating self-identity information."));
    }
}
