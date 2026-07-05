package com.dozycoffee.infrastructure.generator;

import com.dozycoffee.core.security.Credential;
import com.dozycoffee.branch.application.BranchAuthKeyGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RandomBranchAuthKeyGenerator implements BranchAuthKeyGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.";
    private static final int LENGTH = 32;
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    public Credential generate() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        }
        String value = sb.toString();
        return () -> value;
    }
}
