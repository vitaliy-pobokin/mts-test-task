package ru.mts.test.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.mts.test.data.TaskRepository;
import ru.mts.test.model.Status;
import ru.mts.test.model.Task;

import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;

@Component
public class TaskExecutor {

    private static final Long DEFAULT_TASK_RUN_TIME_MS = 120_000L;

    private final ExecutorService executor;
    private final TaskRepository repository;
    private Long taskRunTimeMs;

    @Autowired
    public TaskExecutor(ExecutorService executor, TaskRepository repository) {
        this.executor = executor;
        this.repository = repository;
        this.taskRunTimeMs = DEFAULT_TASK_RUN_TIME_MS;
    }

    @PreDestroy
    public void shutdownNow() {
        executor.shutdownNow();
    }

    public void addTask(Task task) {
        executor.execute(() -> {
            updateStatusAndSave(task, Status.RUNNING);
            try {
                Thread.sleep(taskRunTimeMs);
                updateStatusAndSave(task, Status.FINISHED);
            } catch (InterruptedException e) {
                updateStatusAndSave(task, Status.ERROR);
            }
        });
    }

    public synchronized void setTaskRunTimeMs(Long taskRunTimeMs) {
        this.taskRunTimeMs = taskRunTimeMs;
    }

    private void updateStatusAndSave(Task task, Status status) {
        task.setStatus(status);
        task.setTimestamp(System.currentTimeMillis());
        repository.save(task);
    }
}
