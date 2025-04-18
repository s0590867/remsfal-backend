package de.remsfal.service.boundary.project;

import de.remsfal.core.api.project.ChatEndpoint;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.Optional;

import de.remsfal.core.api.project.DefectEndpoint;
import de.remsfal.core.json.project.TaskJson;
import de.remsfal.core.json.project.TaskListJson;
import de.remsfal.core.model.project.TaskModel;
import de.remsfal.core.model.project.TaskModel.Status;
import de.remsfal.service.control.TaskController;

/**
 * @author Alexander Stanik [alexander.stanik@htw-berlin.de]
 */
@RequestScoped
public class DefectResource extends ProjectSubResource implements DefectEndpoint {

    @Inject
    TaskController defectController;

    @Inject
    Instance<ChatEndpoint> chatEndpoint;

    @Override
    public TaskListJson getDefects(String projectId, String ownerId, Status status) {
        checkReadPermissions(projectId);
        if(ownerId == null || ownerId.isBlank()) {
            return TaskListJson.valueOf(defectController.getDefects(projectId, Optional.ofNullable(status)));
        } else {
            return TaskListJson.valueOf(defectController.getDefects(projectId, ownerId, Optional.ofNullable(status)));
        }
    }

    @Override
    public Response createDefects(String projectId, TaskJson defect) {
        checkWritePermissions(projectId);
        final TaskModel model = defectController.createDefect(projectId, principal, defect);
        final URI location = uri.getAbsolutePathBuilder().path(model.getId()).build();
        return Response.created(location)
            .type(MediaType.APPLICATION_JSON)
            .entity(TaskJson.valueOf(model))
            .build();
    }

    @Override
    public TaskJson getDefect(String projectId, String defectId) {
        checkReadPermissions(projectId);
        return TaskJson.valueOf(defectController.getDefect(projectId, defectId));
    }

    @Override
    public TaskJson updateDefect(String projectId, String defectId, TaskJson defect) {
        checkWritePermissions(projectId);
        return TaskJson.valueOf(defectController.updateDefect(projectId, defectId, defect));
    }

    @Override
    public void deleteDefect(String projectId, String defectId) {
        checkWritePermissions(projectId);
        defectController.deleteDefect(projectId, defectId);
    }

    @Override
    public ChatEndpoint getChatSessionResource() {
        return resourceContext.initResource(chatEndpoint.get());
    }

}