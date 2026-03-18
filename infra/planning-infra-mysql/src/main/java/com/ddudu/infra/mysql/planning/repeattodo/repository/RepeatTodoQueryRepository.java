package com.ddudu.infra.mysql.planning.repeattodo.repository;

public interface RepeatTodoQueryRepository {

  void deleteAllByGoal(Long goalId);

}
