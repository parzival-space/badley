package space.parzival.discord.badley.service.ipapi;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import space.parzival.discord.badley.service.ipapi.model.IpApiGeolocationResponse;

@Service
@ConditionalOnProperty(value = "badley.ai.tools.ipapi.enabled", havingValue = "true")
public class IpApiService {
    private final RestTemplate apiRestTemplate;
    private static final String FIELDS = String.join(",",
        "status", "message", "continent", "continentCode", "country", "countryCode", "region", "regionName",
        "city", "district", "zip", "lat", "lon", "timezone", "offset", "currency", "isp", "org", "as", "asname",
        "reverse", "mobile", "proxy", "hosting", "query");

    public IpApiService(RestTemplateBuilder restTemplateBuilder) {
        this.apiRestTemplate = restTemplateBuilder
            .rootUri("http://ip-api.com/json") // NOSONAR - this api is not https
            .defaultHeader("Accept", "application/json")
            .build();
    }

    /**
     * Requests information about the given IP address.
     *
     * @param ipAddress The IP address to request information about.
     */
    public IpApiGeolocationResponse getGeolocationInfo(String ipAddress) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.newInstance()
            .path("/" + ipAddress)
            .queryParam("fields", FIELDS)
            .queryParam("lang", "en");

        return apiRestTemplate.getForObject(uriBuilder.toUriString(), IpApiGeolocationResponse.class);
    }

    /**
     * Requests information about the current IP address.
     */
    public IpApiGeolocationResponse getGeolocationInfo() {
        return getGeolocationInfo("");
    }
}
