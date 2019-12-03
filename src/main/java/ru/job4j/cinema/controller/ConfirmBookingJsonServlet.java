package ru.job4j.cinema.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;
import ru.job4j.model.Account;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 22.11.2019
 */
public class ConfirmBookingJsonServlet extends HttpServlet {

  private final CinemaService service = CinemaServiceImpl.getInstance();

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {

    BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()));
    String requestData = reader.lines().collect(Collectors.joining());
    ObjectMapper mapper = new ObjectMapper();
    Account account = mapper.readValue(requestData, Account.class);

    String sessionId = "";
    Cookie cookie = Arrays.stream(req.getCookies())
            .filter(c -> c.getName().equals("sessionId"))
            .findFirst().orElse(null);
    if (cookie != null) {
      sessionId = cookie.getValue();
    }

    String code = service.confirmBooking(sessionId, account);
    if (code != null) {
      resp.setStatus(HttpServletResponse.SC_OK);
      String jsonInString = mapper.writeValueAsString(code);
      resp.setContentType("application/json");
      resp.setCharacterEncoding("UTF-8");
      resp.getWriter().write(jsonInString);
      resp.getWriter().flush();
    } else {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
  }
}
