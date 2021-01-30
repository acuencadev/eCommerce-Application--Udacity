package com.acuencadev.ecommerce.controllers;

import com.acuencadev.ecommerce.TestUtils;
import com.acuencadev.ecommerce.model.persistence.Item;
import com.acuencadev.ecommerce.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController(null);
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        Item dummyItem = createDummyItem();
        List<Item> dummyItemList = createDummyItemList();

        when(itemRepository.findById(1L)).thenReturn(Optional.of(dummyItem));
        when(itemRepository.findById(2L)).thenReturn(Optional.ofNullable(null));
        when(itemRepository.findByName("Some Item")).thenReturn(dummyItemList);
        when(itemRepository.findByName("Another Item")).thenReturn(null);
        when(itemRepository.findAll()).thenReturn(dummyItemList);
    }

    @Test
    public void get_items() {
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void get_item_by_item() {
        ResponseEntity<Item> response = itemController.getItemById(2L);

        // Get item with invalid ID
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Get item with valid ID
        response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        assertEquals(response.getBody().getName(), "Some Item");
        assertEquals(response.getBody().getDescription(), "Just another item");
        assertEquals(response.getBody().getPrice(), BigDecimal.valueOf(0.99));
    }

    @Test
    public void get_item_by_name() {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Another Item");

        // Get items with non-existing name
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // Get items with existing name;
        response = itemController.getItemsByName("Some Item");
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
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
}
