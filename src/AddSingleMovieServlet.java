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
 * Servlet implementation class AddSingleMovieServlet
 */
@WebServlet(name = "/AddSingleMovieServlet", urlPatterns = "/api/add-single-movie")
public class AddSingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    public AddSingleMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String year = request.getParameter("year");
		String director = request.getParameter("director");
		String genre = request.getParameter("genre");
		String star = request.getParameter("star");
		int g_id = -1;
		String s_id = "";
		String m_id = "";
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			Statement statement = dbcon.createStatement();
			
			//check if movie exists
			String exists = "select count(*) from movies where title = '" + id + "' AND year = " + year + " AND director = '" + director + "';";
			ResultSet e = statement.executeQuery(exists);
			if (e.next() && e.getInt(1) != 0) {//movie already exists
				JsonArray jsonArray = new JsonArray();
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("count", -2);
				jsonArray.add(jsonObject);
	            // write JSON string to output
	            out.write(jsonArray.toString());
	            // set response status to 500 (Internal Server Error)
				response.setStatus(500);
	            e.close();
	            statement.close();
				dbcon.close();
			}
			
			//check if genre exists, create if not
			exists = "select count(*) from genres where name = '" + genre.toLowerCase() + "';";
			e = statement.executeQuery(exists);
			if (e.next() && e.getInt(1) == 0) {//genre does not exist
				String create = "insert into genres (name) values('" + genre + "');";
				statement.execute(create);
				if (statement.getUpdateCount() != 1) { //genre couldn't be created
					JsonArray jsonArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("count", -3);
					jsonArray.add(jsonObject);
					// write JSON string to output
		            out.write(jsonArray.toString());
		            // set response status to 500 (Internal Server Error)
					response.setStatus(500);
		            e.close();
		            statement.close();
					dbcon.close();
				}
			}
			else { //set the appropriate genre id for stored procedure
				exists = "select id from genres where name = '" + genre.toLowerCase() + "';";
				e = statement.executeQuery(exists);
				if (e.next()) {
					g_id = e.getInt(1);
				}
				else {
					JsonArray jsonArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("count", -3);
					jsonArray.add(jsonObject);
					// write JSON string to output
		            out.write(jsonArray.toString());
		            // set response status to 500 (Internal Server Error)
					response.setStatus(500);
		            e.close();
		            statement.close();
					dbcon.close();
				}
			}
			
			
			//check if star exists
			exists = "select count(*) from stars where name = '" + star.toLowerCase() + "';";
			e = statement.executeQuery(exists);
			if (e.next() && e.getInt(1) == 0) {//star does not exist
				int i = 9999999;
				while (i > 0) {
					String n_id = "select count(*) from stars where id = 'nm" + i + "';";
					ResultSet s = statement.executeQuery(n_id);
					if (s.next() && s.getInt(1) == 0)
						break;
					--i;
				}
				String create = "insert into stars (id, name) values('nm" + i + "','" + star + "');";
				statement.execute(create);
				if (statement.getUpdateCount() != 1) { //star couldn't be created
					JsonArray jsonArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("count", -3);
					jsonArray.add(jsonObject);
					// write JSON string to output
		            out.write(jsonArray.toString());
		            // set response status to 500 (Internal Server Error)
					response.setStatus(500);
		            e.close();
		            statement.close();
					dbcon.close();
				}
			}
			else { //set the appropriate star id for stored procedure
				exists = "select id from stars where name = '" + star.toLowerCase() + "';";
				e = statement.executeQuery(exists);
				if (e.next()) {
					s_id = e.getString(1);
				}
				else {
					JsonArray jsonArray = new JsonArray();
					JsonObject jsonObject = new JsonObject();
					jsonObject.addProperty("count", -3);
					jsonArray.add(jsonObject);
					// write JSON string to output
		            out.write(jsonArray.toString());
		            // set response status to 500 (Internal Server Error)
					response.setStatus(500);
		            e.close();
		            statement.close();
					dbcon.close();
				}
			}
			
			m_id = "select id from movies where name = '" + name + "';";
			e = statement.executeQuery(m_id);
			if (e.next()) {
				m_id = e.getString(1);
			}
			else {
				JsonArray jsonArray = new JsonArray();
				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("count", -3);
				jsonArray.add(jsonObject);
				// write JSON string to output
	            out.write(jsonArray.toString());
	            // set response status to 500 (Internal Server Error)
				response.setStatus(500);
	            e.close();
	            statement.close();
				dbcon.close();
			}
			
			//do call
			String call = "call add_movie('" + m_id + "', '" + name + "', " + year + ", '" + director + "', " + g_id + ", '" + s_id + "');";
			//need to finish.......
			
			
		}
		catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
			response.setStatus(500);
		}
// Perform the query
//			int rc = -1;
//			statement.execute(query);
//			rc = statement.getUpdateCount();			
//			
//			JsonArray jsonArray = new JsonArray();
//			JsonObject jsonObject = new JsonObject();
//			jsonObject.addProperty("count", rc);
//			jsonArray.add(jsonObject);
//			
//            // write JSON string to output
//            out.write(jsonArray.toString());
//            // set response status to 200 (OK)
//            response.setStatus(200);
//
//			statement.close();
//			dbcon.close();
//
//		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
}
