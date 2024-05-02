package com.ddudu.persistence.dao.todo;

import com.ddudu.persistence.entity.TodoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoDao extends JpaRepository<TodoEntity, Long>, TodoDaoCustom {

}
