package com.ecom.userservice.services;

import com.ecom.userservice.dtos.UserResponseDto;
import com.ecom.userservice.exceptions.UserNameAlreadyExistsException;
import com.ecom.userservice.exceptions.UsernameNotFoundException;
import com.ecom.userservice.models.User;
import com.ecom.userservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserRepository userRepository, RedisTemplate redisTemplate) {
        this.userRepository = userRepository;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public UserResponseDto getUserDetails(String id) throws UsernameNotFoundException {
        log.info("Fetching user details with user id "+id);
        UserResponseDto userResp = (UserResponseDto) redisTemplate.opsForHash().get("USERS", "USER" + id);
        if(userResp!=null) return userResp;
        User user= userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(id);
        userResponseDto.setEmail(user.getEmail());
        userResponseDto.setName(user.getUsername());
        redisTemplate.opsForHash().put("USERS", "USER" + id, userResponseDto);
        log.info("Fetched user details with user id "+id);
        return userResponseDto;
    }

    @Override
    public UserResponseDto saveUser(UserResponseDto request) throws UserNameAlreadyExistsException {
        User user= userRepository.findById(request.getId()).orElse(null);
        if(user!=null) {
            throw new UserNameAlreadyExistsException("User with id "+request.getId()+" already exists");
        }
        log.info("Saving user details with user id "+request.getId());
        User newuser = new User();
        newuser.setId(request.getId());
        newuser.setUsername(request.getName());
        newuser.setEmail(request.getEmail());
        User saved = userRepository.save(newuser);
        log.info("Saved user with id "+saved.getId());
        return new UserResponseDto(saved.getId(), saved.getUsername(), saved.getEmail());
    }
}
