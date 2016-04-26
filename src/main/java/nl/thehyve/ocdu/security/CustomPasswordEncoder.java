package nl.thehyve.ocdu.security;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by piotrzakrzewski on 26/04/16.
 */
public class CustomPasswordEncoder implements PasswordEncoder {

    public String encode(CharSequence rawPassword) {
        return null; // TODO implement
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return null; // TODO implement
    }
}
