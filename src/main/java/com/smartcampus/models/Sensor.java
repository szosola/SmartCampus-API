/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.models;

/**
 *
 * @author LENOVO
 */

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Sensor {
    private int id;
    private String type; // e.g., "CO2", "Temperature"
    private double currentValue;
    private int roomId; // Foreign key-like reference to the Room

    public Sensor() {}

    public Sensor(int id, String type, double currentValue, int roomId) {
        this.id = id;
        this.type = type;
        this.currentValue = currentValue;
        this.roomId = roomId;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public double getCurrentValue() { return currentValue; }
    public void setCurrentValue(double currentValue) { this.currentValue = currentValue; }
    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }
}
