package space.parzival.discord.badley.service.steam.model;

import lombok.*;
import space.parzival.discord.badley.service.steam.model.store.StoreAppDetailsEntry;

import java.util.Map;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@With
public class StoreAppDetailsResponse {
    int size;
    Map<String, StoreAppDetailsEntry> items;
}
