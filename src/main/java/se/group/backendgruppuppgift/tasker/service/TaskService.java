package se.group.backendgruppuppgift.tasker.service;

import org.springframework.stereotype.Service;
import se.group.backendgruppuppgift.tasker.model.Issue;
import se.group.backendgruppuppgift.tasker.model.Task;
import se.group.backendgruppuppgift.tasker.model.User;
import se.group.backendgruppuppgift.tasker.repository.IssueRepository;
import se.group.backendgruppuppgift.tasker.repository.TaskRepository;
import se.group.backendgruppuppgift.tasker.repository.UserRepository;
import se.group.backendgruppuppgift.tasker.service.exception.InvalidIssueException;
import se.group.backendgruppuppgift.tasker.service.exception.InvalidTaskException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static se.group.backendgruppuppgift.tasker.model.TaskStatus.*;

@Service
public final class TaskService {

    private final TaskRepository taskRepository;
    private final IssueRepository issueRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, IssueRepository issueRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.issueRepository = issueRepository;
        this.userRepository = userRepository;
    }

    public Task createTask(Task task) {
        validateTask(task);

        return taskRepository.save(new Task(task.getDescription(), task.getStatus()));
    }

    public Optional<Task> findTask(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> findTasksByParams(String status, String team, String user, String text, String issue) {
        List<Task> result;

        if (!isBlank(status) && isAllBlank(team, user, text, issue)) {
            result = findTasksByStatus(status);
        } else if (!isBlank(team) && isAllBlank(status, user, text, issue)) {
            result = findByTeamId(team);
        } else if (!isBlank(user) && isAllBlank(status, team, text, issue)) {
            result = findByUserNumber(user);
        } else if (!isBlank(text) && isAllBlank(status, team, user, issue)) {
            result = taskRepository.findByDescriptionContains(text);
        } else if (!isBlank(issue) && issue.equals("true") && isAllBlank(status, team, user, text)) {
            result = taskRepository.findByIssueNotNull();
        } else if (!isBlank(issue) && issue.equals("false") && isAllBlank(status, team, user, text)) {
            result = taskRepository.findByIssueNull();
        } else {
            result = taskRepository.findAll();
        }

        return result;
    }

    public Optional<Task> updateTask(Long id, Task task) {
        Optional<Task> taskResult = taskRepository.findById(id);

        if (taskResult.isPresent()) {
            Task updatedTask = taskResult.get();

            if (!isBlank(task.getDescription())) {
                updatedTask.setDescription(task.getDescription());
            }

            if (!isBlank(task.getStatus().toString())) {
                updatedTask.setStatus(task.getStatus());
            }

            return Optional.of(taskRepository.save(updatedTask));
        }

        return taskResult;
    }

    public Optional<Task> assignIssue(Long id, Issue issue) {
        validateIssue(issue);
        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {
            Task updatedTask = task.get();
            validateTaskStatus(updatedTask);
            Issue issueResult = issueRepository.save(new Issue(issue.getDescription()));

            updatedTask.setIssue(issueResult);
            updatedTask.setStatus(UNSTARTED);
            updatedTask = taskRepository.save(updatedTask);

            return Optional.of(updatedTask);
        }

        return task;
    }

    public Optional<Task> deleteTask(Long id) {
        Optional<Task> task = taskRepository.findById(id);

        if (task.isPresent()) {
            taskRepository.deleteById(id);
        }

        return task;
    }

    private List<Task> findTasksByStatus(String status) {
        status = prepareString(status);

        switch (status) {
            case "started":
                return taskRepository.findByStatus(STARTED);
            case "unstarted":
                return taskRepository.findByStatus(UNSTARTED);
            case "done":
                return taskRepository.findByStatus(DONE);
            default:
                return new ArrayList<>();
        }
    }

    private String prepareString(String string) {
        return string.trim().toLowerCase();
    }

    private void validateTask(Task task) {
        if (task.getStatus() == null || isBlank(task.getDescription())) {
            throw new InvalidTaskException("Missing/invalid values");
        }
    }

    private void validateTaskStatus(Task task) {
        if (task.getStatus() != DONE) {
            throw new InvalidTaskException("The current Task's status is not DONE");
        }
    }

    private void validateIssue(Issue issue) {
        if (isBlank(issue.getDescription())) {
            throw new InvalidIssueException("Description can not be empty");
        }
    }

    private List<Task> findByTeamId(String id) {
        List<Task> result = new ArrayList<>();

        if (id.matches("[0-9]+")) {
            List<User> users = userRepository.findUsersByTeamId(Long.parseLong(id));

            for (User u : users) {
                result.addAll(taskRepository.findByUserId(u.getId()));
            }
        }

        return result;
    }

    private List<Task> findByUserNumber(String userNumber) {
        List<Task> result = new ArrayList<>();

        if (userNumber.matches("[0-9]+")) {
            Optional<User> user = userRepository.findByUserNumber(Long.parseLong(userNumber));

            if (user.isPresent()) {
                result = taskRepository.findByUserId(user.get().getId());
            }
        }

        return result;
    }
}
