package org.david.boundaries.adapters;

import org.david.domain.models.UserModels;

public class UserEntity {
    public Integer id;
    public String email;
    public String password;

    public UserModels.User mapToUserDTO(){
        return new UserModels.User(id, email, null);
    }
}
