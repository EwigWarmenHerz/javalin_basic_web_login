package user_handler;
import module java.base;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import org.david.boundaries.adapters.UserEntity;
import org.david.boundaries.rest.handlers.UserHandlers;
import org.david.domain.models.UserModels;
import org.david.domain.repository.UserRepository;
import org.david.miscellaneous.custom_exceptions.CustomExceptions.*;
import org.david.miscellaneous.criptography.CryptManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserHandlerTest {
    private UserRepository userRepository;
    private Context ctx;
    private UserHandlers userHandlers;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        ctx = mock(Context.class);
        userHandlers = new UserHandlers(userRepository);
    }

    @Test
    public void testCreateUser_successfulCreation_returns201() throws Exception {
        var user = new UserModels.User(0, "test@example.com", "password123");
        var jsonBody = objectMapper.writeValueAsString(user);

        when(ctx.body()).thenReturn(jsonBody);
        when(userRepository.createUser(any())).thenReturn(1);
        when(ctx.status(201)).thenReturn(ctx);

        var captor = ArgumentCaptor.forClass(Map.class);
        when(ctx.json(captor.capture())).thenReturn(ctx);

        userHandlers.createUser(ctx);

        verify(ctx).status(201);
        assertEquals("User created successfully", captor.getValue().get("Message"));
    }

    @Test
    public void testCreateUser_failedCreation_throwsException() throws Exception {
        var user = new UserModels.User(0, "fail@example.com", "password123");
        var jsonBody = objectMapper.writeValueAsString(user);

        when(ctx.body()).thenReturn(jsonBody);
        when(userRepository.createUser(any())).thenReturn(0);

        var ex = assertThrows(FailedToCreateUserException.class, () -> {
            userHandlers.createUser(ctx);
        });
        assertEquals("Could not create user", ex.message);
    }

    @Test
    public void testGetAllUsers_successful_returnsUsersList() throws Exception {
        var userEntity = new UserEntity(1, "example@example.com", "hashed");

        when(userRepository.getUsers()).thenReturn(List.of(userEntity));

        var captor = ArgumentCaptor.forClass(Map.class);
        when(ctx.json(captor.capture())).thenReturn(ctx);
        when(ctx.status(200)).thenReturn(ctx);

        userHandlers.getAllUsers(ctx);

        verify(ctx).status(200);
        assertNotNull(captor.getValue().get("users"));
    }

    @Test
    public void testGetSingleUser_validCredentials_returnsUser() throws Exception {
        var user = new UserModels.User(0, "valid@example.com", "password123");
        var userEntity = new UserEntity(1, "valid@example.com", CryptManager.hashPassword("password123"));

        when(ctx.body()).thenReturn(objectMapper.writeValueAsString(user));
        when(userRepository.getSingleUser("valid@example.com")).thenReturn(Optional.of(userEntity));

        var captor = ArgumentCaptor.forClass(Map.class);
        when(ctx.json(captor.capture())).thenReturn(ctx);

        userHandlers.getSingleUser(ctx);

        assertNotNull(captor.getValue().get("data"));
    }

    @Test
    public void testGetSingleUser_invalidPassword_throwsException() throws Exception {
        var user = new UserModels.User(0, "invalid@example.com", "wrongPass");
        var userEntity = new UserEntity(1, "invalid@example.com", CryptManager.hashPassword("correctPass"));

        when(ctx.body()).thenReturn(objectMapper.writeValueAsString(user));
        when(userRepository.getSingleUser("invalid@example.com")).thenReturn(Optional.of(userEntity));

        var ex = assertThrows(InvalidPasswordException.class, () -> {
            userHandlers.getSingleUser(ctx);
        });
        assertEquals("The passwords do not match", ex.message);
    }

    @Test
    public void testUpdateUser_successful_returnsUpdatedUser() throws Exception {
        var user = new UserModels.User(0, "update@example.com", "newPass");
        var userEntity = new UserEntity(1, "update@example.com", "oldPass");

        when(ctx.body()).thenReturn(objectMapper.writeValueAsString(user));
        when(userRepository.getSingleUser("update@example.com")).thenReturn(Optional.of(userEntity));
        when(userRepository.updateUser(userEntity)).thenReturn(Optional.of(userEntity));

        var captor = ArgumentCaptor.forClass(Map.class);
        when(ctx.json(captor.capture())).thenReturn(ctx);

        userHandlers.updateUser(ctx);

        assertNotNull(captor.getValue().get("data"));
    }

    @Test
    public void testUpdateUser_notFound_throwsException() throws Exception {
        var user = new UserModels.User(0, "missing@example.com", "newPass");

        when(ctx.body()).thenReturn(objectMapper.writeValueAsString(user));
        when(userRepository.getSingleUser("missing@example.com")).thenReturn(Optional.empty());

        var ex = assertThrows(ElementDoNotExistException.class, () -> {
            userHandlers.updateUser(ctx);
        });
        assertEquals("The user does not exist", ex.message);
    }

    @Test
    public void testUpdateUser_updateFails_throwsException() throws Exception {
        var user = new UserModels.User(0, "failupdate@example.com", "newPass");
        var userEntity = new UserEntity(1, "failupdate@example.com", "oldPass");

        when(ctx.body()).thenReturn(objectMapper.writeValueAsString(user));
        when(userRepository.getSingleUser("failupdate@example.com")).thenReturn(Optional.of(userEntity));
        when(userRepository.updateUser(userEntity)).thenReturn(Optional.empty());

        var ex = assertThrows(DataIntegrityException.class, () -> {
            userHandlers.updateUser(ctx);
        });
        assertEquals("User could not be updated", ex.message);
    }
}
