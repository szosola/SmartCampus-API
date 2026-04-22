/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;

/**
 *
 * @author LENOVO
 */

import com.smartcampus.exceptions.LinkedResourceNotFoundException;
import com.smartcampus.models.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundMapper implements ExceptionMapper<LinkedResourceNotFoundException> {
    @Override
    public Response toResponse(LinkedResourceNotFoundException ex) {
        // Status 422 is used for business logic/reference errors
        ErrorMessage error = new ErrorMessage(ex.getMessage(), 422);
        return Response.status(422) 
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
