package com.wtminsd.jar;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class Filter {
    public List<Event> readEvents(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(new File(filePath), new TypeReference<List<Event>>() {});
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void applyFilter(int distance, int cost, String tags) {
        String filePath = "src/main/resources/static/events.json";
        List<Event> events = readEvents(filePath);
        Writer writer = new Writer();
        DistanceGraph graph = new DistanceGraph();
        CostTree tree = new CostTree();

        // String distance = args[0];
        // String cost = args[1];
        // String tags = args[2];
        System.out.println("Received: " + distance + ", " + cost + ", " + tags);

        Event home = new Event("Home", new Venue("Home", 32.774799, -117.071869, "Home", "5/5"), "24hrs", "Home", "Free", "null", "null", null);
        graph.addEvent(home);

        for(Event event : events) {
            graph.addEdge(home, event);
        }

        List<Event> result = graph.getEventsInDistance(distance);

        for(Event event : result) {
            tree.insert(event);
        }

        result = tree.getEventsUnder(cost);

        List<Event> eventsByTag = getMatchingEvents(result, tags);

        writer.writeEvents(eventsByTag, "src/main/resources/static/filtered.json");
    }

    public List<Event> getMatchingEvents(List<Event> events, String tagsString) {
        // Split the tags string into an array of tags
        String[] tagsArray = tagsString.split(",");

        // Create a Set to hold unique events
        Set<Event> uniqueEvents = new HashSet<>();

        // Group events by tags
        HashMap<String, List<Event>> eventsByTag = groupEventsByTags(events);

        // Iterate over the tags
        for (String tag : tagsArray) {
            // Trim whitespace and check if the tag is not empty
            tag = tag.trim();
            if (!tag.isEmpty() && eventsByTag.containsKey(tag)) {
                // Add all events for this tag to the set
                uniqueEvents.addAll(eventsByTag.get(tag));
            }
        }

        // Convert the set back to a list
        return new ArrayList<>(uniqueEvents);
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
