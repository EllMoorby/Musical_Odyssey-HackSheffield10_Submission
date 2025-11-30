package com.unemployable.spotify_map.service;

import com.unemployable.spotify_map.dto.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MusicLocationService {
  @Autowired
  private GeocodingService geocodingService;

  @Autowired
  private MusicTransferService musicTransferService;

  public Map<String, Object> getMusicForLocation(double lat, double lng) {
    Map<String, Object> result = new HashMap<>();

    try {
      // Get city name from coordinates
      String city = geocodingService.getCityFromCoordinates(lat, lng);
      result.put("city", city);
      result.put("coordinates", Map.of("lat", lat, "lng", lng));

      // Try to get music for this city
      List<Track> tracks = musicTransferService.getTopTracksByCity(city, "United Kingdom", 3);

      result.put("tracks", tracks);
      result.put("tracksFound", tracks.size());
      result.put("success", true);

    } catch (Exception e) {
      result.put("success", false);
      result.put("error", "No music data available for this location");
    }

    return result;
  }
}
