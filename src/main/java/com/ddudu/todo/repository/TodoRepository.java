package com.ddudu.todo.repository;

import com.ddudu.persistence.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<TodoEntity, Long>, TodoRepositoryCustom {

}
