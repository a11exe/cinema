package ru.job4j.cinema.persistence;

import java.util.List;
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

  boolean confirmBooking(Seat seat);

  void loadHall(List<Seat> seats);

  Hall getBooked();

  boolean cancelBooked(Seat seat);

}
