package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.kohsuke.github.GHUser;
import org.kohsuke.github.GitHub;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiTools;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.github.enabled", havingValue = "true")
@AllArgsConstructor
public class GitHubTools implements AiTools {
    private static final String USER_INFO_TEMPLATE = """
        User Information:
        - Username: ${username}
        - Email: ${email}
        - Name: ${name}
        - Bio: ${bio}
        - Location: ${location}
        - Company: ${company}
        - Followers: ${followers}
        - Following: ${following}
        - Public Repos: ${public_repos}
        - Public Gists: ${public_gists}
        - Twitter: ${twitter}
        - Blog / Website: ${blog}
        - Profile URL: ${profile_url}
        - Avatar URL: ${avatar_url}
        """.stripIndent();
    private static final String ORG_INFO_TEMPLATE = """
        Organization Information:
        - Organization Name: ${name}
        - Email: ${email}
        - Location: ${location}
        - Followers: ${followers}
        - Following: ${following}
        - Public Repos: ${public_repos}
        - Public Gists: ${public_gists}
        - Twitter: ${twitter}
        - Blog / Website: ${blog}
        - Profile URL: ${profile_url}
        - Avatar URL: ${avatar_url}
        
        Members:
        ${members}
        """.stripIndent();
    private static final String REPO_INFO_TEMPLATE = """
        Repository Information:
        - Repository Name: ${name}
        - Description: ${description}
        - Homepage: ${homepage}
        - Owner: ${owner}
        - Default Branch: ${default_branch}
        - Stars: ${stars}
        - Forks: ${forks}
        - Watchers: ${watchers}
        - Open Issues: ${open_issues}
        - Created At: ${created_at}
        - Updated At: ${updated_at}
        - Language: ${language}
        - License: ${license}
        - URL: ${url}
        
        README:
        ${readme}
        """.stripIndent();
    private final GitHub gitHub;

    @Tool(description = "Get information about a GitHub user by username.")
    public String getUserInfo(String username) {
        log.debug("AI is requesting information about GitHub user: {}", username);

        try {
            GHUser user = gitHub.getUser(username);

            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", user.getLogin());
            userInfo.put("email", user.getEmail());
            userInfo.put("name", user.getName());
            userInfo.put("bio", user.getBio());
            userInfo.put("location", user.getLocation());
            userInfo.put("company", user.getCompany());
            userInfo.put("followers", user.getFollowersCount());
            userInfo.put("following", user.getFollowingCount());
            userInfo.put("public_repos", user.getPublicRepoCount());
            userInfo.put("public_gists", user.getPublicGistCount());
            userInfo.put("twitter", user.getTwitterUsername());
            userInfo.put("blog", user.getBlog());
            userInfo.put("profile_url", user.getHtmlUrl());
            userInfo.put("avatar_url", user.getAvatarUrl());
            return StringSubstitutor.replace(USER_INFO_TEMPLATE, userInfo);
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

            Map<String, Object> orgInfo = new HashMap<>();
            orgInfo.put("name", org.getLogin());
            orgInfo.put("email", org.getEmail());
            orgInfo.put("location", org.getLocation());
            orgInfo.put("followers", org.getFollowersCount());
            orgInfo.put("following", org.getFollowingCount());
            orgInfo.put("public_repos", org.getPublicRepoCount());
            orgInfo.put("public_gists", org.getPublicGistCount());
            orgInfo.put("twitter", org.getTwitterUsername());
            orgInfo.put("blog", org.getBlog());
            orgInfo.put("profile_url", org.getHtmlUrl());
            orgInfo.put("avatar_url", org.getAvatarUrl());
            orgInfo.put("members", org.listMembers().toList().stream()
                .map(member -> String.format("- %s (%s)", member.getLogin(), member.getHtmlUrl()))
                .reduce((a, b) -> a + "\n" + b).orElse("No members found"));
            return StringSubstitutor.replace(ORG_INFO_TEMPLATE, orgInfo);
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

            Map<String, Object> repoInfo = new HashMap<>();
            repoInfo.put("name", repo.getFullName());
            repoInfo.put("description", repo.getDescription());
            repoInfo.put("homepage", repo.getHomepage());
            repoInfo.put("owner", repo.getOwnerName());
            repoInfo.put("default_branch", repo.getDefaultBranch());
            repoInfo.put("stars", repo.getStargazersCount());
            repoInfo.put("forks", repo.getForksCount());
            repoInfo.put("watchers", repo.getWatchersCount());
            repoInfo.put("open_issues", repo.getOpenIssueCount());
            repoInfo.put("created_at", repo.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            repoInfo.put("updated_at", repo.getUpdatedAt().atZone(ZoneId.systemDefault()).toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            repoInfo.put("language", repo.getLanguage());
            repoInfo.put("license", repo.getLicense() != null ? repo.getLicense().getName() : "No license");
            repoInfo.put("url", repo.getUrl());
            repoInfo.put("readme", repo.getReadme() != null ? repo.getReadme().getContent() : "No README found");
            return StringSubstitutor.replace(REPO_INFO_TEMPLATE, repoInfo);
        } catch (IOException e) {
            log.error("Error fetching repository information: {}", e.getMessage());
            return "Error fetching repository information: " + e.getMessage();
        }
    }
}
