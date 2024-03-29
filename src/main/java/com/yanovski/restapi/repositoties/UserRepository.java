package com.yanovski.restapi.repositoties;

import com.yanovski.restapi.models.Role;
import com.yanovski.restapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u INNER JOIN u.roles r WHERE r.roleName = :role")
    List<User> findAllByRole(Role.Name role);
}
