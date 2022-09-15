package com.bitpay.sdk.model.Bill;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class ItemTest {
  @Test
  public void testGetId() {
    String expectedId = "EL4vx41Nxc5RYhbqDthjE";

    Item item = new Item();
    item.setId(expectedId);

    String actualId = item.getId();

    assertEquals(expectedId, actualId);
  }

  @Test
  public void testGetDescription() {
    String expectedDescription = "Test Item 1";

    Item item = new Item();
    item.setDescription(expectedDescription);

    String actualDescription = item.getDescription();

    assertEquals(expectedDescription, actualDescription);
  }

  @Test
  public void testGetPrice() {
    Double expectedPrice = 12.34;

    Item item = new Item();
    item.setPrice(expectedPrice);

    Double actualPrice = item.getPrice();

    assertEquals(expectedPrice, actualPrice);
  }

  @Test
  public void testGetQuantity() {
    Integer expectedQuantity = 10;

    Item item = new Item();
    item.setQuantity(expectedQuantity);

    Integer actualQuantity = item.getQuantity();

    assertEquals(expectedQuantity, actualQuantity);
  }
}
