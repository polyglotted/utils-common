package io.polyglotted.common.test;

import io.polyglotted.common.web.AbstractHttpHandler;
import io.polyglotted.common.web.HttpRequest;
import io.polyglotted.common.web.HttpResponse;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.io.IOException;
import java.util.List;

@Path("/v2")
public class HttpDemoHandler extends AbstractHttpHandler {
    @POST @Path("{model}")
    public void saveRecords(HttpRequest request, HttpResponse response,
                            @PathParam("model") String model,
                            @QueryParam("realm") String realm,
                            @QueryParam("parent") String parent,
                            @QueryParam("repository") String repository,
                            @DefaultValue("true") @QueryParam("validate") boolean validate,
                            @DefaultValue("false") @QueryParam("replace_var") boolean replaceVars,
                            @DefaultValue("false") @QueryParam("replace_geo") boolean replaceGeos,
                            @DefaultValue("strict_save") @QueryParam("strategy") String strategy,
                            @DefaultValue("json") @QueryParam("format") String format,
                            @DefaultValue("false") @QueryParam("is_attachment") boolean isAttachment) throws IOException {
        //Do Nothing
    }

    @PUT @Path("**")
    public void saveRecord(HttpRequest request, HttpResponse response,
                           @HeaderParam("name") List<String> names,
                           @QueryParam("realm") String realm,
                           @QueryParam("timestamp") String timestamp,
                           @QueryParam("parent") String parent,
                           @QueryParam("repository") String repository,
                           @DefaultValue("true") @QueryParam("validate") boolean validate,
                           @DefaultValue("false") @QueryParam("replace_var") boolean replaceVars,
                           @DefaultValue("false") @QueryParam("replace_geo") boolean replaceGeos,
                           @DefaultValue("strict_save") @QueryParam("strategy") String strategy) throws IOException {
        //Do Nothing
    }

    @GET @Path("**")
    public void fetchRecord(HttpRequest request, HttpResponse response,
                            @QueryParam("realm") String realm,
                            @QueryParam("parent") String parent,
                            @QueryParam("repository") String repository,
                            @QueryParam("verbose") String verbose) throws IOException {
        //Do Nothing
    }


    @DELETE @Path("**")
    public void deleteRecord(HttpRequest request, HttpResponse response,
                             @QueryParam("realm") String realm,
                             @QueryParam("timestamp") String timestamp,
                             @QueryParam("parent") String parent,
                             @DefaultValue("strict_save") @QueryParam("strategy") String strategy) throws IOException {
        //Do Nothing
    }
}
