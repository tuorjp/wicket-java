package com.tuorjp.wicket_java.repository;

import com.tuorjp.wicket_java.model.Todo;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, String> {
}
