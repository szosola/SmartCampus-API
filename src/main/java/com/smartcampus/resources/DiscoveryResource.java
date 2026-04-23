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
import javax.xml.bind.annotation.XmlRootElement;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ws.rs.core.Response;

@Path("/") // This maps to the root of /api/v1
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDiscoveryInfo() {
        // Create the links map
        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        resources.put("self", "/api/v1");

        // Create the contact map
        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("owner", "Imasha Solomon");
        contact.put("email", "w2149635@westminster.ac.uk");

        // Use the wrapper class defined below to satisfy Moxy
        DiscoveryData data = new DiscoveryData(
            "Smart Campus API",
            "v1",
            "API for managing university rooms and sensors",
            contact,
            resources
        );

        return Response.ok(data).build();
    }
    
    @XmlRootElement
    public static class DiscoveryData {
        public String name;
        public String version;
        public String description;
        public Map<String, String> contact;
        public Map<String, String> resources;

        public DiscoveryData() {} // Required for JAXB

        public DiscoveryData(String name, String version, String description, 
                             Map<String, String> contact, Map<String, String> resources) {
            this.name = name;
            this.version = version;
            this.description = description;
            this.contact = contact;
            this.resources = resources;
        }
    }
}
