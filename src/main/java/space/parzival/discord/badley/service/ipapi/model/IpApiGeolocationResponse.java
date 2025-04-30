package space.parzival.discord.badley.service.ipapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = IpApiGeolocationResponse.IpApiGeolocationResponseBuilder.class)
public class IpApiGeolocationResponse {
    /**
     * `success` or `fail`
     */
    @JsonProperty("status")
    String status;

    /**
     * included only when status is `fail`
     * Can be one of the following: `private range`, `reserved range`, `invalid query`
     */
    @JsonProperty("message")
    String message;

    @JsonProperty("continent")
    String continent;

    @JsonProperty("continentCode")
    String continentCode;

    @JsonProperty("country")
    String country;

    @JsonProperty("countryCode")
    String countryCode;

    @JsonProperty("region")
    String region;

    @JsonProperty("regionName")
    String regionName;

    @JsonProperty("city")
    String city;

    @JsonProperty("district")
    String district;

    @JsonProperty("zip")
    String zip;

    @JsonProperty("lat")
    float lat;

    @JsonProperty("lon")
    float lon;

    @JsonProperty("timezone")
    String timezone;

    @JsonProperty("offset")
    int offset;

    @JsonProperty("currency")
    String currency;

    @JsonProperty("isp")
    String isp;

    @JsonProperty("org")
    String org;

    @JsonProperty("as")
    String as;

    @JsonProperty("asname")
    String asName;

    @JsonProperty("reverse")
    String reverse;

    @JsonProperty("mobile")
    boolean mobile;

    @JsonProperty("proxy")
    boolean proxy;

    @JsonProperty("hosting")
    boolean hosting;

    @JsonProperty("query")
    String query;
}
