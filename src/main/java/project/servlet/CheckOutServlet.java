package project.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import project.connection.DbCon;
import project.dao.OrderDao;
import project.model.*;

@WebServlet("/cart-check-out")
public class CheckOutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try(PrintWriter out = response.getWriter()){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Date date = new Date();
            ArrayList<Cart> cart_list = (ArrayList<Cart>) request.getSession().getAttribute("cart-list");
            User auth = (User) request.getSession().getAttribute("auth");

            if(auth == null) {
                response.sendRedirect("login.jsp");
                return;
            }

            if(cart_list != null && !cart_list.isEmpty()) {
                OrderDao oDao = new OrderDao(DbCon.getConnection());
                boolean allInserted = true;
                for(Cart c: cart_list) {
                    Order order = new Order();
                    order.setId(c.getId());
                    order.setUid(auth.getId());
                    order.setQuantity(c.getQuantity());
                    order.setDate(formatter.format(date));

                    boolean result = oDao.insertOrder(order);
                    if(!result) {
                        allInserted = false;
                        break;
                    }
                }
                if (allInserted) {
                    cart_list.clear();
                    response.sendRedirect("orders.jsp");
                } else {
                    response.sendRedirect("cart.jsp");
                }
            } else {
                response.sendRedirect("cart.jsp");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal error occurred");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
