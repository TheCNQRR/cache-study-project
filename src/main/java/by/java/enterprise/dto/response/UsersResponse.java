package by.java.enterprise.dto.response;

import by.java.enterprise.record.User;

import java.util.List;

public record UsersResponse(
        List<User> users
) {
}
