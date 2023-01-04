/**
 * created by kasun weerasinghe
 * Date: 2023-01-04
 * Time: 10:28
 * Project Name: POS-Back-End
 */

package servlet;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = ( (BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){
            PreparedStatement psmt = connection.prepareStatement("select * from Customer");
            ResultSet rst = psmt.executeQuery();
            JsonArrayBuilder array = Json.createArrayBuilder();
            while (rst.next()) {
                JsonObjectBuilder object = Json.createObjectBuilder();
                object.add("id",rst.getString("id"));
                object.add("name",rst.getString("name"));
                object.add("address",rst.getString("address"));
                object.add("salary",rst.getDouble("salary"));
                array.add(object.build());
            }

            JsonObjectBuilder responseObject = Json.createObjectBuilder();
            responseObject.add("state","done");
            responseObject.add("message","Successfully done");
            responseObject.add("data",array.build());
            resp.getWriter().print(responseObject.build());

        }catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String salary = req.getParameter("salary");
        try (Connection connection = ( (BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){
            PreparedStatement pstm2 = connection.prepareStatement("insert into Customer values(?,?,?,?)");
            pstm2.setObject(1, id);
            pstm2.setObject(2, name);
            pstm2.setObject(3, address);
            pstm2.setObject(4, salary);
            boolean output = pstm2.executeUpdate() > 0;
            if (output) {
                JsonObjectBuilder jsonObject = Json.createObjectBuilder();
                jsonObject.add("state","done");
                jsonObject.add("message","successful");
                resp.getWriter().print(jsonObject.build());
            }

        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        try (Connection connection = ( (BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){
            PreparedStatement pstm1 = connection.prepareStatement("delete from Customer where id=?");
            pstm1.setObject(1, id);
            boolean execute = pstm1.executeUpdate() > 0;
            JsonObjectBuilder jObject = Json.createObjectBuilder();
            if (execute) {
                jObject.add("state","done");
                jObject.add("message","Successfully Deleted..!");
            }else{
                jObject.add("state","error");
                jObject.add("message","No such Customer to Delete..!");
                resp.setStatus(400);
            }
            resp.getWriter().print(jObject.build());
        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }

}
