package com.ddudu.user.repository;

import static com.ddudu.user.domain.QUser.user;

import com.ddudu.user.domain.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepositoryCustom {

  private final JPAQueryFactory jpaQueryFactory;

  public UserRepositoryImpl(EntityManager em) {
    this.jpaQueryFactory = new JPAQueryFactory(em);
  }

  @Override
  public List<User> findAllByKeyword(String keyword) {
    String likePattern = "%" + keyword + "%";
    BooleanBuilder whereClause = new BooleanBuilder();
    
    whereClause.or(user.email.address.like(likePattern));
    whereClause.or(user.nickname.like(likePattern));
    whereClause.or(user.optionalUsername.like(likePattern));

    return jpaQueryFactory
        .selectFrom(user)
        .where(whereClause)
        .fetch();
  }

}
