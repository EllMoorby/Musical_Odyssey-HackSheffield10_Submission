package com.unemployable.spotify_map.service;

import com.unemployable.spotify_map.dto.Album;
import com.unemployable.spotify_map.dto.Track;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class SpotifyService {

  @Value("${app.api.spotify.client-id}")
  private String clientId;

  @Value("${app.api.spotify.client-secret}")
  private String clientSecret;

  private final WebClient webClient = WebClient.create();
  private String accessToken;

  public String getAccessToken() {
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("grant_type", "client_credentials");

    String response = webClient.post()
            .uri("https://accounts.spotify.com/api/token")
            .header("Authorization", "Basic " +
                    java.util.Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes()))
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue(formData)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    return extractAccessTokenFromJson(response);
  }

  private String extractAccessTokenFromJson(String json) {
    try {
      JsonNode node = new ObjectMapper().readTree(json);
      return node.get("access_token").asText();
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse access token", e);
    }
  }

  public String searchTracks(String query) {
    String token = getAccessToken();

    return webClient.get()
            .uri("https://api.spotify.com/v1/search?q={query}&type=track&limit=10", query)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(String.class)
            .block();
  }

  public Track getTrack(String trackId) {
    String token = getAccessToken();

    String response = webClient.get()
            .uri("https://api.spotify.com/v1/tracks/{id}", trackId)
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    return convertToTrack(response);
  }

  private Track convertToTrack(String response) {
    try{
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response);

      Track track = new Track();

      if (root.get("id") == null){
        return null;
      }
      track.setId(root.get("id").asText());
      track.setName(root.get("name").asText());
      track.setPopularity(root.get("popularity").asInt());
      track.setDiscNumber(root.get("disc_number").asInt());
      track.setUrl(root.get("uri").asText());

      JsonNode albumNode = root.get("album");

      Album album = new Album();
      album.setId(albumNode.get("id").asText());
      album.setName(albumNode.get("name").asText());
      album.setTotalTracks(albumNode.get("total_tracks").asInt());
      album.setCoverImage(albumNode.get("images").get(0).get("url").asText());
      album.setCoverImageHeight(albumNode.get("images").get(0).get("height").asInt());
      album.setCoverImageWidth(albumNode.get("images").get(0).get("width").asInt());

      track.setAlbum(album);

      track.addArtist(albumNode.get("artists").get(0).get("name").asText(), albumNode.get("artists").get(0).get("id").asText());

      return track;
    }catch (Exception e){
      throw new RuntimeException("Failed to convert JSON to Track", e);
    }

  }

  private List<Track> convertToTracks(String response) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(response);

      JsonNode items = root.path("tracks").path("items");

      List<Track> tracks = new ArrayList<>();
      for (JsonNode trackNode : items) {
        Track track = convertToTrack(trackNode.toString());
        if (track != null) {
          tracks.add(track);
        }
      }
      return tracks;

    } catch (Exception e) {
      System.out.println("Error in convertToTracks: " + e.getMessage());
      throw new RuntimeException("Failed to convert JSON to Tracks", e);
    }
  }

  public Track getTrackFromArtistAndName(String artist, String trackName) {
    String token = getAccessToken();

    String query = "track:" + trackName + " artist:" + artist;


    String response = webClient.get()
            .uri(uriBuilder -> uriBuilder
                    .scheme("https")
                    .host("api.spotify.com")
                    .path("/v1/search")
                    .queryParam("q", query)
                    .queryParam("type", "track")
                    .queryParam("limit", "1")
                    .build())
            .header("Authorization", "Bearer " + token)
            .retrieve()
            .bodyToMono(String.class)
            .block();

    List<Track> tracks = convertToTracks(response);
    if (tracks.isEmpty()) {
      return null;
    }
    return tracks.get(0);

  }


}
