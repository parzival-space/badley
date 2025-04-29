package space.parzival.discord.badley.configuration.google;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.customsearch.v1.CustomSearchAPI;
import com.google.api.services.customsearch.v1.CustomSearchAPIRequestInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.configuration.properties.GoogleProperties;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Configuration
public class GoogleSearchConfiguration {

    @Bean
    public CustomSearchAPI googleCustomSearchAPI(GoogleProperties properties) throws GeneralSecurityException, IOException {
        return new CustomSearchAPI.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            null)
                .setApplicationName("Badley")
                .setCustomSearchAPIRequestInitializer(
                    new CustomSearchAPIRequestInitializer(properties.getToken()))
                .setHttpRequestInitializer(httpRequest -> {
                    // For some weird reason, the Google Custom Search API requires the cx parameter to be set in
                    // the URL. This sucks because we would need to set it in every request. Instead, we are
                    // overwriting the default HttpRequestInitializer to pre-populate the cx parameter.
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUri(httpRequest.getUrl().toURI());
                    uriBuilder.queryParamIfPresent("cx", Optional.ofNullable(properties.getEngineId()));
                    httpRequest.setUrl(new GenericUrl(uriBuilder.build(true).toUri()));
                })
                .build();
    }
}
