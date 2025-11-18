package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.UserDto;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.factory.UserDtoFactory;
import com.springboot.auftragsmanagement.repository.UserRepository;
import com.springboot.auftragsmanagement.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserDtoFactory userDtoFactory;

    public UserServiceImpl(UserRepository userRepository, UserDtoFactory userDtoFactory) {
        this.userRepository = userRepository;
        this.userDtoFactory = userDtoFactory;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = new User();
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());

        User savedUser = userRepository.save(user);

        return userDtoFactory.createUser(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(), savedUser.getEmail());
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return userDtoFactory.createUser(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail());
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> userDtoFactory.createUser(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail()))
                .toList();
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());

        User updatedUser = userRepository.save(user);

        return userDtoFactory.createUser(updatedUser.getId(), updatedUser.getFirstName(), updatedUser.getLastName(), updatedUser.getEmail());
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        userRepository.delete(user);
    }
}