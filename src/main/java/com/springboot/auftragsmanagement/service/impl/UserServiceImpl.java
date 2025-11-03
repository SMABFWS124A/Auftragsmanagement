package com.springboot.auftragsmanagement.service.impl;

import com.springboot.auftragsmanagement.dto.UserDto;
import com.springboot.auftragsmanagement.entity.User;
import com.springboot.auftragsmanagement.exception.ResourceNotFoundException;
import com.springboot.auftragsmanagement.mapper.UserMapper;
import com.springboot.auftragsmanagement.repository.UserRepository;
import com.springboot.auftragsmanagement.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper){

        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @Override
    public UserDto createUser(UserDto userDto) {

        User user = userMapper.toEntity(userDto);
         User savedUser = userRepository.save(user);

        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User mit dieser ID " +id+ " wurde nicht gefunden"));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {

        List <User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {

        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User mit dieser ID " +id+ " wurde nicht gefunden"));

        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());

        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User mit dieser ID " +id+ " wurde nicht gefunden"));

        userRepository.delete(user);
    }
}
