package space.parzival.discord.badley.ai.service;

import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.enums.AlbumType;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.miscellaneous.PlaylistTracksInformation;
import se.michaelthelin.spotify.model_objects.special.AlbumSimplifiedSpecial;
import se.michaelthelin.spotify.model_objects.specification.AlbumSimplified;
import se.michaelthelin.spotify.model_objects.specification.Artist;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.EpisodeSimplified;
import se.michaelthelin.spotify.model_objects.specification.ExternalUrl;
import se.michaelthelin.spotify.model_objects.specification.Followers;
import se.michaelthelin.spotify.model_objects.specification.Paging;
import se.michaelthelin.spotify.model_objects.specification.PlaylistSimplified;
import se.michaelthelin.spotify.model_objects.specification.ShowSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchArtistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchEpisodesRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchPlaylistsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchShowsRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.SearchTracksRequest;
import se.michaelthelin.spotify.requests.data.search.simplified.special.SearchAlbumsSpecialRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SpotifyToolsTest {
    private SpotifyTools spotifyTools;
    private SpotifyApi spotifyApi;

    @BeforeEach
    void setUp() {
        spotifyApi = mock(SpotifyApi.class);
        spotifyTools = new SpotifyTools(spotifyApi);
    }

    @Test
    void searchSpotifyForArtists() throws IOException, ParseException, SpotifyWebApiException {
        Paging<Artist> artists = new Paging.Builder<Artist>()
            .setTotal(1)
            .setItems(new Artist[]{
                new Artist.Builder()
                    .setName("Test Artist")
                    .setFollowers(new Followers.Builder().setTotal(1).build())
                    .setGenres("Test Genre")
                    .setPopularity(0)
                    .setExternalUrls(new ExternalUrl.Builder().setExternalUrls(Map.of()).build())
                    .setUri("spotify:artist:test")
                    .build()
            })
            .build();

        SearchArtistsRequest mockRequest = mock(SearchArtistsRequest.class);
        SearchArtistsRequest.Builder builder = mock(SearchArtistsRequest.Builder.class);
        when(spotifyApi.searchArtists(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(artists);

        String result = spotifyTools.searchSpotifyForArtists("Test Artist");

        assertNotNull(result);
        assertLinesMatch(
            List.of(
                "Test Artist:",
                "- Followers: 1",
                "- Genres: Test Genre",
                "- Popularity: 0%",
                "- External URLs: ",
                "- URL: spotify:artist:test"
            ),
            Arrays.asList(result.split("\\n"))
        );
    }

    @Test
    void searchSpotifyForTracks() throws IOException, ParseException, SpotifyWebApiException {
        Paging<Track> tracks = new Paging.Builder<Track>()
            .setTotal(1)
            .setItems(new Track[]{
                new Track.Builder()
                    .setId("test")
                    .setName("Test Track")
                    .setDurationMs(300000)
                    .setExplicit(true)
                    .setIsPlayable(true)
                    .setPopularity(187)
                    .setArtists(new ArtistSimplified.Builder()
                        .setName("Test Artist")
                        .build())
                    .setAlbum(new AlbumSimplified.Builder()
                        .setName("Test Album")
                        .build())
                    .setTrackNumber(6)
                    .setUri("spotify:track:test")
                    .build()
            })
            .build();

        SearchTracksRequest mockRequest = mock(SearchTracksRequest.class);
        SearchTracksRequest.Builder builder = mock(SearchTracksRequest.Builder.class);
        when(spotifyApi.searchTracks(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(tracks);

        String result = spotifyTools.searchSpotifyForTracks("Test Track");
        assertNotNull(result);
        assertLinesMatch(
            List.of(
                "Test Track:",
                "- Duration: 5 min 0 sec",
                "- Explicit: Yes",
                "- Available: Yes",
                "- Popularity: 187%",
                "- Artists: Test Artist",
                "- Album: Test Album",
                "- Track Number: 6",
                "- URL: https://open.spotify.com/track/test"
            ),
            Arrays.asList(result.split("\\n"))
        );
    }

    @Test
    void searchSpotifyForAlbums() throws IOException, ParseException, SpotifyWebApiException {
        Paging<AlbumSimplifiedSpecial> albums = new Paging.Builder<AlbumSimplifiedSpecial>()
            .setTotal(1)
            .setItems(new AlbumSimplifiedSpecial[]{
                new AlbumSimplifiedSpecial.Builder()
                    .setName("Test Album")
                    .setReleaseDate("2023-01-01")
                    .setTotalTracks(10)
                    .setArtists(new ArtistSimplified.Builder()
                        .setName("Test Artist")
                        .build())
                    .setAlbumType(AlbumType.ALBUM)
                    .setId("test")
                    .build()
            })
            .build();

        SearchAlbumsSpecialRequest mockRequest = mock(SearchAlbumsSpecialRequest.class);
        SearchAlbumsSpecialRequest.Builder builder = mock(SearchAlbumsSpecialRequest.Builder.class);
        when(spotifyApi.searchAlbumsSpecial(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(albums);

        String result = spotifyTools.searchSpotifyForAlbums("Test Album");
        assertNotNull(result);
        assertLinesMatch(
            List.of(
                "Test Album:",
                "- Type: ALBUM",
                "- Artists: Test Artist",
                "- Release Date: 2023-01-01",
                "- Total Tracks: 10",
                "- URL: https://open.spotify.com/album/test"
            ),
            Arrays.asList(result.split("\\n"))
        );
    }

    @Test
    void searchSpotifyForPlaylists() throws IOException, ParseException, SpotifyWebApiException {
        Paging<PlaylistSimplified> playlistSimplifiedPaging = new Paging.Builder<PlaylistSimplified>()
            .setTotal(1)
            .setItems(new PlaylistSimplified[]{
                new PlaylistSimplified.Builder()
                    .setName("Test Playlist")
                    .setDescription("Test Description")
                    .setOwner(new User.Builder()
                        .setDisplayName("Test Owner")
                        .build()
                    )
                    .setTracks(new PlaylistTracksInformation.Builder()
                        .setTotal(200)
                        .build())
                    .setId("test")
                    .build()
            })
            .build();

        SearchPlaylistsRequest mockRequest = mock(SearchPlaylistsRequest.class);
        SearchPlaylistsRequest.Builder builder = mock(SearchPlaylistsRequest.Builder.class);
        when(spotifyApi.searchPlaylists(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(playlistSimplifiedPaging);

        String result = spotifyTools.searchSpotifyForPlaylists("Test Playlist");
        assertNotNull(result);
        assertLinesMatch(
            List.of(
                "Test Playlist:",
                "- Description: Test Description",
                "- Owner: Test Owner",
                "- Tracks: 200",
                "- URL: https://open.spotify.com/playlist/test"
            ),
            Arrays.asList(result.split("\\n"))
        );
    }

    @Test
    void searchSpotifyForShows() throws IOException, ParseException, SpotifyWebApiException {
        Paging<ShowSimplified> showsPaging = new Paging.Builder<ShowSimplified>()
            .setTotal(1)
            .setItems(new ShowSimplified[]{
                new ShowSimplified.Builder()
                    .setName("Test Show")
                    .setPublisher("Test Publisher")
                    .setDescription("Test Description")
                    .setLanguages(new String[]{"DE"})
                    .setId("test")
                    .build()
            })
            .build();

        SearchShowsRequest mockRequest = mock(SearchShowsRequest.class);
        SearchShowsRequest.Builder builder = mock(SearchShowsRequest.Builder.class);
        when(spotifyApi.searchShows(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(showsPaging);

        String result = spotifyTools.searchSpotifyForShows("Test Show");
        assertNotNull(result);
        assertLinesMatch(
            List.of(
                "Test Show:",
                "- Description: Test Description",
                "- Explicit: No",
                "- Languages: DE",
                "- Publisher: Test Publisher",
                "- URL: https://open.spotify.com/show/test"
            ),
            Arrays.asList(result.split("\\n"))
        );
    }

    @Test
    void searchSpotifyForEpisodes() throws IOException, ParseException, SpotifyWebApiException {
        Paging<EpisodeSimplified> episodesPaging = new Paging.Builder<EpisodeSimplified>()
            .setTotal(1)
            .setItems(new EpisodeSimplified[]{
                new EpisodeSimplified.Builder()
                    .setName("Test Episode")
                    .setDescription("Test Description")
                    .setDurationMs(300000)
                    .setExplicit(true)
                    .setLanguages("DE")
                    .setPlayable(true)
                    .setReleaseDate("2023-01-01")
                    .setId("test")
                    .build()
            })
            .build();

        SearchEpisodesRequest mockRequest = mock(SearchEpisodesRequest.class);
        SearchEpisodesRequest.Builder builder = mock(SearchEpisodesRequest.Builder.class);
        when(spotifyApi.searchEpisodes(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(mockRequest);
        when(mockRequest.execute()).thenReturn(episodesPaging);

        String result = spotifyTools.searchSpotifyForEpisodes("Test Episode");
        assertNotNull(result);
        assertLinesMatch(
            List.of(
                "Test Episode:",
                "- Description: Test Description",
                "- Duration: 5 min 0 sec",
                "- Explicit: Yes",
                "- Languages: DE",
                "- Available: Yes",
                "- Release Date: 2023-01-01",
                "- URL: https://open.spotify.com/episode/test"
            ),
            Arrays.asList(result.split("\\n"))
        );
    }
}
