package ru.job4j.cinema.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.job4j.cinema.Config;
import ru.job4j.cinema.persistence.Store;
import ru.job4j.cinema.persistence.StoreImpl;
import ru.job4j.model.Account;
import ru.job4j.model.Seat;
import ru.job4j.model.State;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 10.12.2019
 */
public class CinemaServiceTest {

  private static final CinemaService SERVICE = CinemaServiceImpl.getInstance();
  private static final Logger LOG = LogManager.getLogger(CinemaServiceTest.class);
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
    Store store = StoreImpl.getInstance();
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
  public void getHall() {
    assertThat(SERVICE.getHall("").getSeats().size(), is(102));
  }

  @Test
  public void bookSeat() {
    Seat seat = new Seat.Builder()
        .withRow(3)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    SERVICE.bookSeat(seat);
    assertThat(SERVICE.getHall(seat.getSessionId()).getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.SELECTED));
  }

  @Test
  public void confirmBooking() {
    Seat seat = new Seat.Builder()
        .withRow(4)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    SERVICE.bookSeat(seat);
    SERVICE.confirmBooking(seat.getSessionId(), new Account("Mike", ""));
    assertThat(SERVICE.getHall(seat.getSessionId()).getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.BOOKED));
  }

  @Test
  public void readHallConfig() {
    assertThat(SERVICE.readHallConfig(
        CinemaServiceTest.class.getClassLoader().getResourceAsStream(
            "hall.xml")).size(), is(102));
  }

  @Test
  public void getBooked() {
    Seat seat = new Seat.Builder()
        .withRow(5)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    SERVICE.bookSeat(seat);
    SERVICE.confirmBooking(seat.getSessionId(), new Account("Mike", ""));
    assertThat(SERVICE.getBooked().getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.BOOKED));
  }

  @Test
  public void cancelBooked() {
    Seat seat = new Seat.Builder()
        .withRow(6)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    SERVICE.bookSeat(seat);
    SERVICE.confirmBooking(seat.getSessionId(), new Account("Mike", ""));
    assertThat(SERVICE.getBooked().getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.BOOKED));
    SERVICE.cancelBooked(seat);
    assertThat(SERVICE.getBooked().getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.FREE));
  }

  @Test
  public void cancelBookSeat() {
    Seat seat = new Seat.Builder()
        .withRow(7)
        .withNumber(1)
        .build();
    seat.setSessionId("userOne");
    SERVICE.bookSeat(seat);
    assertThat(SERVICE.getBooked().getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.PENDING));
    SERVICE.cancelBookSeat(seat);
    assertThat(SERVICE.getBooked().getSeats().stream()
        .filter(s -> (s.getRow() == seat.getRow() && s.getNumber() == seat.getNumber()))
        .findFirst().get().getState(), is(State.FREE));
  }
}