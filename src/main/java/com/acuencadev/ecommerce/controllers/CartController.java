package com.acuencadev.ecommerce.controllers;

import com.acuencadev.ecommerce.model.persistence.Cart;
import com.acuencadev.ecommerce.model.persistence.Item;
import com.acuencadev.ecommerce.model.persistence.User;
import com.acuencadev.ecommerce.model.persistence.repositories.CartRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.ItemRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.UserRepository;
import com.acuencadev.ecommerce.model.requests.ModifyCartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final static Logger log = LoggerFactory.getLogger(CartController.class);

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public CartController(UserRepository userRepository, CartRepository cartRepository, ItemRepository itemRepository) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.itemRepository = itemRepository;
    }

    @PostMapping("/addToCart")
    public ResponseEntity<Cart> addTocart(@RequestBody ModifyCartRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user == null) {
            log.debug(String.format("Cannot add item with ID %s to the cart as the user %s is not valid.",
                    String.valueOf(request.getItemId()), request.getUsername()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if(!item.isPresent()) {
            log.debug(String.format("Cannot add item with ID %s to %s's cart as it is not a valid product ID.",
                    String.valueOf(request.getItemId()), request.getUsername()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.addItem(item.get()));
        cartRepository.save(cart);

        log.debug(String.format("Item with ID %s added to %s's cart",
                String.valueOf(request.getItemId()), request.getUsername()));

        return ResponseEntity.ok(cart);
    }

    @PostMapping("/removeFromCart")
    public ResponseEntity<Cart> removeFromcart(@RequestBody ModifyCartRequest request) {
        User user = userRepository.findByUsername(request.getUsername());
        if(user == null) {
            log.debug(String.format("Cannot remove item with ID %s to the cart as the user %s is not valid.",
                    String.valueOf(request.getItemId()), request.getUsername()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Optional<Item> item = itemRepository.findById(request.getItemId());
        if(!item.isPresent()) {
            log.debug(String.format("Cannot remove item with ID %s from %s's cart as it is not a valid product ID.",
                    String.valueOf(request.getItemId()), request.getUsername()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Cart cart = user.getCart();
        IntStream.range(0, request.getQuantity())
                .forEach(i -> cart.removeItem(item.get()));
        cartRepository.save(cart);

        log.debug(String.format("Item with ID %s removed from %s's cart",
                String.valueOf(request.getItemId()), request.getUsername()));

        return ResponseEntity.ok(cart);
    }
}
