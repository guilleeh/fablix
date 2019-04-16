import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mysql.jdbc.ReplicationDriver;

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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class MovieServlet
 */
@WebServlet(name="/MovieServlet", urlPatterns="/api/movies")
public class MovieServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	// Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MovieServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    //outputs correct query for movies
    private String setQuery(String sort) {
    	return "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
				"from movies m, stars s, stars_in_movies sm \n" + 
				"where m.id = sm.movieId and s.id = sm.starId and m.title like ? and m.year like ? and m.director like ? and s.name like ?\n" + 
				"group by m.id, m.title, m.year, m.director) t1 \n" + 
				"inner join \n" + 
				"(select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
				"from movies m, genres g, genres_in_movies gm\n" + 
				"where m.id = gm.movieId and g.id = gm.genreId \n" + 
				"group by m.id) as t2\n" + 
				"on t1.id = t2.movieId)\n" + 
				"\n" + 
				"inner join\n" + 
				"\n" + 
				"(select m.id, r.rating from movies m, ratings r where m.id = r.movieId ) as t3\n" + 
				"\n" + 
				"on t1.id = t3.id\n " + sort + " Limit ?,? ";
    }
    
    private String editString(String str) {
    	StringBuilder newStr = new StringBuilder();
    	String [] arrOfStr = str.split(" ");
    	for(String temp : arrOfStr) {
    		newStr.append("+" + temp + "* ");
    	}
    	return newStr.toString();
    }
    
	private static JsonObject generateJsonObject(String movieID, String movieTitle) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("value", movieTitle);
		
		JsonObject additionalDataJsonObject = new JsonObject();
		additionalDataJsonObject.addProperty("movieID", movieID);
		
		jsonObject.add("data", additionalDataJsonObject);
		return jsonObject;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
	     // Time an event in a program to nanosecond precision
        long startTimeTS = System.nanoTime();
        long startTimeTJ = 0;
        long endTimeTJ = 0;
		response.setContentType("application/json"); // Response mime type
		HttpSession session = request.getSession();

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        
        
        try {
        	//Get connection from dataSource
//        	Connection dbcon = dataSource.getConnection();
        	ReplicationDriver driver = new ReplicationDriver();

            Properties props = new Properties();
//
//            // We want this for failover on the slaves
//            props.put("autoReconnect", "true");
//
//
            props.put("user", "mytestuser");
            props.put("password", "mypassword");
//
//            //
//            // Looks like a normal MySQL JDBC url, with a
//            // comma-separated list of hosts, the first
//            // being the 'master', the rest being any number
//            // of slaves that the driver will load balance against
//            //
//
            Connection dbcon =
                driver.connect("jdbc:mysql:replication://172.31.33.96:3306,172.31.32.77:3306/moviedb?autoReconnect=true&amp;useSSL=false&amp;allowPublicKeyRetrieval=true&amp;cachePrepStmts=true",
                    props);
//
//            
////        	System.out.println(dbcon.toString());
////        	
//            Context initCtx = new InitialContext();
//
//            Context envCtx = (Context) initCtx.lookup("java:comp/env");
//            if (envCtx == null)
//                out.println("envCtx is NULL");
//
//            // Look up our data source
//            DataSource ds = (DataSource) envCtx.lookup("jdbc/moviedb");
//
//            if (ds == null)
//                out.println("ds is null.");
//
//            Connection dbcon = ds.getConnection();
//            dbcon.setReadOnly(true);
////            if (dbcon == null)
////                out.println("dbcon is null.");
        	
        	
        	//Declare our statement
//        	Statement statement = dbcon.createStatement();
        	String title = request.getParameter("title");
        	String year = request.getParameter("year"); 
        	String director = request.getParameter("director"); 
        	String star = request.getParameter("star"); 
        	String genre = request.getParameter("genre");
        	String browse_genre = request.getParameter("browse_genre");
        	String page_num = request.getParameter("page");
        	String results = request.getParameter("results");
        	String sort_by = request.getParameter("sort_by");
        	String browse_title = request.getParameter("browse_title");
        	String search_bar_title = request.getParameter("search_bar_title");
        	String normal_search_bar_title = request.getParameter("normal_search_bar_title");
        	String query;
        	//get number of items per page
        	int page = Integer.parseInt(page_num);
        	int itemsPerPage = Integer.parseInt(results);
        	int offset = (page - 1)  * itemsPerPage;
        	
        	//decide which sorting to use
        	String sorting_query_str = "";
        	if(sort_by.contentEquals("rating_desc")) {
        		sorting_query_str = "order by t3.rating desc ";
        	} else if(sort_by.contentEquals("rating_asc")) {
        		sorting_query_str = "order by t3.rating asc ";
        	} else if(sort_by.contentEquals("title_asc")) {
        		sorting_query_str = "order by t1.title asc ";
        	} else {
        		sorting_query_str = "order by t1.title desc ";
        	}
        	
        	
        	//prepare for queries
        	
			PreparedStatement statement = null;
        	if(title != null || year != null || director != null || star != null) { //Here we process the search content
        		query = setQuery(sorting_query_str);
        		
        		statement = dbcon.prepareStatement(query);

    			statement.setString(1, "%" + title + "%");
    			statement.setString(2, "%" + year + "%");
    			statement.setString(3, "%" + director + "%");
    			statement.setString(4, "%" + star + "%");
    			statement.setInt(5, offset);
    			statement.setInt(6, itemsPerPage);
        	} else if (genre != null) {
	        	query = "select name\n" + 
	        			"from genres\n" + 
	        			"where name IS NOT NULL and name like ?;";
	        	statement = dbcon.prepareStatement(query);
	        	statement.setString(1, "%%");
        	
        	} else if (browse_genre != null) {
        		query = "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
        				"from movies m, stars s, stars_in_movies sm \n" + 
        				"where m.id = sm.movieId and s.id = sm.starId and m.title like '%%' and m.year like '%%' and m.director like '%%' and s.name like '%%'\n" + 
        				"group by m.id, m.title, m.year, m.director) t1 \n" + 
        				"inner join \n" + 
        				"(select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
        				"from movies m, genres g, genres_in_movies gm\n" + 
        				"where m.id = gm.movieId and g.id = gm.genreId and g.name = ?\n" + 
        				"group by m.id) as t2\n" + 
        				"on t1.id = t2.movieId)\n" + 
        				"\n" + 
        				"inner join\n" + 
        				"\n" + 
        				"(select m.id, r.rating from movies m, ratings r where m.id = r.movieId and r.rating ) as t3\n" + 
        				"\n" + 
        				"on t1.id = t3.id \n" + 
        				sorting_query_str + "LIMIT " + Integer.toString(itemsPerPage) + " offset " + Integer.toString(offset);
        		statement = dbcon.prepareStatement(query);
        		statement.setString(1, browse_genre);
        	} else if(browse_title != null) {
        		query = "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
        				"from movies m, stars s, stars_in_movies sm \n" + 
        				"where m.id = sm.movieId and s.id = sm.starId and m.title like ? and m.year like '%%' and m.director like '%%' and s.name like '%%'\n" + 
        				"group by m.id, m.title, m.year, m.director) t1 \n" + 
        				"inner join \n" + 
        				"(select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
        				"from movies m, genres g, genres_in_movies gm\n" + 
        				"where m.id = gm.movieId and g.id = gm.genreId \n" + 
        				"group by m.id) as t2\n" + 
        				"on t1.id = t2.movieId)\n" + 
        				"\n" + 
        				"inner join\n" + 
        				"\n" + 
        				"(select m.id, r.rating from movies m, ratings r where m.id = r.movieId ) as t3\n" + 
        				"\n" + 
        				"on t1.id = t3.id\n" + 
        				sorting_query_str + "limit " + Integer.toString(itemsPerPage) + " offset " + Integer.toString(offset);
        		
        		statement = dbcon.prepareStatement(query);
      			statement.setString(1, browse_title + "%");
        	} else if( search_bar_title != null ) { 
        		query = "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
        				"from (SELECT * FROM movies WHERE MATCH(title)\n" + 
        				"AGAINST(? IN BOOLEAN MODE) LIMIT 10 ) as m, stars s, stars_in_movies sm \n" + 
        				"where  m.id = sm.movieId and s.id = sm.starId \n" + 
        				"group by m.id, m.title, m.year, m.director) t1\n" + 
        				"inner join \n" + 
        				"(select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
        				"from movies m, genres g, genres_in_movies gm\n" + 
        				"where m.id = gm.movieId and g.id = gm.genreId \n" + 
        				"group by m.id) as t2\n" + 
        				"on t1.id = t2.movieId)\n" + 
        				"\n" + 
        				"inner join\n" + 
        				"\n" + 
        				"(select m.id, r.rating from movies m, ratings r where m.id = r.movieId and r.rating ) as t3\n" + 
        				"\n" + 
        				"on t1.id = t3.id";
//        				sorting_query_str + "limit " + Integer.toString(itemsPerPage) + " offset " + Integer.toString(offset);
        		statement = dbcon.prepareStatement(query);
        		String fullTextParams = editString(search_bar_title);
        		statement.setString(1, fullTextParams);
        	} else if(normal_search_bar_title != null) { 
        		query = "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
        				"from (SELECT * FROM (select * from movies where id is not null\n" + 
        				"and title is not null and year is not null and director is not null) movies WHERE MATCH(title)\n" + 
        				"AGAINST(? IN BOOLEAN MODE)) as m, stars s, stars_in_movies sm \n" + 
        				"where  m.id = sm.movieId and s.id = sm.starId \n" + 
        				"group by m.id, m.title, m.year, m.director) t1\n" + 
        				"inner join \n" + 
        				"(select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
        				"from movies m, genres g, genres_in_movies gm\n" + 
        				"where m.id = gm.movieId and g.id = gm.genreId \n" + 
        				"group by m.id) as t2\n" + 
        				"on t1.id = t2.movieId)\n" + 
        				"\n" + 
        				"inner join\n" + 
        				"\n" + 
        				"(select m.id, r.rating from movies m, ratings r where m.id = r.movieId and r.rating ) as t3\n" + 
        				"\n" + 
        				"on t1.id = t3.id\n" +
        				sorting_query_str + "limit " + Integer.toString(itemsPerPage) + " offset " + Integer.toString(offset);
        		statement = dbcon.prepareStatement(query);
        		String fullTextParams = editString(normal_search_bar_title);
        		statement.setString(1, fullTextParams);
        	} else {
        		query = "select t1.id, t1.title, t1.year, t1.director, t2.genre, t1.star, t3.rating from ((select m.*, group_concat(s.name separator ', ') as star\n" + 
            			"from movies m, stars s, stars_in_movies sm\n" + 
            			"where m.id = sm.movieId and s.id = sm.starId\n" + 
            			"group by m.id, m.title, ?, m.director) t1\n" + 
            			"\n" + 
            			"inner join\n" + 
            			"\n" + 
            			" (select gm.movieId , group_concat(g.name separator ', ') as genre\n" + 
            			"from movies m, genres g, genres_in_movies gm\n" + 
            			"where m.id = gm.movieId and g.id = gm.genreId\n" + 
            			"group by m.id) as t2\n" + 
            			"\n" + 
            			"on t1.id = t2.movieId)\n" + 
            			"\n" + 
            			"inner join\n" + 
            			"\n" + 
            			"(select m.id, r.rating from movies m, ratings r where m.id = r.movieId) as t3\n" + 
            			"\n" + 
            			"on t1.id = t3.id\n" + 
            			sorting_query_str + "limit " + Integer.toString(itemsPerPage) + " offset " + Integer.toString(offset);
        		statement = dbcon.prepareStatement(query);
        		statement.setString(1, "m.year");
        	}
			// Declare our statement

			// Set the parameter represented by "?" in the query to the id we get from url,
			// num 1 indicates the first "?" in the query

			// Perform the query
        	startTimeTJ = System.nanoTime();
			ResultSet rs = statement.executeQuery();
			endTimeTJ = System.nanoTime();
        	JsonArray jsonArray = new JsonArray();
        	
        	//Go through results
        	
        	if(genre != null) {
        		while (rs.next()) {
        			String movie_genre = rs.getString("name");
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_genre", movie_genre);
                    jsonArray.add(jsonObject);
        		}
        	} if(search_bar_title != null) { 
        		while (rs.next()) {
	            	String movie_id = rs.getString("id");
	                String movie_title = rs.getString("title");
	                jsonArray.add(generateJsonObject(movie_id, movie_title));
        		}
        		
        	} else {
	            while (rs.next()) {
	            	String movie_id = rs.getString("id");
	                String movie_title = rs.getString("title");
	                String movie_year = rs.getString("year");
	                String movie_director = rs.getString("director");
	                String movie_genre = rs.getString("genre");
	                String movie_star = rs.getString("star");
	                String movie_rating = rs.getString("rating");
	
	                // Create a JsonObject based on the data we retrieve from rs
	                JsonObject jsonObject = new JsonObject();
	                jsonObject.addProperty("movie_id", movie_id);
	                jsonObject.addProperty("movie_title", movie_title);
	                jsonObject.addProperty("movie_year", movie_year);
	                jsonObject.addProperty("movie_director", movie_director);
	                jsonObject.addProperty("movie_genre", movie_genre);
	                jsonObject.addProperty("movie_star", movie_star);
	                jsonObject.addProperty("movie_rating", movie_rating);
	                
	
	                jsonArray.add(jsonObject);
	            }
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
        out.close();
        
        
        long endTimeTS = System.nanoTime();
        long elapsedTimeTS = endTimeTS - startTimeTS; // elapsed time in nano seconds. Note: print the values in nano seconds
        long elapsedTimeTJ = endTimeTJ - startTimeTJ;
        String contextPath = getServletContext().getRealPath("/");
        String filePath= contextPath + "TS.txt";
        String contextPathTJ = getServletContext().getRealPath("/");
        String filePathTJ = contextPath + "TJ.txt";
        System.out.println(filePath);
        System.out.println(filePathTJ);
        File fileTS = new File(filePath);
        File fileTJ = new File(filePathTJ);
        if(!fileTS.exists()) {
        	fileTS.createNewFile();
        }
        
        if(!fileTJ.exists()) {
        	fileTJ.createNewFile();
        }
        
        //TS
        PrintWriter writer = new PrintWriter(new FileWriter(filePath, true));
        writer.println(elapsedTimeTS);
        writer.close();
        
        //TJ
        PrintWriter writerTJ = new PrintWriter(new FileWriter(filePathTJ, true));
        writerTJ.println(elapsedTimeTJ);
        writerTJ.close();
        

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
