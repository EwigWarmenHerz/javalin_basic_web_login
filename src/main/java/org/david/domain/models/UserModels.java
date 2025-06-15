package org.david.domain.models;

import org.david.miscellaneous.criptography.CryptManager;
import java.io.IO;


public interface UserModels {
    record User(Integer id, String email, String password){


        public User withHashedPassword(){
            var hashedPassword = CryptManager.hashPassword(password);
            IO.println(hashedPassword);
            return new User(id, email, hashedPassword);
        }
    }

}
