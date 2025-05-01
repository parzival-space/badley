package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.Test;
import org.kohsuke.github.GHContent;
import org.kohsuke.github.GHLicense;
import org.kohsuke.github.GHOrganization;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.PagedIterable;
import space.parzival.discord.badley.ai.tools.service.GitHubTools;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GitHubToolsTest {

    @Test
    void getUserInfo_returns_validData() throws IOException {
        GitHub gitHub = mock(GitHub.class);
        GHUser user = getUser();
        when(gitHub.getUser(anyString())).thenReturn(user);

        GitHubTools gitHubTools = new GitHubTools(gitHub);
        String result = gitHubTools.getUserInfo("mock-user");

        assertTrue(result.contains("Username: mock-user"));
        assertTrue(result.contains("Email: mock-email"));
        assertTrue(result.contains("Name: mock-name"));
        assertTrue(result.contains("Bio: mock-bio"));
        assertTrue(result.contains("Location: mock-location"));
        assertTrue(result.contains("Company: mock-company"));
        assertTrue(result.contains("Followers: 1"));
        assertTrue(result.contains("Following: 2"));
        assertTrue(result.contains("Public Repos: 3"));
        assertTrue(result.contains("Public Gists: 4"));
        assertTrue(result.contains("Twitter: mock-twitter"));
        assertTrue(result.contains("Blog / Website: mock-blog"));
        assertTrue(result.contains("Profile URL: https://mock-url"));
        assertTrue(result.contains("Avatar URL: mock-avatar"));
    }

    @Test
    void getUserInfo_returns_errorMessage() throws IOException {
        GitHub gitHub = mock(GitHub.class);
        GHUser user = getUser();
        when(user.getEmail()).thenThrow(new IOException());
        when(gitHub.getUser(anyString())).thenReturn(user);

        GitHubTools gitHubTools = new GitHubTools(gitHub);
        String result = gitHubTools.getUserInfo("mock-user");

        assertTrue(result.contains("Error"));
    }

    @Test
    void getOrgInfo_returns_validData() throws IOException {
        GitHub gitHub = mock(GitHub.class);
        GHOrganization organization = getOrganization();
        when(gitHub.getOrganization(anyString())).thenReturn(organization);

        GitHubTools gitHubTools = new GitHubTools(gitHub);
        String result = gitHubTools.getOrgInfo("mock-org");

        assertTrue(result.contains("Organization Name: mock-org"));
        assertTrue(result.contains("Email: mock-email"));
        assertTrue(result.contains("Location: mock-location"));
        assertTrue(result.contains("Followers: 1"));
        assertTrue(result.contains("Following: 2"));
        assertTrue(result.contains("Public Repos: 3"));
        assertTrue(result.contains("Public Gists: 4"));
        assertTrue(result.contains("Twitter: mock-twitter"));
        assertTrue(result.contains("Blog / Website: mock-blog"));
        assertTrue(result.contains("Profile URL: https://mock-url"));
        assertTrue(result.contains("Avatar URL: mock-avatar"));
    }

    @Test
    void getOrgInfo_returns_errorMessage() throws IOException {
        GitHub gitHub = mock(GitHub.class);
        GHOrganization organization = getOrganization();
        when(organization.getEmail()).thenThrow(new IOException());
        when(gitHub.getOrganization(anyString())).thenReturn(organization);

        GitHubTools gitHubTools = new GitHubTools(gitHub);
        String result = gitHubTools.getOrgInfo("mock-org");

        assertTrue(result.contains("Error"));
    }

    @Test
    void getRepoInfo_returns_validData() throws IOException {
        GitHub gitHub = mock(GitHub.class);
        GHRepository repository = getRepository();
        when(gitHub.getRepository(anyString())).thenReturn(repository);

        GitHubTools gitHubTools = new GitHubTools(gitHub);
        String result = gitHubTools.getRepoInfo("mock-repo");

        assertTrue(result.contains("Repository Name: mock-repo"));
        assertTrue(result.contains("Description: mock-description"));
        assertTrue(result.contains("Homepage: https://mock-url"));
        assertTrue(result.contains("Owner: mock-owner"));
        assertTrue(result.contains("Default Branch: mock-branch"));
        assertTrue(result.contains("Stars: 1"));
        assertTrue(result.contains("Forks: 2"));
        assertTrue(result.contains("Watchers: 3"));
        assertTrue(result.contains("Open Issues: 4"));
        assertTrue(result.contains("Language: mock-language"));
        assertTrue(result.contains("License: mock-license"));
        assertTrue(result.contains("URL: https://mock-url"));
    }

    @Test
    void getRepoInfo_returns_errorMessage() throws IOException {
        GitHub gitHub = mock(GitHub.class);
        GHRepository repository = getRepository();
        when(repository.getCreatedAt()).thenThrow(new IOException());
        when(gitHub.getRepository(anyString())).thenReturn(repository);

        GitHubTools gitHubTools = new GitHubTools(gitHub);
        String result = gitHubTools.getRepoInfo("mock-repo");

        assertTrue(result.contains("Error"));
    }

    private GHUser getUser() throws IOException {
        GHUser user = mock(GHUser.class);
        when(user.getLogin()).thenReturn("mock-user");
        when(user.getEmail()).thenReturn("mock-email");
        when(user.getName()).thenReturn("mock-name");
        when(user.getBio()).thenReturn("mock-bio");
        when(user.getLocation()).thenReturn("mock-location");
        when(user.getCompany()).thenReturn("mock-company");
        when(user.getFollowersCount()).thenReturn(1);
        when(user.getFollowingCount()).thenReturn(2);
        when(user.getPublicRepoCount()).thenReturn(3);
        when(user.getPublicGistCount()).thenReturn(4);
        when(user.getTwitterUsername()).thenReturn("mock-twitter");
        when(user.getBlog()).thenReturn("mock-blog");
        when(user.getHtmlUrl()).thenReturn(URI.create("https://mock-url").toURL());
        when(user.getAvatarUrl()).thenReturn("mock-avatar");
        return user;
    }

    private GHOrganization getOrganization() throws IOException {
        GHOrganization organization = mock(GHOrganization.class);
        when(organization.getName()).thenReturn("mock-org");
        when(organization.getLogin()).thenReturn("mock-org");
        when(organization.getEmail()).thenReturn("mock-email");
        when(organization.getLocation()).thenReturn("mock-location");
        when(organization.getFollowersCount()).thenReturn(1);
        when(organization.getFollowingCount()).thenReturn(2);
        when(organization.getPublicRepoCount()).thenReturn(3);
        when(organization.getPublicGistCount()).thenReturn(4);
        when(organization.getTwitterUsername()).thenReturn("mock-twitter");
        when(organization.getBlog()).thenReturn("mock-blog");
        when(organization.getHtmlUrl()).thenReturn(URI.create("https://mock-url").toURL());
        when(organization.getAvatarUrl()).thenReturn("mock-avatar");

        PagedIterable<GHUser> users = mock(PagedIterable.class);
        when(users.withPageSize(anyInt())).thenReturn(users);
        when(users.toList()).thenReturn(List.of());
        when(organization.listMembers()).thenReturn(users);

        return organization;
    }

    private GHRepository getRepository() throws IOException {
        GHRepository repository = mock(GHRepository.class);

        GHLicense license = mock(GHLicense.class);
        when(license.getName()).thenReturn("mock-license");

        GHContent content = mock(GHContent.class);
        when(content.toString()).thenReturn("mock-content");

        when(repository.getFullName()).thenReturn("mock-repo");
        when(repository.getDescription()).thenReturn("mock-description");
        when(repository.getHomepage()).thenReturn("https://mock-url");
        when(repository.getOwnerName()).thenReturn("mock-owner");
        when(repository.getDefaultBranch()).thenReturn("mock-branch");
        when(repository.getStargazersCount()).thenReturn(1);
        when(repository.getForksCount()).thenReturn(2);
        when(repository.getWatchersCount()).thenReturn(3);
        when(repository.getOpenIssueCount()).thenReturn(4);
        when(repository.getCreatedAt()).thenReturn(Instant.now());
        when(repository.getUpdatedAt()).thenReturn(Instant.now());
        when(repository.getLanguage()).thenReturn("mock-language");
        when(repository.getLicense()).thenReturn(license);
        when(repository.getReadme()).thenReturn(content);
        when(repository.getUrl()).thenReturn(URI.create("https://mock-url").toURL());
        return repository;
    }
}
