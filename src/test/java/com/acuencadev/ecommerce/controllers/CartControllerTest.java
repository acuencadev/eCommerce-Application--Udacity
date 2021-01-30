package com.acuencadev.ecommerce.controllers;

import com.acuencadev.ecommerce.TestUtils;
import com.acuencadev.ecommerce.model.persistence.Cart;
import com.acuencadev.ecommerce.model.persistence.Item;
import com.acuencadev.ecommerce.model.persistence.User;
import com.acuencadev.ecommerce.model.persistence.repositories.CartRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.ItemRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.UserRepository;
import com.acuencadev.ecommerce.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController(null, null, null);

        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);

        User dummyUser = createDummyUser();
        Item dummyItem = createDummyItem();

        when(userRepository.findByUsername("test")).thenReturn(dummyUser);
        when(userRepository.findByUsername("johndoe")).thenReturn(null);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(dummyItem));
        when(itemRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
    }

    @Test
    public void add_to_cart() {
        ModifyCartRequest r = dummyCartRequest();

        // Add item to cart with invalid user
        r.setUsername("johndoe");
        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Add item with invalid item id
        r.setUsername("test");
        r.setItemId(2L);
        response = cartController.addTocart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Add a valid item for a valid user
        r = dummyCartRequest();
        response = cartController.addTocart(r);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(response.getBody().getTotal(), BigDecimal.valueOf(0.99));
    }

    @Test
    public void remove_from_cart() {
        ModifyCartRequest r = dummyCartRequest();
        r.setQuantity(10);

        ResponseEntity<Cart> response = cartController.addTocart(r);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCodeValue());

        // Remove from cart with invalid user
        r.setUsername("johndoe");
        r.setQuantity(2);

        response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Remove item with invalid id
        r = dummyCartRequest();
        r.setItemId(2L);

        response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Remove a valid item for a valid user
        r = dummyCartRequest();
        r.setQuantity(2);

        response = cartController.removeFromcart(r);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(BigDecimal.valueOf(0.99 * 8), response.getBody().getTotal());
    }

    private User createDummyUser() {
        User u = new User();

        u.setId(0);
        u.setUsername("test");
        u.setPassword("testPassword");
        u.setCart(new Cart());

        return u;
    }

    private ModifyCartRequest dummyCartRequest() {
        ModifyCartRequest r = new ModifyCartRequest();

        r.setItemId(1L);
        r.setQuantity(1);
        r.setUsername("test");

        return r;
    }

    private Item createDummyItem() {
        Item i = new Item();

        i.setId(1L);
        i.setName("Some Item");
        i.setDescription("Just another item");
        i.setPrice(BigDecimal.valueOf(0.99));

        return i;
    }
}
