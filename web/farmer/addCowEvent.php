<?php
class CowEventHandler {

	private $settings;
	private $codes;
	private $jsonObject;
	public function __construct() {
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->connectToDatabase();
		
		//get the cowID
		$simCardSN = $this->jsonObject['simCardSN'];
		$cowEarTagNumber = $this->jsonObject['cowEarTagNumber'];
		$cowName = $this->jsonObject['cowName'];
		$query = "SELECT `cow`.`id` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` WHERE `farmer`.`sim_card_sn` = '{$simCardSN}' AND `cow`.`name` = '{$cowName}' AND `cow`.`ear_tag_number` = '{$cowEarTagNumber}'";
		$result = $this->runMySQLQuery($query, true);
		$cowID = $result[0]['id'];
		
		//add event to database
		$eventTypeID = $this->getEventTypeID();
		$eventDate = $this->jsonObject['date'];
		$remarks = $this->jsonObject['remarks'];
		$time = $this->getTime("EAT");
		$query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}')";
		$this->runMySQLQuery($query, false);
		echo $this->codes['acknowledge_ok'];
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
   
   private function getEventTypeID() {
	   $eventTypeName = $this->jsonObject['eventType'];
	   $query = "SELECT `id` FROM `event` WHERE `name` = '{$eventTypeName}'";
	   $result = $this->runMySQLQuery($query, true);
	   if(sizeOf($result)==1) {
		   return $result[0]['id'];
	   }
	   else {
		   die();
	   }
   }
   
   private function getPOSTJsonObject() {
	   $this->jsonObject=json_decode($_POST["json"],true);
	}
}
$obj = new CowEventHandler;
?>
