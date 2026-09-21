package com.ecom.userservice.services;

import com.ecom.userservice.dtos.UserResponseDto;
import com.ecom.userservice.exceptions.UsernameNotFoundException;
import com.ecom.userservice.models.User;
import com.ecom.userservice.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RedisTemplate redisTemplate;

    @Mock
    private HashOperations hashOperations;

    @Captor
    private ArgumentCaptor<UserResponseDto> userResponseDtoCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Test
    void testGetUserDetails_ReturnsCachedDtoAndSkipsRepository() throws Exception {
        String id = "123";
        UserResponseDto cached = new UserResponseDto(id, "john", "john@example.com");

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("USERS", "USER" + id)).thenReturn(cached);

        UserServiceImpl service = new UserServiceImpl(userRepository, redisTemplate);

        UserResponseDto result = service.getUserDetails(id);

        assertSame(cached, result);
        verify(hashOperations).get("USERS", "USER" + id);
        verify(userRepository, never()).findById(anyString());
        verify(hashOperations, never()).put(any(), any(), any());
    }

    @Test
    void testGetUserDetails_CacheMiss_FetchesMapsAndCaches() throws Exception {
        String id = "321";
        User user = new User();
        user.setId(id);
        user.setUsername("alice");
        user.setEmail("alice@example.com");

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("USERS", "USER" + id)).thenReturn(null);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserServiceImpl service = new UserServiceImpl(userRepository, redisTemplate);

        UserResponseDto result = service.getUserDetails(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());

        verify(hashOperations).get("USERS", "USER" + id);
        verify(userRepository).findById(id);
        verify(hashOperations).put(eq("USERS"), eq("USER" + id), userResponseDtoCaptor.capture());

        UserResponseDto cachedDto = userResponseDtoCaptor.getValue();
        assertEquals(id, cachedDto.getId());
        assertEquals("alice", cachedDto.getName());
        assertEquals("alice@example.com", cachedDto.getEmail());
    }

    @Test
    void testSaveUser_PersistsEntityAndReturnsMappedDto() {
        UserResponseDto request = new UserResponseDto("555", "reqName", "req@example.com");

        User saved = new User();
        saved.setId("555");
        saved.setUsername("persistedName");
        saved.setEmail("persisted@example.com");

        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserServiceImpl service = new UserServiceImpl(userRepository, redisTemplate);

        UserResponseDto response = service.saveUser(request);

        verify(userRepository).save(userCaptor.capture());
        User toSave = userCaptor.getValue();
        assertEquals("555", toSave.getId());
        assertEquals("reqName", toSave.getUsername());
        assertEquals("req@example.com", toSave.getEmail());

        assertNotNull(response);
        assertEquals("555", response.getId());
        assertEquals("persistedName", response.getName());
        assertEquals("persisted@example.com", response.getEmail());
    }

    @Test
    void testGetUserDetails_UserNotFound_ThrowsUsernameNotFoundException() {
        String id = "999";

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("USERS", "USER" + id)).thenReturn(null);
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        UserServiceImpl service = new UserServiceImpl(userRepository, redisTemplate);

        assertThrows(UsernameNotFoundException.class, () -> service.getUserDetails(id));

        verify(hashOperations).get("USERS", "USER" + id);
        verify(userRepository).findById(id);
        verify(hashOperations, never()).put(any(), any(), any());
    }

    @Test
    void testGetUserDetails_CachedValueWrongType_ThrowsClassCastException() {
        String id = "777";

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("USERS", "USER" + id)).thenReturn("not-a-dto");

        UserServiceImpl service = new UserServiceImpl(userRepository, redisTemplate);

        assertThrows(ClassCastException.class, () -> service.getUserDetails(id));

        verify(userRepository, never()).findById(anyString());
        verify(hashOperations, never()).put(any(), any(), any());
    }

    @Test
    void testGetUserDetails_RedisGetFailure_PropagatesAndSkipsRepository() {
        String id = "888";

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
        when(hashOperations.get("USERS", "USER" + id)).thenThrow(new RuntimeException("Redis failure"));

        UserServiceImpl service = new UserServiceImpl(userRepository, redisTemplate);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.getUserDetails(id));
        assertEquals("Redis failure", ex.getMessage());

        verify(userRepository, never()).findById(anyString());
        verify(hashOperations, never()).put(any(), any(), any());
    }
}