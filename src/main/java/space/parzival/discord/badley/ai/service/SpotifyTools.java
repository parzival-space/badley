package space.parzival.discord.badley.ai.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.special.AlbumSimplifiedSpecial;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.EpisodeSimplified;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ShowSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import space.parzival.discord.badley.ai.AiTools;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "badley.ai.tools.spotify.enabled", havingValue = "true")
@AllArgsConstructor
public class SpotifyTools implements AiTools {
    private static final String ARTIST_INFO = """
        ${artist.name}:
        - Followers: ${artist.followers}
        - Genres: ${artist.genres}
        - Popularity: ${artist.popularity}%
        - External URLs: ${artist.externalUrls}
        - URL: ${artist.spotifyUrl}
        """.stripIndent();
    private static final String TRACK_INFO = """
        ${track.name}:
        - Duration: ${track.duration}
        - Explicit: ${track.explicit}
        - Available: ${track.playable}
        - Popularity: ${track.popularity}%
        - Artists: ${track.artists}
        - Album: ${track.album}
        - Track Number: ${track.trackNumber}
        - URL: ${track.spotifyUrl}
        """.stripIndent();
    private static final String ALBUM_INFO = """
        ${album.name}:
        - Type: ${album.type}
        - Artists: ${album.artists}
        - Release Date: ${album.releaseDate}
        - Total Tracks: ${album.tracks}
        - URL: ${album.spotifyUrl}
        """.stripIndent();
    private static final String PLAYLIST_INFO = """
        ${playlist.name}:
        - Description: ${playlist.description}
        - Owner: ${playlist.owner}
        - Tracks: ${playlist.tracks}
        - URL: ${playlist.spotifyUrl}
        """.stripIndent();
    private static final String SHOW_INFO = """
        ${show.name}:
        - Description: ${show.description}
        - Explicit: ${show.explicit}
        - Languages: ${show.languages}
        - Publisher: ${show.publisher}
        - URL: ${show.spotifyUrl}
        """.stripIndent();
    private static final String EPISODE_INFO = """
        ${episode.name}:
        - Description: ${episode.description}
        - Duration: ${episode.duration}
        - Explicit: ${episode.explicit}
        - Languages: ${episode.languages}
        - Available: ${episode.playable}
        - Release Date: ${episode.releaseDate}
        - URL: ${episode.spotifyUrl}
        """.stripIndent();
    private SpotifyApi spotifyApi;

    @Tool(description = "Search Spotify for artists.")
    public String searchSpotifyForArtists(String query) {
        log.debug("AI is searching Spotify for artists: {}", query);

        try {
            Paging<Artist> artistPaging = spotifyApi.searchArtists(query).build().execute();

            if (artistPaging == null || artistPaging.getTotal() == 0)
                return "No artists found for the given query.";

            return Arrays.stream(artistPaging.getItems())
                .filter(Objects::nonNull)
                .limit(10)
                .map(artist -> StringSubstitutor.replace(ARTIST_INFO, Map.of(
                    "artist.name", artist.getName(),
                    "artist.followers", artist.getFollowers().getTotal(),
                    "artist.genres", String.join(", ", artist.getGenres()),
                    "artist.popularity", artist.getPopularity(),
                    "artist.externalUrls", String.join(", ", artist.getExternalUrls().getExternalUrls().values()),
                    "artist.spotifyUrl", artist.getUri()
                )))
                .collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            log.error("Communication error with Spotify API: {}", e.getMessage(), e);
            return "Error communicating with Spotify API. Please try again later.";
        } catch (ParseException | SpotifyWebApiException e) {
            log.error("Spotify send an invalid response: {}", e.getMessage(), e);
            return "Error processing Spotify API response. " + e.getMessage();
        }
    }

    @Tool(description = "Search Spotify for tracks.")
    public String searchSpotifyForTracks(String query) {
        log.debug("AI is searching Spotify for tracks: {}", query);

        try {
            Paging<Track> trackPaging = spotifyApi.searchTracks(query).build().execute();

            if (trackPaging == null || trackPaging.getTotal() == 0)
                return "No tracks found for the given query.";

            return Arrays.stream(trackPaging.getItems())
                .filter(Objects::nonNull)
                .limit(10)
                .map(track -> StringSubstitutor.replace(TRACK_INFO, Map.of(
                    "track.name", track.getName(),
                    "track.duration", DurationFormatUtils.formatDuration(track.getDurationMs(), "m 'min' s 'sec'"),
                    "track.explicit", Boolean.TRUE.equals(track.getIsExplicit()) ? "Yes" : "No",
                    "track.playable", Boolean.TRUE.equals(track.getIsPlayable()) ? "Yes" : "No",
                    "track.popularity", track.getPopularity(),
                    "track.artists", Arrays.stream(track.getArtists())
                        .map(ArtistSimplified::getName)
                        .collect(Collectors.joining(", ")),
                    "track.album", track.getAlbum().getName(),
                    "track.trackNumber", track.getTrackNumber(),
                    "track.spotifyUrl", "https://open.spotify.com/track/" + track.getId()
                )))
                .collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            log.error("Communication error with Spotify API: {}", e.getMessage(), e);
            return "Error communicating with Spotify API. Please try again later.";
        } catch (ParseException | SpotifyWebApiException e) {
            log.error("Spotify send an invalid response: {}", e.getMessage(), e);
            return "Error processing Spotify API response. " + e.getMessage();
        }
    }

