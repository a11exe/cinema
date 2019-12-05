package ru.job4j.cinema.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.job4j.cinema.service.PropertiesService;
import ru.job4j.cinema.service.PropertiesServiceImpl;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 29.11.2019
 */
public class PropJsonServlet  extends HttpServlet {

  private final PropertiesService propertiesService = PropertiesServiceImpl.getInstance();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    resp.setContentType("application/json");
    resp.setCharacterEncoding("UTF-8");
    resp.getWriter().write("{\"timeout\":" + propertiesService.getProperties().getProperty("booking.timeout.seconds") + "}");
    resp.getWriter().flush();
  }

}
