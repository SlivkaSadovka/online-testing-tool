package org.testingTool.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.testingTool.model.Role;
import org.testingTool.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByUid(String uid);

  Optional<UserEntity> findByUid(String uid);

  List<UserEntity> findByRole(Role role);

  @Query("SELECT u FROM UserEntity u WHERE u.role = :role AND u.createdAt < :expirationDate")
  List<UserEntity> findExpired(
      @Param("role") Role role, @Param("expirationDate") LocalDateTime expirationDate);
}
