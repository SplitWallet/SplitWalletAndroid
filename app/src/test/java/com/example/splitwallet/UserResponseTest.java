package com.example.splitwallet;

import com.example.splitwallet.models.UserResponse;
import org.junit.Test;
import static org.junit.Assert.*;

public class UserResponseTest {
    @Test
    public void testConstructorAndGetters() {
        UserResponse user = new UserResponse("1", "Alice", "alice@mail.com", "123456");
        assertEquals("1", user.getId());
        assertEquals("Alice", user.getName());
        assertEquals("alice@mail.com", user.getEmail());
        assertEquals("123456", user.getPhoneNumber());
    }
}

