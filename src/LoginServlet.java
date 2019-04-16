import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * This class is declared as LoginServlet in web annotation, 
 * which is mapped to the URL pattern /api/login
 */

@WebServlet(name = "/LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private String gRecaptchaResponse;
    

    
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
	
	
private static boolean verifyCredentials(String email, String password) throws Exception {
		
		String loginUser = "mytestuser";
		String loginPasswd = "mypassword";
		String loginUrl = "jdbc:mysql://localhost:3306/moviedb?allowPublicKeyRetrieval=true&useSSL=false";

//		Class.forName("com.mysql.jdbc.Driver").newInstance();
//		Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd);
//		Statement statement = connection.createStatement();
		
        Context initCtx = new InitialContext();

        Context envCtx = (Context) initCtx.lookup("java:comp/env");
        if (envCtx == null)
            System.out.println("envCtx is NULL");

        // Look up our data source
        DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

        if (ds == null)
            System.out.println("ds is null.");

        Connection dbcon = ds.getConnection();
        if (dbcon == null)
            System.out.println("dbcon is null.");

        // Declare our statement
        Statement statement = dbcon.createStatement();

		String query = String.format("SELECT * from customers where email='%s'", email);

		ResultSet rs = statement.executeQuery(query);

		boolean success = false;
		if (rs.next()) {
		    // get the encrypted password from the database
			String encryptedPassword = rs.getString("password");
			
			// use the same encryptor to compare the user input password with encrypted password stored in DB
			success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
		}

		rs.close();
		statement.close();
		dbcon.close();
		
		System.out.println("verify " + email + " - " + password);

		return success;
	}
	
	
	
	
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.username = request.getParameter("username");
        this.password = request.getParameter("password");
        // determine request origin by HTTP Header User Agent string
        String userAgent = request.getHeader("User-Agent");
        System.out.println("recieved login request");
        System.out.println("userAgent: " + userAgent);
        
        PrintWriter out = response.getWriter();
        
//        // Verify reCAPTCHA
//        if (userAgent != null && !userAgent.contains("Android")) {
//	        try {
//	        	this.gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//	            System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);
//	            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
//	        } catch (Exception e) {
//				System.out.println(e);
//				JsonObject responseJsonObject = new JsonObject();
//	            responseJsonObject.addProperty("status", "fail");
//	            responseJsonObject.addProperty("message", "ReCaptcha not completed.");
//	            
//	            // write JSON string to output
//				response.getWriter().write(responseJsonObject.toString());
//	            // set response status to 200 (OK)
//	            response.setStatus(200);
//	
//				//leave, don't try the rest, there is no point.
//	            return;
//	        }
//        }
        
    	try {
			
			JsonObject responseJsonObject = new JsonObject();

			System.out.println("here");

			
			//check if we found an account with provided credentials
			if(verifyCredentials(this.username, this.password)) { //if found
				String sessionId = ((HttpServletRequest) request).getSession().getId();
				System.out.println("SessionId: " + sessionId);
	            Long lastAccessTime = ((HttpServletRequest) request).getSession().getLastAccessedTime();
				System.out.println("lastAccessTime: " + lastAccessTime);

	            request.getSession().setAttribute("user", new User(username));
	            System.out.println("user: " + ((HttpServletRequest) request).getSession().getAttribute("user"));
	
	            responseJsonObject.addProperty("status", "success");
	            responseJsonObject.addProperty("message", "success");
			} else { //no accounts
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

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}

        /**
         * This example only allows username/password to be anteater/123456
         * In real world projects, you should talk to the database to verify username/password
         */
	
    }
}
