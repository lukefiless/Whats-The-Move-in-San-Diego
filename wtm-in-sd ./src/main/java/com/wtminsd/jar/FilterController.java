package com.wtminsd.jar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:8080")
public class FilterController {

    @Autowired
    private Filter filter;

    @PostMapping("/apply-filter")
    public void applyFilter(@RequestParam("distance") int distance, @RequestParam("cost") int cost, @RequestParam("tags") String tags) {
        try {
            // runJavaFile(distance, cost, tags);  // Call the method to run your Java file
            filter.applyFilter(distance, cost, tags);
            // return "Filter applied with distance: " + distance + 
            //     "\nFilter applied with cost: " + cost + 
            //     "\nFilter applied with tags: " + tags;
        } catch (Exception e) {
            e.printStackTrace();
            // return "Error applying filter: " + e.getMessage();
        }
    }

    private void runJavaFile(int distance, int cost, String tags) throws Exception {
        System.out.println("Sending " + distance + ", " + cost + ", " + tags);
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "target/jar-0.0.1-SNAPSHOT.jar", String.valueOf(distance), String.valueOf(cost), tags);
        Process process = processBuilder.start();
        process.waitFor();
    }
}


