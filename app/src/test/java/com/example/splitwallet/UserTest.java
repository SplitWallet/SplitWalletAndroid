package com.example.splitwallet;

import org.junit.Test;
import static org.junit.Assert.*;
import com.example.splitwallet.models.User;

public class UserTest {

    @Test
    public void testUserConstructorAndGetters() {
        User user = new User("123", "john_doe", "john@example.com", "1234567890");

        assertEquals("123", user.getId());
        assertEquals("john_doe", user.getUsername());
        assertEquals("john_doe", user.getName());
        assertEquals("john@example.com", user.getEmail());
        assertEquals("1234567890", user.getPhoneNumber());
    }

    @Test
    public void testDefaultConstructorAndSetters() {
        User user = new User();
        user.setId("321");
        user.setUsername("jane_doe");
        user.setEmail("jane@example.com");
        user.setPhoneNumber("0987654321");

        assertEquals("321", user.getId());
        assertEquals("jane_doe", user.getUsername());
        assertEquals("jane_doe", user.getName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("0987654321", user.getPhoneNumber());
    }
}

