/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.repository;

/**
 *
 * @author LENOVO
 */

import com.smartcampus.models.Room;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class DataStore {
    // Static ensures the data stays alive as long as the server is running
    public static Map<Integer, Room> rooms = new ConcurrentHashMap<>();
    
    // We will use this in Part 3 to check if a room has sensors
    public static Map<Integer, Integer> sensorCountPerRoom = new ConcurrentHashMap<>();

    static {
        // Adding some sample data for testing
        rooms.put(1, new Room(1, "Lab 101", 30));
        rooms.put(2, new Room(2, "Lecture Hall A", 100));
    }
}
