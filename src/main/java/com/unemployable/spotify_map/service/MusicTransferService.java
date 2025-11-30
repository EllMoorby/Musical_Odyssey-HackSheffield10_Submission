package com.unemployable.spotify_map.service;

import com.unemployable.spotify_map.dto.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.Tuple;

import java.util.ArrayList;
import java.util.List;

@Service
public class MusicTransferService {
  @Autowired
  private SpotifyService spotifyService;
  @Autowired
  private LastFMService lastFMService;

  public List<Track> getTopTracksByCity(String city, String country, int count){
    // Artist, Trackname
    List<Tuple<String,String>> artistTracks = lastFMService.getRandomTop30TracksByCity(city, country, count);
    System.out.println(artistTracks);
    List<Track> tracks = new ArrayList<>();

    for (Tuple<String, String> trackpair : artistTracks) {
      Track trackObj = spotifyService.getTrackFromArtistAndName(trackpair._1(), trackpair._2());
      if (trackObj != null) {
        tracks.add(trackObj);
      }
    }
    return tracks;
  }

}
