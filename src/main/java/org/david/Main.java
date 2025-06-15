import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;
import org.david.boundaries.rest.handlers.UserHandlers;
import org.david.miscellaneous.validators.UserValidators;

import static io.javalin.apibuilder.ApiBuilder.*;

void main() {

    var app = Javalin.create(javalinConfig -> {
        javalinConfig.useVirtualThreads = true;
        javalinConfig.router.apiBuilder(() ->{
            path("users", () ->{
                get("get-all", UserHandlers::getAllUsers);
                post("login", UserHandlers::getSingleUser);
                post("create", ctx ->{
                    UserValidators.userDtoValidator(ctx);
                    UserHandlers.createUser(ctx);
                });
                patch("update", ctx -> {
                    UserValidators.userDtoValidator(ctx);
                    UserHandlers.updateUser(ctx);
                });
            });
        });
        javalinConfig.jsonMapper(new JavalinJackson().updateMapper(mapper -> {
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        }));
    });

    app.exception(ValidationException.class, (e, ctx) -> {
        var messages = e.getErrors().values().stream()
            .flatMap(Collection::stream)
            .map(ValidationError::getMessage)
            .toList();
        ctx.status(400).json(Map.of("errors", messages));
    }).start(8081);

    app.exception(RuntimeException.class, (e, ctx) -> {
        ctx.status(400).json(Map.of("error", e.getMessage()));
    });



}








