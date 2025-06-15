package org.david.domain.models;

import org.david.miscellaneous.criptography.CryptManager;


public interface UserModels {
    record User(Integer id, String email, String password){

        public User withHashedPassword(){
            var hashedPassword = CryptManager.hashPassword(password);
            return new User(id, email, hashedPassword);
        }
    }

}
