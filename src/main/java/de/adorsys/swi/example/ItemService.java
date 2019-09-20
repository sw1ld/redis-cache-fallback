package de.adorsys.swi.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;

@Component
public class ItemService {

  private static Logger log = LoggerFactory.getLogger(ItemService.class);
  private ItemCacheService itemCacheService;
  private TestDataService testDataService;

  public ItemService(
    ItemCacheService itemCacheService, TestDataService testDataService) {
    this.itemCacheService = itemCacheService;
    this.testDataService = testDataService;
  }

  void addItem(Item item) {
    itemCacheService.addItem(item);
  }

  Item getItemById(int id) {
    Item item;
    try {
      item = itemCacheService.getItemById(id);
    } catch (RedisConnectionFailureException e) {
      log.warn("No Redis Connection while fetching item [{}]", id);
      item = testDataService.getItem(id);
    }
    return item;
  }
}
