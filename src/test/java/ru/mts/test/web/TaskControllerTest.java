package ru.mts.test.web;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.mts.test.controller.TaskExecutor;
import ru.mts.test.data.TaskRepository;
import ru.mts.test.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest
public class TaskControllerTest {

    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}");
    private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository repository;

    @MockBean
    private TaskExecutor taskExecutor;

    @Test
    public void testPostRequestCreatesTaskAndReturnsCorrectResponse() throws Exception {
        MvcResult result = mockMvc.perform(post("/task"))
                .andExpect(status().is(202))
                .andReturn();
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any(Task.class));
        Mockito.verify(taskExecutor, Mockito.times(1)).addTask(Mockito.any(Task.class));
        Assert.assertTrue(UUID_PATTERN.matcher(result.getResponse().getContentAsString()).matches());
    }

    @Test
    public void testGetRequestWithCorrectUUIDReturnsCorrectResponse() throws Exception {
        Task mockTask = new Task(UUID.randomUUID().toString());
        Mockito.when(repository.findById(Mockito.anyString())).thenReturn(Optional.of(mockTask));
        mockMvc.perform(get("/task/" + mockTask.getUuid()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(mockTask.getStatus().toString()))
                .andExpect(jsonPath("$.timestamp").value(FORMAT.format(mockTask.getTimestamp())));
    }

    @Test
    public void testGetRequestWithAbsentUUIDReturnsNotFoundResponse() throws Exception {
        mockMvc.perform(get("/task/" + UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetRequestWithIncorrectUUIDReturnsBadRequestResponse() throws Exception {
        mockMvc.perform(get("/task/123"))
                .andExpect(status().isBadRequest());
    }
}
