package org.david.domain.repository;
import module java.base;
import org.david.boundaries.adapters.DB;
import org.david.boundaries.adapters.UserEntity;
import org.david.domain.models.UserModels.*;
import org.david.miscellaneous.custom_exceptions.CustomExceptions;
import org.jetbrains.annotations.NotNull;
import org.jooq.Field;
import org.jooq.Table;

import java.sql.SQLException;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class UserRepository {

    public static final Table<?> USERS = table("users");
    public static final Field<Integer> ID = field("id", Integer.class);
    public static final Field<String> EMAIL = field("email", String.class);
    public static final Field<String> PASSWORD = field("password", String.class);
    private final DB db;

    public UserRepository(DB db){
        this.db = db;
    }


    public int createUser(User newUser) {
        int res;
        try {
            res = db.execute(dslContext -> dslContext
                .insertInto(USERS)
                .columns(EMAIL, PASSWORD)
                .values(newUser.email(), newUser.password())
                .execute());
            return res;
        } catch (SQLException e) {

            throw new CustomExceptions.FailedToCreateUserException(e.getMessage());
        }
    }

    @NotNull
    public  Optional<UserEntity> updateUser(UserEntity user) throws SQLException {
        try {
            var res = db.execute(dslContext -> dslContext
                .update(USERS)
                .set(PASSWORD, user.password)
                .set(EMAIL, user.email)
                .where(ID.eq(user.id))
                .execute());
            return res == 1 ? Optional.of(user) : Optional.empty();
        }catch (SQLException e){
            throw new RuntimeException(e.getMessage());
        }
    }
    @NotNull
    public  List<UserEntity> getUsers() throws SQLException {
       try{
           return db.execute(dslContext -> dslContext
               .selectFrom(USERS)
               .fetchInto(UserEntity.class));
       }catch (SQLException e){
           throw new RuntimeException(e.getMessage());
       }
    }
    public  Optional<UserEntity> getSingleUser(String email) throws SQLException {
       try {
           return db.execute(dslContext ->
               dslContext.selectFrom(USERS)
                   .where(EMAIL.eq(email))
                   .fetchOptionalInto(UserEntity.class));
       }catch (SQLException e){
           throw new RuntimeException(e.getMessage());
       }
    }
}
