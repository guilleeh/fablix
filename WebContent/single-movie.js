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
function buildStarString(array, movie_id) {
	let rowHTML = "<tr><th><h4>Stars:<h4></th><td>";
	array.forEach(function(star) {
        rowHTML +=
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-star.html?star=' + star + '&movie_id=' + movie_id + '">'
            + star +     // display star_name for the link text
            '</a>' + ", ";
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

    let moviesLink = jQuery("#movie_page").attr('href','movies.html');

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#movie_table");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
    	
    	//prepare movie_star links
    	let movie_stars = resultData[i]["movie_star"];
    	let each_star = movie_stars.split(", ");
    	
        let rowHTML = "<div class='container p-3 bg-light rounded'><table>";
        rowHTML +=
            "<div class='d-flex justify-content-between'><h3>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            resultData[i]["movie_title"] +     // display star_name for the link text
            "</h3><button id='" +  resultData[i]['movie_id'] + "' type='button' class='btn btn-info'><i class='fas fa-shopping-cart'></i>Add to Cart</button></a></div>";
        rowHTML += "<table><tr><th><h4>Year:</h4></th><td>" + '<h4>' +resultData[i]["movie_year"] + '</h4>' + "</td></tr>";
        rowHTML += "<tr><th class='pr-5'><h4>Movie ID:</h4></th><td><h4>" + resultData[i]["movie_id"] + "</h4></td></tr>";
        rowHTML += "<tr><th><h4>Year:</h4></th><td><h4>" + resultData[i]["movie_year"] + "</h4></td></tr>";
        rowHTML += "<tr><th><h4>Director:</h4></th><td><h4>" + resultData[i]["movie_director"] + "</h4></td></tr>";
        rowHTML += "<tr><th><h4>Genre:</h4></th><td><h4>" + resultData[i]["movie_genre"] + "</h4></td></tr>";
        
        rowHTML += buildStarString(each_star, resultData[i]['movie_id']);
        
        
        rowHTML += "<tr><th><h4>Rating:</h4></th><td><h4>" + resultData[i]["movie_rating"] + "</h4></td></tr>";
        rowHTML += "</table></div>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
        let click_add_cart = jQuery("#" + resultData[i]['movie_id']).on('click', function(){
        	jQuery.ajax({
        	    dataType: "json", // Setting return data type
        	    method: "GET", // Setting request method
        	    url: "api/cart?movieId=" + resultData[i]['movie_id'] + "&movieTitle=" + encodeURIComponent(resultData[i]['movie_title']), // Setting request url, which is mapped by StarsServlet in Stars.java
        	    success: (resultData) => window.location.replace("./cart.html")// Setting callback function to handle data returned successfully by the StarsServlet
        	});
        });
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});