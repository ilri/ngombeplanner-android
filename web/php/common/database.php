<?php

class DatabaseHandler {

	private $TAG = "database.php";
	private $ROOT = "../../";
	private $settingsDir;
	private $settings;
	private $codes;
	private $logHandler;
	
	public function __construct() {
		$this->settingsDir = $this->ROOT."config/settings.ini";
		$this->logHandler = new LogHandler;
		$this->logHandler->log(3, $this->TAG,"Starting DatabaseHandler");
		$this->getSettings();
		$this->getCodes();
		$this->connectToDatabase();
	}

	private function getTime($timeZone) {
		$time = new DateTime('now', new DateTimeZone($timeZone));
		return $time->format('Y-m-d H:i:s');
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
   
    private function connectToDatabase() {
		if($this->settings != null) {
         $mysqlCreds = $this->settings['mysql_creds'];
		 $this->logHandler->log(3, $this->TAG,"connecting to ".$this->settings['db_name']." on ".$mysqlCreds['host']." using ".$mysqlCreds['username']." as the username");
         mysql_connect($mysqlCreds['host'], $mysqlCreds['username'], $mysqlCreds['password']);
         $mysqlError = mysql_error();
         if($mysqlError!="") {
			 $this->logHandler->log(1, $this->TAG,"something went wrong while trying to connect to MySQL. '".$mysqlError."', exiting");
			 die();
		 }
		 
         mysql_select_db($this->settings['db_name']);
         $mysqlError = mysql_error();
         if($mysqlError!="") {
			 $this->logHandler->log(1, $this->TAG,"something went wrong while trying to select ".$this->settings['db_name']." as the database. '".$mysqlError."', exiting");
			 die();
		 }
      }
   }
   
   public function runMySQLQuery($query, $getResult, $errorResponse = "") {
	  $this->logHandler->log(3, $this->TAG,"running query ".$query);
      $result = mysql_query($query) ;
      $mysqlError = mysql_error();
      if($mysqlError != "") {
		  $this->logHandler->log(1, $this->TAG,"MySQL error '".$mysqlError."' thrown while trying to run query '".$query."', exiting");
			 die($errorResponse);
	  }
      
      if($getResult == true) {
         $resultArray = array();
         $count = 0;
         while($fetchedRow = mysql_fetch_assoc($result)) {
            $resultArray[$count] = $fetchedRow;
            $count++;
         }
         $this->logHandler->log(4, $this->TAG,"results gotten from '".$query."' are ".print_r($resultArray, true));
         if(sizeOf($resultArray) == 0){
			 $this->logHandler->log(2, $this->TAG,"no results gotten for  '".$query."'");
		 }
         return $resultArray;
      }
   }
   
   public function getLastInsertID(){
       return mysql_insert_id();
   }
}
?>
