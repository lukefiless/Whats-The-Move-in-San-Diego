package com.wtminsd.jar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class Scraper {
    private final String BASE_URL = "https://www.sandiegoreader.com";
    private final String GOOGLE_API_KEY = "AIzaSyBrVJcEjxWMAqtDrFsPuUWLZeB8_DLwqMk";
    
    public List<Event> read() {
        List<Event> events = new ArrayList<>();
        int count = 0;
        int maxRetries = 3; // max retries
        try {
            String url = "https://www.sandiegoreader.com/events/";
            Document doc = Jsoup.connect(url).get();
    
            Elements eventContainers = doc.select("div.event-single-top");
    
            for (Element event : eventContainers) {
                count++;
                String title = event.select("a.event-title").text();
                String location = event.select("div.event-location").text();
                String time = event.select("div.event-time-single div").text();
                String imageUrl = event.select("div.event-avatar").attr("style");
                String imageUrlClean = imageUrl.replace("background-image: url('", "").replace("');", "");
                String eventLink = BASE_URL + event.select("a.event-title").attr("href");
    
                // retry logic
                Document eventDetailsDoc = null;
                int retries = 0;
                while (retries < maxRetries) {
                    try {
                        eventDetailsDoc = Jsoup.connect(eventLink).get();
                        break;
                    } catch (IOException e) {
                        retries++;
                        System.err.println("Error fetching event details for " + title + ". Retry " + retries + " of " + maxRetries);
                        if (retries == maxRetries) {
                            System.err.println("Failed to fetch event details for " + title + " after " + maxRetries + " retries.");
                            continue; 
                        }
                    }
                }
    
                if (eventDetailsDoc == null) {
                    // skip if eventDetailsDoc is still null
                    continue;
                }
    
                String description = eventDetailsDoc.select("#place-description p").text();
                String cost = "0";
    
                Element costElement = eventDetailsDoc.select("span:contains(Cost:)").first();
                if (costElement != null) {
                    cost = costElement.select(".value-label").text().trim();
                    if (cost.isEmpty()) {
                        cost = "0";
                    }
                }
    
                String lowQualityImageUrl = eventDetailsDoc.select("#lead-art__image").attr("style");
                if (lowQualityImageUrl.contains("background-image: url('")) {
                    lowQualityImageUrl = lowQualityImageUrl.split("url\\('")[1].split("'")[0]; // Extract the image URL
                }
    
                String highQualityImageUrl = eventDetailsDoc.select("#lead-art__zoom").attr("href");
                String address = eventDetailsDoc.select("address div").text();
    
                if (location.isEmpty()) {
                    location = eventDetailsDoc.select("address div").text();
                }

                List<String> tags = new ArrayList<>();
                Elements tagElements = eventDetailsDoc.select("div.article-tags a.article-tag div");
                for (Element tagElement : tagElements) {
                    tags.add(tagElement.text());
                }
                System.out.println(tags);
    
                double[] coordinates = getCoordinates(location, address);
                if (coordinates != null) {
                    events.add(new Event(title, new Venue(location, coordinates[0], coordinates[1], address, "5/5"), time, description, cost, imageUrlClean, highQualityImageUrl, tags));
                } else {
                    System.out.println("Location or Address not found.");
                }
                System.out.println("-----");
            }
    
        } catch (IOException e) {
            System.out.println("Some error occurred.");
            e.printStackTrace();
        }
        System.out.println("Events counted: " + count);
        return events;
    }    

    // make api call
    @SuppressWarnings("deprecation")
    public double[] getCoordinates(String location, String fullAddress) {
        if ((location == null || location.isEmpty()) && (fullAddress == null || fullAddress.isEmpty())) {
            System.err.println("Both location and fullAddress are null or empty");
            return null;
        }

        try {
            // use full address if available
            String query = (fullAddress != null && !fullAddress.isEmpty()) ? fullAddress : location;
            if (query == null || query.isEmpty()) {
                System.err.println("Query is null or empty after fallback");
                return null;
            }

            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
            String urlString = "https://maps.googleapis.com/maps/api/geocode/json?address=" 
                + encodedQuery + "&key=" + GOOGLE_API_KEY;

            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            StringBuilder response = new StringBuilder();
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
            }

            // parse json jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.toString());
            String status = jsonResponse.get("status").asText();

            if ("OK".equals(status)) {
                JsonNode locationObj = jsonResponse.get("results").get(0).get("geometry").get("location");
                double lat = locationObj.get("lat").asDouble();
                double lng = locationObj.get("lng").asDouble();
                return new double[]{lat, lng};
            } else {
                System.err.println("API returned status: " + status);
                if (jsonResponse.has("error_message")) {
                    System.err.println("Error message: " + jsonResponse.get("error_message").asText());
                }
            }
        } catch (Exception e) {
            System.err.println("Exception occurred while fetching coordinates: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}