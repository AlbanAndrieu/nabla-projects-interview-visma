package com.nabla.project.visma;

import javax.annotation.security.PermitAll;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/ping")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class PingResourceImpl {
  @GET
  @PermitAll
  // optional. Is needed if you protected your ressources f.e. with a SecurityInterceptor.
  public Response ping() {
    return Response.ok().build();
  }
}
