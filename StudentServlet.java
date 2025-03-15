import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;

public class StudentServlet extends HttpServlet {
    private Connection conn;

    @Override
    public void init() throws ServletException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/sms_db", "root", "password");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String course = request.getParameter("course");

            try {
                String query = "INSERT INTO students (name, email, course) VALUES (?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, course);
                stmt.executeUpdate();
                response.sendRedirect("index.jsp");
            } catch (SQLException e) {
                e.printStackTrace();
                response.getWriter().write("Error adding student");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");

        if ("list".equals(action)) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM students");

                PrintWriter out = response.getWriter();
                out.print("<table>");
                out.print("<thead><tr><th>ID</th><th>Name</th><th>Email</th><th>Course</th><th>Actions</th></tr></thead>");
                out.print("<tbody>");
                
                while (rs.next()) {
                    out.print("<tr>");
                    out.print("<td>" + rs.getInt("id") + "</td>");
                    out.print("<td>" + rs.getString("name") + "</td>");
                    out.print("<td>" + rs.getString("email") + "</td>");
                    out.print("<td>" + rs.getString("course") + "</td>");
                    out.print("<td><button class='edit-btn' onclick='editStudent(" + rs.getInt("id") + ")'>Edit</button>");
                    out.print("<button class='delete-btn' onclick='deleteStudent(" + rs.getInt("id") + ")'>Delete</button></td>");
                    out.print("</tr>");
                }
                out.print("</tbody></table>");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}