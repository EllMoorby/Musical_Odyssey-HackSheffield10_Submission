package com.unemployable.spotify_map.dto;

public class Album {
  private String id;
  private String name;
  private int totalTracks;
  private String coverImage;
  private int coverImageHeight;
  private int coverImageWidth;

  public Album() {}

  public Album(String id, String name, int totalTracks, String coverImage, int coverImageHeight, int coverImageWidth) {
    this.id = id;
    this.name = name;
    this.totalTracks = totalTracks;
    this.coverImage = coverImage;
    this.coverImageHeight = coverImageHeight;
    this.coverImageWidth = coverImageWidth;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getTotalTracks() {
    return totalTracks;
  }

  public void setTotalTracks(int totalTracks) {
    this.totalTracks = totalTracks;
  }

  public String getCoverImage() {
    return coverImage;
  }

  public void setCoverImage(String coverImage) {
    this.coverImage = coverImage;
  }

  public int getCoverImageHeight() {
    return coverImageHeight;
  }

  public void setCoverImageHeight(int coverImageHeight) {
    this.coverImageHeight = coverImageHeight;
  }

  public int getCoverImageWidth() {
    return coverImageWidth;
  }

  public void setCoverImageWidth(int coverImageWidth) {
    this.coverImageWidth = coverImageWidth;
  }
}
