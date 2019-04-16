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

function handleCartResult(resultData) {
	
	let cart_table = jQuery('#cart_table');
	let noMovies = resultData[0]["noMovies"]
	console.log(noMovies);
	if(noMovies !== undefined) {
		let rowHTML = "<div class='container p-3 rounded d-flex justify-content-center'>";
		rowHTML += "<h2>You have no movies in your cart.</h2></div>";
		cart_table.append(rowHTML);
	} else {
		let checkout_button = "<div class='container p-3 rounded d-flex justify-content-end'>";
		checkout_button += "<a href='./checkout.html'><button type='button' class='btn btn-info ml-2' id='checkout'>Checkout</button></a></div>";
		cart_table.append(checkout_button);
		for (let i = 0; i <resultData.length; i++) {
			let movieId = resultData[i]["movieId"];
			let movieTitle = resultData[i]["movieTitle"];
			let rowHTML = "<div class='container p-3 bg-light rounded d-flex justify-content-between'>";
			rowHTML += "<h3>" + movieTitle + "</h3>"
			rowHTML += "<div><input type='number' id='" + resultData[i]["movieId"] + "' name='tentacles' min='0' max='100'>" +
					"<button type='button' id='" + resultData[i]["movieId"] + "_update" + "' class='btn btn-primary ml-2'>Update</button><button id='" + resultData[i]["movieId"] + "_delete" + "' type='button' class='btn btn-primary ml-2'>Remove</button></div></div><br>"
			cart_table.append(rowHTML);
			
			
			$("#" + resultData[i]["movieId"]).val(resultData[i]["amount"]);
			console.log("#" + resultData[i]["movieId"] + "_update");
	        $("#" + resultData[i]['movieId'] + "_update").on('click', function(){
	        	console.log("CLICKED!");
	        	jQuery.ajax({
	        	    dataType: "json", // Setting return data type
	        	    method: "GET", // Setting request method
	        	    url: "api/cart?movieId=" + resultData[i]["movieId"] + "&amount=" + $("#" + resultData[i]["movieId"]).val(), // Setting request url, which is mapped by StarsServlet in Stars.java
	        	    success: (resultData) => window.location.replace("./cart.html")// Setting callback function to handle data returned successfully by the StarsServlet
	        	});
	        });
	        
	        $("#" + resultData[i]['movieId'] + "_delete").on('click', function(){
	        	console.log("CLICKED!");
	        	jQuery.ajax({
	        	    dataType: "json", // Setting return data type
	        	    method: "GET", // Setting request method
	        	    url: "api/cart?movieId=" + resultData[i]["movieId"] + "&amount=0", // Setting request url, which is mapped by StarsServlet in Stars.java
	        	    success: (resultData) => window.location.replace("./cart.html")// Setting callback function to handle data returned successfully by the StarsServlet
	        	});
	        });
		}
	}
	
}

//Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/cart" , // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleCartResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});