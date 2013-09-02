<?php
class CowEventsHandler {
	
	private $settings;
	private $codes;
	private $jsonObject;
	
	public function __construct() {
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->connectToDatabase();
		
		$simCardSN = $this->jsonObject['simCardSN'];
		$fromID = $this->jsonObject['fromID'];//from which cow_events ID this object should start fetching
		if($fromID == -1) {
			$query = "SELECT `cow`.`name` AS `cow_name`, `cow`.`ear_tag_number` , `cow_event`.*, `event`.`name` AS `event_name` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` INNER JOIN `cow_event` ON `cow`.`id` = `cow_event`.`cow_id` INNER JOIN `event` ON `cow_event`.`event_id` = `event`.`id` WHERE  `farmer`.`sim_card_sn` = '{$simCardSN}' ORDER BY `cow_event`.`id` DESC LIMIT 40";
		}
		else {
			$query = "SELECT `cow`.`name` AS `cow_name`, `cow`.`ear_tag_number` , `cow_event`.*, `event`.`name` AS `event_name` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` INNER JOIN `cow_event` ON `cow`.`id` = `cow_event`.`cow_id` INNER JOIN `event` ON `cow_event`.`event_id` = `event`.`id` WHERE  `farmer`.`sim_card_sn` = '{$simCardSN}' AND `cow_event`.`id` < {$fromID}  ORDER BY `cow_event`.`id` DESC LIMIT 40";
		}
		
		$result = $this->runMySQLQuery($query, true);
		if(sizeof($result) == 0) {
			echo $this->codes['no_data'];
		}
		else {
			$jsonArray=array();
			$jsonArray['history']=$result;
			echo json_encode($jsonArray);
		}
	}
	
	private function getTime($timeZone) {
		$time = new DateTime('now', new DateTimeZone($timeZone));
		return $time->format('Y-m-d H:i:s');
	}

	private function getSettings() {
      if(file_exists("../settings.ini")) {
         $settings = parse_ini_file("../settings.ini");
         $mysqlCreds = parse_ini_file($settings['mysql_creds']);
         $settings['mysql_creds'] = $mysqlCreds;
         $this->settings = $settings;
      }
      else {
         die();
      }
   }

   private function getCodes() {
	   if(file_exists("../codes.ini")) {
		   $this->codes = parse_ini_file("../codes.ini");
	   }
   }
   
    private function connectToDatabase() {
		if($this->settings != null) {
         $mysqlCreds = $this->settings['mysql_creds'];
         mysql_connect($mysqlCreds['host'], $mysqlCreds['username'], $mysqlCreds['password']) or die( mysql_error());
         mysql_select_db($this->settings['db_name']) or die(mysql_error());
      }
   }
   
   private function runMySQLQuery($query, $getResult) {
      $result = mysql_query($query) or die (mysql_error());
      
      if($getResult == true) {
         $resultArray = array();
         $count = 0;
         while($fetchedRow = mysql_fetch_assoc($result)) {
            $resultArray[$count] = $fetchedRow;
            $count++;
         }
         return $resultArray;
      }
   }
   
    private function getPOSTJsonObject() {
	   $this->jsonObject=json_decode($_POST["json"],true);
	}
}

$obj = new CowEventsHandler;
?>
