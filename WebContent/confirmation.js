/**
 * 
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

function handleConfResult(resultData) {
	console.log("We are inside successfully");
	console.log(resultData);
	let confirm_table = jQuery('#confirm_table');
	let headHTML = "<div class='container p-3 rounded'>";
	headHTML += "<div class='text-center'><h2>Thank you! Your purchase has been processed!</h2></div>" +
			"<div class='text-center'><h3>Transaction #: " + getParameterByName("id") + "</h3></div></div>";
	confirm_table.append(headHTML);
	
	for (let i = 0; i <resultData.length; i++) {
		let rowHTML = "<div class='container p-3 bg-light rounded d-flex justify-content-between'>";
		rowHTML += "<h3>" + resultData[i]["title"] + "</h3>"
		rowHTML += "<div><h4 id='" + resultData[i]["movieId"] + "'></h4>" + "<h4 id='" + resultData[i]["movieId"] + "_update'></h4>"
				"</div></div><br>"
		confirm_table.append(rowHTML);
		
		
		$("#" + resultData[i]["movieId"] + "_update").html(resultData[i]["amount"]);
		console.log("#" + resultData[i]["movieId"] + "_update");
	}
}



//Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/confirmation?id=" +  getParameterByName("id"), // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleConfResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});