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
import java.util.Date;

@XmlRootElement
public class SensorReading {
    private int id;
    private double value;
    private Date timestamp;

    public SensorReading() {
        this.timestamp = new Date(); // Default to current time
    }

    public SensorReading(int id, double value) {
        this.id = id;
        this.value = value;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
