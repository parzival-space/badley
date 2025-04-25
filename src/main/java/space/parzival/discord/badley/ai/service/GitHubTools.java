package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.github.GitHub;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.github.token")
@AllArgsConstructor
public class GitHubTools implements AiTools {
    private final GitHub gitHub;

    private static final String USER_INFO_TEMPLATE = """
            User Information:
            - Username: %s
            - Email: %s
            - Name: %s
            - Bio: %s
            - Location: %s
            - Company: %s
            - Followers: %d
            - Following: %d
            - Public Repos: %d
            - Public Gists: %d
            - Twitter: %s
            - Blog / Website: %s
            - Profile URL: %s
            - Avatar URL: %s
            """.stripIndent();

    private static final String ORG_INFO_TEMPLATE = """
            Organization Information:
            - Organization Name: %s
            - Email: %s
            - Location: %s
            - Followers: %d
            - Following: %d
            - Public Repos: %d
            - Public Gists: %d
            - Twitter: %s
            - Blog / Website: %s
            - Profile URL: %s
            - Avatar URL: %s
            
            Members:
            %s
            """.stripIndent();

    private static final String REPO_INFO_TEMPLATE = """
            Repository Information:
            - Repository Name: %s
            - Description: %s
            - Homepage: %s
            - Owner: %s
            - Default Branch: %s
            - Stars: %d
            - Forks: %d
            - Watchers: %d
            - Open Issues: %d
            - Created At: %s
            - Updated At: %s
            - Language: %s
            - License: %s
            - URL: %s
            
            README:
            %s
            """.stripIndent();

    @Tool(description = "Get information about a GitHub user by username.")
    public String getUserInfo(String username) {
        log.debug("AI is requesting information about GitHub user: {}", username);

        try {
            var user = gitHub.getUser(username);
            return String.format(
                    USER_INFO_TEMPLATE,
                    user.getLogin(), user.getEmail(), user.getName(), user.getBio(), user.getLocation(),
                    user.getCompany(), user.getFollowersCount(), user.getFollowingCount(), user.getPublicRepoCount(),
                    user.getPublicGistCount(), user.getTwitterUsername(), user.getBlog(), user.getHtmlUrl(),
                    user.getAvatarUrl());
        } catch (IOException e) {
            log.error("Error fetching user information: {}", e.getMessage());
            return "Error fetching user information: " + e.getMessage();
        }
    }

    @Tool(description = "Get information about a GitHub organization by its name.")
    public String getOrgInfo(String orgName) {
        log.debug("AI is requesting information about GitHub organization: {}", orgName);

        try {
            var org = gitHub.getOrganization(orgName);
            return String.format(
                    ORG_INFO_TEMPLATE,
                    org.getName(), org.getEmail(), org.getLocation(), org.getFollowersCount(),
                    org.getFollowingCount(), org.getPublicRepoCount(), org.getPublicGistCount(),
                    org.getTwitterUsername(), org.getBlog(), org.getHtmlUrl(), org.getAvatarUrl(),
                    org.listMembers().withPageSize(100).toList().stream().map(member -> {
                        try {
                            return String.format("- %s (%s) <%s>", member.getLogin(), member.getName(), member.getHtmlUrl());
                        } catch (IOException e) {
                            return String.format("- %s (%s) <%s>", member.getLogin(), "?", member.getHtmlUrl());
                        }
                    }).reduce((a, b) -> a + "\n" + b).orElse("No members found"));
        } catch (IOException e) {
            log.error("Error fetching organization information: {}", e.getMessage());
            return "Error fetching organization information: " + e.getMessage();
        }
    }

    @Tool(description = "Get information about a GitHub repository by its full name (owner/repo).")
    public String getRepoInfo(@ToolParam(description = "The repository name in the format owner/repo") String repoName) {
        log.debug("AI is requesting information about GitHub repository: {}", repoName);

        try {
            var repo = gitHub.getRepository(repoName);
            return String.format(
                    REPO_INFO_TEMPLATE,
                    repo.getFullName(), repo.getDescription(), repo.getHomepage(), repo.getOwnerName(),
                    repo.getDefaultBranch(), repo.getStargazersCount(), repo.getForksCount(), repo.getWatchersCount(),
                    repo.getOpenIssueCount(),
                    repo.getCreatedAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    repo.getUpdatedAt().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    repo.getLanguage(), repo.getLicense().getName(), repo.getUrl().toString(),
                    repo.getReadme().toString());
        } catch (IOException e) {
            log.error("Error fetching repository information: {}", e.getMessage());
            return "Error fetching repository information: " + e.getMessage();
        }
    }
}
