package com.springboot.auftragsmanagement.dto;

public record UserDto(Long id,
                      String firstName,
                      String lastName,
                      String email) {
}
