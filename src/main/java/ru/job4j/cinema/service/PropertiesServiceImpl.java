package ru.job4j.cinema.service;

import java.io.InputStream;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.cinema.persistence.StoreImpl;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 05.12.2019
 */
public class PropertiesServiceImpl implements PropertiesService {

  private static final Logger LOG = LogManager.getLogger(PropertiesServiceImpl.class);
  private final static PropertiesService INSTANCE = new PropertiesServiceImpl();
  private Properties properties;

  private PropertiesServiceImpl() {
    properties = new Properties();
    LOG.info("loading properties");
    try (InputStream in = StoreImpl.class.getClassLoader().getResourceAsStream("app.properties")) {
      properties.load(in);
    } catch (Exception e) {
      LOG.error("Error loading properties: " + e.getMessage());
    }
  }

  public static PropertiesService getInstance() {
    return INSTANCE;
  }

  @Override
  public Properties getProperties() {
    return properties;
  }
}
