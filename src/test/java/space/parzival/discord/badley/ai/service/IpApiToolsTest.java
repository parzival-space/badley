package space.parzival.discord.badley.ai.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import space.parzival.discord.badley.ai.tools.service.IpApiTools;
import space.parzival.discord.badley.configuration.properties.tools.IpApiProperties;
import space.parzival.discord.badley.service.ipapi.IpApiService;
import space.parzival.discord.badley.service.ipapi.model.IpApiGeolocationResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IpApiToolsTest {
    private IpApiService ipApiService;
    private IpApiProperties ipApiProperties;

    @BeforeEach
    void setUp() {
        ipApiService = mock(IpApiService.class);
        ipApiProperties = new IpApiProperties(true, IpApiProperties.IpVisibility.SHOW_IP_AND_LOCATION);
    }

    @Test
    void getIpInfo() {
        IpApiTools ipApiTools = new IpApiTools(ipApiService, ipApiProperties);

        IpApiGeolocationResponse mockResponse = getMockResponse();
        when(ipApiService.getGeolocationInfo(anyString())).thenReturn(mockResponse);

        String result = ipApiTools.getIpInfo("mock-ip");

        assertTrue(result.contains("query:"));
        assertTrue(result.contains("Continent: North America"));
        assertTrue(result.contains("Country: United States"));
    }

    @Test
    void getIpInfo_handleException() {
        IpApiTools ipApiTools = new IpApiTools(ipApiService, ipApiProperties);

        when(ipApiService.getGeolocationInfo(anyString())).thenThrow(new RuntimeException("Mock exception"));

        String result = ipApiTools.getIpInfo("mock-ip");

        assertTrue(result.contains("Mock exception"));
    }

    @Test
    void getOwnIpInfo() {
        IpApiTools ipApiTools = new IpApiTools(ipApiService, ipApiProperties);

        IpApiGeolocationResponse mockResponse = getMockResponse();
        when(ipApiService.getGeolocationInfo()).thenReturn(mockResponse);

        String result = ipApiTools.getOwnIpInfo();

        assertTrue(result.contains("query:"));
        assertTrue(result.contains("Continent: North America"));
        assertTrue(result.contains("Country: United States"));
    }

    @Test
    void getOwnIpInfo_handleException() {
        IpApiTools ipApiTools = new IpApiTools(ipApiService, ipApiProperties);

        when(ipApiService.getGeolocationInfo()).thenThrow(new RuntimeException("Mock exception"));

        String result = ipApiTools.getOwnIpInfo();

        assertTrue(result.contains("Mock exception"));
    }

    private IpApiGeolocationResponse getMockResponse() {
        return IpApiGeolocationResponse.builder()
            .status("success")
            .continent("North America")
            .continentCode("NA")
            .country("United States")
            .countryCode("US")
            .region("VA")
            .regionName("Virginia")
            .city("Ashburn")
            .district("Loudoun")
            .zip("20147")
            .lat(39.0437f)
            .lon(-77.4874f)
            .timezone("America/New_York")
            .offset(-14400)
            .currency("USD")
            .isp("Amazon Technologies Inc.")
            .org("Amazon.com, Inc.")
            .as("AS14618 Amazon.com, Inc.")
            .asName("AMAZON")
            .reverse("mock.domain.com")
            .mobile(false)
            .proxy(false)
            .hosting(true)
            .query("query")
            .build();
    }
}
