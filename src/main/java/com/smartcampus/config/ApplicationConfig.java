/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.config;

/**
 *
 * @author LENOVO
 */

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class establishes the base URI for all API resources.
 */
@ApplicationPath("/api/v1")
public class ApplicationConfig extends ResourceConfig {
    
    public ApplicationConfig() {
        // 1. Tell Jersey where your resources, mappers, and filters are
        // This scans the entire com.smartcampus package
        packages("com.smartcampus");

        // 2. Register the JSON Moxy Feature explicitly 
        // This fixes the "MessageBodyWriter not found" error
        register(MoxyJsonFeature.class);

        // 3. Optional: Enable advanced error tracing (helpful for debugging Part 5)
        // property(org.glassfish.jersey.server.ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        
        Logger.getLogger(ApplicationConfig.class.getName()).log(Level.INFO, "Smart Campus API Initialized successfully.");
    }
}
