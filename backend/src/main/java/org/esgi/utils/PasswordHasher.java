package org.esgi.utils;


import jakarta.enterprise.context.ApplicationScoped;
import org.mindrot.jbcrypt.BCrypt;

@ApplicationScoped
public class PasswordHasher {

    public String hash(String plain) {
        return BCrypt.hashpw(plain, BCrypt.gensalt());
    }

    public boolean match(String plain, String hashed) {
        return BCrypt.checkpw(plain, hashed);
    }
}
