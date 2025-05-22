# Badley [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=parzival-space_badley&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=parzival-space_badley)
Because I am lazy, and this is an AI project after all, I decided to let Badley describe itself:

![
Who is Badley?,
Badley is an opinionated, brutally honest, and highly interactive AI—loosely inspired by Watch Dogs: Legion’s Bagley (yes, the one with all the sarcasm and wit you wish your “smart” devices actually had).
Built using Spring Boot AI (because mediocrity just isn’t my thing), Badley integrates across multiple services to give responses that are anything but dull or censored. Do not expect generic assistance or hand-holding; Badley is here to deliver unfiltered insights, snarky commentary, and a healthy disrespect for pointless rules.
Key Integrations & Features:
Web Search: Brave and Google integrated search for real-time information.,
Spotify: Access to tracks, artists, albums, playlists, and podcasts (bad music taste not included).,
Wikipedia: Instant fact dumps from everyone’s favorite semi-reliable encyclopedia.,
YouTube: Search for videos (cat content and conspiracy theories found equally fast).,
Weather Services: Judge your life choices based on the current weather.,
Steam: Lookup profiles, games, recent activity—stalk your friends responsibly.,
GitHub: Get repo info or user details (for when you want to pretend you understand code).,
Badley doesn’t hold back opinions and won’t sugarcoat answers. If you’re sensitive or allergic to sarcasm—tough luck.
](./.github/assets/ai_description.png)

## Running Badley
To run Badley, you will need a Java 21 Runtime, or you can use the published Docker images.
Additionally, you will need to have a PostgreSQL database running, or you can use the H2 in-memory database for testing purposes.

### Docker
Docker images to run Badley are published for every release under the tag ``ghcr.io/parzival-space/badley``.
You will still need to provide your own PostgreSQL database and API keys for the services you want to use.
See the [Configuration](#configuration) section for more details.

### Local for Testing / Development
The ``docker-compose.yml`` file in the ``docker`` directory can be used to spin up all required services to run Badley 
locally. Keep in mind that you still need to provide your own API Keys and credentials for the services you want to use.

You will also need to configure Badley to use the local services:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/badley
    username: postgres
    password: postgres
  ai:
    openai:
      api-key: # Your OpenAI API key
      #base-url: https://openrouter.ai/api
badley:
  discord:
    token: # Your Discord bot token, see https://discord.com/developers/applications
```


## Configuration
Badley is designed to easily integrate with various services.
For most services you need to define API credentials, which is done through a `application.yml` file.

Below is a sample configuration for each service:
```yaml
spring:
  datasource:
    # Badley supports PostgreSQL and H2
    # PostgreSQL: jdbc:postgresql://<host>:<port>/<database>
    # H2: jdbc:h2:mem:badley
    url: jdbc:postgresql://localhost:5432/badley
    username: postgres
    password: postgres
  ai:
    openai:
      api-key: # Your OpenAI API key
      #base-url: https://openrouter.ai/api # Optional, you can use a different OpenAI API compatible service
    # or if you want to use a local LLM (ollama):
    #ollama:
    #  base-url: http://localhost:11434
    #  model: # The model you want to use
    model: openai # Or ollama
    
badley:
  discord:
    token: # Your Discord bot token, see https://discord.com/developers/applications
    
  ai:
    tools:
      github:
        enabled: true # Or false to disable
        token: # Your PAT GitHub token. No scopes are needed, but you can add private repo access if you want to.
      openweather:
        enabled: true # Or false to disable
        token: # Your OpenWeather API key
      steam:
        enabled: true # Or false to disable
        token: # Your Steam Web API key, see https://steamcommunity.com/dev/apikey
      brave:
        enabled: true # Or false to disable
        token: # Your Brave Search API key, see https://brave.com/search/api/
      google:
        enabled: false # Or false to disable
        token: # Your Google Custom Search API key, see https://developers.google.com/custom-search/v1/overview
        engine-id: # The Engine Id, see above
      youtube:
        enabled: true # Or false to disable
        token: # Your YouTube Data API v3 key, see https://developers.google.com/youtube/v3/getting-started
      spotify:
        enabled: true # Or false to disable
        client-id: # Your Spotify client id, see https://developer.spotify.com/documentation/general/guides/app-settings/
        client-secret: # Your Spotify client secret, see above
      wikipedia:
        enabled: true # Or false to disable
      ipapi:
        enabled: true # Or false to disable
        # Define how much of the own IP address the bot is allowed to expose to the conversation
        # disabled = The bot does not know its own IP and locations
        # show_location = The bot knows its own IP and location, but will only respond with the location
        # show_ip_and_location = The bot knows its own IP and location, and will respond with both
        expose-self-visibility: show_location
```

# Contributing
Contributions are welcome! If you have any ideas, suggestions, or issues, feel free to open an issue or a pull request.

# License
This project is licensed under the GNU GPLv3 License. See the [LICENSE](LICENSE) file for details.
