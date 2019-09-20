package de.adorsys.swi.example;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import de.adorsys.swi.RedisCacheFallbackExampleApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = RedisCacheFallbackExampleApplication.class)
public class ItemIT {

  @LocalServerPort
  private int randomServerPort;
  private String baseUrl;
  private HttpHeaders headers;
  private TestRestTemplate testRestTemplate;

  @Autowired
  private TestDataService testDataService;

  @Before
  public void setup() {
    baseUrl = "http://localhost:" + randomServerPort;

    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    testRestTemplate = new TestRestTemplate();

    initItemDatabase();
  }

  @Test
  public void createItem() {
    HttpEntity<Item> requestEntity = new HttpEntity<>(
      new Item(10, "Totenfang", "Books"),
      headers
    );

    ResponseEntity<Item> response = testRestTemplate.exchange(
      baseUrl + "/addItem",
      HttpMethod.POST,
      requestEntity,
      Item.class
    );

    assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
  }

  @Test
  public void getItem() {
    ResponseEntity<Item> response = testRestTemplate.exchange(
      baseUrl + "/item/8",
      HttpMethod.GET,
      new HttpEntity<>(headers),
      Item.class
    );

    assertThat(response.getStatusCode(), is(HttpStatus.OK));

    assertThat(response.getBody().getCategory(), is("Books"));
    assertThat(response.getBody().getName(), is("Kalte Asche"));
  }

  private void initItemDatabase() {
    testDataService.addItem(new Item(8, "Kalte Asche", "Books"));
    testDataService.addItem(new Item(9, "Fight Club", "Films"));
  }
}