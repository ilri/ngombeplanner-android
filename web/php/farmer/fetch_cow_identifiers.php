<?php
include '../common/log.php';
include '../common/database.php';

class CowIdentifierFetcher {

	private $TAG = "fetch_cow_identifiers.php";
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
		$this->logHandler->log(3, $this->TAG,"Starting CowIdentifierFetcher");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
		$simCardSN = $this->jsonObject['simCardSN'];
		if(array_key_exists("cowSex",$this->jsonObject)) {
			$this->logHandler->log(4, $this->TAG,"CowSex specified as ".$this->jsonObject['cowSex']);
			$query="SELECT `cow`.`name`,`cow`.`ear_tag_number` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id`=`cow`.`farmer_id` WHERE `farmer`.`sim_card_sn`='{$simCardSN}' AND `cow`.`sex` = '{$this->jsonObject['cowSex']}'";
		}
		else {
			$this->logHandler->log(4, $this->TAG,"CowSex not specified");
			$query="SELECT `cow`.`name`,`cow`.`ear_tag_number`,`cow`.`sex` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id`=`cow`.`farmer_id` WHERE `farmer`.`sim_card_sn`='{$simCardSN}'";
		}
		$result = $this->database->runMySQLQuery($query, true);
		$cowNameArray=array();
		$earTagNumberArray=array();
                $sexArray=  array();
		//$index=0;
		for($i = 0; $i<sizeOf($result); $i++) {
			$cowNameArray[$i]=$result[$i]['name'];
			$earTagNumberArray[$i]=$result[$i]['ear_tag_number'];
                        $sexArray[$i]=$result[$i]['sex'];
		}
		$this->logHandler->log(3, $this->TAG,"sending cow identifiers back to client as a json string");
		$jsonArray=array();
		$jsonArray['cowNames']=$cowNameArray;
		$jsonArray['earTagNumbers']=$earTagNumberArray;
                $jsonArray['sex']=$sexArray;
		$jsonString = json_encode($jsonArray);
		$this->logHandler->log(4, $this->TAG," cow identifiers encoded from an array and is '".$jsonString."'");
		echo $jsonString;
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
$obj = new CowIdentifierFetcher;
?>
