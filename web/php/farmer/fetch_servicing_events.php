<?php
include '../common/log.php';
include '../common/database.php';

class CowServicingEventsHandler {
	
	private $TAG = "fetch_servicing_events_history.php";
	private $ROOT = "../../";
	private $settingsDir;
	private $settings;
	private $codes;
	private $jsonObject;
	private $logHandler;
	private $database;
	
	public function __construct() {
		$this->settingsDir = $this->ROOT."config/settings.ini";
		$this->logHandler = new LogHandler;
		$this->logHandler->log(3, $this->TAG,"Starting CowEventsHandler");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
		$simCardSN = $this->jsonObject['simCardSN'];
		$query = "SELECT `cow`.`name` AS `cow_name`, `cow`.`ear_tag_number` , `cow_event`.*, `event`.`name` AS `event_name`, `vet`.`name` AS `vet_name`".
                        " FROM `farmer`".
                        " INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id`".
                        " INNER JOIN `cow_event` ON `cow`.`id` = `cow_event`.`cow_id`".
                        " INNER JOIN `event` ON `cow_event`.`event_id` = `event`.`id`".
                        " INNER JOIN `vet` ON `cow_event`.`vet_id` = `vet`.`id`".
                        " WHERE  `farmer`.`sim_card_sn` = '{$simCardSN}' AND (`event`.`name` = 'Artificial Insemination' OR `event`.`name` = 'Bull Servicing')";
		
		$result = $this->database->runMySQLQuery($query, true);
		if(sizeof($result) == 0) {
			$this->logHandler->log(3, $this->TAG,"sending the no_data response code to client");
			echo $this->codes['no_data'];
		}
		else {
			$jsonArray=array();
			$jsonArray['history']=$result;
			$this->logHandler->log(3, $this->TAG,"sending cow events history data back to client as a json string");
			$jsonString = json_encode($jsonArray);
			$this->logHandler->log(4, $this->TAG," cow events history encoded from an array and is '".$jsonString."'");
			echo $jsonString;
		}
		$this->logHandler->log(3, $this->TAG,"gracefully exiting");
	}
	
	private function getTime($format) {
		$timeZone = $this->settings['time_zone'];
		$time = new DateTime('now', new DateTimeZone($timeZone));
		$formatedTime = $time->format($format);
		$this->logHandler->log(4, $this->TAG,"returning time '".$formatedTime."' using format '".$format."'");
		return $formatedTime;
	}

	private function getSettings() {
		$this->logHandler->log(3, $this->TAG,"getting settings from: ".$this->settingsDir);
      if(file_exists($this->settingsDir)) {
         $settings = parse_ini_file($this->settingsDir);
         $mysqlCreds = parse_ini_file($settings['mysql_creds']);
         $settings['mysql_creds'] = $mysqlCreds;
         $this->settings = $settings;
         $this->logHandler->log(4, $this->TAG,"settings obtained: ".print_r($this->settings, true));
      }
      else {
		  $this->logHandler->log(1, $this->TAG,"unable to get settings from ".$this->settingsDir.", exiting");
         die();
      }
   }

   private function getCodes() {
	   $responseCodesLocation = $this->ROOT."config/".$this->settings['response_codes'];
	   $this->logHandler->log(3, $this->TAG,"getting response codes from: ".$responseCodesLocation);
	   if(file_exists($responseCodesLocation)) {
		   $this->codes = parse_ini_file($responseCodesLocation);
		   $this->logHandler->log(4, $this->TAG,"response codes are: ".print_r($this->codes, true));
	   }
	   else {
		  $this->logHandler->log(1, $this->TAG,"unable to get response codes from ".$responseCodesLocation.", exiting");
         die();
      }
   }
   
    private function getPOSTJsonObject() {
		$this->logHandler->log(3, $this->TAG,"obtaining POST request");
		$this->jsonObject=json_decode($_POST["json"],true);
		$this->logHandler->log(4, $this->TAG,"json_decode returned: ".print_r($this->jsonObject, true));
	}
}

$obj = new CowServicingEventsHandler;
?>