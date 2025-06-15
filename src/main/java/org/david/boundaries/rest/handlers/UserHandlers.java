package org.david.boundaries.rest.handlers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.david.boundaries.adapters.DB;
import org.david.boundaries.adapters.UserEntity;
import org.david.domain.models.ResponseModel;
import org.david.domain.models.UserModels;
import org.david.domain.repository.UserRepository;
import org.david.miscellaneous.CustomExceptions.*;
import org.david.miscellaneous.criptography.CryptManager;

import java.sql.SQLException;

import java.util.Map;

public class UserHandlers {
    private static final ObjectMapper json = new ObjectMapper();

    public static void getAllUsers(Context ctx) throws SQLException {
        var users = UserRepository.getUsers()
            .stream()
            .map(UserEntity::mapToUserDTO)
            .toList();
        ctx.json(Map.of("users", new ResponseModel<>(users, null))).status(200);
    }
    public static void getSingleUser(Context ctx) throws JsonProcessingException, SQLException {
        var userDto = json.readValue(ctx.body(),UserModels.User.class);
        var userEntity = findSingleUser(userDto.email());
        var isVerified = CryptManager.verifyPassword(userDto.password(), userEntity.password);
        if(isVerified){
            ctx.json(Map.of("data", new ResponseModel<>(userEntity.mapToUserDTO(), null)));
            return;
        }
        throw new InvalidPasswordException("The passwords do not match");

    }


    public static void createUser(Context ctx) throws JsonProcessingException, SQLException {
        var body = ctx.body();
        var newUser = json.readValue(body, UserModels.User.class);
        var newUserHP = newUser.withHashedPassword();
        var res = DB.execute(dslContext -> UserRepository.createUser(dslContext, newUserHP));
        if(res.errors() != null){
            ctx.status(400).json(res.errors());
        }
        ctx.status(201).json(Map.of("Message", "User created successfully"));
    }

    public static void updateUser(Context ctx) throws JsonProcessingException, SQLException {
        var userDomain = json.readValue(ctx.body(),UserModels.User.class);
        var user = findSingleUser(userDomain.email());
        user.email = userDomain.email();
        user.password = userDomain.password();
        var res = DB.execute(dslContext -> {
            try {
                return UserRepository.updateUser(user);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        if(res.isPresent()){
            ctx.json(Map.of("data", new ResponseModel<>(res.get(),null)));
        }else {
            throw new DataIntegrityException("User could not be updated");
        }

    }
    private static UserEntity findSingleUser(String email) throws  SQLException {
        return UserRepository.getSingleUser(email)
            .orElseThrow(() -> new ElementDoNotExistException("The user does not exist"));
    }

}
