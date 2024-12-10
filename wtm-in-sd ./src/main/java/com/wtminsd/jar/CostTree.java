package com.wtminsd.jar;

import java.util.ArrayList;
import java.util.List;

public class CostTree {
    private EventNode root;

    public void insert(Event event) {
        double cost = parseCost(event);
        root = insertRecursive(root, event, cost);
    }

    private EventNode insertRecursive(EventNode root, Event event, double cost) {
        if (root == null) {
            return new EventNode(event, cost);
        }
        if (cost < root.cost) {
            root.left = insertRecursive(root.left, event, cost);
        } else {
            root.right = insertRecursive(root.right, event, cost);
        }
        return root;
    } 

    public double parseCost(Event event) {
        String cost = event.getCost();
        if (cost == null || cost.trim().isEmpty()) {
            return Double.MAX_VALUE; // if somehow there's no cost
        }
    
        cost = cost.toLowerCase().trim();
    
        if (cost.equals("free") || cost.equals("$0")) {
            return 0.0;
        }
    
        // if its in a range
        if (cost.contains("-")) {
            String[] parts = cost.split("-");
            try {
                // make sure it isnt free-price
                if (parts[0].trim().equalsIgnoreCase("free")) {
                    return 0.0;
                }
                // use first number if paired
                return Double.parseDouble(parts[0].replaceAll("[^0-9.]", "").trim());
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse range: " + cost);
                return Double.MAX_VALUE;
            }
        }
    
        // if its only single price like $20
        try {
            return Double.parseDouble(cost.replaceAll("[^0-9.]", "").trim());
        } catch (NumberFormatException e) {
            System.err.println("Failed to parse single cost: " + cost);
            return Double.MAX_VALUE;
        }
    }

    public List<Event> getEventsUnder(double maxPrice) {
        List<Event> result = new ArrayList<>();
        searchUnderPrice(root, maxPrice, result);
        return result;
    }

    private void searchUnderPrice(EventNode root, double maxPrice, List<Event> result) {
        if (root == null) {
            return;
        }

        if (root.cost <= maxPrice) {
            result.add(root.event);
        }

        if (root.cost >= maxPrice || root.cost <= maxPrice) {
            searchUnderPrice(root.left, maxPrice, result);
        }
    
        if (root.cost < maxPrice) {
            searchUnderPrice(root.right, maxPrice, result);
        }
    }
    
}

class EventNode {
    Event event;
    double cost;
    EventNode left, right;

    public EventNode(Event event, double cost) {
        this.event = event;
        this.cost = cost;
        this.left = this.right;
    }
}
