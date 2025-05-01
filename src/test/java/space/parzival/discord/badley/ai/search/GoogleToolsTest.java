package space.parzival.discord.badley.ai.search;

import com.google.api.services.customsearch.v1.CustomSearchAPI;
import com.google.api.services.customsearch.v1.model.Result;
import com.google.api.services.customsearch.v1.model.Search;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.ai.tools.search.GoogleTools;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GoogleToolsTest {
    private CustomSearchAPI customSearchApi;
    private CustomSearchAPI.Cse cse;
    private CustomSearchAPI.Cse.List cseList;

    @BeforeEach
    void setUp() throws IOException {
        customSearchApi = mock(CustomSearchAPI.class);
        cse = mock(CustomSearchAPI.Cse.class);
        cseList = mock(CustomSearchAPI.Cse.List.class);
        when(customSearchApi.cse()).thenReturn(cse);
        when(cse.list()).thenReturn(cseList);
    }

    @Test
    void searchGoogle_returns_validData() throws IOException {
        when(cseList.setQ(anyString())).thenReturn(cseList);
        when(cseList.setSafe(anyString())).thenReturn(cseList);
        when(cseList.execute()).thenReturn(createSearch());

        GoogleTools googleTools = new GoogleTools(customSearchApi);
        String result = googleTools.searchGoogle("test query");

        assertNotNull(result);
        assertTrue(result.contains("https://example.com"));
        assertTrue(result.contains("Title: Example Title"));
        assertTrue(result.contains("Description: Example Description"));
    }

    @Test
    void searchGoogle_returns_errorMessage() throws IOException {
        when(cseList.setQ(anyString())).thenReturn(cseList);
        when(cseList.setSafe(anyString())).thenReturn(cseList);
        when(cseList.execute()).thenThrow(new IOException("Test exception"));

        GoogleTools googleTools = new GoogleTools(customSearchApi);
        String result = googleTools.searchGoogle("test query");

        assertNotNull(result);
        assertTrue(result.contains("Error fetching web search results: Test exception"));
    }

    private Search createSearch() {
        Search search = new Search();
        search.setItems(List.of(createResult()));
        return search;
    }

    private Result createResult() {
        Result search = new Result();
        search.setLink("https://example.com");
        search.setTitle("Example Title");
        search.setSnippet("Example Description");
        return search;
    }
}
