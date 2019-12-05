package ru.job4j.cinema.persistence;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 05.12.2019
 */
public class StoreImplTest {

  private Store store = StoreImpl.getInstance();

  @BeforeClass
  public static void initDB() {

  }

  @Test
  public void getHall() {
    assertThat(store.getHall("").getSeats().size(), is(50));
  }

  @Test
  public void bookSeat() {
  }

  @Test
  public void confirmBooking() {
  }

  @Test
  public void loadHall() {
  }

  @Test
  public void getBooked() {
  }

  @Test
  public void cancelBooked() {
  }

  @Test
  public void cancelBookSeat() {
  }
}