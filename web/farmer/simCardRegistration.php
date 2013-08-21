<?php
include "../log.php";
$TAG="farmer/simCardRegistration.php";
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
	$connect=mysql_connect($host,$username,$password) or die("1");
	mysql_select_db($database) or die("2");
	$jsonObject=json_decode($_POST["json"],true);

	$fh = fopen("../log/json.log","a");
	fputs($fh,$_POST['json']);
	fclose($fh);
	
	//check if old mobile number exists
	$query="SELECT `sim_card_sn` FROM `farmer` WHERE `mobile_no` = '{$jsonObject['oldMobileNumber']}'";
	$result=mysql_query($query) or die("3");
	log_error($TAG,$timeEAT,mysql_error(),$query);
	if($fetchedRow=mysql_fetch_array($result))
	{
		$query="UPDATE `farmer` SET `mobile_no`='{$jsonObject['newMobileNumber']}', `sim_card_sn`='{$jsonObject['newSimCardSN']}' WHERE `mobile_no` = '{$jsonObject['oldMobileNumber']}'";
		$result=mysql_query($query) or die("4");
		log_error($TAG,$timeEAT,mysql_error(),$query);
		echo $codes['sim_card_registered'];
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
