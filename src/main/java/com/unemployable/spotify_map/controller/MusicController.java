package com.unemployable.spotify_map.controller;

import com.unemployable.spotify_map.dto.Track;
import com.unemployable.spotify_map.service.SpotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/spotify")
public class MusicController {
  @Autowired
  private SpotifyService spotifyService;

  @GetMapping("/track")
  public Track getTrackByArtistAndName(
          @RequestParam String artist,
          @RequestParam String trackName) {
    return spotifyService.getTrackFromArtistAndName(artist, trackName);
  }


}
