package org.testingTool.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.testingTool.model.TestEntity;
import org.testingTool.model.UserEntity;
import org.testingTool.model.UserTestAccessEntity;

public interface UserTestAccessRepository extends CrudRepository<UserTestAccessEntity, Long> {
  @Query(
      "SELECT COUNT(uta) > 0 FROM UserTestAccessEntity uta "
          + "WHERE uta.user.id = :userId "
          + "AND uta.test.id = :testId "
          + "AND uta.isPassed = false")
  boolean hasAccess(@Param("userId") Long userId, @Param("testId") Long testId);

  Optional<UserTestAccessEntity> findByUser_IdAndTest_Id(Long userId, Long testId);

  Optional<UserTestAccessEntity> findByUserId(Long userId);

  List<UserTestAccessEntity> findAllByTest(TestEntity test);

  List<UserTestAccessEntity> findAllByUser(UserEntity user);
}
