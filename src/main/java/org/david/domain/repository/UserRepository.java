package org.david.domain.repository;
import module java.base;
import org.david.boundaries.adapters.DB;
import org.david.boundaries.adapters.UserEntity;
import org.david.domain.models.Errors;
import org.david.domain.models.ResponseModel;
import org.david.domain.models.UserModels.*;
import org.jetbrains.annotations.NotNull;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Table;

import java.sql.SQLException;


import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

public class UserRepository {

    private static final Table<?> USERS = table("users");
    private static final Field<Integer> ID = field("id", Integer.class);
    private static final Field<String> EMAIL = field("email", String.class);
    private static final Field<String> PASSWORD = field("password", String.class);

    @NotNull
    public static ResponseModel<?> createUser(DSLContext dslContext, User newUser) {
        try{
            var queryRes =  dslContext.insertInto(USERS)
                .columns(EMAIL, PASSWORD)
                .values(newUser.email(), newUser.password())
                .execute();
            return new ResponseModel<>(queryRes, null);
        } catch (RuntimeException e) {
            var error = new Errors(List.of(e.getMessage()));
            return new ResponseModel<>(null, error);
        }
    }
    @NotNull
    public static Optional<UserEntity> updateUser(UserEntity user) throws SQLException {
        var res = DB.execute(dslContext -> dslContext
            .update(USERS)
            .set(PASSWORD, user.password)
            .set(EMAIL, user.email)
            .where(ID.eq(user.id))
            .execute());
        return res == 1 ? Optional.of(user) : Optional.empty();
    }
    @NotNull
    public static List<UserEntity> getUsers() throws SQLException {
        return DB.execute(dslContext -> dslContext
            .selectFrom(USERS)
            .fetchInto(UserEntity.class));
    }
    public static Optional<UserEntity> getSingleUser(String email) throws SQLException {
        return DB.execute(dslContext ->
            dslContext.selectFrom(USERS)
            .where(EMAIL.eq(email))
                .fetchOptionalInto(UserEntity.class));
    }
}
