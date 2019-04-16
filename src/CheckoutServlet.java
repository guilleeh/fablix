

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.JsonObject;

/**
 * Servlet implementation class CheckoutServlet
 */
@WebServlet(name = "/CheckoutServlet", urlPatterns = "/api/checkout")
public class CheckoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String card;
	private String firstName;
	private String lastName;
	private String expiration;
       
    /**
     * @see HttpServlet#HttpServlet()
     *
     */
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    public CheckoutServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	PrintWriter out = response.getWriter();
		// TODO Auto-generated method stub
    	
		HttpSession session = request.getSession();
		
		System.out.println(card);
		System.out.println(firstName);
		System.out.println(lastName);
		System.out.println(expiration);
		
		try {
			// Get a connection from dataSource
            Context initCtx = new InitialContext();

            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            if (envCtx == null)
                out.println("envCtx is NULL");

            // Look up our data source
            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");

            if (ds == null)
                out.println("ds is null.");

            Connection dbcon = ds.getConnection();
            dbcon.setReadOnly(true);

			// Construct a query with parameter represented by "?"	
			String query = "select count(*) as card, cu.id\n" + 
					"from creditcards c, customers cu\n" + 
					"where c.id = ? and c.firstName = ? and c.lastName = ? and c.expiration = ? and c.id = cu.ccId\n" + 
					"group by cu.id";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, card);
			statement.setString(2, firstName);
			statement.setString(3, lastName);
			String [] arrOfStr = expiration.split("-");
			String year = arrOfStr[0];
			String day= arrOfStr[1];
			String month = arrOfStr[2];
			expiration = String.join("-", year, month, day);
			statement.setString(4, expiration);
			
			System.out.println("HERE!");
			// Perform the query
			ResultSet rs = statement.executeQuery();
			
			JsonObject responseJsonObject = new JsonObject();


			if(!rs.next()) {
				//only returning 1 result
				responseJsonObject.addProperty("status", "fail");
	            responseJsonObject.addProperty("message", "No account with given credentials found.");
			} else {

				String accountMatch = rs.getString("card");
				String id = rs.getString("id");
				
				if(accountMatch != null) { //if found	
		            responseJsonObject.addProperty("status", "success");
		            responseJsonObject.addProperty("message", "success");
		            responseJsonObject.addProperty("id", id);
				} else { //no accounts
		            responseJsonObject.addProperty("status", "fail");
		            responseJsonObject.addProperty("message", "No account with given credentials found.");
				}
			}
			
            // write JSON string to output
			response.getWriter().write(responseJsonObject.toString());
            // set response status to 200 (OK)
            response.setStatus(200);
			rs.close();
			statement.close();
			dbcon.close();

			
		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set reponse status to 500 (Internal Server Error)
			response.setStatus(500);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		this.card = request.getParameter("card");
		this.firstName = request.getParameter("first");
		this.lastName = request.getParameter("second");
		this.expiration = request.getParameter("date");
		doGet(request, response);
	}

}
