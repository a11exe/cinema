package ru.job4j.model;

import java.util.List;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.11.2019
 */
public class Hall {

  private final List<Seat> seats;

  public Hall(List<Seat> seats) {
    this.seats = seats;
  }

  public List<Seat> getSeats() {
    return seats;
  }
}
