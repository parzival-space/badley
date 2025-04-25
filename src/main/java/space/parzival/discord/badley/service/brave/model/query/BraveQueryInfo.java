package space.parzival.discord.badley.service.brave.model.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
@JsonDeserialize(builder = BraveQueryInfo.BraveQueryInfoBuilder.class)
public class BraveQueryInfo {
    @JsonProperty("original")
    String originalQuery;

    @JsonProperty("show_strict_warning")
    boolean showStrictWarning;

    @JsonProperty("is_navigational")
    boolean navigational;

    @JsonProperty("is_news_breaking")
    boolean newsBreaking;

    @JsonProperty("spellcheck_off")
    boolean spellcheckOff;

    @JsonProperty("country")
    String country;

    @JsonProperty("bad_results")
    boolean badResults;

    @JsonProperty("should_fallback")
    boolean shouldFallback;

    @JsonProperty("postal_code")
    String postalCode;

    @JsonProperty("city")
    String city;

    @JsonProperty("header_country")
    String headerCountry;

    @JsonProperty("more_results_available")
    boolean moreResultsAvailable;

    @JsonProperty("state")
    String state;
}
