package space.parzival.discord.badley.ai.service;

import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.SearchResultSnippet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class YouTubeToolsTest {
    private YouTube youtube;

    @BeforeEach
    void setUp() {
        this.youtube = mock(YouTube.class);
    }

    @Test
    void searchYouTube() throws IOException {
        YouTube.Search mockSearch = mock(YouTube.Search.class);
        YouTube.Search.List mockList = mock(YouTube.Search.List.class);
        when(mockSearch.list(anyList())).thenReturn(mockList);
        when(mockList.setQ(anyString())).thenReturn(mockList);

        when(youtube.search()).thenReturn(mockSearch);

        SearchListResponse searchListResponse = new SearchListResponse();
        searchListResponse.setItems(List.of(
            createSearchResult("youtube#video"),
            createSearchResult("youtube#channel"),
            createSearchResult("youtube#playlist")
        ));

        when(mockList.execute()).thenReturn(searchListResponse);

        YouTubeTools youTubeTools = new YouTubeTools(youtube);
        String result = youTubeTools.searchYouTube("test query");

        assertNotNull(result);
        assertTrue(result.contains("https://youtube.com/video/videoId"));
        assertTrue(result.contains("https://youtube.com/channel/channelId"));
        assertTrue(result.contains("https://youtube.com/playlist/playlistId"));
        assertTrue(result.contains("Title: Title"));
        assertTrue(result.contains("Description: Description"));
        assertTrue(result.contains("Channel: Channel Title"));
    }

    private SearchResult createSearchResult(String kind) {
        ResourceId resourceId = new ResourceId();
        resourceId.setKind(kind);
        resourceId.setVideoId("videoId");
        resourceId.setChannelId("channelId");
        resourceId.setPlaylistId("playlistId");

        SearchResultSnippet snippet = new SearchResultSnippet();
        snippet.setTitle("Title");
        snippet.setDescription("Description");
        snippet.setChannelTitle("Channel Title");
        snippet.setPublishedAt(DateTime.parseRfc3339(
            OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)));
        snippet.setLiveBroadcastContent("none");

        SearchResult searchResult = new SearchResult();
        searchResult.setId(resourceId);
        searchResult.setSnippet(snippet);

        return searchResult;
    }
}
