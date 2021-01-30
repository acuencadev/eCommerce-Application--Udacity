package com.acuencadev.ecommerce.controllers;

import com.acuencadev.ecommerce.TestUtils;
import com.acuencadev.ecommerce.model.persistence.Cart;
import com.acuencadev.ecommerce.model.persistence.Item;
import com.acuencadev.ecommerce.model.persistence.User;
import com.acuencadev.ecommerce.model.persistence.UserOrder;
import com.acuencadev.ecommerce.model.persistence.repositories.OrderRepository;
import com.acuencadev.ecommerce.model.persistence.repositories.UserRepository;
import com.acuencadev.ecommerce.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController(null, null);

        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);

        User dummyUser = createDummyUser();
        List<UserOrder> dummyUserOrderList = dummyUserOrderList(dummyUser);

        when(userRepository.findByUsername("test")).thenReturn(dummyUser);
        when(userRepository.findByUsername("johndoe")).thenReturn(null);
        when(orderRepository.findByUser(dummyUser)).thenReturn(dummyUserOrderList);
    }

    @Test
    public void submit_order() {
        // Non existing user
        ResponseEntity<UserOrder> response = orderController.submit("johndoe");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Existing user
        response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(response.getBody().getTotal(), BigDecimal.valueOf(0.99));
        assertEquals(response.getBody().getItems().size(), 1);
    }

    @Test
    public void get_orders_for_user() {
        // Non existing user
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("johndoe");

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Existing user
        response = orderController.getOrdersForUser("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(response.getBody().size(), 1);
    }

    private Item createDummyItem() {
        Item i = new Item();

        i.setId(1L);
        i.setName("Some Item");
        i.setDescription("Just another item");
        i.setPrice(BigDecimal.valueOf(0.99));

        return i;
    }

    private List<Item> createDummyItemList() {
        List<Item> l = new ArrayList<>();

        l.add(createDummyItem());

        return l;
    }

    private User createDummyUser() {
        User u = new User();

        u.setId(0);
        u.setUsername("test");
        u.setPassword("testPassword");
        u.setCart(dummyCart(u));

        return u;
    }

    private Cart dummyCart(User user) {
        Cart c = new Cart();

        c.setId(1L);
        c.setUser(user);
        c.setItems(createDummyItemList());
        c.setTotal(BigDecimal.valueOf(0.99));

        return c;
    }

    private List<UserOrder> dummyUserOrderList(User user) {
        List<UserOrder> l = new ArrayList<>();

        l.add(dummyUserOrder(user));

        return l;
    }

    private UserOrder dummyUserOrder(User user) {
        UserOrder u = new UserOrder();

        u.setId(1L);
        u.setUser(user);
        u.setItems(createDummyItemList());
        u.setTotal(BigDecimal.valueOf(0.99));

        return u;
    }

    private ModifyCartRequest dummyCartRequest() {
        ModifyCartRequest r = new ModifyCartRequest();

        r.setItemId(1L);
        r.setQuantity(1);
        r.setUsername("test");

        return r;
    }
}
