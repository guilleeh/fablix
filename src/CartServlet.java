

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
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
import java.sql.PreparedStatement;

/**
 * Servlet implementation class CartServlet
 */
@WebServlet(name="/CartServlet", urlPatterns="/api/cart")
public class CartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    
    public CartServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Output stream to STDOUT
		response.setContentType("application/json");
		
		HttpSession session = request.getSession();
		
		
        String id = request.getParameter("movieId");
        String title = request.getParameter("movieTitle");
        String updateAmount = request.getParameter("amount");
        PrintWriter out = response.getWriter();
        ResultSet rs;
        //ONLY Trigger to reset values!!
//        session.invalidate();

        // get the previous items in a ArrayList
	    ArrayList<Tuple> previousItems = (ArrayList<Tuple>)session.getAttribute("previousItems");
	    System.out.println(id);
	    if(id != null) {
	        if (previousItems == null) {
	            previousItems = new ArrayList<Tuple>();
	            previousItems.add(new Tuple(id, 1, title));
	            session.setAttribute("previousItems", previousItems);
	        } else {
	            // prevent corrupted states through sharing under multi-threads
	            // will only be executed by one thread at a time
	            synchronized (previousItems) {
	            	if(updateAmount == null) {
		            	boolean found = false;
		            	for(int i = 0; i < previousItems.size(); i++) {
		            		Tuple movieTuple = previousItems.get(i);
		            		if(movieTuple.getKey().equals(id)) {
		            			movieTuple.setValue(movieTuple.getValue()+1);
		            			found = true;
		            		}
		            	}
		            	if(!found) {
		            		previousItems.add(new Tuple(id, 1, title));
		            	}
	            	} else { //we need to update the amount with what was given
	            		System.out.println("Update value");
	            		for(int i = 0; i < previousItems.size(); i++) {
		            		Tuple movieTuple = previousItems.get(i);
		            		System.out.println(id);
		            		System.out.println("==");
		            		System.out.println(movieTuple.getKey());
		            		if(movieTuple.getKey().equals(id)) {
		            			if(Integer.parseInt(updateAmount) > 0) {
		            				movieTuple.setValue(Integer.parseInt(updateAmount));
		            			} else {
		            				previousItems.remove(i); //The input was 0, so we remove the movie.
		            			}
		            		}
		            	}
	            		
	            		if(previousItems.size() == 0) { //NO MORE Items, get rid of data structure
	            			System.out.println("EMPTy");
	            			previousItems = null;
	            			session.setAttribute("previousItems", previousItems);
	            		}
	            	}
	            }
	        }
        }
        JsonArray jsonArray = new JsonArray();
        
        if(previousItems != null) {
	        for( int i = 0; i < previousItems.size(); i++ ) {
	        	Tuple movieTuple = previousItems.get(i);
	        	JsonObject jsonObject = new JsonObject();
	            jsonObject.addProperty("movieId", movieTuple.getKey());
	            jsonObject.addProperty("movieTitle", movieTuple.getTitle());
	            jsonObject.addProperty("amount", movieTuple.getValue());
	            jsonArray.add(jsonObject);
	        }
	        System.out.println(jsonArray);
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
}
