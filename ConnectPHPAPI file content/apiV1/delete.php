<?php
include "connect.php";
$std_id = $_GET['std_id'];
$sql = "DELETE FROM register WHERE std_id = '$std_id'";
$result = mysqli_query($conn,$sql);
if($result) echo "delete";
else echo "error sql ";
mysqli_close($conn);
?>