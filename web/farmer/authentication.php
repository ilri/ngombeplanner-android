<?php
include "../log.php";
$TAG="farmer/authentication.php";
$time = new DateTime('now', new DateTimeZone('EAT'));
$timeEAT=$time->format('Y-m-d H:i:s');
if(file_exists("../settings.ini") && file_exists("../codes.ini"))
{
	$settings=array();
	$settings=parse_ini_file("../settings.ini");
	$username=$settings["username"];
	$password=$settings["password"];
	$database=$settings["db_name"];
	$host=$settings["host"];
	$codes=parse_ini_file("../codes.ini");
	$connect=mysql_connect($host,$username,$password) or die("101");
	mysql_select_db($database) or die("102");
	$jsonObject=json_decode($_POST["json"],true);
	
	$fh = fopen("../log/json.log","a");
	fputs($fh,$_POST['json']);
	fclose($fh);
	
	//$farmerID=$jsonObject['farmerID'];
	//$mobileNumber=$jsonObject['mobileNumber'];
	$simCardSN=$jsonObject['simCardSN'];
	$query="SELECT `name` FROM `farmer` WHERE `sim_card_sn`='$simCardSN'";
	$result=mysql_query($query) or die("103");
	log_error($TAG,$timeEAT,mysql_error(),$query);
	if($fetchedResult=mysql_fetch_array($result))
	{
		echo $fetchedResult['name'];
	}
	else
	{
		echo $codes['user_not_authenticated'];
	}
}
else
{
	log_error($TAG,$timeEAT,"unable to open settings.ini");
}
?>
