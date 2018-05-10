package se.group.backendgruppuppgift.tasker.resource;

import org.springframework.stereotype.Component;
import se.group.backendgruppuppgift.tasker.model.web.TaskWeb;
import se.group.backendgruppuppgift.tasker.service.TaskService;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Component
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Path("tasks")
public final class TaskResource {

    @Context
    private UriInfo uriInfo;

    private final TaskService service;

    public TaskResource(TaskService service) {
        this.service = service;
    }

    @POST
    public Response createTask(TaskWeb task) {
        TaskWeb result = service.createTask(task);

        return Response.created(URI.create(uriInfo
                .getAbsolutePathBuilder()
                .path(result.getId().toString())
                .toString()))
                .build();
    }

    @GET
    public List<TaskWeb> findTasksByStatus(@QueryParam("status") String status) {
        return service.findTasksByStatus(status);
    }

    @GET
    @Path("{id}")
    public Response findTask(@PathParam("id") Long id) {
        return service.findTask(id)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @GET
    public Response findTasksByParameter(@QueryParam("status") @DefaultValue("") String status, @QueryParam("text") @DefaultValue("") String text) {
// TODO: 2018-05-10 Business logic should be in service layer
        if (!status.isEmpty()) {
            return Response.ok(service.findTasksByStatus(status)).build();
        } else if (!text.isEmpty()) {
            return Response.ok(service.findTasksByText(text)).build();
        }

        return Response.status(NOT_FOUND).build();
    }

    @PUT
    @Path("{id}")
    public Response updateTask(@PathParam("id") Long id, TaskWeb task) {
        return service.updateTask(id, task)
                .map(Response::ok)
                .orElse(Response.status(NOT_FOUND))
                .build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteTask(@PathParam("id") Long id) {
        return service.deleteTask(id)
                .map(t -> Response.noContent())
                .orElse(Response.status(NOT_FOUND))
                .build();
    }
}
