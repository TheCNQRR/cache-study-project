package by.java.enterprise;

import by.java.enterprise.dto.request.CreateUserRequest;
import by.java.enterprise.dto.response.CreateUserResponse;
import by.java.enterprise.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {

    }

    @Test
    void getUserById_returns200_whenUserExists() throws Exception { // TODO привести к единому код стайлу, добавить test в начале
        CreateUserResponse createdUser = userService.createUser(new CreateUserRequest("username", "displayName"));

        mockMvc.perform(get("/api/v1/users/{id}", createdUser.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdUser.id()))
                .andExpect(jsonPath("$.username").value(createdUser.username()))
                .andExpect(jsonPath("$.displayName").value(createdUser.displayName()));
    }

    @Test
    void getUserById_returns404_whenUserNotExists() throws Exception {
        mockMvc.perform(get("/api/v1/users/{id}", 999999L))
                .andExpect(status().isNotFound());
    }
}
