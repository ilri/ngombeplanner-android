<?php
include '../common/log.php';
include '../common/database.php';

class MilkProductionAdder {

	private $TAG = "add_milk_production.php";
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
		$this->logHandler->log(3, $this->TAG,"Starting MilkProductionAdder");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
		//get cow ID
		$cowName = $this->jsonObject['cowName'];
                $earTagNumber = $this->jsonObject['cowEarTagNumber'];
                if(isset($this->jsonObject['simCardSN'])){
                    $simCardSN = $this->jsonObject['simCardSN'];
                    $query = "SELECT cow.`id` FROM farmer INNER JOIN `cow` ON farmer.id = cow.`farmer_id` WHERE cow.`ear_tag_number` = '{$earTagNumber}' AND cow.`name` = '{$cowName}' AND farmer.`sim_card_sn` = '{$simCardSN}'";
                }
                else if(isset ($this->jsonObject['mobile_no'])){
                    $mobileNumber = $this->jsonObject['mobile_no'];
                    $query = "SELECT cow.`id` FROM farmer INNER JOIN `cow` ON farmer.id = cow.`farmer_id` WHERE cow.`ear_tag_number` = '{$earTagNumber}' AND cow.`name` = '{$cowName}' AND farmer.`mobile_no` = '{$mobileNumber}'";
                }
		
		$result = $this->database->runMySQLQuery($query, true);
		if(sizeOf($result) == 1) { //only one cow ID should have been fetched
			$cowID = $result[0]['id'];
			$this->logHandler->log(4, $this->TAG,"cow.id fetched and is ".$cowID);
			$dateEAT = $this->jsonObject['date'];
			
			//check if there is a combined record for date
			$query = "SELECT `time` FROM `milk_production` WHERE `cow_id` = {$cowID} AND `date` = STR_TO_DATE('{$dateEAT}', '%d/%m/%Y')";
			$result = $this->database->runMySQLQuery($query, true);
			$dataThereFlag = false;
			if(sizeOf($result) > 0 && $this->jsonObject['time'] == "Combined") {
				$dataThereFlag = true;
			}
			else {
				for($i = 0; $i < sizeOf($result) ; $i++){
					if($result[$i]["time"] == "Combined") {
						$dataThereFlag = true;
					}
				}
			}
			if($dataThereFlag == false) {
				$timeEAT = $this->getTime('Y-m-d H:i:s');
				$query = "INSERT INTO `milk_production`(`cow_id`,`time`,`quantity`, `quantity_type`,`date`,`date_added`) VALUES({$cowID},'{$this->jsonObject['time']}',{$this->jsonObject['quantity']},'{$this->jsonObject['quantityType']}',STR_TO_DATE('{$dateEAT}', '%d/%m/%Y'),'$timeEAT')";
				$this->database->runMySQLQuery($query, false, $this->codes['data_error']);
				$this->logHandler->log(3, $this->TAG,"returning response code ".$this->codes['acknowledge_ok']);
				echo $this->codes['acknowledge_ok'];
				$this->logHandler->log(3, $this->TAG,"gracefully exiting");
			}
			else {
				$this->logHandler->log(1, $this->TAG,"a record already exists in the database with combined data for the date or the user is trying to enter combilned data when there is data record for this date, exiting");
				die($this->codes['data_error']);
			}
		}
		else {
			$this->logHandler->log(1, $this->TAG,"it appears like there is no cow by the name '".$cowName."' and '".$earTagNumber."' as its ear tag number or more than one cow goes by these credentials, exiting");
			die();
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
$obj = new MilkProductionAdder;
?>
