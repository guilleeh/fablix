function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Use regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleStarAddition(resultData) {
	console.log("handleStarAddition: checking status of insert from resultData");
	let addssBodyElement = jQuery("#add_single_star");
	for (let i = 0; i < resultData.length; i++) {
		let rowHTML = "<p>";
		if (resultData[i]["count"] > 0)
			rowHTML = "Star added!";
		else
			rowHTML = "Unknown failure.";
		rowHTML += "</p>";
		addssBodyElement.append(rowHTML);
	}
}

function handleMovieAddition(resultData) {
	console.log("handleMovieAddition: checking status of insert from resultData");
	let addsmBodyElement = jQuery("add_single_movie");
	for (let i = 0; i < resultData.length; i++) {
		let rowHTML = "<p>";
		if (resultData[i]["count"] > 0)
			rowHTML = "Movie added!";
		else if (resultData[i]["count"] == -2)
			rowHTML = "Movie already exists. Not added to Fablix's database";
		else
			rowHTML = "Database connecton error.";
		rowHTML += "</p>";
		addsmBodyElement.append(rowHTML);
	}
}

function handleResult(resultData) {

    console.log("handleResult: populating db metadata info from resultData");
    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let dbTableBodyElement = jQuery("#db_table");
    let curr_table = null;
    if (resultData.length == 0) {//error?
    	let rowHTML = "<p>Database connection error.</p>";
    	dbTableBodyElement.append(rowHTML);
    }
     //Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < resultData.length; i++) {
    	let rowHTML = "<div class='container p-3 bg-light rounded'><table>";
    	rowHTML = rowHTML + "<tr><th class='pr-5'>" + resultData[i][0]["table"] + ":</th></tr>";
    	for (let k = 0; k < resultData[i].length; ++k) {
    		rowHTML = rowHTML + "<tr><td>" + resultData[i][k]["column_name"] + " (of type " + resultData[i][k]["column_type"] + ")</td></tr>";
    	}
    	rowHTML += "</table></div><br>";
    	dbTableBodyElement.append(rowHTML);
    }
}

let type = null
try {
	type = getParameterByName('type')
}
catch (ReferenceError) {
	type = "null"
}

console.log(type);

if (type == "star") {
	console.log("adding star");
	let star = getParameterByName('star')
	let year = null
	try {
		year = getParamaterByName('year')
	}
	catch (ReferenceError) {
		year = null
	}
	if (year == null) {
		jQuery.ajax({
			dataType: "json",
			method: "GET",
			url: "api/add-single-star?star=" + star,
			success: (resultData) => handleStarAddition(resultData)
		});
	}
	else {
		jQuery.ajax({
			dataType: "json",
			method: "GET",
			url: "api/add-single-star?star=" + star + "&year=" + year,
			success: (resultData) => handleStarAddition(resultData)
		});
	}
}
if (type == "movie") {
	console.log("adding movie");
	let id = getParameterByName('id')
	let name = getParameterByName('name')
	let year = getParameterByName('year')
	let director = getParameterByName('director')
	let genre = getParameterByName('genre')
	let star = getParameterByName('star')
	//do the rest of the parameter retrieval + jQuery here
}

// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/db-metadata", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});