/**
 * created by kasun weerasinghe
 * Date: 2023-01-06
 * Time: 09:58
 * Project Name: POS-Back-End
 */

package servlet;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.json.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@WebServlet(urlPatterns = "/purchase")
public class PurchaseOrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Connection connection = null;
        try {
            connection = ((BasicDataSource) getServletContext().getAttribute("dbcp")).getConnection();
            connection.setAutoCommit(false);

            JsonReader reader = Json.createReader(req.getReader());
            JsonObject requestjob = reader.readObject();
            String oid = requestjob.getString("oid");
            String date = requestjob.getString("date");
            String cusID = requestjob.getString("cusID");

            PreparedStatement psmt = connection.prepareStatement("Insert into `Order` values(?,?,?)");
            psmt.setObject(1, oid);
            psmt.setObject(2, date);
            psmt.setObject(3, cusID);
            if (!(psmt.executeUpdate() > 0)) {
                connection.rollback();
                throw new RuntimeException("Can't Save The Order");
            } else {
                JsonArray orderDetails = requestjob.getJsonArray("orderDetails");
                for (JsonValue od : orderDetails) {
                    String code = od.asJsonObject().getString("code");
                    String qty = od.asJsonObject().getString("qty");
                    String price = od.asJsonObject().getString("price");

                    PreparedStatement pstm2 = connection.prepareStatement("Insert into OrderDetails values(?,?,?,?)");
                    pstm2.setObject(1, oid);
                    pstm2.setObject(2, code);
                    pstm2.setObject(3, qty);
                    pstm2.setObject(4, price);

                    if (!(pstm2.executeUpdate() > 0)) {
                        connection.rollback();
                        throw new RuntimeException("There is a Problem With Order Details.");
                    }
                }
                connection.commit();
                connection.setAutoCommit(true);
                connection.close();

                JsonObjectBuilder responseObject = Json.createObjectBuilder();
                responseObject.add("state", "done");
                responseObject.add("message", "Successfully Purchased");
                responseObject.add("data", "");
                resp.getWriter().print(responseObject.build());

            }

        } catch (SQLException | RuntimeException e) {
            JsonObjectBuilder jsonObject = Json.createObjectBuilder();
            try {
                connection.rollback();
                connection.setAutoCommit(true);
                connection.close();
            } catch (SQLException ex) {
                jsonObject.add("state", "error");
                jsonObject.add("message", e.getMessage());
            }
            jsonObject.add("state", "error");
            jsonObject.add("message", e.getMessage());
            resp.getWriter().print(jsonObject.build());
            resp.setStatus(500);
        }


    }

}
