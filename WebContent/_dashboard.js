/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleEmployeeLoginResult(resultDataString) {
    resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("_employee.html?type=null");
    } else {
        // If login fails, the web page will display 
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").addClass("alert alert-danger");
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitEmployeeLoginForm(formSubmitEvent) {
    console.log("submit employee login form");
    console.log($("#employee_login_form").serialize());
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.post(
        "api/employee_login",
        // Serialize the login form to the data sent by POST request
        $("#employee_login_form").serialize(),
        (resultDataString) => handleEmployeeLoginResult(resultDataString)
    );
}

// Bind the submit action of the form to a handler function
$("#employee_login_form").submit((event) => submitEmployeeLoginForm(event));

