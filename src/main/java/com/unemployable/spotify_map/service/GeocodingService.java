package com.unemployable.spotify_map.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class GeocodingService {

  private final WebClient webClient = WebClient.create();
  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final Map<String, String> COUNTRY_MAP = Map.of(
          "United States of America", "United States"
  );

  // Get city from coordinates
  public String getCityFromCoordinates(double lat, double lng) {
    try {
      String response = webClient.get()
              .uri("https://nominatim.openstreetmap.org/reverse?format=json&lat={lat}&lon={lng}&addressdetails=1",
                      lat, lng)
              .header("User-Agent", "YourMusicApp/1.0")
              .retrieve()
              .bodyToMono(String.class)
              .block();

      JsonNode root = objectMapper.readTree(response);
      JsonNode address = root.path("address");

      // Try different city fields
      String city = address.path("city").asText();
      if (city.isEmpty()) city = address.path("town").asText();
      if (city.isEmpty()) city = address.path("village").asText();
      if (city.isEmpty()) city = address.path("municipality").asText();

      return city.isEmpty() ? "Unknown Location" : city;

    } catch (Exception e) {
      return "Unknown Location";
    }
  }

  // Get nearby cities within radius
  public List<NearbyPlace> getNearbyCities(double lat, double lng, double radiusKm, int limit) {
    try {
      System.out.println("Getting nearby cities...");
      double degreeRange = radiusKm / 111.0; // Rough conversion km to degrees

      String url = String.format(
              "https://nominatim.openstreetmap.org/search?format=json&q=city&" +
                      "bounded=1&viewbox=%f,%f,%f,%f&limit=%d",
              lng - degreeRange,
              lat - degreeRange,
              lng + degreeRange,
              lat + degreeRange,
              limit
      );


      String response = webClient.get()
              .uri(url)
              .header("User-Agent", "unemployable_spotify_map/1.0")
              .retrieve()
              .bodyToMono(String.class)
              .block();


      System.out.println(response);
      JsonNode results = objectMapper.readTree(response);
      List<NearbyPlace> nearbyPlaces = new ArrayList<>();

      for (JsonNode result : results) {
        NearbyPlace place = new NearbyPlace();
        place.setName(result.path("display_name").asText().split(",")[0]);
        String[] parts = result.path("display_name").asText().split(",");
        String rawCountry = parts[parts.length - 1].trim();
        String country = COUNTRY_MAP.getOrDefault(rawCountry, rawCountry);
        place.setCountry(country);
        place.setLat(result.path("lat").asDouble());
        place.setLng(result.path("lon").asDouble());
        place.setType(result.path("type").asText());
        place.setImportance(result.path("importance").asDouble());
        place.setDistance(calculateDistance(lat, lng, place.getLat(), place.getLng()));

        nearbyPlaces.add(place);
      }

      nearbyPlaces.sort(Comparator.comparingDouble(NearbyPlace::getDistance));
      return nearbyPlaces;

    } catch (Exception e) {
      e.printStackTrace();
      return new ArrayList<>();
    }
  }

  // Calculate distance between two coordinates (Haversine formula)
  private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
    double earthRadius = 6371; // kilometers

    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);

    double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLng/2) * Math.sin(dLng/2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    return earthRadius * c;
  }

  public static class NearbyPlace {
    private String name;
    private String country;
    private double lat;
    private double lng;
    private String type;
    private double distance; // km
    private double importance;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }
    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }
    public double getImportance() { return importance; }
    public void setImportance(double importance) { this.importance = importance; }
  }
}
