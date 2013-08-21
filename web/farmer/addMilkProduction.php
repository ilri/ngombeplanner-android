<?php
include "../log.php";
$TAG="farmer/addMilkProduction.php";
$time = new DateTime('now', new DateTimeZone('EAT'));
$timeEAT=$time->format('Y-m-d H:i:s');
$dateEAT=$time->format('Y-m-d');
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
	
	//get the cow id
	$query="SELECT `id` FROM `cow` WHERE `ear_tag_number` = '{$jsonObject['cowEarTagNumber']}' AND `name` = '{$jsonObject['cowName']}'";
	$result=mysql_query($query) or die("103");
	log_error($TAG,$timeEAT,mysql_error(),$query);
	if($fetchedRow=mysql_fetch_array($result))
	{
		$cowID=$fetchedRow['id'];
		$query="INSERT INTO `milk_production`(`cow_id`,`time`,`quantity`,`date`,`date_added`) VALUES($cowID,{$jsonObject['time']},{$jsonObject['quantity']},'$dateEAT','$timeEAT')";
		$result=mysql_query($query) or die(mysql_error());
		log_error($TAG,$timeEAT,mysql_error(),$query);
		echo $codes['acknowledge_ok'];
	}
	else
	{
		echo $codes['data_error'];
	}
}
else
{
	log_error($TAG,$timeEAT,"unable to open settings.ini");
}
?>
