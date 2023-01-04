package servlet;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = "/item")
public class ItemServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "Kasun2023..");
            PreparedStatement psmt = connection.prepareStatement("select * from Item");
            ResultSet rst = psmt.executeQuery();
            JsonArrayBuilder array = Json.createArrayBuilder();
            resp.addHeader("Access-Control-Allow-Origin","*");

            while (rst.next()) {
                JsonObjectBuilder object = Json.createObjectBuilder();
                object.add("code",rst.getString("ItemCode"));
                object.add("description",rst.getString("Description"));
                object.add("qtyOnHand",rst.getString("QtyOnHand"));
                object.add("unitPrice",rst.getDouble("UnitPrice"));
                array.add(object.build());
            }

            resp.setContentType("application/json");//MIME Types

            JsonObjectBuilder responseObject = Json.createObjectBuilder();
            responseObject.add("state","done");
            responseObject.add("message","Successfully done");
            responseObject.add("data",array.build());
            resp.getWriter().print(responseObject.build());

        } catch (ClassNotFoundException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(500);

        } catch (SQLException e) {

            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("description");
        String qtyOnHand = req.getParameter("qtyOnHand");
        String unitPrice = req.getParameter("unitPrice");
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.setContentType("application/json");//MIME Types
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "Kasun2023..");
            PreparedStatement pstm2 = connection.prepareStatement("insert into Item values(?,?,?,?)");
            pstm2.setObject(1, code);
            pstm2.setObject(2, name);
            pstm2.setObject(3, qtyOnHand);
            pstm2.setObject(4, unitPrice);
            boolean output = pstm2.executeUpdate() > 0;
            if (output) {
                JsonObjectBuilder jsonObject = Json.createObjectBuilder();
                jsonObject.add("state","done");
                jsonObject.add("message","successful");
                resp.getWriter().print(jsonObject.build());
            }

        } catch (ClassNotFoundException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(500);

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
        String id = req.getParameter("code");
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.setContentType("application/json");//MIME Types

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "Kasun2023..");
            PreparedStatement pstm1 = connection.prepareStatement("delete from Item where ItemCode=?");
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
        } catch (ClassNotFoundException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(500);
        } catch (SQLException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.addHeader("Access-Control-Allow-Methods","DELETE,PUT");
        resp.addHeader("Access-Control-Allow-Headers","Content-Type");
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JsonReader reader = Json.createReader(req.getReader());
        JsonObject customer = reader.readObject();
        String code = customer.getString("code");
        String name = customer.getString("description");
        String qtyOnHand = customer.getString("qtyOnHand");
        String unitPrice = customer.getString("unitPrice");
        resp.addHeader("Access-Control-Allow-Origin","*");
        resp.setContentType("application/json");//MIME Types
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/Thogakade", "root", "Kasun2023..");
            PreparedStatement pstm3 = connection.prepareStatement("update Item set Description=?,QtyOnHand=?,UnitPrice=? where ItemCode=?");
            pstm3.setObject(4, code);
            pstm3.setObject(1, name);
            pstm3.setObject(2, qtyOnHand);
            pstm3.setObject(3, unitPrice);
            boolean execute3 = pstm3.executeUpdate() > 0;
            JsonObjectBuilder responseObject = Json.createObjectBuilder();

            if (execute3) {
                responseObject.add("state","done");
                responseObject.add("message","Successfully Updated..!");
            }else{
                responseObject.add("state","Error");
                responseObject.add("message","No Customer For the Given ID..!");
            }
            resp.getWriter().print(responseObject.build());
        } catch (ClassNotFoundException e) {

            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(500);

        } catch (SQLException e) {

            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            jsonObject.add("state","error");
            jsonObject.add("message",e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(400);
        }
    }
}
