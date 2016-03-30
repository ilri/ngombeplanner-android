<?php
include '../common/log.php';
include '../common/database.php';

class MilkProductionHistoryHandler {
	
	private $TAG = "fetch_milk_production_history.php";
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
		$this->logHandler->log(3, $this->TAG,"Starting MilkProductionHistoryHandler");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
		
		if($this->jsonObject['fromID']==-1) {
			$query="SELECT `milk_production`.*,`cow`.`name`,`cow`.`ear_tag_number` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` INNER JOIN `milk_production` ON `cow`.`id`=`milk_production`.`cow_id` WHERE `farmer`.`sim_card_sn`='{$this->jsonObject['simCardSN']}' AND `milk_production`.`date` >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ORDER BY `milk_production`.`date` DESC";
		}
		else {
			$query="SELECT `milk_production`.*,`cow`.`name`,`cow`.`ear_tag_number` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` INNER JOIN `milk_production` ON `cow`.`id`=`milk_production`.`cow_id` WHERE `farmer`.`sim_card_sn`='{$this->jsonObject['simCardSN']}' AND `milk_production`.`id`<{$this->jsonObject['fromID']} AND `milk_production`.`date` >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) ORDER BY `milk_production`.`date` DESC";
		}
		$result = $this->database->runMySQLQuery($query, true);
		if(sizeof($result) == 0) {
			$this->logHandler->log(3, $this->TAG,"sending the no_data response code to client");
			echo $codes['no_data'];
		}
		else {
			$jsonArray=array();
			$jsonArray['history']=$result;
			$this->logHandler->log(3, $this->TAG,"sending milk production history data back to client as a json string");
			$jsonString = json_encode($jsonArray);
			$this->logHandler->log(4, $this->TAG," cow milk production history encoded from an array and is '".$jsonString."'");
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

$obj = new MilkProductionHistoryHandler;
?>
