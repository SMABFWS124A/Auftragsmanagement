package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.UserDto;

public interface UserDtoFactory {
    UserDto createUser(Long id, String firstName, String lastName, String email);
}