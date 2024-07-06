package dev.tpcoder.goutbackend.user.service;

import dev.tpcoder.goutbackend.user.dto.UserCreationDto;
import dev.tpcoder.goutbackend.user.dto.UserInfoDto;
import dev.tpcoder.goutbackend.user.dto.UserUpdateDto;
import dev.tpcoder.goutbackend.user.model.User;

public interface UserService {
    User getUserById(int id);

    UserInfoDto getUserDtoById(int id);

    UserInfoDto createUser(UserCreationDto body);

    UserInfoDto updateUser(int id, UserUpdateDto body);

    boolean deleteUserById(int id);
}
