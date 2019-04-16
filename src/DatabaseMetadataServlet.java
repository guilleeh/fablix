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
import java.sql.DatabaseMetaData;
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
 * Servlet implementation class DatabaseMetadataServlet
 */
@WebServlet(name = "/DatabaseMetadataServlet", urlPatterns = "/api/db-metadata")
public class DatabaseMetadataServlet extends HttpServlet {
private static final long serialVersionUID = 1L;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
	
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;
    
    public DatabaseMetadataServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession session = request.getSession();
		
		// Output stream to STDOUT
		PrintWriter out = response.getWriter();
		
		try {
			// Get a connection from dataSource
			Connection dbcon = dataSource.getConnection();
			DatabaseMetaData dbmd = dbcon.getMetaData();
			
			ResultSet rs = dbmd.getTables(null, null, null, new String[]{"TABLE"});
			
			JsonArray ja_tables = new JsonArray();

			// Iterate through each row of rs
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");


				// Create a JsonObject based on the data we retrieve from rs
				JsonArray ja_cols = new JsonArray();
				ResultSet c_rs = dbmd.getColumns(null, null, tableName, null);
				while (c_rs.next()) {
					JsonObject json = new JsonObject();
					json.addProperty("table", tableName);
					json.addProperty("column_name", c_rs.getString("COLUMN_NAME"));
					json.addProperty("column_type", c_rs.getString("TYPE_NAME"));
					ja_cols.add(json);
				}
				ja_tables.add(ja_cols);
			}
			
            // write JSON string to output
            out.write(ja_tables.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

			rs.close();
			dbcon.close();

		} catch (Exception e) {
			// write error message JSON object to output
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("errorMessage", e.getMessage());
			out.write(jsonObject.toString());

			// set response status to 500 (Internal Server Error)
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
