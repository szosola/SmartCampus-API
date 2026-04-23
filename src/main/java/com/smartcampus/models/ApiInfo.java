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
import java.util.Map;

@XmlRootElement
public class ApiInfo {
    private String name;
    private String version;
    private String description;
    private Map<String, String> links;

    public ApiInfo() {}

    public ApiInfo(String name, String version, String description, Map<String, String> links) {
        this.name = name;
        this.version = version;
        this.description = description;
        this.links = links;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Map<String, String> getLinks() { return links; }
    public void setLinks(Map<String, String> links) { this.links = links; }
}
