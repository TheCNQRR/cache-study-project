import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.dto.response.UserResponse;
import by.java.enterprise.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(null, null);
    }

    @Test
    void createUser_savesUserAndReturnsUser() {
        CreateUserResponse createdUser = userService.createUser(new CreateUserRequest("username", "displayName"));

        UserResponse foundUser = userService.findUser(createdUser.id());

        assertEquals(createdUser.id(), foundUser.id());
        assertEquals(createdUser.username(), foundUser.username());
        assertEquals(createdUser.displayName(), foundUser.displayName());
    }
}
