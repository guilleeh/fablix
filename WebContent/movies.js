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
	let rowHTML = "<tr><th><h4>Stars:</h4></th><td>";
	array.forEach(function(star) {
        rowHTML +=
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-star.html?star=' + star + '&movie_id=' + movie_id + '">'
            + star +     // display star_name for the link text
            '</a>' + ", ";
	})
	rowHTML = rowHTML.slice(0, -2);
	rowHTML += "</td></tr>";

//	console.log(rowHTML);
	return rowHTML;
}


function determine_sort() {
	let sort = getParameterByName("sort_by");
  	if(sort.localeCompare("rating_desc") == 0) {
		return "High Rating";
	} else if(sort.localeCompare("rating_asc") == 0) {
		return "Low Rating";
	} else if(sort.localeCompare("title_desc") == 0) {
		return "Z-A";
	} else if(sort.localeCompare("title_asc") == 0){
		return "A-Z";
	}
}


function handleStarResult(resultData) {
	//We are populating the tables with the results
	
    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table");
    let moviesLink = jQuery("#movie_page").attr('href','movies.html' + determine_url(0) + "&results=10" +"&sort_by=rating_desc");
    
    //configure pre and next buttons
    let page_num = getParameterByName('page');
    
    //configure results page
    let show_10 = jQuery("#show_10").attr('href', 'movies.html' + determine_url(0) + "&results=" + 10 + "&sort_by=" + getParameterByName('sort_by'));
    let show_25 = jQuery("#show_25").attr('href', 'movies.html' + determine_url(0) + "&results=" + 25 + "&sort_by=" + getParameterByName('sort_by'));
    let show_50 = jQuery("#show_50").attr('href', 'movies.html' + determine_url(0) + "&results=" + 50 + "&sort_by=" + getParameterByName('sort_by'));
    let show_100 = jQuery("#show_100").attr('href', 'movies.html' + determine_url(0) + "&results=" + 100 + "&sort_by=" + getParameterByName('sort_by'));
    
    let results_button = jQuery("#results_button").html("Results: " + getParameterByName('results'));
    let sorting_button = jQuery("#sorting_button").html("Sort By: " + determine_sort());
    
    //configure sorting
    let title_asc = jQuery("#title_asc").attr('href', 'movies.html' + determine_url(0) + "&results=" +getParameterByName('results') + "&sort_by=title_asc");
    let title_desc = jQuery("#title_desc").attr('href', 'movies.html' + determine_url(0) + "&results=" +getParameterByName('results') + "&sort_by=title_desc");
    let rating_asc = jQuery("#rating_asc").attr('href', 'movies.html' + determine_url(0) + "&results=" +getParameterByName('results') + "&sort_by=rating_asc");
    let rating_desc = jQuery("#rating_desc").attr('href', 'movies.html' + determine_url(0) + "&results=" +getParameterByName('results') + "&sort_by=rating_desc");

    
 // Iterate through resultData, no more than 10 entries
    for (let i = 0; i <resultData.length; i++) {
    	
    	
    	//prepare movie_star links
    	let movie_stars = resultData[i]["movie_star"];
    	let each_star = movie_stars.split(", ");
    	
    	
        // Concatenate the html tags with resultData jsonObject
        let rowHTML = "<div class='container p-3 bg-light rounded'>";
        rowHTML +=
            "<div class='d-flex justify-content-between'><h3>" +
            // Add a link to single-movie.html with id passed with GET url parameter
            '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">'
             + resultData[i]["movie_title"] +     // display star_name for the link text
            '</a>' +
            "</h3><button id='" +  resultData[i]['movie_id'] + "' type='button' class='btn btn-info'><i class='fas fa-shopping-cart'></i>Add to Cart</button></a></div>";
        rowHTML += "<table><tr><th><h4>Year:</h4></th><td>" + '<h4>' +resultData[i]["movie_year"] + '</h4>' + "</td></tr>";
        rowHTML += "<tr><th style='padding-right:18px;'><h4>Director:</h4></th><td>" + '<h4>' + resultData[i]["movie_director"] + '</h4>' + "</td></tr>";
        rowHTML += "<tr><th><h4>Genre:</h4></th><td>" + '<h4>' + resultData[i]["movie_genre"] + '</h4>' + "</td></tr>";
        
        
        
        //rowHTML += "<th>" + resultData[i]["movie_star"] + "</th>";
        rowHTML += '<h4>' + buildStarString(each_star, resultData[i]['movie_id']) + '</h4>';
        
        
        
        
        rowHTML += "<tr><th><h4>Rating:</h4></th><td>" + '<h4>' + resultData[i]["movie_rating"] + '</h4>' + "</td></tr>";
        rowHTML += "</table></div><br>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
        let click_add_cart = jQuery("#" + resultData[i]['movie_id']).on('click', function(){
        	jQuery.ajax({
        	    dataType: "json", // Setting return data type
        	    method: "GET", // Setting request method
        	    url: "api/cart?movieId=" + resultData[i]['movie_id'] + "&movieTitle=" + encodeURIComponent(resultData[i]['movie_title']), // Setting request url, which is mapped by StarsServlet in Stars.java
        	    success: (resultData) => window.location.replace("./cart.html")// Setting callback function to handle data returned successfully by the StarsServlet
        	});
        });
    }
    
    //add prev/next pages
    let pre_next_list = jQuery("#prev_next");
    pre_next_list.append('<li class="page-item"><a class="page-link" id="prev" href="#">Prev</a></li>');
    let prev = jQuery("#prev").attr('href', 'movies.html' + determine_url(-1)  + "&results=" + getParameterByName('results') + "&sort_by=" + getParameterByName('sort_by')); //subtract 1 to get prev page
    
    if(!(resultData.length < Number(getParameterByName('results')))) {
        pre_next_list.append('<li class="page-item"><a class="page-link" id="next" href="#">Next</a></li>');
        let next = jQuery("#next").attr('href', 'movies.html' + determine_url(1) +  "&results=" + getParameterByName('results') + "&sort_by=" + getParameterByName('sort_by')); //add 1 for next page
    }
}