    @Tool(description = "Search Spotify for albums.")
    public String searchSpotifyForAlbums(String query) {
        log.debug("AI is searching Spotify for albums: {}", query);

        try {
            Paging<AlbumSimplifiedSpecial> albumPaging = spotifyApi.searchAlbumsSpecial(query).build().execute();

            if (albumPaging == null || albumPaging.getTotal() == 0)
                return "No albums found for the given query.";

            return Arrays.stream(albumPaging.getItems())
                .filter(Objects::nonNull)
                .limit(10)
                .map(album -> StringSubstitutor.replace(ALBUM_INFO, Map.of(
                    "album.name", album.getName(),
                    "album.type", album.getAlbumType().name(),
                    "album.artists", Arrays.stream(album.getArtists())
                        .map(ArtistSimplified::getName)
                        .collect(Collectors.joining(", ")),
                    "album.releaseDate", album.getReleaseDate(),
                    "album.spotifyUrl", "https://open.spotify.com/album/" + album.getId(),
                    "album.tracks", album.getTotalTracks())
                ))
                .collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            log.error("Communication error with Spotify API: {}", e.getMessage(), e);
            return "Error communicating with Spotify API. Please try again later.";
        } catch (ParseException | SpotifyWebApiException e) {
            log.error("Spotify send an invalid response: {}", e.getMessage(), e);
            return "Error processing Spotify API response. " + e.getMessage();
        }
    }

    @Tool(description = "Search Spotify for playlists.")
    public String searchSpotifyForPlaylists(String query) {
        log.debug("AI is searching Spotify for playlists: {}", query);

        try {
            Paging<PlaylistSimplified> playlistPaging = spotifyApi.searchPlaylists(query).build().execute();

            if (playlistPaging == null || playlistPaging.getTotal() == 0)
                return "No playlists found for the given query.";

            return Arrays.stream(playlistPaging.getItems())
                .filter(Objects::nonNull)
                .limit(10)
                .map(playlist -> StringSubstitutor.replace(PLAYLIST_INFO, Map.of(
                    "playlist.name", playlist.getName(),
                    "playlist.description", Optional.ofNullable(playlist.getDescription()).orElse("No description available"),
                    "playlist.owner", playlist.getOwner().getDisplayName(),
                    "playlist.tracks", playlist.getTracks().getTotal(),
                    "playlist.spotifyUrl", "https://open.spotify.com/playlist/" + playlist.getId())
                ))
                .collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            log.error("Communication error with Spotify API: {}", e.getMessage(), e);
            return "Error communicating with Spotify API. Please try again later.";
        } catch (ParseException | SpotifyWebApiException e) {
            log.error("Spotify send an invalid response: {}", e.getMessage(), e);
            return "Error processing Spotify API response. " + e.getMessage();
        }
    }

    @Tool(description = "Search Spotify for podcasts.")
    public String searchSpotifyForShows(String query) {
        log.debug("AI is searching Spotify for Shows: {}", query);

        try {
            Paging<ShowSimplified> showPaging = spotifyApi.searchShows(query).build().execute();

            if (showPaging == null || showPaging.getTotal() == 0)
                return "No shows found for the given query.";

            return Arrays.stream(showPaging.getItems())
                .filter(Objects::nonNull)
                .limit(10)
                .map(show -> StringSubstitutor.replace(SHOW_INFO, Map.of(
                    "show.name", show.getName(),
                    "show.description", Optional
                        .ofNullable(show.getDescription())
                        .orElse("No description available"),
                    "show.explicit", Boolean.TRUE.equals(show.getExplicit()) ? "Yes" : "No",
                    "show.languages", String.join(", ", show.getLanguages()),
                    "show.publisher", show.getPublisher(),
                    "show.spotifyUrl", "https://open.spotify.com/show/" + show.getId())
                )).collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            log.error("Communication error with Spotify API: {}", e.getMessage(), e);
            return "Error communicating with Spotify API. Please try again later.";
        } catch (ParseException | SpotifyWebApiException e) {
            log.error("Spotify send an invalid response: {}", e.getMessage(), e);
            return "Error processing Spotify API response. " + e.getMessage();
        }
    }

    @Tool(description = "Search Spotify for podcast episodes.")
    public String searchSpotifyForEpisodes(String query) {
        log.debug("AI is searching Spotify for episodes: {}", query);

        try {
            Paging<EpisodeSimplified> episodePaging = spotifyApi.searchEpisodes(query).build().execute();

            if (episodePaging == null || episodePaging.getTotal() == 0)
                return "No episodes found for the given query.";

            return Arrays.stream(episodePaging.getItems())
                .filter(Objects::nonNull)
                .limit(10)
                .map(episode -> StringSubstitutor.replace(EPISODE_INFO, Map.of(
                    "episode.name", episode.getName(),
                    "episode.description", Optional
                        .ofNullable(episode.getDescription())
                        .orElse("No description available"),
                    "episode.duration", DurationFormatUtils
                        .formatDuration(episode.getDurationMs(), "m 'min' s 'sec'"),
                    "episode.explicit", Boolean.TRUE.equals(episode.getExplicit()) ? "Yes" : "No",
                    "episode.languages", String.join(", ", episode.getLanguages()),
                    "episode.playable", Boolean.TRUE.equals(episode.getPlayable()) ? "Yes" : "No",
                    "episode.releaseDate", episode.getReleaseDate(),
                    "episode.spotifyUrl", "https://open.spotify.com/episode/" + episode.getId())
                )).collect(Collectors.joining("\n\n"));
        } catch (IOException e) {
            log.error("Communication error with Spotify API: {}", e.getMessage(), e);
            return "Error communicating with Spotify API. Please try again later.";
        } catch (ParseException | SpotifyWebApiException e) {
            log.error("Spotify send an invalid response: {}", e.getMessage(), e);
            return "Error processing Spotify API response. " + e.getMessage();
        }
    }
}

