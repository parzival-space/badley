package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.AiToolsService;
import space.parzival.discord.badley.service.wikipedia.WikipediaService;
import space.parzival.discord.badley.service.wikipedia.model.WikiParsePageResponse;
import space.parzival.discord.badley.service.wikipedia.model.WikiQueryPagesResponse;
import space.parzival.discord.badley.service.wikipedia.model.query.WikiQueryPage;

import java.util.Objects;

@Slf4j
@Component
@AllArgsConstructor
public class WikipediaTools implements AiToolsService {
    private WikipediaService wikipediaService;

    private static final String PAGE_INFO_TEMPLATE = """
            Title: %s
            Page ID: %d
            
            Content:
            %s
            """;

    @Tool(description = "Get information from Wikipedia")
    public String queryWikipedia(String title) {
        log.debug("AI is requesting information from Wikipedia for title: {}", title);

        try {
            WikiQueryPagesResponse response = wikipediaService.queryForPages(title);

            if (response.getPages().containsKey("-1")) {
                log.debug("No page found for title: {}", title);
                return "No page found for the given title.";
            }

            WikiQueryPage page = response.getPages().values().stream().findFirst().orElseThrow();
            String pageContent = page.getExtract();

            // no page content found, we need explicitly ask for the page content
            if (Strings.isEmpty(pageContent)) {
                WikiParsePageResponse parsePageResponse = wikipediaService.parsePage(page.getTitle());

                if (parsePageResponse == null || parsePageResponse.getParseResult() == null) {
                    log.debug("No page content found for title: {}", title);
                    return "No page content found for the given title.";
                }

                pageContent = parsePageResponse.getParseResult().getParsedText();
            }

            return String.format(PAGE_INFO_TEMPLATE, page.getTitle(), page.getPageId(), pageContent);
        } catch (Exception e) {
            log.error("Error while querying Wikipedia: {}", e.getMessage(), e);
            return "Error while querying Wikipedia: " + e.getMessage();
        }
    }

    @Tool(description = "Get a random information from Wikipedia")
    public String randomWikipedia() {
        log.debug("AI is requesting a random information from Wikipedia");

        try {
            WikiQueryPagesResponse response = wikipediaService.getRandomPage();
            Objects.requireNonNull(response); // if this is null, the something is wrong with the API

            WikiQueryPage page = response.getPages().values().stream().findFirst().orElseThrow();

            return String.format(PAGE_INFO_TEMPLATE, page.getTitle(), page.getPageId(), page.getExtract());
        } catch (Exception e) {
            log.error("Error while retrieving random page from Wikipedia: {}", e.getMessage(), e);
            return "Error while retrieving random page from Wikipedia: " + e.getMessage();
        }
    }
}
