package servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/customer")
public class CustomerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            PreparedStatement psmt = connection.prepareStatement("SELECT * FROM Customer");
            ResultSet rst = psmt.executeQuery();
            JsonArrayBuilder array = Json.createArrayBuilder();


            while (rst.next()) {
                JsonObjectBuilder object = Json.createObjectBuilder();
                object.add("id", rst.getString("CusID"));
                object.add("name", rst.getString("CusName"));
                object.add("address", rst.getString("CusAddress"));
                object.add("salary", rst.getDouble("Salary"));
                array.add(object.build());
            }

            resp.setContentType("application/json");//MIME Types

            JsonObjectBuilder responseObject = Json.createObjectBuilder();
            responseObject.add("state", "done");
            responseObject.add("message", "Successfully done");
            responseObject.add("data", array.build());
            resp.getWriter().print(responseObject.build());

        } catch (SQLException e) {

            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state", "error");
            jsonObject.add("message", e.getMessage());
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

        resp.setContentType("application/json");//MIME Types
        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            PreparedStatement pstm2 = connection.prepareStatement("insert into Customer values(?,?,?,?)");
            pstm2.setObject(1, id);
            pstm2.setObject(2, name);
            pstm2.setObject(3, address);
            pstm2.setObject(4, salary);
            boolean output = pstm2.executeUpdate() > 0;
            if (output) {
                JsonObjectBuilder jsonObject = Json.createObjectBuilder();
                jsonObject.add("state", "done");
                jsonObject.add("message", "successful");
                resp.getWriter().print(jsonObject.build());
            }

        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state", "error");
            jsonObject.add("message", e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }


    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String id = req.getParameter("id");

        try (Connection connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()) {
            PreparedStatement pstm1 = connection.prepareStatement("delete from Customer where CusID=?");
            pstm1.setObject(1, id);
            boolean execute = pstm1.executeUpdate() > 0;
            JsonObjectBuilder jObject = Json.createObjectBuilder();
            resp.setContentType("application/json");//MIME Types
            if (execute) {
                jObject.add("state", "done");
                jObject.add("message", "Successfully Deleted..!");
            } else {
                jObject.add("state", "error");
                jObject.add("message", "No such Customer to Delete..!");
                resp.setStatus(400);
            }
            resp.getWriter().print(jObject.build());
        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state", "error");
            jsonObject.add("message", e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }


    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject customer = reader.readObject();
        String id = customer.getString("id");
        String name = customer.getString("name");
        String address = customer.getString("address");
        String salary = customer.getString("salary");

        resp.setContentType("application/json");//MIME Types
        try (Connection connection = ( (BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection()){
            PreparedStatement pstm3 = connection.prepareStatement("update Customer set CusName=?,CusAddress=?,salary=? where CusID=?");
            pstm3.setObject(4, id);
            pstm3.setObject(1, name);
            pstm3.setObject(2, address);
            pstm3.setObject(3, salary);
            boolean execute3 = pstm3.executeUpdate() > 0;
            JsonObjectBuilder responseObject = Json.createObjectBuilder();

            if (execute3) {
                responseObject.add("state", "done");
                responseObject.add("message", "Successfully Updated..!");
            } else {
                responseObject.add("state", "Error");
                responseObject.add("message", "No Customer For the Given ID..!");
            }
            resp.getWriter().print(responseObject.build());
        }  catch (SQLException e) {

            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state", "error");
            jsonObject.add("message", e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }
}
