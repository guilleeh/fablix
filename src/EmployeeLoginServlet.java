import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * This class is declared as EmployeeLoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/employee_login
 */
@WebServlet(name = "/EmployeeLoginServlet", urlPatterns = "/api/employee_login")
public class EmployeeLoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    

    
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */

private static boolean verifyCredentials(String email, String password) throws Exception {
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb";

		Class.forName("com.mysql.jdbc.Driver").newInstance();
		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
		Statement statement = connection.createStatement();

		String query = String.format("SELECT * from employees where email='%s'", email);

		ResultSet rs = statement.executeQuery(query);

		boolean success = false;
		if (rs.next()) {
		    // get the encrypted password from the database
			String encryptedPassword = rs.getString("password");
			
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
			System.out.println(success);
		}

		rs.close();
		statement.close();
		connection.close();
		
		System.out.println("verify " + email + " - " + password);

		return success;
	}
	
	
	
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.username = request.getParameter("username");
        this.password = request.getParameter("password");
        
        PrintWriter out = response.getWriter();
                
    	try {
			
			JsonObject responseJsonObject = new JsonObject();

			
			//check if we found an account with provided credentials
			if(verifyCredentials(this.username, this.password)) { //if found
				String sessionId = ((HttpServletRequest) request).getSession().getId();
	            Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
	            request.getSession().setAttribute("employee", new Employee(username));
	            request.getSession().setAttribute("user", new Employee(username));
	
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
	            System.out.println("employee found");
			} else { //no accounts
				System.out.println("employee not found");
	            responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "Username or password is incorrect");
			}

			// Create a JsonObject based on the data we retrieve from rs

			System.out.println(responseJsonObject.toString());
			
            // write JSON string to output
			response.getWriter().write(responseJsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
		} catch (Exception e) {
			// write error message JSON object to output
			System.out.println(e);
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	
    }
}
