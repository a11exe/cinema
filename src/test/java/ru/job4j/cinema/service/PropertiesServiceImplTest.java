package ru.job4j.cinema.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 05.12.2019
 */
public class PropertiesServiceImplTest {

  @Test
  public void getProperties() {
    PropertiesService propertiesService = PropertiesServiceImpl.getInstance();
    assertThat(propertiesService.getProperties().getProperty("admin.pass"), is("admin"));
    assertThat(propertiesService.getProperties().getProperty("booking.timeout.seconds"), is("300"));
    assertThat(propertiesService.getProperties().getProperty("reload.hall"), is("true"));

    assertThat(propertiesService.getProperties().getProperty("url"), is("jdbc:h2:mem:cinema"));
    assertThat(propertiesService.getProperties().getProperty("username"), is("sa"));
    assertThat(propertiesService.getProperties().getProperty("password"), is(""));
    assertThat(propertiesService.getProperties().getProperty("driver"), is("org.h2.Driver"));
    assertThat(propertiesService.getProperties().getProperty("minIdle"), is("5"));
    assertThat(propertiesService.getProperties().getProperty("maxIdle"), is("10"));
    assertThat(propertiesService.getProperties().getProperty("maxOpenPreparedStatements"), is("100"));
  }
}