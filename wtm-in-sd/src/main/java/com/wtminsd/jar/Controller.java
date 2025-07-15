package com.wtminsd.jar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class Controller {

    @Autowired
    private Filter filter;

    @Autowired
    private Scraper scraper;

    @PostMapping("/apply-filter")
    public void applyFilter(@RequestParam("distance") int distance, @RequestParam("cost") int cost, @RequestParam("tags") String tags) {
        try {
            filter.applyFilter(distance, cost, tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh() {
        System.out.println("Received POST");
        try {
            List<Event> events = scraper.read();
            Filter.setCachedEvents(events);
            return ResponseEntity.ok("Refreshed");
        } catch(Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to refresh");
        } 
    }

    @GetMapping("/events")
    public List<Event> getEvents() {
        return Filter.getCachedEvents();
    }

}


