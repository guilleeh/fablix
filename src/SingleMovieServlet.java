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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SingleMovieServlet
 */
//Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "/SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	@Resource(name = "jdbc/moviedb")
	private DataSource dataSource;
	
    public SingleMovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// Retrieve parameter id from url request.
		String id = request.getParameter("id");
		
		HttpSession session = request.getSession();

		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();

			// Construct a query with parameter represented by "?"	
			String query = "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
					"					from movies m, stars s, stars_in_movies sm\n" + 
					"					where m.id = ? and m.id = sm.movieId and s.id = sm.starId\n" + 
					"					group by m.id, m.title, m.year, m.director) t1 \n" + 
					"					inner join\n" + 
					"					(select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
					"					from movies m, genres g, genres_in_movies gm\n" + 
					"					where m.id = gm.movieId and g.id = gm.genreId\n" + 
					"					group by m.id) as t2\n" + 
					"					on t1.id = t2.movieId)\n" + 
					"					inner join\n" + 
					"					(select m.id, r.rating from movies m, ratings r where m.id = r.movieId order by r.rating) as t3\n" + 
					"					on t1.id = t3.id\n" + 
					"					order by t3.rating desc";

			// Declare our statement
			PreparedStatement statement = dbcon.prepareStatement(query);

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query
			statement.setString(1, id);

			// Perform the query
			ResultSet rs = statement.executeQuery();
			
			JsonArray jsonArray = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {
				String movieId = rs.getString("id");
				String movieTitle = rs.getString("title");
				String movieYear = rs.getString("year");
				String movieDirector = rs.getString("director");
				String movieGenre = rs.getString("genre");
				String movieStar = rs.getString("star");
				String movieRating = rs.getString("rating");

				// Create a JsonObject based on the data we retrieve from rs

				JsonObject jsonObject = new JsonObject();
				jsonObject.addProperty("movie_id", movieId);
				jsonObject.addProperty("movie_title", movieTitle);
				jsonObject.addProperty("movie_year", movieYear);
				jsonObject.addProperty("movie_director", movieDirector);
				jsonObject.addProperty("movie_genre", movieGenre);
				jsonObject.addProperty("movie_star", movieStar);
				jsonObject.addProperty("movie_rating", movieRating);

				jsonArray.add(jsonObject);
			}
			
            // write JSON string to output
            out.write(jsonArray.toString());
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
		doGet(request, response);
	}

}
