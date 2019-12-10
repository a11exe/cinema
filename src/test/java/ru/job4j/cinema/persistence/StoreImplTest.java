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

  private static final Store STORE = StoreImpl.getInstance();
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
    try (Connection connection = STORE.getDataSource().getConnection();
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
    assertNotNull(STORE.getDataSource());
  }

  @Test
  public void getHall() {
    assertThat(STORE.getHall("").getSeats().size(), is(102));
  }

  @Test
  public void whenBookSeatThanBooked() {
    Seat seat = new Seat.Builder()
        .withRow(3)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    assertTrue(STORE.bookSeat(seat, 1));
    seat.setSessionId("userTwo");
    assertFalse(STORE.bookSeat(seat, 1));
  }

  @Test
  public void whenBookAndTimeOutExpiredThanBookAgain() throws InterruptedException {
    Seat seat = new Seat.Builder()
        .withRow(4)
        .withNumber(1)
        .build();
    seat.setSessionId("Bill");
    assertTrue(STORE.bookSeat(seat, 1));
    seat.setSessionId("Mike");
    assertFalse(STORE.bookSeat(seat, 1));
    TimeUnit.SECONDS.sleep(1);
    assertTrue(STORE.bookSeat(seat, 1));
  }

  @Test
  public void whenBookAndConfirmThanConfirmed() {
    Seat seat1 = new Seat.Builder()
        .withRow(1)
        .withNumber(1)
        .build();
    Seat seat2 = new Seat.Builder()
        .withRow(1)
        .withNumber(2)
        .build();
    Seat seat3 = new Seat.Builder()
        .withRow(1)
        .withNumber(3)
        .build();
    seat1.setSessionId("userOne");
    seat2.setSessionId("userOne");
    seat3.setSessionId("userOne");
    assertTrue(STORE.bookSeat(seat1, 1));
    assertTrue(STORE.bookSeat(seat2, 1));
    assertTrue(STORE.bookSeat(seat3, 1));
    assertTrue(STORE.confirmBooking(
        "userOne", new Account("userOne", "89102354545"), ""));
  }

  @Test
  public void whenBookedThanAnotherBookFails() {
    Seat seat1 = new Seat.Builder()
        .withRow(2)
        .withNumber(1)
        .build();
    Seat seat2 = new Seat.Builder()
        .withRow(2)
        .withNumber(2)
        .build();
    Seat seat3 = new Seat.Builder()
        .withRow(2)
        .withNumber(3)
        .build();
    seat1.setSessionId("userOne");
    seat2.setSessionId("userOne");
    seat3.setSessionId("userOne");
    assertTrue(STORE.bookSeat(seat1, 1));
    assertTrue(STORE.bookSeat(seat2, 1));
    assertTrue(STORE.bookSeat(seat3, 1));
    assertTrue(STORE.confirmBooking(
        "userOne", new Account("userOne", "89102354545"), ""));
    assertFalse(STORE.bookSeat(seat1, 1));
    assertFalse(STORE.bookSeat(seat2, 1));
    assertFalse(STORE.bookSeat(seat3, 1));
  }

  @Test
  public void loadHall() {
    List<Seat> seats = STORE.getHall("").getSeats();
    assertThat(seats.size(), is(102));
    List<Seat> seatsNew = new ArrayList<>();
    STORE.loadHall(seatsNew);
    assertThat(STORE.getHall("").getSeats().size(), is(0));
    STORE.loadHall(seats);
    assertThat(seats.size(), is(102));
  }

  @Test
  public void getBooked() {
    Seat seat = new Seat.Builder()
        .withRow(5)
        .withNumber(1)
        .build();
    seat.setSessionId("Bill");
    assertTrue(STORE.bookSeat(seat, 1));
    assertTrue(
        STORE.confirmBooking(
            "Bill", new Account("Bill", "789"), "dsd"));

    List<Seat> seats = STORE.getBooked().getSeats();
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
    Seat seat = new Seat.Builder()
        .withRow(6)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    assertTrue(STORE.bookSeat(seat, 1));
    assertTrue(STORE.confirmBooking(
        "userOne", new Account("userOne", "89102354545"), "dsd"));
    assertFalse(STORE.bookSeat(seat, 1));
    STORE.cancelBooked(seat);
    assertTrue(STORE.bookSeat(seat, 1));
  }

  @Test
  public void cancelBookSeat() {
    Seat seatOne = new Seat.Builder()
        .withRow(7)
        .withNumber(1)
        .build();
    seatOne.setSessionId("userOne");

    Seat seatTwo = new Seat.Builder()
        .withRow(7)
        .withNumber(1)
        .build();
    seatTwo.setSessionId("userTwo");

    assertTrue(STORE.bookSeat(seatOne, 2));
    assertFalse(STORE.bookSeat(seatTwo, 2));
    STORE.getHall("");
    STORE.cancelBookSeat(seatOne);
    STORE.getHall("");
    assertTrue(STORE.bookSeat(seatTwo, 1));
  }

}