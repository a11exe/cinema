package ru.job4j.cinema.persistence;

import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;
import ru.job4j.model.Account;
import ru.job4j.model.Hall;
import ru.job4j.model.Seat;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.11.2019
 */
public interface Store {

  Hall getHall(String sessionId);

  boolean bookSeat(Seat seat, int timeOutSec);

  boolean confirmBooking(String sessionId, Account account, String code);

  void loadHall(List<Seat> seats);

  Hall getBooked();

  boolean cancelBooked(Seat seat);

  boolean cancelBookSeat(Seat seat);

  BasicDataSource getDataSource();

}
