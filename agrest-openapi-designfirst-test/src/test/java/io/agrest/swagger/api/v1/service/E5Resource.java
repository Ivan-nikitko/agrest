package io.agrest.swagger.api.v1.service;

import io.agrest.it.fixture.cayenne.E5;

import io.agrest.AgRequest;
import io.agrest.DataResponse;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import io.agrest.Ag;
import io.agrest.SimpleResponse;

@Path("/")
public class E5Resource {

    @Context
    private Configuration config;

    @POST
    @Path("/v1/e5")
    @Consumes({ "application/json" })
    public DataResponse<E5> create(String e5) {

        AgRequest agRequest = AgRequest.builder()
                .build();

        return Ag.create(E5.class, config)
                 .request(agRequest)
                 .syncAndSelect(e5);
    }

    @GET
    @Path("/v1/e5")
    @Produces({ "application/json" })
    public DataResponse<E5> getAll() {

        AgRequest agRequest = AgRequest.builder()
                .build();

        return Ag.select(E5.class, config)
                 .request(agRequest)
                 .get();
    }

}
