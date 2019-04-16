/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}


//this function builds the list of stars with their respective links
function buildMovieString(array, star_name) {
	console.log()
	let rowHTML = "<tr><th>Movies:</th><td>";
	array.forEach(function(star) {
        rowHTML +=
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + getParameterByName('movie_id') + '">' + star_name + '</a>' + ", ";
	})
	rowHTML = rowHTML.slice(0, -2);
	rowHTML += "</td></tr>";

	console.log(rowHTML);
	return rowHTML;
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "star_info"
    let starInfoElement = jQuery("#star_info");
    let moviesLink = jQuery("#movie_page").attr('href','movies.html');

    // append two html <p> created to the h3 body, which will refresh the page
    starInfoElement.append("<p>Star Name: " + resultData[0]["star_name"] + "</p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#star_table");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
    	
    	//prepare movie_star links
    	let movie_stars = resultData[i]["star_movie"];
    	let each_star = movie_stars.split(", ");
    	
    	let star = resultData[i]["star_name"];
    	let year = resultData[i]["star_year"];
    	
    	if(year == null) {
    		year = "N/A";
    	}
    	
        let rowHTML = "<div class='container p-3 bg-light rounded'><table>";
        rowHTML += "<tr>";
        rowHTML += "<th class='pr-5'>Star:</th><td>" + star + "</td></tr>";
        
        rowHTML += "<tr><th>Year:</th><td>" + year + "</td></tr>";
        
        rowHTML += buildMovieString(each_star, resultData[i]['star_movie']);
        
        rowHTML += "</table></div><br>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

//// Get id from URL
let starName = getParameterByName('star')
let movieId = getParameterByName('movie_id');


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-star?star=" + starName + "&id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});