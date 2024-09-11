package com.shoppingbackend.admin;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderTest {
    @Test
    public void testEncodePassword(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "dat03122003";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("--------- Raw password: "+rawPassword);
        System.out.println("--------- Encoded password: "+encodedPassword);

        boolean result = passwordEncoder.matches(rawPassword,encodedPassword);
        System.out.println("Check whether password matches: "+result);

        Assertions.assertThat(result).isTrue();
    }
}
