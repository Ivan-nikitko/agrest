package io.agrest.swagger.api.v1.service;

import io.agrest.it.fixture.cayenne.E20;

import io.agrest.AgRequest;
import io.agrest.DataResponse;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

import io.agrest.Ag;
import io.agrest.SimpleResponse;

@Path("/")
public class E20Resource {

    @Context
    private Configuration config;

    @POST
    @Path("/v1/e20")
    @Consumes({ "application/json" })
    public DataResponse<E20> create(String e20, @QueryParam("exclude") List<io.agrest.protocol.Exclude> excludes) {

        AgRequest agRequest = AgRequest.builder()
                .excludes(excludes)
                .build();

        return Ag.create(E20.class, config)
                 .request(agRequest)
                 .syncAndSelect(e20);
    }

    @DELETE
    @Path("/v1/e20/{name}")
    public SimpleResponse deleteByName(@PathParam("name") String name) {

        return Ag.delete(E20.class, config)
                 .id(name)
                 .delete();
    }

    @GET
    @Path("/v1/e20/{name}")
    @Produces({ "application/json" })
        public DataResponse<E20> getOneByName(@PathParam("name") String name, @QueryParam("exclude") List<io.agrest.protocol.Exclude> excludes) {

        AgRequest agRequest = AgRequest.builder()
                .excludes(excludes)
                .build();

        return Ag.select(E20.class, config)
                 .byId(name)
                 .request(agRequest)
                 .get();
    }

    @PUT
    @Path("/v1/e20/{name}")
    @Consumes({ "application/json" })
    public DataResponse<E20> updateByName(@PathParam("name") String name, String e20) {

        AgRequest agRequest = AgRequest.builder()
                .build();

        return Ag.idempotentCreateOrUpdate(E20.class, config)
                 .id(name)
                 .request(agRequest)
                 .syncAndSelect(e20);
    }

}
