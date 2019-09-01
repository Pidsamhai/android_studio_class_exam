<?php
$host = "localhost";
$user = "root";
$pass = "";
$db_name = "student";

$conn = mysqli_connect($host,$user,$pass,$db_name);
if(mysqli_connect_errno())
    die("Database connection failed"."(".mysqli_connect_error()."_".mysqli_connect_errorno().")");
else 
    mysqli_set_charset($conn,"utf8");
    mysqli_query($conn,"SET character_set_results=utf8");
    mysqli_query($conn,"SET character_set_client=utf8");
    mysqli_query($conn,"SET character_set_connection=utf8");
?>