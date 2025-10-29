package com.tuorjp.wicket_java.service;

import com.tuorjp.wicket_java.model.Todo;
import com.tuorjp.wicket_java.repository.TodoRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class MongoDBService {
    @Autowired
    private TodoRepository todoRepository;

    @PostConstruct
    void setup() {
        log.info("+++ mongoDB is populated");
        todoRepository.deleteAll();

        for(int i = 1; i <= 8; i++) {
            Todo todo = new Todo();
            todo.setTitle("Todo number " + i);
            todo.setTitle("Body number " + i);
            save(todo);
        }
    }

    public void save(Todo todo) {
        todoRepository.save(todo);
    }

    public List<Todo> fetchAllItems(){
        return StreamSupport
                .stream(todoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }
}
