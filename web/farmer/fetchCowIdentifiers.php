<?php
include "../log.php";
$TAG="farmer/fetchCowIdentifiers.php";
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
	$query="SELECT `cow`.`name`,`cow`.`ear_tag_number` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id`=`cow`.`farmer_id` WHERE `farmer`.`sim_card_sn`='{$jsonObject['simCardSN']}'";
	$result=mysql_query($query) or die("103");
	$cowNameArray=array();
	$earTagNumberArray=array();
	$index=0;
	while($fetchedRow=mysql_fetch_array($result))
	{
		$cowNameArray[$index]=$fetchedRow['name'];
		$earTagNumberArray[$index]=$fetchedRow['ear_tag_number'];
		$index++;
	}
	$jsonArray=array();
	$jsonArray['cowNames']=$cowNameArray;
	$jsonArray['earTagNumbers']=$earTagNumberArray;
	echo json_encode($jsonArray);
}
else
{
	log_error($TAG,$timeEAT,"unable to open settings.ini");
}
?>
