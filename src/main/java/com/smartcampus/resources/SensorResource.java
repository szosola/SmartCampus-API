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
import com.smartcampus.repository.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
public class SensorResource {
    
    // GET /sensors OR /sensors?type=CO2
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Sensor> getSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.sensors.values());
        
        if (type == null || type.isEmpty()) {
            return allSensors;
        }

        // Filtering logic using Java Streams
        return allSensors.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
    }

    // POST /sensors
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addSensor(Sensor sensor) {
        // Validation: Does the room exist?
        if (!DataStore.rooms.containsKey(sensor.getRoomId())) {
            // We will use a custom ExceptionMapper for this in Part 5
            return Response.status(422) // Unprocessable Entity
                    .entity("Error: Room ID " + sensor.getRoomId() + " does not exist.")
                    .build();
        }

        DataStore.sensors.put(sensor.getId(), sensor);
        
        // Increment the count in the room to prevent accidental room deletion
        int currentCount = DataStore.sensorCountPerRoom.getOrDefault(sensor.getRoomId(), 0);
        DataStore.sensorCountPerRoom.put(sensor.getRoomId(), currentCount + 1);

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }
}
