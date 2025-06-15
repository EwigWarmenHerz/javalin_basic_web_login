package org.david.domain.models;

import org.david.miscellaneous.criptography.CryptManager;


public interface UserModels {
    record User(Integer id, String email, String password){
        public User {
            if (email == null || email.isBlank()) {
                throw new IllegalArgumentException("Email must not be null or blank");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("Password must not be null or blank");
            }
        }

        public User withHashedPassword(){
            var hashedPassword = CryptManager.hashPassword(password);
            return new User(id, email, hashedPassword);
        }
    }

}
