package de.adorsys.swi;

import io.micrometer.core.instrument.config.MeterFilter;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ActuatorConfig {

  @Value("#{'${management.custom.exclude}'.split(',')}")
  private List<String> myList;

  @Bean
  public MeterFilter excludeMeterFilter() {
    return MeterFilter.deny(
      id -> id.getName().startsWith("tomcat") ||
        myList.contains(id.getName())
    );
  }
}
