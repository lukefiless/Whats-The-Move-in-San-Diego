package com.wtminsd.jar;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class Writer {
    public void main(String[] args) {
        WebScraper scraper = new WebScraper();
        List<Event> events = scraper.read();
        DistanceGraph graph = new DistanceGraph();
        CostTree tree = new CostTree();

        Event home = new Event("Home", new Venue("Home", 32.774799, -117.071869, "Home", "5/5"), "24hrs", "Home", "Free", "null", "null", null);
        graph.addEvent(home);

        for(Event event: events) {
            graph.addEdge(home, event);
            tree.insert(event);
        }

        HashMap<String, List<Event>> eventsByTag = groupEventsByTags(events);

        for (Map.Entry<String, List<Event>> entry : eventsByTag.entrySet()) {
            System.out.println("Tag: " + entry.getKey());
            for (Event event : entry.getValue()) {
                System.out.println(" - " + event.getName());
            }
        }

        writeEvents(events, "src/main/resources/events.json");
    }

    public void writeEvents(List<Event> events, String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // For pretty printing
        try (FileWriter writer = new FileWriter(path)) {
            objectMapper.writeValue(writer, events);
            System.out.println("Event data written to .json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, List<Event>> groupEventsByTags(List<Event> events) {
        HashMap<String, List<Event>> eventsByTag = new HashMap<>();

        for (Event event : events) {
            List<String> tags = event.getTags();

            for (String tag : tags) {
                eventsByTag.putIfAbsent(tag, new ArrayList<>());
                eventsByTag.get(tag).add(event);
            }
        }

        return eventsByTag;
    }
}