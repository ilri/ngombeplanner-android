<?php
include '../common/log.php';
include '../common/database.php';

class Authenticator {

	private $TAG = "authentication.php";
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
		$this->logHandler->log(3, $this->TAG,"Starting Authenticator");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
                if(isset($this->jsonObject['simCardSN'])){
                    $simCardSN=$this->jsonObject['simCardSN'];
                    $query="SELECT `name` FROM `farmer` WHERE `sim_card_sn`='{$simCardSN}'";
                    $result = $this->database->runMySQLQuery($query, true);
                    if(sizeOf($result) == 1) {
                            $farmerName = $result[0]['name'];
                            $this->logHandler->log(3, $this->TAG,"the SIM card serial number being used (".$simCardSN.") is authenticated for '".$farmerName."'. Sending farmer's name back to client");
                            echo $farmerName;
                    }
                    else {
                            $this->logHandler->log(2, $this->TAG,"SIM card serial number (".$simCardSN.") not matching any farmer. Sending user_not_authenticated response code back to client");
                            echo $this->codes['user_not_authenticated'];
                    }
                    $this->logHandler->log(3, $this->TAG,"gracefully exiting");                     
                }
                else if(isset ($this->jsonObject['mobileNumber'])){
                    $mobileNumber = $this->jsonObject['mobileNumber'];
                    $query = "SELECT * FROM farmer WHERE `mobile_no` = '{$mobileNumber}'";
                    $result = $this->database->runMySQLQuery($query, true);
                    if(sizeOf($result) == 1) {
                            $farmer = $result[0];
                            $this->logHandler->log(3, $this->TAG,"the mobile number being used (".$mobileNumber.") is authenticated for '".$farmer['name']."'. Sending farmer's name back to client");
                            $query  = "SELECT * FROM cow WHERE farmer_id = {$farmer['id']}";
                            $cows = $this->database->runMySQLQuery($query, true);
                            for ($index = 0; $index < count($cows); $index++) {
                                $cow = $cows[$index];
                                
                                $query = "SELECT id, time, quantity, quantity_type, DATE_FORMAT(date, \"%d/%l/%Y\") AS date FROM `milk_production` WHERE `cow_id` = {$cow['id']} ORDER BY id DESC";
                                $milk_production = $this->database->runMySQLQuery($query, true);
                                $cow['milk_production'] = $milk_production;
                                
                                $cows[$index] = $cow;
                            }
                            $farmer['cows'] = $cows;
                            echo json_encode($farmer);
                    }
                    else {
                            $this->logHandler->log(2, $this->TAG,"The mobile number (".$mobileNumber.") not matching any farmer. Sending user_not_authenticated response code back to client");
                            echo $this->codes['user_not_authenticated'];
                    }
                    $this->logHandler->log(3, $this->TAG,"gracefully exiting");
                }
		
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
$obj = new Authenticator;
?>
