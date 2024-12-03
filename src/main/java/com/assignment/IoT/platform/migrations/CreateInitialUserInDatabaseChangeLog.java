package com.assignment.IoT.platform.migrations;

import com.assignment.IoT.platform.model.User;
import com.github.cloudyrock.mongock.ChangeLog;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Set;

@ChangeUnit(order = "2", id = "user-init-seeder", author = "Gangadhar")
public class CreateInitialUserInDatabaseChangeLog {

    private final MongoTemplate mongoTemplate;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CreateInitialUserInDatabaseChangeLog(MongoTemplate mongoTemplate, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.mongoTemplate = mongoTemplate;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    @Execution
    public void insertUser() {
        if(mongoTemplate.findOne(Query.query(Criteria.where("username").is("default-user")), User.class) == null) {
            User user = User.builder()
                    .username("default-user")
                    .passwordEncrypted(bCryptPasswordEncoder.encode("user@123"))
                    .firstName("User")
                    .lastName("1")
                    .roles(Set.of("ROLE_USER"))
                    .createdAt(LocalDateTime.now())
                    .build();
            mongoTemplate.save(user, "users");
        }
    }

    @RollbackExecution
    public void rollbackInsertUser() {
        mongoTemplate.remove(new Query(Criteria.where("username").is("default-user")), User.class, "users");
    }
}
