package com.main.icrsbackend.repository;

import com.main.icrsbackend.model.User;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    // ✅ MAIN SEARCH (name/email)
    @Query("""
        SELECT u FROM User u
        WHERE
            LOWER(TRIM(CONCAT(u.fname,' ',u.lname))) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.fname) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.lname) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
    """)
    List<User> searchPeople(@Param("q") String q);

    // ✅ SEARCH EXCLUDING CURRENT USER (recommended for your SearchPeople page)
    @Query("""
        SELECT u FROM User u
        WHERE u.id <> :meId AND (
            LOWER(TRIM(CONCAT(u.fname,' ',u.lname))) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.fname) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.lname) LIKE LOWER(CONCAT('%', :q, '%'))
            OR LOWER(u.email) LIKE LOWER(CONCAT('%', :q, '%'))
        )
    """)
    List<User> searchPeopleExcludeMe(@Param("meId") Long meId, @Param("q") String q);
}