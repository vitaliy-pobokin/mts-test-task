package ru.mts.test.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.mts.test.controller.TaskExecutor;
import ru.mts.test.data.TaskRepository;
import ru.mts.test.model.Task;
import ru.mts.test.model.errors.IncorrectUUIDException;
import ru.mts.test.model.errors.NotFoundException;

import java.util.Optional;
import java.util.UUID;

@RestController
public class TaskController {

    private final TaskRepository repository;
    private final TaskExecutor taskExecutor;

    @Autowired
    public TaskController(TaskRepository repository, TaskExecutor taskExecutor) {
        this.repository = repository;
        this.taskExecutor = taskExecutor;
    }

    @PostMapping("/task")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String createTask() {
        Task task = new Task(UUID.randomUUID().toString());
        repository.save(task);
        taskExecutor.addTask(task);
        return task.getUuid();
    }

    @GetMapping("/task/{uuid}")
    @ResponseStatus(HttpStatus.OK)
    public Task getTaskInfo(@PathVariable String uuid) {
        checkUUID(uuid);
        Optional<Task> task = repository.findById(uuid);
        return task.orElseThrow(NotFoundException::new);
    }

    private void checkUUID(String uuid) {
        try {
            UUID.fromString(uuid);
        } catch (IllegalArgumentException e) {
            throw new IncorrectUUIDException();
        }
    }
}
