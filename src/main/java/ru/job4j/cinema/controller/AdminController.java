package ru.job4j.cinema.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;
import ru.job4j.model.Hall;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 02.12.2019
 */
public class AdminController  extends HttpServlet {

  private final CinemaService logic = CinemaServiceImpl.getInstance();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    Hall hall = logic.getBooked();
    req.setAttribute("seats", hall.getSeats());
    req.getRequestDispatcher("WEB-INF/views/booked.jsp").forward(req, resp);

  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    super.doPost(req, resp);
  }
}
