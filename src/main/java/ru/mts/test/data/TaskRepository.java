package ru.mts.test.data;

import org.springframework.data.repository.CrudRepository;
import ru.mts.test.model.Task;

public interface TaskRepository extends CrudRepository<Task, String> {
}
