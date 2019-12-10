package ru.job4j.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 21.11.2019
 */
public class Seat {

  private int id;
  private int row;
  private int number;
  private BigDecimal price;
  private String sessionId;
  private Account account;
  private State state;
  private String code;

  public Seat() {
  }

  public Seat(Builder builder) {
    this.id = builder.id;
    this.row = builder.row;
    this.number = builder.number;
    this.price = builder.price;
    this.sessionId = builder.sessionId;
    this.account = builder.account;
    this.state = builder.state;
    this.code = builder.code;
  }

  public static class Builder {

    private int id;
    private int row;
    private int number;
    private BigDecimal price;
    private String sessionId;
    private Account account;
    private State state;
    private String code;

    public Seat build() {
      return new Seat(this);
    }

    public Builder withId(int id) {
      this.id = id;
      return this;
    }

    public Builder withRow(int row) {
      this.row = row;
      return this;
    }

    public Builder withNumber(int number) {
      this.number = number;
      return this;
    }

    public Builder withPrice(BigDecimal price) {
      this.price = price;
      return this;
    }

    public Builder withSessionId(String sessionId) {
      this.sessionId = sessionId;
      return this;
    }

    public Builder withAccount(Account account) {
      this.account = account;
      return this;
    }

    public Builder withState(State state) {
      this.state = state;
      return this;
    }

    public Builder withCode(String code) {
      this.code = code;
      return this;
    }

  }

  public void setId(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public int getRow() {
    return row;
  }

  public int getNumber() {
    return number;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public Account getAccount() {
    return account;
  }

  public void setAccount(Account account) {
    this.account = account;
  }

  public String getSessionId() {
    return sessionId;
  }

  public void setSessionId(String sessionId) {
    this.sessionId = sessionId;
  }

  public State getState() {
    return state;
  }

  public void setState(State state) {
    this.state = state;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Seat)) {
      return false;
    }
    Seat seat = (Seat) o;
    return id == seat.id;
  }

  @Override
  public int hashCode() {

    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "Row=" + row
        + ", Seat=" + number;
  }
}
