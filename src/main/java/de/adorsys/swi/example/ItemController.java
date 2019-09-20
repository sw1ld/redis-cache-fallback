package de.adorsys.swi.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {

  private ItemService itemService;

  public ItemController(ItemService itemService) {
    this.itemService = itemService;
  }

  @PostMapping(value = "/addItem", consumes = {"application/json"}, produces = {"application/json"})
  @ResponseBody
  public ResponseEntity addItem(@RequestBody Item item) {
    itemService.addItem(item);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping("/item/{itemId}")
  @ResponseBody
  public ResponseEntity<Item> getItem(@PathVariable int itemId) {
    Item item = itemService.getItemById(itemId);
    return new ResponseEntity<>(item, HttpStatus.OK);
  }
}
