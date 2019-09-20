package de.adorsys.swi.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class ItemCacheService {

  private static Logger log = LoggerFactory.getLogger(ItemCacheService.class);
  private TestDataService testDataService;

  public ItemCacheService(
    TestDataService testDataService) {
    this.testDataService = testDataService;
  }

  @CachePut(value = "itemCache", key = "#id", condition = "#result != null")
  public void addItem(Item item) {
    log.info("addItem [{}, {}, {}]", item.getId(), item.getCategory(), item.getName());
    testDataService.addItem(item);
  }

  @Cacheable(value = "itemCache", key = "#id")
  public Item getItemById(int id) {
    log.info("getItem by Id [{}]", id);
    Item item = null;
    try {
      item = testDataService.getItem(id);
    } catch (Exception e) {
      log.error("Error while fetching item [{}], {}", id, e);
    }
    return item;
  }
}
