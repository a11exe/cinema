package ru.job4j.cinema.service;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ru.job4j.cinema.persistence.Store;
import ru.job4j.cinema.persistence.StoreImpl;
import ru.job4j.model.Account;
import ru.job4j.model.Hall;
import ru.job4j.model.Seat;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.11.2019
 */
public class CinemaServiceImpl implements CinemaService {

  private final Store store = StoreImpl.getInstance();
  private final static CinemaService INSTANCE = new CinemaServiceImpl();
  private static final Logger LOG = LogManager.getLogger(CinemaServiceImpl.class);
  private final PropertiesService properties = PropertiesServiceImpl.getInstance();

  private CinemaServiceImpl() {
  }

  public static CinemaService getInstance() {
    return INSTANCE;
  }


  @Override
  public Hall getHall(String sessionId) {
    return store.getHall(sessionId);
  }

  @Override
  public void bookSeat(Seat seat) {
    store.bookSeat(seat, Integer.valueOf(properties.getProperties().getProperty("booking.timeout.seconds")));
  }

  @Override
  public String confirmBooking(String sessionId, Account account) {
    String code = generateBookingConfirmationCode();
    if (store.confirmBooking(sessionId, account, code)) {
      return code;
    } else {
      return null;
    }
  }

  @Override
  public List<Seat> readHallConfig(InputStream hallConfigXMLIS) {

    List<Seat> seats = new ArrayList<>();

    try {
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
      Document doc = dBuilder.parse(hallConfigXMLIS);

      //optional, but recommended
      //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
      doc.getDocumentElement().normalize();

      LOG.info("Root element :" + doc.getDocumentElement().getNodeName());

      NodeList nList = doc.getElementsByTagName("Row");

      for (int temp = 0; temp < nList.getLength(); temp++) {
        Node nRows = nList.item(temp);
        LOG.info("\nCurrent Element :" + nRows.getNodeName());
        Element eRow = (Element) nRows;
        int row = Integer.valueOf(eRow.getAttribute("number"));
        LOG.info("Row number : " + row);

        NodeList nSeats = eRow.getElementsByTagName("Seat");
        for (int seatN = 0; seatN < nSeats.getLength(); seatN++) {
          Element eSeat = (Element) nSeats.item(seatN);
          int seat = Integer.valueOf(eSeat.getAttribute("number"));
          BigDecimal price = BigDecimal.valueOf(Long.valueOf(eSeat.getAttribute("price")));
          LOG.info("Seat number : " + seat);

          seats.add(new Seat.Builder()
              .withRow(row)
              .withNumber(seat)
              .withPrice(price)
              .build());
        }
      }
    } catch (Exception e) {
      LOG.error("Error parsing hall.xml");
    }

    return seats;
  }

  @Override
  public void initHall(List<Seat> seats) {
      store.loadHall(seats);
  }

  private String generateBookingConfirmationCode() {

    Random rnd = new Random();
    int number = rnd.nextInt(999999);

    // this will convert any number sequence into 6 character.
    return String.format("%06d", number);
  }

  @Override
  public Hall getBooked() {
    return store.getBooked();
  }

  @Override
  public boolean cancelBooked(Seat seat) {
    return store.cancelBooked(seat);
  }

  @Override
  public boolean cancelBookSeat(Seat seat) {
    return store.cancelBookSeat(seat);
  }
}
