package ru.job4j.cinema.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.job4j.cinema.service.PropertiesService;
import ru.job4j.cinema.service.PropertiesServiceImpl;
import ru.job4j.model.Account;
import ru.job4j.model.Hall;
import ru.job4j.model.Seat;
import ru.job4j.model.State;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.11.2019
 */
public class StoreImpl implements Store {

  private static final Logger LOG = LogManager.getLogger(StoreImpl.class);
  private static final BasicDataSource SOURCE = new BasicDataSource();
  private final PropertiesService properties = PropertiesServiceImpl.getInstance();

  private final static Store INSTANCE = new StoreImpl();
  private static final String SQL_HALL =
          "SELECT id, row, seat_number, price, booked_until, session_id, account_id " +
                  "FROM HALLS ORDER BY row, seat_number";
  private static final String SQL_BOOKING =
          "UPDATE HALLS SET session_id = ?, booked_until = ? " +
          "WHERE row = ? AND seat_number = ? " +
          "AND ((session_id = ? OR session_id is NULL) OR (booked_until <= ? OR booked_until is NULL)) " +
          "AND account_id is NULL";
  private static final String SQL_CANCEL_BOOKING =
          "UPDATE HALLS SET session_id = ?, booked_until = ? " +
          //"WHERE session_id = ? AND row = ? AND seat_number = ?";
          "WHERE session_id = ? AND account_id is NULL AND row = ? AND seat_number = ?";
  private static final String SQL_CONFIRM_BOOKING =
          "UPDATE HALLS SET account_id = ?, code = ? " +
          "WHERE session_id = ? AND account_id is NULL";
  private static final String SQL_FIND_ACCOUNT =
          "SELECT id, fio, phone " +
                  "FROM ACCOUNTS WHERE fio = ? AND phone = ?";
  private static final String SQL_INSERT_ACCOUNT =
          "INSERT INTO ACCOUNTS (fio, phone) VALUES (?, ?)";
  private static final String SQL_CLEAR_HALL =
      "TRUNCATE TABLE HALLS";
  private static final String SQL_INSERT_SEAT =
      "INSERT INTO HALLS (row, seat_number, price) VALUES (?, ?, ?)";
  private static final String SQL_ADMIN_BOOKED =
      "SELECT halls.id, row, seat_number, price, booked_until, session_id, account_id, code, fio, phone " +
          "FROM HALLS LEFT JOIN ACCOUNTS ON account_id = accounts.id ORDER BY row, seat_number";
  private static final String SQL_ADMIN_CANCEL_BOOKED =
      "UPDATE HALLS SET session_id = ?, account_id = ?, code = ?, booked_until = ? WHERE row = ? AND seat_number = ?";

  private StoreImpl() {

    SOURCE.setDriverClassName(properties.getProperties().getProperty("driver"));
    SOURCE.setUrl(properties.getProperties().getProperty("url"));
    SOURCE.setUsername(properties.getProperties().getProperty("username"));
    SOURCE.setPassword(properties.getProperties().getProperty("password"));
    SOURCE.setMinIdle(Integer.parseInt(properties.getProperties().getProperty("minIdle")));
    SOURCE.setMaxIdle(Integer.parseInt(properties.getProperties().getProperty("maxIdle")));
    SOURCE.setMaxOpenPreparedStatements(Integer.parseInt(properties.getProperties().getProperty("maxOpenPreparedStatements")));
  }

  public static Store getInstance() {
    return INSTANCE;
  }

  private State getState(
          Timestamp now, Timestamp bookedUntil, String sessionId, String bookSessionId, int accountId) {

    State state = State.FREE;
    if (accountId > 0) {
      state = State.BOOKED;
    } else {
      if (bookedUntil != null && bookedUntil.getTime() > now.getTime()) {
        if (sessionId.equals(bookSessionId)) {
         state = State.SELECTED;
        } else {
          state = State.PENDING;
        }
      }
    }
    return state;
  }

  @Override
  public BasicDataSource getDataSource() {
    return SOURCE;
  }

  @Override
  public Hall getHall(String sessionId) {

    List<Seat> seats = new ArrayList<>();
    try (Connection connection = SOURCE.getConnection();
        PreparedStatement st = connection.prepareStatement(SQL_HALL)
    ) {
      ResultSet rs = st.executeQuery();
      while (rs.next()) {

        seats.add(new Seat.Builder()
            .withId(rs.getInt("id"))
            .withRow(rs.getInt("row"))
            .withNumber(rs.getInt("seat_number"))
            .withPrice(rs.getBigDecimal("price"))
            .withState(
                getState(
                    new Timestamp(System.currentTimeMillis()),
                    rs.getTimestamp("booked_until"),
                    sessionId,
                    rs.getString("session_id"),
                    rs.getInt("account_id")))
            .build());

      }
    } catch (Exception e) {
      LOG.error("error getSeats" + e.getMessage());
    }
    return new Hall(seats);
  }

  @Override
  public boolean bookSeat(Seat seat, int timeOutSec) {

    boolean result = false;
    Timestamp bookUntil = new Timestamp(System.currentTimeMillis());
    bookUntil.setTime(bookUntil.getTime() + TimeUnit.SECONDS.toMillis(timeOutSec));

    try (Connection connection = SOURCE.getConnection();
        PreparedStatement bookSt = connection.prepareStatement(SQL_BOOKING)
    ) {

      connection.setAutoCommit(false);

      bookSt.setString(1, seat.getSessionId());
      bookSt.setTimestamp(2, bookUntil);
      bookSt.setInt(3, seat.getRow());
      bookSt.setInt(4, seat.getNumber());
      bookSt.setString(5, seat.getSessionId());
      bookSt.setTimestamp(6, new Timestamp(System.currentTimeMillis()));

      result = (bookSt.executeUpdate() > 0);

      connection.commit();

    } catch (Exception e) {
      LOG.error("error bookSeat" + e.getMessage());
    }

    return result;
  }

