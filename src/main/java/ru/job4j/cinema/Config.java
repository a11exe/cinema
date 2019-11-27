package ru.job4j.cinema;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 27.11.2019
 */
public class Config implements ServletContextListener {

  private static final Logger LOG = LogManager.getLogger(Config.class);

  public void contextInitialized(ServletContextEvent event) {
    // Webapp startup.
    generateHallConfigFromXml();
  }

  public void contextDestroyed(ServletContextEvent event) {
    // Webapp shutdown.
  }

  private static void generateHallConfigFromXml() {
    //TODO read Hall.xml
    try {

      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(Config.class.getClassLoader().getResourceAsStream("hall.xml"));

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      LOG.info("Root element :" + doc.getDocumentElement().getNodeName());

      NodeList nList = doc.getElementsByTagName("Row");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        Node nRows = nList.item(temp);
        LOG.info("\nCurrent Element :" + nRows.getNodeName());
        Element eElement = (Element) nRows;
        LOG.info("Row number : " + eElement.getAttribute("number"));

        NodeList nSeats = eElement.getElementsByTagName("Seat");
        for (int seatN = 0; seatN < nSeats.getLength(); seatN++) {
          Element eSeat = (Element) nSeats.item(seatN);
          LOG.info("Seat number : " + eSeat.getAttribute("number"));
        }
      }

    } catch (Exception e) {
      LOG.error("Error parsing hall.xml");
    }
    //TODO create schema in DB
  }

}
