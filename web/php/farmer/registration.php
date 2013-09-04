<?php
include "../log.php";
$TAG="farmer/registration.php";
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

	//$jsonObject=json_decode('{"longitude":"36.72190326265991","latitude":"-1.2692801048979163","extensionPersonnel":"Test","fullName":"Sn Tester","mobileNumber":"0715023805","cows":[],"simCardSN":"89254029541005994303"}',true) or die ("json error");
	$query="INSERT INTO `farmer`(name,`mobile_no`,`gps_longitude`,`gps_latitude`,`extension_personnel`,`sim_card_sn`,`date_added`) VALUES('{$jsonObject['fullName']}','{$jsonObject['mobileNumber']}','{$jsonObject['longitude']}','{$jsonObject['latitude']}','{$jsonObject['extensionPersonnel']}','{$jsonObject['simCardSN']}','$timeEAT')";
	$result=mysql_query($query) or die(mysql_error()." ".$query);
	log_error($TAG." 2",$timeEAT,mysql_error(),$query);
	$farmerID=mysql_insert_id();
	$cows=array();
	$cows=$jsonObject['cows'];
	for($i=0;$i<sizeof($cows);$i++)
	{
		//insert cow
		$currentCow=$cows[$i];
		$query="INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`type`,`date_added`) VALUES($farmerID,'{$currentCow['name']}','{$currentCow['earTagNumber']}','{$currentCow['dateOfBirth']}',{$currentCow['age']},{$currentCow['ageType']},{$currentCow['sex']},'cow','$timeEAT')";
		$result=mysql_query($query) or die(mysql_error()." ".$query);
		log_error($TAG." 3",$timeEAT,mysql_error(),$query);
		$cowID=mysql_insert_id();
		$breeds=$currentCow['breeds'];
		for($j=0;$j<sizeof($breeds);$j++)
		{
			$currentBreed=$breeds[$j];
			$query="INSERT INTO `breed`(`cow_id`,`text`,`date_added`) VALUES($cowID,'$currentBreed','$timeEAT')";
			$result=mysql_query($query) or die(mysql_error()." ".$query);
			log_error($TAG." 4",$timeEAT,mysql_error(),$query);
		}
		$deformities=$currentCow['deformities'];
		for($j=0;$j<sizeof($deformities);$j++)
		{
			$currentDeformity=$deformities[$j];
			$query="INSERT INTO `deformity`(`cow_id`,`text`,`date_added`) VALUES($cowID,'$currentDeformity','$timeEAT')";
			$result=mysql_query($query) or die(mysql_error()." ".$query);
			log_error($TAG." 5",$timeEAT,mysql_error(),$query);
		}
		$sire=$currentCow['sire'];
		if($sire['earTagNumber']!=""||$sire['strawNumber']!=""||$sire['name']!="")
		{
			$query="INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`type`,`service_type`,`straw_number`,`vet_used`,`date_added`) VALUES($farmerID,'{$sire['name']}','{$sire['earTagNumber']}','{$sire['dateOfBirth']}',{$sire['age']},{$sire['ageType']},{$sire['sex']},'sire',{$sire['serviceType']},'{$sire['strawNumber']}','{$sire['vetUsed']}','$timeEAT')";
			$result=mysql_query($query) or die(mysql_error()." ".$query);
			log_error($TAG." 6",$timeEAT,mysql_error(),$query);
			$sireID=mysql_insert_id();
			$query="UPDATE `cow` SET `sire_id` = $sireID WHERE `id` = $cowID";
			$result=mysql_query($query) or die(mysql_error()." ".$query);
			log_error($TAG." 7",$timeEAT,mysql_error(),$query);
		}
		$dam=$currentCow['dam'];
		if($dam['earTagNumber']!=""||$dam['embryoNumber']!=""||$dam['name']!="")
		{
			$query="INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`type`,`service_type`,`embryo_number`,`vet_used`,`date_added`) VALUES($farmerID,'{$dam['name']}','{$dam['earTagNumber']}','{$dam['dateOfBirth']}',{$dam['age']},{$dam['ageType']},{$dam['sex']},'dam',{$dam['serviceType']},'{$dam['embryoNumber']}','{$dam['vetUsed']}','$timeEAT')";
			$result=mysql_query($query) or die(mysql_error()." ".$query);
			log_error($TAG." 8",$timeEAT,mysql_error(),$query);
			$damID=mysql_insert_id();
			$query="UPDATE `cow` SET `dam_id` = $damID WHERE `id` = $cowID";
			$result=mysql_query($query) or die(mysql_error()." ".$query);
			log_error($TAG." 9",$timeEAT,mysql_error(),$query);
		}
	}
	echo "ok";
	
}
else
{
	log_error($TAG,$timeEAT,"unable to open settings.ini");
}
?>