  @Override
  public boolean confirmBooking(String sessionId, Account account, String code) {

    boolean result = false;

    try (Connection connection = SOURCE.getConnection();
         PreparedStatement findAccSt = connection.prepareStatement(SQL_FIND_ACCOUNT);
         PreparedStatement createAccSt =
             connection.prepareStatement(SQL_INSERT_ACCOUNT, Statement.RETURN_GENERATED_KEYS);
         PreparedStatement confirmSt = connection.prepareStatement(SQL_CONFIRM_BOOKING)
    ) {

      connection.setAutoCommit(false);

      Integer accountId = null;

      findAccSt.setString(1, account.getName());
      findAccSt.setString(2, account.getPhone());

      ResultSet rs = findAccSt.executeQuery();
      while (rs.next()) {
        accountId = rs.getInt("id");
      }
      if (accountId == null) {
        createAccSt.setString(1, account.getName());
        createAccSt.setString(2, account.getPhone());
        createAccSt.executeUpdate();
        rs = createAccSt.getGeneratedKeys();

        if (rs.next()) {
          accountId = rs.getInt(1);
        }
      }

      account.setId(accountId);

      confirmSt.setInt(1, account.getId());
      confirmSt.setString(2, code);
      confirmSt.setString(3, sessionId);

      result = (confirmSt.executeUpdate() > 0);

      connection.commit();

    } catch (Exception e) {
      LOG.error("error confirmBooking" + e.getMessage());
    }

    return result;
  }

  @Override
  public void loadHall(List<Seat> seats) {

    try (Connection connection = SOURCE.getConnection();
        PreparedStatement seatSt = connection.prepareStatement(SQL_INSERT_SEAT);
        PreparedStatement clearSt = connection.prepareStatement(SQL_CLEAR_HALL)
    ) {

      connection.setAutoCommit(false);

      clearSt.executeUpdate();

      for (Seat seat: seats
      ) {
        seatSt.setInt(1, seat.getRow());
        seatSt.setInt(2, seat.getNumber());
        seatSt.setBigDecimal(3, seat.getPrice());
        seatSt.executeUpdate();
      }

      connection.commit();

    } catch (Exception e) {
      LOG.error("error loadHall" + e.getMessage());
    }
  }

  @Override
  public Hall getBooked() {
    List<Seat> seats = new ArrayList<>();
    try (Connection connection = SOURCE.getConnection();
        PreparedStatement st = connection.prepareStatement(SQL_ADMIN_BOOKED)
    ) {
      ResultSet rs = st.executeQuery();
      while (rs.next()) {

        seats.add(new Seat.Builder()
            .withId(rs.getInt("id"))
            .withRow(rs.getInt("row"))
            .withNumber(rs.getInt("seat_number"))
            .withPrice(rs.getBigDecimal("price"))
            .withSessionId(rs.getString("session_id"))
            .withAccount(new Account(
                rs.getInt("account_id"),
                rs.getString("fio"),
                rs.getString("phone")
            ))
            .withState(
                getState(
                    new Timestamp(System.currentTimeMillis()),
                    rs.getTimestamp("booked_until"),
                    "",
                    rs.getString("session_id"),
                    rs.getInt("account_id")
                ))
            .withCode(rs.getString("code"))
            .build());

      }
    } catch (Exception e) {
      LOG.error("error getBooked" + e.getMessage());
    }
    return new Hall(seats);
  }

  @Override
  public boolean cancelBooked(Seat seat) {
    boolean result = false;

    try (Connection connection = SOURCE.getConnection();
        PreparedStatement cancelBookedSt = connection.prepareStatement(SQL_ADMIN_CANCEL_BOOKED)
    ) {

      connection.setAutoCommit(false);

      cancelBookedSt.setNull(1, Types.NULL);
      cancelBookedSt.setNull(2, Types.NULL);
      cancelBookedSt.setNull(3, Types.NULL);
      cancelBookedSt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
      cancelBookedSt.setInt(5, seat.getRow());
      cancelBookedSt.setInt(6, seat.getNumber());
      cancelBookedSt.executeUpdate();

      result = (cancelBookedSt.executeUpdate() > 0);

      connection.commit();

    } catch (Exception e) {
      LOG.error("error cancelBooked" + e.getMessage());
    }

    return result;
  }

  @Override
  public boolean cancelBookSeat(Seat seat) {
    boolean result = false;

    try (Connection connection = SOURCE.getConnection();
        PreparedStatement cancelBookSt = connection.prepareStatement(SQL_CANCEL_BOOKING)
    ) {

      connection.setAutoCommit(false);

      cancelBookSt.setNull(1, Types.NULL);
      cancelBookSt.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
      cancelBookSt.setString(3, seat.getSessionId());
      cancelBookSt.setInt(4, seat.getRow());
      cancelBookSt.setInt(5, seat.getNumber());
      cancelBookSt.executeUpdate();

      result = (cancelBookSt.executeUpdate() > 0);

      connection.commit();

    } catch (Exception e) {
      LOG.error("error cancelBookSeat" + e.getMessage());
    }

    return result;
  }
}
