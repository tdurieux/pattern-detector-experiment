package edu.washington.escience.myria.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.LoggerFactory;

import edu.washington.escience.myria.DbException;
import edu.washington.escience.myria.perfenforce.PerfEnforceDriver;
import edu.washington.escience.myria.perfenforce.QueryMetaData;

/**
 * This is the class that handles API calls for PerfEnforce
 */
@Path("/perfenforce")
public final class PerfEnforceResource {

  @Context private PerfEnforceDriver perfenforceDriver;

  /** Logger. */
  protected static final org.slf4j.Logger LOGGER =
      LoggerFactory.getLogger(PerfEnforceResource.class);

  @POST
  @Path("/preparePSLA")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response prepareData() {
    perfenforceDriver.preparePSLA();
    return Response.noContent().build();
  }

  @POST
  @Path("/setTier")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response setTier(@FormDataParam("tier") final int queryRuntime) {
    perfenforceDriver.setTier(queryRuntime);
    return Response.noContent().build();
  }

  @POST
  @Path("/findSLA")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response findSLA(@FormDataParam("querySQL") final String querySQL) throws DbException {
    perfenforceDriver.findSLA(querySQL);
    return Response.noContent().build();
  }

  @POST
  @Path("/recordRealRuntime")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response recordRealRuntime(@FormDataParam("dataPointRuntime") final Double queryRuntime)
      throws DbException {
    perfenforceDriver.recordRealRuntime(queryRuntime);
    return Response.noContent().build();
  }

  @GET
  @Path("/isDonePSLA")
  public Response isDonePSLA() throws DbException {
    return Response.ok(perfenforceDriver.isDonePSLA()).build();
  }

  @GET
  @Path("/getClusterSize")
  public Response getClusterSize() throws DbException {
    return Response.ok(perfenforceDriver.getClusterSize()).build();
  }

  @GET
  @Path("/getPreviousQuery")
  public QueryMetaData getPreviousQuery() throws DbException {
    return perfenforceDriver.getPreviousQuery();
  }

  @GET
  @Path("/getCurrentQuery")
  public QueryMetaData getCurrentQuery() throws DbException {
    return perfenforceDriver.getCurrentQuery();
  }
}
