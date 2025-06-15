package org.david.boundaries.rest.handlers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.david.boundaries.adapters.UserEntity;
import org.david.domain.models.ResponseModel;
import org.david.domain.models.UserModels;
import org.david.domain.repository.UserRepository;
import org.david.miscellaneous.custom_exceptions.CustomExceptions.*;
import org.david.miscellaneous.criptography.CryptManager;

import java.sql.SQLException;

import java.util.Map;

public class UserHandlers {
    private static final ObjectMapper json = new ObjectMapper();
    private final UserRepository userRepository;


    public UserHandlers(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void getAllUsers(Context ctx) throws SQLException {
        var users = userRepository.getUsers()
            .stream()
            .map(UserEntity::mapToUserDTO)
            .toList();
        ctx.json(Map.of("users", new ResponseModel<>(users, null))).status(200);
    }
    public void getSingleUser(Context ctx) throws JsonProcessingException, SQLException {
        var userDto = json.readValue(ctx.body(),UserModels.User.class);
        var userEntity = findSingleUser(userDto.email());
        var isVerified = CryptManager.verifyPassword(userDto.password(), userEntity.password);
        if(isVerified){
            ctx.json(Map.of("data", new ResponseModel<>(userEntity.mapToUserDTO(), null)));
            return;
        }
        throw new InvalidPasswordException("The passwords do not match");

    }
    public void createUser(Context ctx) throws JsonProcessingException, SQLException {
        var body = ctx.body();
        var newUser = json.readValue(body, UserModels.User.class);
        var newUserHP = newUser.withHashedPassword();
        var res = userRepository.createUser(newUserHP);
        if(res ==1){
            ctx.status(201).json(Map.of("Message", "User created successfully"));
            return;
        }
        throw new FailedToCreateUserException("Could not create user");
    }

    public void updateUser(Context ctx) throws JsonProcessingException, SQLException {
        var userDomain = json.readValue(ctx.body(),UserModels.User.class);
        var user = findSingleUser(userDomain.email());
        user.email = userDomain.email();
        user.password = userDomain.password();
        var res = userRepository.updateUser(user);

        if(res.isPresent()){
            ctx.json(Map.of("data", new ResponseModel<>(res.get(),null)));
            return;
        }
        throw new DataIntegrityException("User could not be updated");
    }
    private  UserEntity findSingleUser(String email) throws  SQLException {
        return userRepository.getSingleUser(email)
            .orElseThrow(() -> new ElementDoNotExistException("The user does not exist"));
    }

}
