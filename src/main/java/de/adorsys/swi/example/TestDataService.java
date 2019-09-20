package de.adorsys.swi.example;

import java.util.HashMap;
import org.springframework.stereotype.Service;

@Service
public class TestDataService {

  private HashMap<Integer, Item> items;

  public TestDataService() {
    items = new HashMap<>();
    initTestData();
  }

  void addItem(Item item) {
    items.put(item.getId(), item);
  }

  Item getItem(int id) {
    return items.get(id);
  }

  private void initTestData() {
    items.put(8, new Item(8, "Kalte Asche", "Books"));
    items.put(9, new Item(9, "Fight Club", "Films"));
  }
}
