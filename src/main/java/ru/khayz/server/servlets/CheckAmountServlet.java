package ru.khayz.server.servlets;

import ru.khayz.db.DbException;
import ru.khayz.db.DbService;
import ru.khayz.server.Application;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CheckAmountServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        long id;
        long amount;
        try {
            id = Long.valueOf(req.getParameter("id"));
            amount = Long.valueOf(req.getParameter("amount"));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        DbService db = Application.getDbService();
        String result = null;
        try {
            result = String.format("<Response>%s</Response>", String.valueOf(db.checkAmount(id, amount)));
        } catch (DbException e) {
            e.printStackTrace();
        }
        resp.getWriter().write(result);
        resp.getWriter().flush();
        resp.getWriter().close();
    }
}
