package ru.job4j.cinema;

import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;
import ru.job4j.cinema.service.PropertiesService;
import ru.job4j.cinema.service.PropertiesServiceImpl;
import ru.job4j.model.Seat;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 27.11.2019
 */
public class Config implements ServletContextListener {

  private static final Logger LOG = LogManager.getLogger(Config.class);
  private final CinemaService service = CinemaServiceImpl.getInstance();
  private final PropertiesService properties = PropertiesServiceImpl.getInstance();

  public void contextInitialized(ServletContextEvent event) {
    // Webapp startup.
    boolean loadHall = Boolean.valueOf(properties.getProperties().getProperty("reload.hall"));
    LOG.info("reload.hall property: " + loadHall);
    if (loadHall) {
      LOG.info("loading hall from xml");
      List<Seat> seats = service
          .readHallConfig(Config.class.getClassLoader().getResourceAsStream("hall.xml"));
      LOG.info("loading hall to DB");
      service.initHall(seats);
    }
  }

  public void contextDestroyed(ServletContextEvent event) {
    // Webapp shutdown.
  }

}
