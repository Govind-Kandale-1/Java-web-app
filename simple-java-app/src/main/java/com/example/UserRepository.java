package com.example;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    // Find user by name
    Optional<User> findByName(String name);

    // Find user by email
    Optional<User> findByEmail(String email);

    // Find users by name containing (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);

    // Find users by email containing
    List<User> findByEmailContaining(String emailPart);

    // Custom query to find users by name or email
    @Query("{ '$or': [ { 'name': { '$regex': ?0, '$options': 'i' } }, { 'email': { '$regex': ?0, '$options': 'i' } } ] }")
    List<User> findByNameOrEmailContaining(String searchTerm);

    // Check if user exists by email
    boolean existsByEmail(String email);
}

