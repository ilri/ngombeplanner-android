<?php
include '../common/log.php';
include '../common/database.php';

class SimRegistrationHandler {

	private $TAG = "sim_card_registration.php";
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
		$this->logHandler->log(3, $this->TAG,"Starting SimRegistrationHandler");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
		$query="SELECT `sim_card_sn` FROM `farmer` WHERE `mobile_no` = '{$this->jsonObject['oldMobileNumber']}'";
		$result = $this->database->runMySQLQuery($query, true);
		if(sizeOf($result) == 1) {
			$query="UPDATE `farmer` SET `mobile_no`='{$this->jsonObject['newMobileNumber']}', `sim_card_sn`='{$this->jsonObject['newSimCardSN']}' WHERE `mobile_no` = '{$this->jsonObject['oldMobileNumber']}'";
			$this->database->runMySQLQuery($query, false);
			$this->logHandler->log(3, $this->TAG,"successfully registered the new SIM card serial number (".$this->jsonObject['newSimCardSN']."). Sending the sim_card_registered response code back to the client");
			echo $this->codes['sim_card_registered'];
		}
		else {
			$this->logHandler->log(2, $this->TAG,"SIM card with mobile number as ".$this->jsonObject['oldMobileNumber']." is not registered in the database. Sending the user_not_authenticated response code back to the client");
			echo $this->codes['user_not_authenticated'];
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
$obj = new SimRegistrationHandler;
?>
