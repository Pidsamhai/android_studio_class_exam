<?php
header('Content-Type: text/html; charset=utf-8'); // Fix Utf 8 Response

include "connect.php";

$std_id = $_POST['std_id'];
$name = $_POST['name'];
$phone = $_POST['phone'];
$faculty = $_POST['faculty'];
$major = $_POST['major'];
$username = $_POST['username'];
$password = $_POST['password'];

$sql = "UPDATE register SET std_name  = '$name',std_phone = '$phone',std_faculty = '$faculty',std_major = '$major',username = '$username',password = '$password' WHERE std_id = '$std_id'";
$result = mysqli_query($conn,$sql);
if($result) echo "แก้ไขข้อมูลเรียบร้อยแล้ว";
mysqli_close($conn);
?>