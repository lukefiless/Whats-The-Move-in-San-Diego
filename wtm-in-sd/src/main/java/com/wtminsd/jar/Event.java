package com.wtminsd.jar;

import java.util.Objects;
import java.util.List;

public class Event {
    private String name;
    private Venue venue;
    private String time;
    private String desc;
    private String cost;
    private String smlImg;
    private String lrgImg;
    private List<String> tags;

    public Event() {
    }

    public Event(String name, Venue venue, String time, String desc, String cost, String smlImg, String lrgImg, List<String> tags) {
        this.name = name;
        this.venue = venue;
        this.time = time;
        this.desc = desc;
        this.cost = cost;
        this.smlImg = smlImg;
        this.lrgImg = lrgImg;
        this.tags = tags;
    }

    public String getName() { return name; }
    public String getTime() { return time; }
    public String getDesc() { return desc; }
    public Venue getVenue() { return venue; }
    public String getCost() { return cost; }
    public String getSmlImg() { return smlImg; }
    public String getLrgImg() { return lrgImg; }
    public List<String> getTags() { return tags; }

    public double distanceTo(Event center) {
        // System.out.println("Testing the distance between" + center.getVenue().getName() + " and " + getName());
        return venue.distanceTo(center.getVenue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return name.equals(event.name) && venue.equals(event.venue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, venue);
    }
}

class Venue {
    private String name;
    private double lat;
    private double lng;
    private String address;
    private String rating;

    public Venue() {
        
    }

    public Venue(String name, double lat, double lng, String address, String rating) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.address = address;
        this.rating = rating;
    }

    public String getName() { return name; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public String getAddress() { return address; }
    public String getRating() { return rating; }

    public double distanceTo(Venue other) {
        final int R = 3959;
        double lat1 = Math.toRadians(this.lat);
        double lng1 = Math.toRadians(this.lng);
        double lat2 = Math.toRadians(other.lat);
        double lng2 = Math.toRadians(other.lng);

        double dlat = lat2 - lat1;
        double dlon = lng2 - lng1;
        
        double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dlon / 2) * Math.sin(dlon / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double result = R * c;
        return result; // Distance in miles
    }
}
