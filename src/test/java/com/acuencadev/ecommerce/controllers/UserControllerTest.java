package com.acuencadev.ecommerce.controllers;

import com.acuencadev.ecommerce.TestUtils;
import com.acuencadev.ecommerce.model.persistence.User;
import com.acuencadev.ecommerce.model.persistence.repositories.CartRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.UserRepository;
import com.acuencadev.ecommerce.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder bcryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController(null, null, null);

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bcryptPasswordEncoder", bcryptPasswordEncoder);

        User dummyUser = createDummyUser();

        when(userRepository.findById(0L)).thenReturn(Optional.of(dummyUser));
        when(userRepository.findById(2L)).thenReturn(null);
        when(userRepository.findByUsername("test")).thenReturn(dummyUser);
        when(userRepository.findByUsername("johndoe")).thenReturn(null);
    }

    @Test
    public void create_user_happy_path() throws Exception {
        when(bcryptPasswordEncoder.encode("testPassword")).thenReturn("thisIsHashed");

        CreateUserRequest r = createDummyUserRequest();

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User u = response.getBody();

        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("test", u.getUsername());
        assertEquals("thisIsHashed", u.getPassword());

        response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void create_user_that_does_not_satisfy_strength_requirements() {
        CreateUserRequest r = createDummyUserRequest();

        // Password and confirm password are different
        r.setPassword("SomethingElse");

        ResponseEntity<User> response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());

        // Password too short
        r.setPassword("123");
        r.setConfirmPassword("123");

        response = userController.createUser(r);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_username() {
        ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        response = userController.findByUserName("johndoe");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void find_user_by_id() {
        ResponseEntity<User> response = userController.findById(0L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        response = userController.findById(1L);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    private CreateUserRequest createDummyUserRequest() {
        CreateUserRequest r = new CreateUserRequest();

        r.setUsername("test");
        r.setPassword("testPassword");
        r.setConfirmPassword("testPassword");

        return r;
    }

    private User createDummyUser() {
        User u = new User();

        u.setId(0);
        u.setUsername("test");
        u.setPassword("testPassword");

        return u;
    }
}
