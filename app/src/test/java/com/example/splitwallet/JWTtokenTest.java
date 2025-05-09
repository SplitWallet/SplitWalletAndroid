package com.example.splitwallet;

import com.example.splitwallet.models.JWTtoken;
import org.junit.Test;
import static org.junit.Assert.*;

public class JWTtokenTest {
    @Test
    public void testSetAndGetToken() {
        JWTtoken token = new JWTtoken();
        String testToken = "abc.def.ghi";
        token.setJwtToken(testToken);
        assertEquals(testToken, token.getJwtToken());
    }
}

