package com.wtminsd.jar;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.springframework.stereotype.Component;

@Component
public class Writer {

    public void writeEvents(List<Event> events, String path) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try (FileWriter writer = new FileWriter(path)) {
            objectMapper.writeValue(writer, events);
            System.out.println("Event data written to .json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeEvents(List<Event> events) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File file = new File(getClass().getClassLoader().getResource("static/events.json").toURI());
        
            try (FileWriter writer = new FileWriter(file)) {
                objectMapper.writeValue(writer, events);
                System.out.println("Wrote to: " + file.getAbsolutePath());
            }
        
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
    
    public void onLoad() {
        Scraper scraper = new Scraper();
        List<Event> events = scraper.read();
        writeEvents(events);
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