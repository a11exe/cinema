package ru.job4j.cinema.service;

import java.io.InputStream;
import java.util.List;
import ru.job4j.model.Account;
import ru.job4j.model.Hall;
import ru.job4j.model.Seat;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.11.2019
 */
public interface CinemaService {

  Hall getHall(String sessionId);

  void bookSeat(Seat seat);

  String confirmBooking(String sessionId, Account account);

  List<Seat> readHallConfig(InputStream hallConfigXMLIS);

  void initHall(List<Seat> seats);

  Hall getBooked();

  boolean cancelBooked(Seat seat);

  boolean cancelBookSeat(Seat seat);
}
