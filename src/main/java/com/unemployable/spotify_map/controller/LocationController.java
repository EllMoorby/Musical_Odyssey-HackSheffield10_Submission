package com.unemployable.spotify_map.controller;

import com.unemployable.spotify_map.service.GeocodingService;
import com.unemployable.spotify_map.service.MusicLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController // Add this for API endpoints
public class LocationController {

  @Autowired
  private MusicLocationService musicLocationService;

  @Autowired
  private GeocodingService geocodingService;

  @GetMapping("/city")
  public Map<String, Object> getCityFromCoordinates(@RequestParam double lat,
                                                    @RequestParam double lng) {
    return musicLocationService.getMusicForLocation(lat, lng);
  }
  @GetMapping("/music")
  public Map<String, Object> getMusicForLocation(@RequestParam double lat,
                                                 @RequestParam double lng) {
    return musicLocationService.getMusicForLocation(lat, lng);
  }

  // Get just nearby cities
  @GetMapping("/nearby")
  public List<GeocodingService.NearbyPlace> getNearbyCities(@RequestParam double lat,
                                                            @RequestParam double lng,
                                                            @RequestParam(defaultValue = "50") double radiusKm,
                                                            @RequestParam(defaultValue = "5") int limit) {
    return geocodingService.getNearbyCities(lat, lng, radiusKm, limit);
  }
}
