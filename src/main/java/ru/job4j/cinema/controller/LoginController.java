/**
 * @author Alexander Abramov (alllexe@mail.ru)
 * @version 1
 * @since 12.10.2019
 */
package ru.job4j.cinema.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ru.job4j.cinema.service.CinemaService;
import ru.job4j.cinema.service.CinemaServiceImpl;

public class LoginController extends HttpServlet {

    private final CinemaService logic = CinemaServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("userNotFound", false);
        req.setAttribute("passwordIncorrect", false);
        req.getRequestDispatcher("WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String password = req.getParameter("password");

        if (password.equals(logic.getProperties().getProperty("admin.pass"))) {
            HttpSession session = req.getSession();
            session.setAttribute("loggedUser", "admin");
            resp.sendRedirect("/");
        } else {
            req.setAttribute("passwordIncorrect", "");
            req.getRequestDispatcher("WEB-INF/views/login.jsp").forward(req, resp);
        }
    }
}
