package com.unemployable.spotify_map.controller;


import com.unemployable.spotify_map.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spotify")
public class SpotifyController {
  @Autowired
  private SpotifyService spotifyService;

  @GetMapping("/search")
  public String searchTracks(@RequestParam String query) {
    return spotifyService.searchTracks(query);
  }

  @GetMapping("/token")
  public String getToken() {
    return spotifyService.getAccessToken();
  }


}
