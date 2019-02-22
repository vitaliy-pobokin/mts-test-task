package ru.mts.test.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mts.test.data.TaskRepository;
import ru.mts.test.model.Status;
import ru.mts.test.model.Task;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RunWith(SpringRunner.class)
public class TaskExecutorTest {

    @MockBean
    private TaskRepository repository;

    @Captor
    private ArgumentCaptor<Task> captor;

    private TaskExecutor sut;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        this.sut = new TaskExecutor(executor, repository);
    }

    @Test
    public void testWhenTaskSubmittedAndStartsExecutingThenStatusAndTimestampUpdated() {
        Task task = Mockito.spy(new Task(UUID.randomUUID().toString()));
        Status initialStatus = task.getStatus();
        sut.addTask(task);
        Mockito.verify(repository, Mockito.timeout(200).times(1)).save(captor.capture());
        Mockito.verify(task, Mockito.times(1)).setTimestamp(Mockito.anyLong());
        Assert.assertNotEquals(initialStatus, captor.getValue().getStatus());
        Assert.assertEquals(Status.RUNNING, captor.getValue().getStatus());
    }

    @Test
    public void testWhenTaskCompletesThenStatusAndTimestampUpdated() {
        Task task = new Task(UUID.randomUUID().toString());
        Status initialStatus = task.getStatus();
        Long initialTimestamp = task.getTimestamp();
        sut.setTaskRunTimeMs(100L);
        sut.addTask(task);
        Mockito.verify(repository, Mockito.timeout(200).times(2)).save(captor.capture());
        Assert.assertTrue(initialTimestamp < captor.getAllValues().get(1).getTimestamp());
        Assert.assertNotEquals(initialStatus, captor.getAllValues().get(1).getStatus());
        Assert.assertEquals(Status.FINISHED, captor.getAllValues().get(1).getStatus());
    }

    @Test
    public void testWhenTaskExecutorShutdownsThenTaskStatusSetToError() {
        Task task = new Task(UUID.randomUUID().toString());
        Status initialStatus = task.getStatus();
        Long initialTimestamp = task.getTimestamp();
        sut.addTask(task);
        sut.shutdownNow();
        Mockito.verify(repository, Mockito.timeout(200).times(2)).save(captor.capture());
        Assert.assertTrue(initialTimestamp < captor.getAllValues().get(1).getTimestamp());
        Assert.assertNotEquals(initialStatus, captor.getAllValues().get(1).getStatus());
        Assert.assertEquals(Status.ERROR, captor.getAllValues().get(1).getStatus());
    }
}
