package ru.job4j.cinema.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.cinema.Config;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;
import ru.job4j.model.Account;
import ru.job4j.model.Seat;
import ru.job4j.model.State;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 05.12.2019
 */
public class StoreImplTest {

  private static final Store store = StoreImpl.getInstance();
  private static final Logger LOG = LogManager.getLogger(StoreImplTest.class);
  private static final String SQL_INIT =
             "CREATE TABLE ACCOUNTS"
          + "("
          + "    ID integer AUTO_INCREMENT PRIMARY KEY NOT NULL,"
          + "    FIO varchar(255),"
          + "    PHONE varchar(25)"
          + ");"
          + "CREATE TABLE HALLS"
          + "("
          + "    ID integer AUTO_INCREMENT PRIMARY KEY NOT NULL,"
          + "    ROW integer,"
          + "    SEAT_NUMBER integer,"
          + "    PRICE decimal(10,2),"
          + "    BOOKED_UNTIL timestamp,"
          + "    SESSION_ID varchar(255),"
          + "    ACCOUNT_ID integer,"
          + "    CODE varchar(6),"
          + "    CONSTRAINT FK_HALLS_ACCOUNTS FOREIGN KEY (ACCOUNT_ID) REFERENCES ACCOUNTS (ID) ON DELETE CASCADE"
          + ");";

  @BeforeClass
  public static void initDb() {
    try (Connection connection = store.getDataSource().getConnection();
        PreparedStatement bookSt = connection.prepareStatement(SQL_INIT)
    ) {
      bookSt.executeUpdate();
    } catch (Exception e) {
      LOG.error("error bookSeat" + e.getMessage());
    }
    CinemaService service = CinemaServiceImpl.getInstance();
    List<Seat> seats = service
        .readHallConfig(Config.class.getClassLoader().getResourceAsStream("hall.xml"));
    LOG.info("loading hall to DB");
    service.initHall(seats);
  }


  @Test
  public void getDataSource() {
    assertNotNull(store.getDataSource());
  }

  @Test
  public void getHall() {
    assertThat(store.getHall("").getSeats().size(), is(102));
  }

  @Test
  public void whenBookSeatThanBooked() {
    Seat seat = new Seat(3, 1, BigDecimal.ZERO);
    seat.setSessionId("userOne");
    assertTrue(store.bookSeat(seat, 1));
    seat.setSessionId("userTwo");
    assertFalse(store.bookSeat(seat, 1));
  }

  @Test
  public void whenBookAndTimeOutExpiredThanBookAgain() throws InterruptedException {
    Seat seat = new Seat(4, 1, BigDecimal.ZERO);
    seat.setSessionId("Bill");
    assertTrue(store.bookSeat(seat, 1));
    seat.setSessionId("Mike");
    assertFalse(store.bookSeat(seat, 1));
    TimeUnit.SECONDS.sleep(1);
    assertTrue(store.bookSeat(seat, 1));
  }

  @Test
  public void whenBookAndConfirmThanConfirmed() {
    Seat seat1 = new Seat(1, 1, BigDecimal.ZERO);
    Seat seat2 = new Seat(1, 2, BigDecimal.ZERO);
    Seat seat3 = new Seat(1, 3, BigDecimal.ZERO);
    seat1.setSessionId("userOne");
    seat2.setSessionId("userOne");
    seat3.setSessionId("userOne");
    assertTrue(store.bookSeat(seat1, 1));
    assertTrue(store.bookSeat(seat2, 1));
    assertTrue(store.bookSeat(seat3, 1));
    assertTrue(store.confirmBooking(
        "userOne", new Account("userOne", "89102354545"), ""));
  }

  @Test
  public void whenBookedThanAnotherBookFails() {
    Seat seat1 = new Seat(2, 1, BigDecimal.ZERO);
    Seat seat2 = new Seat(2, 2, BigDecimal.ZERO);
    Seat seat3 = new Seat(2, 3, BigDecimal.ZERO);
    seat1.setSessionId("userOne");
    seat2.setSessionId("userOne");
    seat3.setSessionId("userOne");
    assertTrue(store.bookSeat(seat1, 1));
    assertTrue(store.bookSeat(seat2, 1));
    assertTrue(store.bookSeat(seat3, 1));
    assertTrue(store.confirmBooking(
        "userOne", new Account("userOne", "89102354545"), ""));
    assertFalse(store.bookSeat(seat1, 1));
    assertFalse(store.bookSeat(seat2, 1));
    assertFalse(store.bookSeat(seat3, 1));
  }

  @Test
  public void loadHall() {
    List<Seat> seats = store.getHall("").getSeats();
    assertThat(seats.size(), is(102));
    List<Seat> seatsNew = new ArrayList<>();
    store.loadHall(seatsNew);
    assertThat(store.getHall("").getSeats().size(), is(0));
    store.loadHall(seats);
    assertThat(seats.size(), is(102));
  }

  @Test
  public void getBooked() {
    Seat seat = new Seat(5, 1, BigDecimal.ZERO);
    seat.setSessionId("Bill");
    assertTrue(store.bookSeat(seat, 1));
    assertTrue(
        store.confirmBooking(
            "Bill", new Account("Bill", "789"), "dsd"));

    List<Seat> seats = store.getBooked().getSeats();
    boolean hasBooked = false;
    for (Seat seatDb: seats
    ) {
      if (seatDb.getRow() == seat.getRow()
          && seatDb.getNumber() == seat.getNumber()
          && seatDb.getState().equals(State.BOOKED)) {
        hasBooked = true;
        break;
      }
    }
    assertTrue(hasBooked);
  }

  @Test
  public void cancelBooked() {
    Seat seat = new Seat(6, 1, BigDecimal.ZERO);
    seat.setSessionId("userOne");
    assertTrue(store.bookSeat(seat, 1));
    assertTrue(store.confirmBooking(
        "userOne", new Account("userOne", "89102354545"), "dsd"));
    assertFalse(store.bookSeat(seat, 1));
    store.cancelBooked(seat);
    assertTrue(store.bookSeat(seat, 1));
  }

  @Test
  public void cancelBookSeat() {
    Seat seatOne = new Seat(7, 1, BigDecimal.ZERO);
    seatOne.setSessionId("userOne");

    Seat seatTwo = new Seat(7, 1, BigDecimal.ZERO);
    seatTwo.setSessionId("userTwo");

    assertTrue(store.bookSeat(seatOne, 2));
    assertFalse(store.bookSeat(seatTwo, 2));
    store.getHall("");
    store.cancelBookSeat(seatOne);
    store.getHall("");
    assertTrue(store.bookSeat(seatTwo, 1));
  }

}