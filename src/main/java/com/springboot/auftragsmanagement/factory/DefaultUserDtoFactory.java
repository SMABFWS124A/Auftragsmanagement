package com.springboot.auftragsmanagement.factory;

import com.springboot.auftragsmanagement.dto.UserDto;

public class DefaultUserDtoFactory implements UserDtoFactory {
    @Override
    public UserDto createUser(Long id, String firstName, String lastName, String email) {
        return UserDto.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .build();
    }
}