package org.david.miscellaneous.validators;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.david.domain.models.UserModels;
import org.david.miscellaneous.custom_exceptions.CustomExceptions.*;

public class UserValidators {

    public static void userDtoValidator(Context ctx) {
        checkJsonIntegrity(ctx);
        ctx.bodyValidator(UserModels.User.class)
            .check(user -> user.email() != null && !user.email().isBlank(),"Email can't be null or blank")
            .check(user -> user.password() != null && !user.password().isBlank(), "Password can't be null or blank")
            .get();
    }

    private static void checkJsonIntegrity(Context ctx) {
        if(ctx.body().isBlank()) {
            throw new InvalidBodyException("Body can't be null");
        }
        try {
            var json = new ObjectMapper();
            json.readValue(ctx.body(), Object.class);
        } catch (JsonProcessingException e) {
            throw new InvalidBodyException(e.getMessage());
        }
    }
}
