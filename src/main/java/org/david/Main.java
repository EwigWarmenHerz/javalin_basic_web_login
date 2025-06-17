package org.david;

import static io.javalin.apibuilder.ApiBuilder.get;
import static io.javalin.apibuilder.ApiBuilder.patch;
import static io.javalin.apibuilder.ApiBuilder.path;
import static io.javalin.apibuilder.ApiBuilder.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import io.javalin.validation.ValidationError;
import io.javalin.validation.ValidationException;
import java.util.Collection;
import java.util.Map;
import org.david.boundaries.adapters.DB;
import org.david.boundaries.rest.handlers.UserHandlers;
import org.david.domain.repository.UserRepository;
import org.david.miscellaneous.custom_exceptions.HttpCustomException;
import org.david.miscellaneous.validators.UserValidators;

public class Main {

  static final DB db = new DB();
  static final UserRepository userRepository = new UserRepository(db);
  static final UserHandlers userHandler = new UserHandlers(userRepository);

  public static void main(String[] args) {
    var app =
        Javalin.create(
            javalinConfig -> {
              javalinConfig.useVirtualThreads = true;
              javalinConfig.router.apiBuilder(
                  () ->
                      path(
                          "users",
                          () -> {
                            get("get-all", userHandler::getAllUsers);
                            post("login", userHandler::getSingleUser);
                            post(
                                "create",
                                ctx -> {
                                  UserValidators.userDtoValidator(ctx);
                                  userHandler.createUser(ctx);
                                });
                            patch(
                                "update",
                                ctx -> {
                                  UserValidators.userDtoValidator(ctx);
                                  userHandler.updateUser(ctx);
                                });
                          }));
              javalinConfig.jsonMapper(
                  new JavalinJackson()
                      .updateMapper(
                          mapper -> {
                            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                            mapper.configure(
                                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                          }));
            });

    app.exception(
            ValidationException.class,
            (e, ctx) -> {
              var messages =
                  e.getErrors().values().stream()
                      .flatMap(Collection::stream)
                      .map(ValidationError::getMessage)
                      .toList();
              ctx.status(400).json(Map.of("errors", messages));
            })
        .start(8081);

    app.exception(
        JsonParseException.class,
        (e, ctx) -> ctx.status(400).json(Map.of("error", e.getMessage())));

    app.exception(
        HttpCustomException.class,
        (e, ctx) -> ctx.json(e.statusCode).json(Map.of("error", e.message)));
  }
}
