package com.tuorjp.wicket_java.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
public class Todo {
    @Id
    private String id;
    private String title;
    private String body;
    private LocalDateTime createdAt = LocalDateTime.now();
    private boolean closed = false;

    //não carrega nem persiste esse campo
    @Transient
    private boolean selected;
}
