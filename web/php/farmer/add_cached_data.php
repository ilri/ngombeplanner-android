<?php
include '../common/log.php';
include '../common/database.php';

class CachedDataSaver{
	
	private $TAG = "add_cached_data.php";
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
        $this->logHandler->log(3, $this->TAG,"Starting CachedDataSaver");
        $this->getPOSTJsonObject();
        $this->getSettings();
        $this->getCodes();
        $this->database = new DatabaseHandler;
		$jsonArray = $this->jsonObject['pastRequests'];
		for($index = 0; $index < count($jsonArray); $index++){
			$currRequest = $jsonArray[$index];
			$response = $this->sendJsonString($currRequest['requestURL'], json_encode($currRequest['requestData']));
		}
		
		$simCardSN=$this->jsonObject['simCardSN'];
        $query="SELECT * FROM `farmer` WHERE `sim_card_sn`='{$simCardSN}'";
		$result = $this->database->runMySQLQuery($query, true);
		if(sizeOf($result) == 1) {
			$farmer = $result[0];
			$this->logHandler->log(3, $this->TAG,"the SIM card serial number being used (".$simCardSN.") is authenticated for '".$farmer['name']."'. Sending farmer's name back to client");

			$query  = "SELECT * FROM cow WHERE farmer_id = {$farmer['id']}";
			$cows = $this->database->runMySQLQuery($query, true);
			if(is_array($cows)){
				for($cowIndex = 0; $cowIndex < count($cows); $cowIndex++){
					//get events
					$query = "SELECT a.*, b.name AS event_name, c.name as cause_of_death FROM cow_event AS a INNER JOIN event AS b ON a.event_id = b.id LEFT JOIN cause_of_death AS c ON a.cod_id = c.id WHERE a.cow_id = ".$cows[$cowIndex]['id'];
					$cows[$cowIndex]['events'] = $this->database->runMySQLQuery($query, true);
					
					//get milking data
                    $query = "SELECT a.* FROM milk_production AS a WHERE a.cow_id = ".$cows[$cowIndex]['id'];
                    $cows[$cowIndex]['milk_production'] = $this->database->runMySQLQuery($query, true);
				}
			}
			else{
				$cows = array();
			}
			$farmer['cows'] = $cows;
			echo json_encode($farmer);

		}
		else {
			$this->logHandler->log(2, $this->TAG,"SIM card serial number (".$simCardSN.") not matching any farmer. Sending user_not_authenticated response code back to client");
				echo $this->codes['user_not_authenticated'];
		}
		$this->logHandler->log(3, $this->TAG,"gracefully exiting");
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
		$this->logHandler->log(3, $this->TAG, "Data from client = ".print_r($this->jsonObject, true));
		$this->logHandler->log(4, $this->TAG,"json_decode returned: ".print_r($this->jsonObject, true));
	}

	private function sendJsonString($uri, $jsonString){
		$url = $this->settings['base_url'].$uri;
		$this->logHandler->log(3, $this->TAG,"using curl to send data to ".$url);
		$postFields = array("json"=>  urlencode($jsonString));

		//url-ify the post data
		foreach ($postFields as $key=>$value){
			$postDataString .= $key.'='.$value.'&';
		}
		rtrim($postDataString, '&');

		//open curl connection
		$ch = curl_init();
	
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_POST, count($postFields));
		curl_setopt($ch, CURLOPT_POSTFIELDS, $postDataString);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);//return web page
		curl_setopt($ch, CURLOPT_HEADER, FALSE);//dont return headers
	
		//execute post
		$result = curl_exec($ch);
		curl_close($ch);
	
		$this->logHandler->log(3, $this->TAG,"response gotten from ".$url);
		$this->logHandler->log(3, $this->TAG,"server's response is  ".$result);
		return $result;
   	}
}
$obj = new CachedDataSaver;
?>
