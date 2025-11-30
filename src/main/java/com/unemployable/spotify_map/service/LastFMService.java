package com.unemployable.spotify_map.service;

import ch.qos.logback.core.joran.sanity.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.yaml.snakeyaml.util.Tuple;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class LastFMService {

  @Value("${app.api.lastfm.key}")
  private String APIKey;

  @Value("${app.api.lastfm.secret}")
  private String secret;

  private final WebClient webClient = WebClient.create();
  private final ObjectMapper objectMapper = new ObjectMapper();
  private final String BASE_URL = "http://ws.audioscrobbler.com/2.0/";

  public List<Tuple<String, String>> getTopTracksByCity(String city, String country, int limit) {
    try {
      String response = webClient.get()
              .uri(BASE_URL + "?method=geo.gettoptracks&city={city}&country={country}&limit={limit}&api_key={apiKey}&format=json",
                      city, country, limit, APIKey)
              .retrieve()
              .bodyToMono(String.class)
              .block();

      JsonNode root = objectMapper.readTree(response);
      JsonNode tracks = root.path("tracks").path("track");

      List<Tuple<String, String>> trackNames = new ArrayList<>();
      for (JsonNode track : tracks) {
        String trackName = track.path("name").asText();
        String artist = track.path("artist").path("name").asText();

        trackNames.add(new Tuple<>(artist, trackName));
      }

      return trackNames;

    } catch (Exception e) {
      throw new RuntimeException("Failed to get top tracks for city: " + city, e);
    }
  }

  public List<Tuple<String, String>> getRandomTop30TracksByCity(String city, String country, int num) {
    try {
      final int limit = 30;

      if (num > limit) {
        return getTopTracksByCity(city, country, num);
      }

      String response = webClient.get()
              .uri(BASE_URL + "?method=geo.gettoptracks&city={city}&country={country}&limit={limit}&api_key={apiKey}&format=json",
                      city, country, limit, APIKey)
              .retrieve()
              .bodyToMono(String.class)
              .block();

      JsonNode root = objectMapper.readTree(response);
      JsonNode tracks = root.path("tracks").path("track");

      List<Tuple<String, String>> trackNames = new ArrayList<>();
      for (JsonNode track : tracks) {
        String trackName = track.path("name").asText();
        String artist = track.path("artist").path("name").asText();

        trackNames.add(new Tuple<>(artist, trackName));
      }

      Collections.shuffle(trackNames);
      int max = Math.min(num, trackNames.size());
      return trackNames.subList(0, max);

    } catch (Exception e) {
      throw new RuntimeException("Failed to get top tracks for city: " + city, e);
    }
  }
}
