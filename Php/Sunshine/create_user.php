<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['value_username']) && isset($_POST['value_email']) && isset($_POST['value_password'])) {
    
    $username = $_POST['value_username'];
    $password = $_POST['value_password'];
    $email = $_POST['value_email'];

    // include db connect class
    //require_once __DIR__ . '/db_connect.php';

    // connecting to db
    //$db = new DB_CONNECT();
	$db = mysqli_connect("localhost","root","thanks123", "sunshine") or die ("could not connect to mysql"); 

    // mysql inserting a new row
    $result = mysqli_query($db, "INSERT INTO users(username, password, email) VALUES('$username', '$password', '$email')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "User successfully created.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
        
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";

    // echoing JSON response
    echo json_encode($response);
}
?>