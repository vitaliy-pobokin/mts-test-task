package ru.mts.test.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ru.mts.test.model.serialization.TimestampSerializer;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Task {
    @Id
    @JsonIgnore
    private String uuid;

    private Status status;

    @JsonSerialize(using = TimestampSerializer.class)
    private Long timestamp;

    public Task() {
    }

    public Task(String uuid) {
        this.uuid = uuid;
        this.status = Status.CREATED;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
