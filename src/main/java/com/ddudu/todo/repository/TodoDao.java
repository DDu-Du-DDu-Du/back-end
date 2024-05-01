package com.ddudu.todo.repository;

import com.ddudu.persistence.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoDao extends JpaRepository<TodoEntity, Long>, TodoDaoCustom {

}
