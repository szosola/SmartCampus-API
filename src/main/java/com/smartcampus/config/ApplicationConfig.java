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

/**
 * This class establishes the base URI for all API resources.
 */
@ApplicationPath("/api/v1")
public class ApplicationConfig extends Application {
    // No code is needed inside here. 
    // The annotation handles the setup.
}
