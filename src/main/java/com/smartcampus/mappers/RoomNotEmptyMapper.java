/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.smartcampus.mappers;

/**
 *
 * @author LENOVO
 */

import com.smartcampus.exceptions.RoomNotEmptyException;
import com.smartcampus.models.ErrorMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider // This tells Jersey to use this class
public class RoomNotEmptyMapper implements ExceptionMapper<RoomNotEmptyException> { 
    @Override
    public Response toResponse(RoomNotEmptyException ex) {
        ErrorMessage error = new ErrorMessage(ex.getMessage(), 409);
        return Response.status(Response.Status.CONFLICT)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
