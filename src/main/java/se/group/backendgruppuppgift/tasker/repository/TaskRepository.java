package se.group.backendgruppuppgift.tasker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import se.group.backendgruppuppgift.tasker.model.Task;
import se.group.backendgruppuppgift.tasker.model.TaskStatus;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByUserId(Long id);

    List<Task> findByDescriptionContains(String text);

    List<Task> findByIssueNotNull();

    List<Task> findByIssueNull();

    List<Task> findAllByUserUserNumber(Long userNumber);
}
