<?php

/*
 * Following code will create a new product row
 * All product details are read from HTTP Post Request
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['value_description'])) {
    
	$user_id = (int)$_POST['value_user_id'];
    $description = $_POST['value_description'];
    
    // include db connect class
    //require_once __DIR__ . '/db_connect.php';

    // connecting to db
    //$db = new DB_CONNECT();
	$db = mysqli_connect("localhost","root","thanks123", "sunshine") or die ("could not connect to mysql"); 

    // mysql inserting a new row
    $result = mysqli_query($db, "INSERT INTO polls(user_id, description) VALUES($user_id, '$description')");
	
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
		$poll_id = mysqli_insert_id($db);
		$number_of_options = (int)$_POST['no_options'];
		$first_option = $_POST['option_1'];
		$sql = "INSERT INTO vote_options(poll_id,title) VALUES($poll_id,'$first_option');";
		for ($x = 2; $x < $number_of_options; $x++) {
			$option = $_POST['option_'.(string)$x];
			$sql .= "INSERT INTO vote_options(poll_id,title) VALUES($poll_id,'$option');";
		}
		if (mysqli_multi_query($db, $sql)) {
			 $response["success"] = 1;
			 $response["message"] = "User successfully created.";
		} else {
			echo "Error: " . $sql . "<br>" . mysqli_error($db);
		}

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