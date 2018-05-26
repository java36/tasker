package se.group.backendgruppuppgift.tasker.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public final class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Column
    private LocalDate unstartedDate;

    @Column
    private LocalDate startedDate;

    @Column
    private LocalDate doneDate;

    @ManyToOne
    private User user;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(unique = true)
    private Issue issue;

    protected Task() {
    }

    public Task(String description) {
        this.description = description;
        this.status = TaskStatus.UNSTARTED;
        setUnstartedDate();
    }

    public Task(String description, TaskStatus status) {
        this.description = description;
        this.status = status;

        switch(status){
            case UNSTARTED:
                setUnstartedDate();
                break;
            case STARTED:
                setStartedDate();
                break;
            case DONE:
                setDoneDate();
                break;
        }
    }

    public Long getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public LocalDate getUnstartedDate() {
        return unstartedDate;
    }

    public void setUnstartedDate() {
        this.unstartedDate = LocalDate.now();
        this.startedDate = null;
        this.doneDate = null;
    }

    public LocalDate getStartedDate() {
        return startedDate;
    }

    public void setStartedDate() {
        this.startedDate = LocalDate.now();
        this.doneDate = null;
    }

    public LocalDate getDoneDate() {
        return doneDate;
    }

    public void setDoneDate() {
        this.doneDate = LocalDate.now();
    }


    @Override
    public String toString() {
        return String.format(
                "Task[id=%d, description='%s', status='%s', user=%s, issue=%s]",
                id, description, status, user, issue
        );
    }
}
