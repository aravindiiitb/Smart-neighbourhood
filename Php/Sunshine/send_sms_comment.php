<?php

$username = $_POST['value_username'];
$comments = $_POST['value_body'];

$post_data = array(
    // 'From' doesn't matter; For transactional, this will be replaced with your SenderId;
    // For promotional, this will be ignored by the SMS gateway
    'From'   => '',
    'To'    => '09738554979',
    'Body'  => 'Hello !! '.$username.' Has commented on your post: '.$comments, //Incase you are wondering who Dr. Rajasekhar is http://en.wikipedia.org/wiki/Dr._Rajasekhar_(actor)
);
 
$exotel_sid = "iiitb2"; // Your Exotel SID - Get it from here: http://my.exotel.in/Exotel/settings/site#api-settings
$exotel_token = "bfa65f8beb90f095202da8a1f2f558ebf82078c1"; // Your exotel token - Get it from here: http://my.exotel.in/Exotel/settings/site#api-settings
 
$url = "https://".$exotel_sid.":".$exotel_token."@twilix.exotel.in/v1/Accounts/".$exotel_sid."/Sms/send";

$ch = curl_init();
curl_setopt($ch, CURLOPT_VERBOSE, 1);
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_POST, 1);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
curl_setopt($ch, CURLOPT_FAILONERROR, 0);
curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, 0);
curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($post_data));
 
$http_result = curl_exec($ch);
$error = curl_error($ch);
$http_code = curl_getinfo($ch ,CURLINFO_HTTP_CODE);
$response["success"] = 1;
$response["message"] = "SMS successfully sent.";

 
curl_close($ch);
echo json_encode($response);
print "Response = ".print_r($http_result);
?>