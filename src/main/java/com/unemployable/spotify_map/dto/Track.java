package com.unemployable.spotify_map.dto;

import java.util.HashMap;

public class Track {
  private String id;
  private String name;
  private Album album;
  private HashMap<String, String> artists = new HashMap<>();
  private int discNumber;
  private int popularity;
  private String url;

  public Track() {}

  public Track(String id, String name, Album album, HashMap<String, String> artists, int discNumber, int popularity) {
    this.id = id;
    this.name = name;
    this.album = album;
    this.artists = artists;
    this.discNumber = discNumber;
    this.popularity = popularity;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Album getAlbum() {
    return album;
  }

  public void setAlbum(Album album) {
    this.album = album;
  }

  public HashMap<String, String> getArtists() {
    return artists;
  }

  public void addArtist(String artistName, String artistId) {
    artists.put(artistId, artistName );
  }

  public int getDiscNumber() {
    return discNumber;
  }

  public void setDiscNumber(int discNumber) {
    this.discNumber = discNumber;
  }

  public int getPopularity() {
    return popularity;
  }

  public void setPopularity(int popularity) {
    this.popularity = popularity;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUrl() {return url;}
  public void setUrl(String url) {this.url = url;}
}
