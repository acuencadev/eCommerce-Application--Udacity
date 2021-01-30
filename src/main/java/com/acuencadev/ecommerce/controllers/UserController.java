package com.acuencadev.ecommerce.controllers;

import com.acuencadev.ecommerce.model.persistence.Cart;
import com.acuencadev.ecommerce.model.persistence.User;
import com.acuencadev.ecommerce.model.persistence.repositories.CartRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.UserRepository;
import com.acuencadev.ecommerce.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final static Logger log = LoggerFactory.getLogger(UserController.class);

    private UserRepository userRepository;
    private CartRepository cartRepository;
    private BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public UserController(UserRepository userRepository, CartRepository cartRepository, BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);

        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            log.debug(String.format("Cannot create User %s as the password does not meet the strength requirements.", user.getUsername()));

            return ResponseEntity.badRequest().build();
        }
        
        user.setPassword(bcryptPasswordEncoder.encode(createUserRequest.getPassword()));

        log.debug(String.format("User %s has logged in successfully.", user.getUsername()));

        userRepository.save(user);
        return ResponseEntity.ok(user);
    }
}