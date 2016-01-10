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
if (isset($_GET['value_username'])) {
	
    $username = $_GET['value_username'];
	$db = mysqli_connect("localhost","root","thanks123", "sunshine") or die ("could not connect to mysql"); 

    // get a product from products table
    $result = mysqli_query($db, "SELECT * FROM users WHERE username = '$username'");

    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {

            while($row = mysqli_fetch_assoc($result)) {

				$product = array();
				$product["id"] = $row["id"];
				$product["username"] = $row["username"];
				$product["password"] = $row["password"];
				$product["email"] = $row["email"];
				$product["is_admin"] = (boolean)$row["is_admin"];
				$product["occupation"] = $row["occupation"];
				$product["is_verified"] = $row["is_verified"];
				
				// success
				$response["success"] = 1;

				// user node
				$response["user"] = array();

				array_push($response["user"], $product);
			}
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No product found";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No product found";

        // echo no users JSON
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