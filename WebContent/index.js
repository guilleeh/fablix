/**
 * This example is following frontend and backend separation.
**/


//this function builds the list of stars with their respective links
function buildStarString(array, movie_id) {
	let rowHTML = "<th>";
	array.forEach(function(star) {
        rowHTML +=
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-star.html?star=' + star + '&movie_id=' + movie_id + '">'
            + star +     // display star_name for the link text
            '</a>' + ", ";
	})
	rowHTML = rowHTML.slice(0, -2);
	rowHTML += "</th>";

	console.log(rowHTML);
	return rowHTML;
}

function handleMenuResult(resultData) {
	
    let starTableBodyElement = jQuery("#movie_table_body");
    
    let defaultParameters = "page=1&results=10&sort_by=rating_desc"; //All pages should have these
    
    let moviesLink = jQuery("#movie_page").attr('href','movies.html?' + defaultParameters);
	
	let genre_table = jQuery("#genres");
	
	// Iterate through resultData
    for (let i = 0; i <resultData.length; i++) {
    	let genre = resultData[i]["movie_genre"];
    	let rowHTML = `<li'><a class='pr-3' href='./movies.html?browse_genre=${genre}&${defaultParameters}'>${genre}</a><li>`;
    	genre_table.append(rowHTML);
    }
    
    for (var i = 48; i <= 57; i++) {
    	let char = String.fromCharCode(i);
        $('#titles').append(`<li'><a class='pr-3' href='./movies.html?browse_title=${char}&${defaultParameters}'>${char}</a><li>`);
    }
    
    for (var i = 65; i <= 90; i++) {
    	let char = String.fromCharCode(i);
        $('#titles').append(`<li'><a class='pr-3' href='./movies.html?browse_title=${char}&${defaultParameters}'>${char}</a><li>`);
    }
}


/*
 * This function is called by the library when it needs to lookup a query.
 * 
 * The parameter query is the query string.
 * The doneCallback is a callback function provided by the library, after you get the
 *   suggestion list from AJAX, you need to call this function to let the library know.
 */

var cacheMap = new Map(); //Store cached results

function handleLookup(query, doneCallback) {
	console.log("autocomplete initiated")
	
	// TODO: if you want to check past query results first, you can do it here
	if(cacheMap.get(query) !== undefined) {
		console.log("Using cached results");
		handleLookupAjaxSuccess(cacheMap.get(query), query, doneCallback);
	} else {
		// sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
		// with the query data
		console.log("sending AJAX request to backend Java Servlet")
		jQuery.ajax({
			"method": "GET",
			// generate the request url from the query.
			// escape the query string to avoid errors caused by special characters 
			"url": `./api/movies?search_bar_title=${escape(query)}&page=1&results=10&sort_by=rating_desc`,
			"success": function(data) {
				// pass the data, query, and doneCallback function into the success handler
				handleLookupAjaxSuccess(data, query, doneCallback)
				
			},
			"error": function(errorData) {
				console.log("lookup ajax error")
				console.log(errorData)
			}
		});
	}
}


/*
 * This function is used to handle the ajax success callback function.
 * It is called by our own code upon the success of the AJAX request
 * 
 * data is the JSON data string you get from your Java Servlet
 * 
 */
function handleLookupAjaxSuccess(data, query, doneCallback) {
	console.log("lookup ajax successful")
	console.log(data);

	
	// TODO: if you want to cache the result into a global variable you can do it here
	cacheMap.set(query, data);
	
	// call the callback function provided by the autocomplete library
	// add "{suggestions: jsonData}" to satisfy the library response format according to
	//   the "Response Format" section in documentation
	doneCallback( { suggestions: data } );
}


/*
 * This function is the select suggestion handler function. 
 * When a suggestion is selected, this function is called by the library.
 * 
 * You can redirect to the page you want using the suggestion data.
 */
function handleSelectSuggestion(suggestion) {
	// TODO: jump to the specific result page based on the selected suggestion
	
	console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["movieID"])
	console.log(suggestion["value"]);
	window.location.href = `./single-movie.html?id=${suggestion["data"]["movieID"]}`;
}


$('#search-bar').autocomplete({
	// documentation of the lookup function can be found under the "Custom lookup function" section
    lookup: function (query, doneCallback) {
    		handleLookup(query, doneCallback)
    },
    onSelect: function(suggestion) {
    		handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,
    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
    minChars: 3,
});


/*
 * do normal full text search if no suggestion is selected 
 */
function handleNormalSearch(query) {
	console.log("doing normal search with query: " + query);
	// TODO: you should do normal search here
	window.location.href = `./movies.html?normal_search_bar_title=${$("#search-bar").val()}&page=1&results=10&sort_by=rating_desc`;
}

//bind pressing enter key to a handler function
$('#search-bar').keypress(function(event) {
	// keyCode 13 is the enter key
	if (event.keyCode == 13) {
		// pass the value of the input box to the handler function
		handleNormalSearch($('#search-bar').val())
	}
})

//SETUP Search Bar with Onclick event
$("#search_button").click(function() {
	if($("#search-bar").val()) {
		window.location.href = `./movies.html?normal_search_bar_title=${$("#search-bar").val()}&page=1&results=10&sort_by=rating_desc`;
	}
});


//HTTP GET request to populate Browsing menu
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies?genre=name&page=1&results=10&sort_by=rating_desc", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleMenuResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