/*
 * Function to determine what kind of url the backend should receive
 * 
 */
function determine_url(page_count = 0) {
	let title = getParameterByName('title');
	let year = getParameterByName('year');
	let director = getParameterByName('director');
	let star = getParameterByName('star');
	let browse_genre = getParameterByName('browse_genre');
	let browse_title = getParameterByName('browse_title');
	let page_num = Number(getParameterByName('page')) + page_count;
	let search_bar_title = getParameterByName('search_bar_title');
	let normal_search_bar_title = getParameterByName('normal_search_bar_title');
//	let page_results = Number(getParameterByName('results'));
	if( page_num < 1) { //No negative pages
		page_num = 1;
	}
	let params = "";
	let constant_params = "page=" + page_num;
	if(!(title === null)) {
		params=`?title=${title}&year=${year}&director=${director}&star=${star}&${constant_params}`;
	} else if(!(browse_genre === null)) {
		console.log(browse_genre);
		params = `?browse_genre=${browse_genre}&${constant_params}`;
	} else if(!(browse_title === null)){
		console.log(browse_title);
		params = `?browse_title=${browse_title}&${constant_params}`;
	} else if(!(search_bar_title === null)) {
		console.log(search_bar_title);
		params = `?search_bar_title=${search_bar_title}&${constant_params}`
	} else if(!(normal_search_bar_title === null)) {
		console.log(normal_search_bar_title);
		params = `?normal_search_bar_title=${normal_search_bar_title}&${constant_params}`;
	} else {
		params = `?${constant_params}`;
	}
	return params;
}

//Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movies" + determine_url(0) + "&results=" + getParameterByName('results') + "&sort_by=" + getParameterByName("sort_by"), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleStarResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});
