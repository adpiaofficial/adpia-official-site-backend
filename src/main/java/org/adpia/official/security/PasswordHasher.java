package org.adpia.official.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordHasher {
    private final PasswordEncoder encoder;

    public String hash(String raw) { return encoder.encode(raw); }
    public boolean matches(String raw, String hashed) { return encoder.matches(raw, hashed); }
}
