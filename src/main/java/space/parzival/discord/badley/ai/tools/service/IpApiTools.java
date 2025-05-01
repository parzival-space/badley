package space.parzival.discord.badley.ai.tools.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import space.parzival.discord.badley.ai.tools.AiTools;
import space.parzival.discord.badley.configuration.properties.tools.IpApiProperties;
import space.parzival.discord.badley.service.ipapi.IpApiService;
import space.parzival.discord.badley.service.ipapi.model.IpApiGeolocationResponse;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.ipapi.enabled", havingValue = "true")
@AllArgsConstructor
public class IpApiTools implements AiTools {
    private final IpApiService ipApiService;
    private final IpApiProperties ipApiProperties;

    private static final String IP_INFO_TEMPLATE = """
        ${ip.address}:
        - Continent: ${ip.continent}
        - Continent Code: ${ip.continentCode}
        - Country: ${ip.country}
        - Country Code: ${ip.countryCode}
        - Region: ${ip.region}
        - Region Name: ${ip.regionName}
        - City: ${ip.city}
        - District: ${ip.district}
        - Zip: ${ip.zip}
        - Latitude: ${ip.lat}
        - Longitude: ${ip.lon}
        - Timezone: ${ip.timezone}
        - Offset: ${ip.offset}
        - National Currency: ${ip.currency}
        - ISP: ${ip.isp}
        - Organization: ${ip.org}
        - AS: ${ip.as}
        - AS Name: ${ip.asname}
        - Reverse DNS: ${ip.reverse}
        - Is Mobile: ${ip.mobile}
        - Is Proxy: ${ip.proxy}
        - Is Hosting: ${ip.hosting}
        """.stripIndent();

    @Tool(description = "Get information about an IP address.")
    public String getIpInfo(String ipAddress) {
        log.debug("AI is requesting information for IP address: {}", ipAddress);

        try {
            IpApiGeolocationResponse response = ipApiService.getGeolocationInfo(ipAddress);

            return StringSubstitutor.replace(IP_INFO_TEMPLATE, mapResponse(response));
        } catch (Exception e) {
            log.error("Error while fetching IP information for {}: {}", ipAddress, e.getMessage(), e);
            return "Error while fetching IP information: " + e.getMessage();
        }
    }

    @Tool(description = "Get ASSISTANT's IP and location information.")
    public String getOwnIpInfo() {
        log.debug("AI is requesting information for own IP address.");

        if (ipApiProperties.getExposeSelfVisibility().equals(IpApiProperties.IpVisibility.DISABLED))
            return "This feature is disabled by configuration.";

        try {
            IpApiGeolocationResponse response = ipApiService.getGeolocationInfo();

            Map<String, Object> ipData = mapResponse(response);
            if (ipApiProperties.getExposeSelfVisibility().equals(IpApiProperties.IpVisibility.SHOW_LOCATION))
                ipData.put("ip.address", "Hidden by configuration");

            return StringSubstitutor.replace(IP_INFO_TEMPLATE, ipData);
        } catch (Exception e) {
            log.error("Error while fetching own IP information: {}", e.getMessage(), e);
            return "Error while fetching IP information: " + e.getMessage();
        }
    }

    private Map<String, Object> mapResponse(IpApiGeolocationResponse response) {
        Map<String, Object> ipData = new HashMap<>();
        ipData.put("ip.address", response.getQuery());
        ipData.put("ip.continent", response.getContinent());
        ipData.put("ip.continentCode", response.getContinentCode());
        ipData.put("ip.country", response.getCountry());
        ipData.put("ip.countryCode", response.getCountryCode());
        ipData.put("ip.region", response.getRegion());
        ipData.put("ip.regionName", response.getRegionName());
        ipData.put("ip.city", response.getCity());
        ipData.put("ip.district", response.getDistrict());
        ipData.put("ip.zip", response.getZip());
        ipData.put("ip.lat", response.getLat());
        ipData.put("ip.lon", response.getLon());
        ipData.put("ip.timezone", response.getTimezone());
        ipData.put("ip.offset", response.getOffset());
        ipData.put("ip.currency", response.getCurrency());
        ipData.put("ip.isp", response.getIsp());
        ipData.put("ip.org", response.getOrg());
        ipData.put("ip.as", response.getAs());
        ipData.put("ip.asname", response.getAsName());
        ipData.put("ip.reverse", response.getReverse());
        ipData.put("ip.mobile", response.isMobile());
        ipData.put("ip.proxy", response.isProxy());
        ipData.put("ip.hosting", response.isHosting());
        return ipData;
    }
}
