<?php

/*
 * Following code will get single product details
 * A product is identified by product id (pid)
 */

// array for JSON response
$response = array();


// include db connect class
//require_once __DIR__ . '/db_connect.php';

// connecting to db
//$db = new DB_CONNECT();

// check for post data
if (true) {
	
    $username = $_POST["value_username"];
    $password = $_POST["value_password"];
    $email = $_POST["value_email"];
    $user_id = (int) $_POST["value_userid"];
    $db = mysqli_connect("localhost","root","thanks123", "sunshine") or die ("could not connect to mysql"); 

    // get a product from products table
    $result = mysqli_query($db, "UPDATE users set username = '$username', password = '$password', email = '$email' where id = $user_id");

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