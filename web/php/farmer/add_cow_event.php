<?php
include '../common/log.php';
include '../common/database.php';
include 'general.php';

class CowEventHandler {

	private $TAG = "add_cow_event.php";
	private $ROOT = "../../";
	private $settingsDir;
	private $settings;
	private $codes;
	private $jsonObject;
	private $logHandler;
	private $database;
        private $general;
	
	public function __construct() {
		$this->settingsDir = $this->ROOT."config/settings.ini";
		$this->logHandler = new LogHandler;
		$this->logHandler->log(3, $this->TAG,"Starting CowEventHandler");
		$this->getPOSTJsonObject();
		$this->getSettings();
		$this->getCodes();
		$this->database = new DatabaseHandler;
                $this->general = new General();
		
		//get the cowID
                $cowEarTagNumber = $this->jsonObject['cowEarTagNumber'];
		$cowName = $this->jsonObject['cowName'];
                if(isset($this->jsonObject['simCardSN'])){
                    $simCardSN = $this->jsonObject['simCardSN'];
                    $query = "SELECT `cow`.`id`,`cow`.`farmer_id` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` WHERE `farmer`.`sim_card_sn` = '{$simCardSN}' AND `cow`.`name` = '{$cowName}' AND `cow`.`ear_tag_number` = '{$cowEarTagNumber}'";
                }
                else if(isset($this->jsonObject['mobileNo'])){
                    $mobileNo = $this->jsonObject['mobileNo'];
                    $query = "SELECT `cow`.`id`,`cow`.`farmer_id` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` WHERE `farmer`.`mobile_no` = '{$mobileNo}' AND `cow`.`name` = '{$cowName}' AND `cow`.`ear_tag_number` = '{$cowEarTagNumber}'";
                }
		
		$result = $this->database->runMySQLQuery($query, true);
		$cowID = $result[0]['id'];
                $farmerID = $result[0]['farmer_id'];
		
		//add event to database
		$eventTypeID = $this->getEventTypeID();
		$eventDate = $this->jsonObject['date'];
		$remarks = $this->jsonObject['remarks'];
		$time = $this->getTime("EAT");
                if($this->jsonObject['eventType'] == "Abortion") {
                   //TODO: remember to add parent_cow_event
                   $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`,`parent_cow_event`)".
                           " VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',{$this->jsonObject['parentEvent']})";
                }
                else if($this->jsonObject['eventType'] == "Artificial Insemination") {
                    /*
                     * 1. check if ear_tag_number is null
                     * 2. if eartagnumber is not null check if bull already exists 
                     * 3. if bull does not exist insert bull into database
                     */
                   if(isset($this->jsonObject['strawNumber']) && $this->jsonObject['strawNumber']!== ""){
                       $strawID = $this->general->getStrawID($this->jsonObject['strawNumber'], true);
                   }
                   else{
                       $strawID = -1;
                   }
                   if(isset($this->jsonObject['vetUsed']) && $this->jsonObject['vetUsed'] !== ""){
                       $vetID = $this->general->getVetID($this->jsonObject['vetUsed'],true);
                   }
                   else{
                       $vetID = -1;
                   }
                       
                       
                   if(isset($this->jsonObject['bullEarTagNo']) && $this->jsonObject['bullEarTagNo'] !== ""){
                       $this->logHandler->log(3, $this->TAG,"checking if bull in db");
                       $bullID = $this->general->getCowID($farmerID,$this->jsonObject['bullEarTagNo']);
                       if($bullID === -1){
                           $query = "INSERT INTO cow(`name`,`ear_tag_number`,`farmer_id`,`sex`) VALUES('{$this->jsonObject['bullName']}','{$this->jsonObject['bullEarTagNo']}',{$farmerID},'Male')";
                           $result = $this->database->runMySQLQuery($query, true);
                           $bullID = $this->database->getLastInsertID();
                           
                       }
                       if($strawID !== -1){
                           $query = "UPDATE `straw` SET `sire_id` = $bullID WHERE id = $strawID";
                           $this->database->runMySQLQuery($query, false);
                       }
                   }
                   if($strawID === -1) $strawID = "NULL";
                   if($vetID === -1) $vetID = "NULL";
                   $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`event_date`,`date_added`,`straw_id`,`vet_id`)".
                           " VALUES({$cowID},{$eventTypeID},STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',{$strawID},{$vetID})";
                }
                else if($this->jsonObject['eventType'] == "Bull Servicing") {
                   $bullID = $this->general->getCowID($farmerID,$this->jsonObject['bullEarTagNo']);
                   //check if the bull already exists
                   if($bullID === -1) {
                      $query = "INSERT INTO cow(`name`,`ear_tag_number`,`farmer_id`,`sex`) VALUES('{$this->jsonObject['bullName']}','{$this->jsonObject['bullEarTagNo']}',{$farmerID},'Male')";
                      $result = $this->database->runMySQLQuery($query, true);
                      $bullID = $this->database->getLastInsertID();
                   }
                   $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`event_date`,`date_added`,`bull_id`)".
                           " VALUES({$cowID},{$eventTypeID},STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',$bullID)";
                   
                }
                else if($this->jsonObject['eventType'] == "Death") {
                   $causeOfDeathID = $this->general->getCODID($this->jsonObject['causeOfDeath']);
                   if($causeOfDeathID!= -1) {
                      $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`,`cod_id`)" .
                           " VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',$causeOfDeathID)";
                   }
                }
                else {
                   $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}')";
                }
		
		$this->database->runMySQLQuery($query, false);
		$this->logHandler->log(3, $this->TAG,"returning response code ".$this->codes['acknowledge_ok']);
		echo $this->codes['acknowledge_ok'];
		$this->logHandler->log(3, $this->TAG,"gracefully exiting");
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
   
   private function getEventTypeID() {
	   $eventTypeName = $this->jsonObject['eventType'];
	   $query = "SELECT `id` FROM `event` WHERE `name` = '{$eventTypeName}'";
	   $result = $this->database->runMySQLQuery($query, true);
	   if(sizeOf($result)==1) {
		   $this->logHandler->log(4, $this->TAG, "fetched event type ID for ".$eventTypeName." which is ".$result[0]['id']);
		   return $result[0]['id'];
	   }
	   else {
		   $this->logHandler->log(1, $this->TAG, "it appears like there is no event type by the name '".$eventTypeName."' or more than one event type goes by this name, exiting");
		   die();
	   }
   }
   
   private function getPOSTJsonObject() {
	   $this->logHandler->log(3, $this->TAG,"obtaining POST request");
	   $this->jsonObject=json_decode($_POST["json"],true);
	   $this->logHandler->log(4, $this->TAG,"json_decode returned: ".print_r($this->jsonObject, true));
	}
}
$obj = new CowEventHandler;
?>
