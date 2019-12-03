package ru.job4j.cinema.controller.admin;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 02.12.2019
 */
public class AdminServlet extends HttpServlet {

  private final CinemaService logic = CinemaServiceImpl.getInstance();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    req.getRequestDispatcher("WEB-INF/views/admin.jsp").forward(req, resp);

  }
}
