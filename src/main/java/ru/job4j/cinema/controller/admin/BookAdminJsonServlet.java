package ru.job4j.cinema.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;
import ru.job4j.model.Hall;
import ru.job4j.model.Seat;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 02.12.2019
 */
public class BookAdminJsonServlet extends HttpServlet {

  private final CinemaService service = CinemaServiceImpl.getInstance();

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    resp.setContentType("text/json");

    Hall hall = service.getBooked();
    ObjectMapper mapper = new ObjectMapper();
    String jsonInString = mapper.writeValueAsString(hall);
    resp.getWriter().write(jsonInString);
    resp.getWriter().flush();

  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
    String requestData = reader.lines().collect(Collectors.joining());
    ObjectMapper mapper = new ObjectMapper();
    Seat seat = mapper.readValue(requestData, Seat.class);

    if (service.cancelBooked(seat)) {
      resp.setStatus(HttpServletResponse.SC_OK);
    } else {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
