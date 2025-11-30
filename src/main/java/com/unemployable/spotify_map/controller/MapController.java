package com.unemployable.spotify_map.controller;

import com.unemployable.spotify_map.dto.Track;
import com.unemployable.spotify_map.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.Tuple;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Controller
public class MapController {
  @Autowired
  private SpotifyService spotifyService;

  @Autowired
  private MusicLocationService musicLocationService;

  @Autowired
  private MusicTransferService musicTransferService;

  @Autowired
  private GeocodingService geocodingService;

  private ObjectMapper objectMapper = new ObjectMapper();

  @GetMapping("/")
  public String home() {
    return "index";
  }

  @GetMapping("/map")
  public String map() {
    return "map";
  }

  @GetMapping("/about")
  public String about() {
    return "about";
  }

  @PostMapping("/results")
  public String results(@RequestParam Double lat1, @RequestParam Double lng1, @RequestParam Double lat2, @RequestParam Double lng2, Model model){
    model.addAttribute("lat1", lat1);
    model.addAttribute("lng1", lng1);
    model.addAttribute("lat2", lat2);
    model.addAttribute("lng2", lng2);
    return "loading";
  }


  @GetMapping("/results-final")
  public String showResults(@RequestParam Double lat1, @RequestParam Double lng1, @RequestParam Double lat2, @RequestParam Double lng2, Model model){

    final int EARTH_RADIUS_KM = 6371; // radius of Earth in kilometers

    double dLat = Math.toRadians(lat2 - lat1);
    double dLng = Math.toRadians(lng2 - lng1);

    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLng / 2) * Math.sin(dLng / 2);

    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

    double dist = EARTH_RADIUS_KM * c; // distance in km
    double songCount = 20;
    int songCountRounded = (int) Math.round(songCount);


    final int SAMPLES = 3;

    double radius = dist / ((SAMPLES-1) * 2); //Search radius in km and gap

    List<Tuple<Double, Double>> pointsAlongLine = new ArrayList<>(); // Points to search along

    for (int i = 0; i < SAMPLES; i++) {
      double fraction = (double) i / (SAMPLES - 1); // 0 to 1 along the line
      double lat = lat1 + (lat2 - lat1) * fraction;
      double lng = lng1 + (lng2 - lng1) * fraction;
      pointsAlongLine.add(new Tuple<>(lat,lng));
    }


    List<GeocodingService.NearbyPlace> nearbyPlaces = new ArrayList<>();
    for (Tuple<Double, Double> point : pointsAlongLine) { //Get cities at location
      nearbyPlaces.addAll(geocodingService.getNearbyCities(point._1(), point._2(), radius, 20));
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    nearbyPlaces.sort(Comparator.comparingDouble(GeocodingService.NearbyPlace::getImportance));

    List<GeocodingService.NearbyPlace> filteredPlaces = new ArrayList<>();
    List<String> filteredCities = new ArrayList<>();
    //Remove duplicates
    for (GeocodingService.NearbyPlace place : nearbyPlaces) {
      if (!filteredCities.contains(place.getName()) && !place.getName().equals("")) {
        filteredPlaces.add(place);
        filteredCities.add(place.getName());
      }
    }


    final int cityCount = 7;


    filteredPlaces.sort(Comparator.comparingDouble(GeocodingService.NearbyPlace::getImportance));

    int citiesWithSongs = 0;
    int songsPerCity = songCountRounded / cityCount;

    HashMap<GeocodingService.NearbyPlace, List<Track>> tracks = new HashMap<>();
    List<String> artists = new ArrayList<>();
    int numTracks = 0;

    List<String> trackNamesArtist = new ArrayList<>();
    for (GeocodingService.NearbyPlace place : filteredPlaces) {
      System.out.println(place.getName() + " " + place.getImportance());
      if (citiesWithSongs >= cityCount) {
        break;
      }
      List<Track> trackList = musicTransferService.getTopTracksByCity(place.getName(), place.getCountry(), songsPerCity);
      if (trackList.size() > 0) {
        List<Track> filteredTracks = new ArrayList<>();
        for (Track track : trackList) {
          if (!trackNamesArtist.contains(new String(track.getName()+" "+track.getArtists().keySet().toArray()[0]))){
            filteredTracks.add(track);
            trackNamesArtist.add(new String(track.getName()+" "+track.getArtists().keySet().toArray()[0]));
          }
          if (!artists.contains(track.getArtists().keySet().toArray()[0].toString())) {
            artists.add(track.getArtists().keySet().toArray()[0].toString());
          }
        }
        tracks.put(place, filteredTracks);
        numTracks += filteredTracks.size();
        citiesWithSongs++;
      }
    }


    List<Map<String, Object>> output = new ArrayList<>();

    for (Map.Entry<GeocodingService.NearbyPlace, List<Track>> entry : tracks.entrySet()) {
      Map<String, Object> obj = new HashMap<>();
      GeocodingService.NearbyPlace place = entry.getKey();

      obj.put("name", place.getName());
      obj.put("country", place.getCountry());
      obj.put("lat", place.getLat());
      obj.put("lng", place.getLng());
      obj.put("tracks", entry.getValue());

      output.add(obj);
    }

    String json = objectMapper.writeValueAsString(output);
    model.addAttribute("tracksJson", json);

    model.addAttribute("tracks",tracks);

    model.addAttribute("lat1", lat1);
    model.addAttribute("lng1", lng1);
    model.addAttribute("lat2", lat2);
    model.addAttribute("lng2", lng2);

    model.addAttribute("totalTracks", numTracks);
    model.addAttribute("uniqueArtists", artists.size());

    return "results";
  }


}
