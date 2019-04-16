import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddSingleStarServlet
 */
@WebServlet(name = "/AddSingleStarServlet", urlPatterns = "/api/add-single-star")
public class AddSingleStarServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    public AddSingleStarServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		
		String star = request.getParameter("star");
		String year = request.getParameter("year");
		
		
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			//find a unique ID
			int i = 9999999;
			while (i > 0) {
				String n_id = "select count(*) from stars where id = 'nm" + i + "';";
				ResultSet s = statement.executeQuery(n_id);
				if (s.next() && s.getInt(1) == 0)
					break;
				--i;
			}
			
			String id = "nm" + Integer.toString(i);
			String query;
			if (year == null)
				query = "insert into stars (id, name) values('" + id + "','" + star + "');";
			else
				query = "insert into stars (id, name, birthYear) values('" + id + "','" + star + "'," + year + ");";

			// Perform the query
			int rc = -1;
			statement.execute(query);
			rc = statement.getUpdateCount();			
			
			JsonArray jsonArray = new JsonArray();
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("count", rc);
			jsonArray.add(jsonObject);
			
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

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
		doGet(request, response);
	}

}