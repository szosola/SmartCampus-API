/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.resources;

/**
 *
 * @author LENOVO
 */

import com.smartcampus.models.Room;
import com.smartcampus.repository.DataStore;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/rooms")
public class RoomResource {
    
    // 1. GET /rooms - Get all rooms
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.rooms.values());
    }

    // 2. GET /rooms/{id} - Get a specific room
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getRoom(@PathParam("id") int id) {
        Room room = DataStore.rooms.get(id);
        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(room).build();
    }

    // 3. POST /rooms - Add a new room
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addRoom(Room room) {
        DataStore.rooms.put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    // 4. DELETE /rooms/{id} - Delete a room
    @DELETE
    @Path("/{id}")
    public Response deleteRoom(@PathParam("id") int id) {
        // Logic: Prevent deleting if room has sensors (Part 2 requirement)
        // We check our sensor count map (even if empty for now)
        Integer sensors = DataStore.sensorCountPerRoom.getOrDefault(id, 0);
        
        if (sensors > 0) {
            // We will improve this with a Custom Exception in Part 5
            return Response.status(Response.Status.CONFLICT)
                    .entity("Cannot delete room: It has active sensors.")
                    .build();
        }

        if (DataStore.rooms.remove(id) == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        return Response.noContent().build(); // Status 204
    }
}
