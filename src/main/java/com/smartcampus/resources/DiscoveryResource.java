/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

/**
 *
 * @author LENOVO
 */

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

@Path("/") // This maps to the root of /api/v1
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getDiscovery() {
        Map<String, Object> metadata = new HashMap<>();
        
        metadata.put("name", "Smart Campus API");
        metadata.put("version", "1.0.0");
        metadata.put("description", "API for managing university rooms and sensors.");
        metadata.put("contact", "admin@westminster.ac.uk");
        
        // Resource maps (links to other parts of your API)
        Map<String, String> links = new HashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        metadata.put("links", links);
        
        return metadata;
    }
}
