/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

/**
 *
 * @author LENOVO
 */

import com.smartcampus.models.Sensor;
import com.smartcampus.models.SensorReading;
import com.smartcampus.repository.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import com.smartcampus.exceptions.SensorUnavailableException;

public class SensorReadingResource {
    
    private int sensorId;

    // We pass the sensorId from the parent to this sub-resource
    public SensorReadingResource(int sensorId) {
        this.sensorId = sensorId;
    }

    // GET /sensors/{id}/readings
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SensorReading> getReadings() {
        return DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
    }

    // POST /sensors/{id}/readings
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor parentSensor = DataStore.sensors.get(sensorId);
        
        // Logic for Part 5 (403 Forbidden)
        // Checks if the sensor is in maintenance before accepting readings
        if (parentSensor != null && "MAINTENANCE".equalsIgnoreCase(parentSensor.getStatus())) {
            throw new SensorUnavailableException("Sensor " + sensorId + " is under maintenance and cannot accept readings.");
        }
        
        // 1. Save the reading
        List<SensorReading> sensorReadings = DataStore.readings.getOrDefault(sensorId, new ArrayList<>());
        sensorReadings.add(reading);
        DataStore.readings.put(sensorId, sensorReadings);

        // 2. LOGIC: Update the parent sensor's currentValue automatically
        if (parentSensor != null) {
            parentSensor.setCurrentValue(reading.getValue());
        }

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}
