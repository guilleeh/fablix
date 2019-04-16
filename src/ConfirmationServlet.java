

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class ConfirmationServlet
 */
@WebServlet(name = "/ConfirmationServlet", urlPatterns = "/api/confirmation")
public class ConfirmationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    public ConfirmationServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("application/json");
    	PrintWriter out = response.getWriter();
		
		HttpSession session = request.getSession();
		
		ArrayList<Tuple> previousItems = (ArrayList<Tuple>)session.getAttribute("previousItems");
		
		String custId = request.getParameter("id");
		System.out.println("ID: " + custId);
        JsonArray jsonArray = new JsonArray();
        
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int day = now.getDayOfMonth();
        String date = Integer.toString(year) + "-" + Integer.toString(month) + "-" + Integer.toString(day);
        Connection dbcon = null;
        try {
	        Context initCtx = new InitialContext();
	
	        Context envCtx = (Context) initCtx.lookup("java:comp/env");
	        if (envCtx == null)
	            out.println("envCtx is NULL");
	
	        // Look up our data source
	        DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
	
	        if (ds == null)
	            out.println("ds is null.");
	
	        dbcon = ds.getConnection();
	        dbcon.setReadOnly(true);
        } catch(Exception e) {
        	
        }
        
        if(previousItems != null) {
	        for( int i = 0; i < previousItems.size(); i++ ) {
	        	Tuple movieTuple = previousItems.get(i);
	        	JsonObject jsonObject = new JsonObject();
	
	            jsonObject.addProperty("movieId", movieTuple.getKey());
	            jsonObject.addProperty("amount", movieTuple.getValue());
	            jsonObject.addProperty("title", movieTuple.getTitle());
	            jsonArray.add(jsonObject);
		        try {
		            	
//			        Connection dbcon = dataSource.getConnection();
			        String query = "insert into sales(customerId, movieId, saleData)\n" + 
			        		"values (?, ?, ?);";
			        PreparedStatement statement = dbcon.prepareStatement(query);
			        statement.setString(1, custId);
			        statement.setString(2, movieTuple.getKey());
			        statement.setString(3, date);
			        System.out.println(date);
		            for( int count = 0; count < movieTuple.getValue(); count++) { //execute for N 
		            	statement.executeUpdate(); //Execute query, we don't need the results
		            }
			            
		        } catch (Exception e) {
		        	System.out.println(e);
		        }
	            //erase the movies we had in the cart
	            session.setAttribute("previousItems", null);
	            
	        }
        } else {
        	System.out.println("We should be here now...");
        	JsonObject jsonObject = new JsonObject();
        	jsonObject.addProperty("noMovies", "noMovies");
        	jsonArray.add(jsonObject);
        }
     // write JSON string to output
        response.getWriter().write(jsonArray.toString());
        // set response status to 200 (OK)
        response.setStatus(200);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
