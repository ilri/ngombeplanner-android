<?php
include "../log.php";
$TAG="farmer/fetchMilkProductionHistory.php";
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
	
	if($jsonObject['fromID']==-1)
	{
		$query="SELECT `milk_production`.*,`cow`.`name` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` INNER JOIN `milk_production` ON `cow`.`id`=`milk_production`.`cow_id` WHERE `farmer`.`sim_card_sn`='{$jsonObject['simCardSN']}' ORDER BY `milk_production`.`id` DESC LIMIT 40";
	}
	else
	{
		$query="SELECT `milk_production`.*,`cow`.`name` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` INNER JOIN `milk_production` ON `cow`.`id`=`milk_production`.`cow_id` WHERE `farmer`.`sim_card_sn`='{$jsonObject['simCardSN']}' AND `milk_production`.`id`<{$jsonObject['fromID']} ORDER BY `milk_production`.`id` DESC LIMIT 40";
	}
	$result=mysql_query($query) or die("103");
	log_error($TAG,$timeEAT,mysql_error(),$query);
	$history=array();
	$count=0;
	while($fetchedRow=mysql_fetch_array($result))
	{
		$history[$count]=$fetchedRow;
		$count++;
	}
	if(sizeof($history)==0)
	{
		echo $codes['no_data'];
	}
	else
	{
		$jsonArray=array();
		$jsonArray['history']=$history;
		echo json_encode($jsonArray);
	}
}
else
{
	log_error($TAG,$timeEAT,"unable to open settings.ini");
}
?>
