package com.wtminsd.jar;

import java.util.*;

public class DistanceGraph {
    private Map<Event, List<EventEdge>> adjacencyList;

    public DistanceGraph() {
        adjacencyList = new HashMap<>();
    }

    public void addEvent(Event event) {
        if(!adjacencyList.containsKey(event)) {
            adjacencyList.put(event, new ArrayList<>());
        }
    }

    public void addEdge(Event from, Event to) {
        // makes sure events are added
        addEvent(from);
        addEvent(to);

        //only need edges to point in one direction
        adjacencyList.get(from).add(new EventEdge(to, from.distanceTo(to)));
    }

    public List<EventEdge> getEdges(Event event) {
        return adjacencyList.getOrDefault(event, new ArrayList<>());
    }

    public void displayGraph() {
        int count = 0;
        for (Map.Entry<Event, List<EventEdge>> entry : adjacencyList.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase("Home")) {
                System.out.println("Connections from: " + entry.getKey().getName());
                for (EventEdge edge : entry.getValue()) {
                    System.out.println("  -> " + edge.getTo().getName() + " (Distance: " + edge.getDistance() + " miles)");
                    count++;
                }
                System.out.println(count);
                break;
            }
        }
    }

    public List<Event> getEventsInDistance(int dist) {
        List<Event> newEvents = new ArrayList<>();

        for (Map.Entry<Event, List<EventEdge>> entry : adjacencyList.entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase("Home")) {
                for (EventEdge edge : entry.getValue()) {
                    if(edge.getDistance() <= dist) {
                        newEvents.add(edge.getTo());
                    }
                }
                break;
            }
        }
        return newEvents;
    }
}

class EventEdge {
    private Event to;
    private double distance;

    public EventEdge(Event to, double distance) {
        this.to = to;
        this.distance = distance;
    }

    public Event getTo() {
        return to;
    }

    public double getDistance() {
        return distance;
    }
}