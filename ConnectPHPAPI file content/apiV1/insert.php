<?php

header('Content-Type: text/html; charset=utf-8'); // Fix Utf 8 Response

include "connect.php";

$name = $_POST['name'];
$phone = $_POST['phone'];
$faculty = $_POST['faculty'];
$major = $_POST['major'];
$username = $_POST['username'];
$password = $_POST['password'];

$sql = "INSERT INTO register (std_name,std_phone,std_faculty,std_major,username,password) VALUES ('$name','$phone','$faculty','$major','$username','$password')";
$result = mysqli_query($conn,$sql);
if($result) echo "บันทึกข้อมูลเรียบร้อย";
else "ไม่สามารถประมวลผล SQL ได้";
mysqli_close($conn);
?>