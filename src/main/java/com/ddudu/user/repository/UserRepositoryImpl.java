package com.ddudu.user.repository;

import static com.ddudu.user.domain.QUser.user;

import com.ddudu.user.domain.User;
import com.ddudu.user.domain.UserSearchType;
import com.querydsl.jpa.impl.JPAQuery;
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
  public List<User> findAllByKeywordAndSearchType(String keyword, UserSearchType userSearchType) {
    JPAQuery<User> query = jpaQueryFactory
        .selectFrom(user);

    switch (userSearchType) {
      case EMAIL -> query.where(user.email.address.eq(keyword));
      case NICKNAME -> query.where(user.nickname.eq(keyword));
      case OPTIONAL_USERNAME -> query.where(user.optionalUsername.eq(keyword));
    }

    return query.fetch();
  }

}
