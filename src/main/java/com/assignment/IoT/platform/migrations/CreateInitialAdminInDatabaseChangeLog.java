package com.assignment.IoT.platform.migrations;

import com.assignment.IoT.platform.model.User;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@ChangeUnit(order = "1", id = "admin-init-seeder", author = "Gangadhar")
public class CreateInitialAdminInDatabaseChangeLog {

    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CreateInitialAdminInDatabaseChangeLog(MongoTemplate mongoTemplate, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Execution
    public void insertAdmin() {
        if (mongoTemplate.findOne(Query.query(Criteria.where("username").is("gangadhar")), User.class) == null) {
            User admin = User.builder()
                    .username("gangadhar")
                    .passwordEncrypted(bCryptPasswordEncoder.encode("gangadhar@123"))
                    .firstName("Gangadhar")
                    .lastName("Gottiveeti")
                    .roles(Set.of("ROLE_ADMIN"))
                    .createdAt(LocalDateTime.now())
                    .build();
            mongoTemplate.save(admin, "users");
        }
    }

    @RollbackExecution
    public void rollbackInsertAdmin() {
        mongoTemplate.remove(new Query(Criteria.where("username").is("gangadhar")), User.class, "users");
    }
}
