<?php

//header('Content-Type: text/html; charset=utf-8'); // Fix Utf 8 Response
header('Content-Type: application/json; charset=utf-8'); // Fix Utf 8 Response View as Json

include "connect.php";
$sql = "SELECT * FROM register";
$result = mysqli_query($conn,$sql);
if(!$result){
    echo "       ";
    return;
}
$data = array();
while($rows = mysqli_fetch_array($result)){
    $data[] = $rows;
}
// Fix Jsonencode UTF8 add JSON_UNESCAPED_UNICODE
echo "{\"data\" : ".json_encode($data,JSON_UNESCAPED_UNICODE)."}";
mysqli_close($conn);
?>