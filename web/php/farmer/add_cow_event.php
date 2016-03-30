<?php

include '../common/log.php';
include '../common/database.php';
include '../common/alerts.php';
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
   private $alertHandler;
   private $appUsed;

   public function __construct() {
      $this->settingsDir = $this->ROOT . "config/settings.ini";
      $this->logHandler = new LogHandler;
      $this->logHandler->log(3, $this->TAG, "Starting CowEventHandler");
      $this->getPOSTJsonObject();
      $this->getSettings();
      $this->getCodes();
      $this->database = new DatabaseHandler;
      $this->general = new General();
      $this->alertHandler = new AlertHandler($this->ROOT, $this->database, $this->logHandler);

      //get the cowID
      $cowEarTagNumber = $this->jsonObject['cowEarTagNumber'];
      $cowName = $this->jsonObject['cowName'];
      if (isset($this->jsonObject['simCardSN'])) {
         $this->appUsed = "Android";
         $simCardSN = $this->jsonObject['simCardSN'];
         $query = "SELECT `cow`.`id`,`cow`.`farmer_id`,`farmer`.`mobile_no` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` WHERE `farmer`.`sim_card_sn` = '{$simCardSN}' AND `cow`.`name` = '{$cowName}' AND `cow`.`ear_tag_number` = '{$cowEarTagNumber}'";
      } else if (isset($this->jsonObject['mobileNo'])) {
         $this->appUsed = "JavaME";
         $mobileNo = $this->jsonObject['mobileNo'];
         $query = "SELECT `cow`.`id`,`cow`.`farmer_id`,`farmer`.`mobile_no` FROM `farmer` INNER JOIN `cow` ON `farmer`.`id` = `cow`.`farmer_id` WHERE `farmer`.`mobile_no` = '{$mobileNo}' AND `cow`.`name` = '{$cowName}' AND `cow`.`ear_tag_number` = '{$cowEarTagNumber}'";
      }

      $result = $this->database->runMySQLQuery($query, true);
      $cowID = $result[0]['id'];
      $farmerID = $result[0]['farmer_id'];
      $numberUsed = $result[0]['mobile_no'];

      //add event to database
      $eventTypeID = $this->getEventTypeID();
      $eventDate = $this->jsonObject['date'];
      $remarks = $this->jsonObject['remarks'];
      $time = $this->getTime("EAT");
      if ($this->jsonObject['eventType'] == "Abortion") {
         //TODO: remember to add parent_cow_event
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`,`parent_cow_event`, `app_used`, `no_used`)" .
                 " VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',{$this->jsonObject['parentEvent']}, '{$this->appUsed}', '{$numberUsed}')";
      } else if ($this->jsonObject['eventType'] == "Birth") {
         $this->logHandler->log(3, $this->TAG, "Event type is CALVING");
         $eventTypeID = $this->general->getEventID("Calving");
         
         if($this->jsonObject['birthType'] == "Still" || $this->jsonObject['birthType'] == "Abortion"){
            $this->alertHandler->sendMiscarriageAlert($cowID);
         }
         
         $query = "UPDATE cow SET milking_status = 'adult_milking', in_calf = 0 WHERE id = {$cowID}";
         $this->database->runMySQLQuery($query, false);
         
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`event_date`,`date_added`,`birth_type`, `app_used`, `no_used`)" .
                 " VALUES({$cowID},{$eventTypeID},STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}','{$this->jsonObject['birthType']}', '{$this->appUsed}', '{$numberUsed}')";
      } else if ($this->jsonObject['eventType'] == "Artificial Insemination") {
         /*
          * 1. check if ear_tag_number is null
          * 2. if eartagnumber is not null check if bull already exists 
          * 3. if bull does not exist insert bull into database
          */
         if (isset($this->jsonObject['strawNumber']) && $this->jsonObject['strawNumber'] !== "") {
            $strawID = $this->general->getStrawID($this->jsonObject['strawNumber'], true);
         } else {
            $strawID = -1;
         }
         if (isset($this->jsonObject['vetUsed']) && $this->jsonObject['vetUsed'] !== "") {
            $vetID = $this->general->getVetID($this->jsonObject['vetUsed'], true);
         } else {
            $vetID = -1;
         }


         if (isset($this->jsonObject['bullEarTagNo']) && $this->jsonObject['bullEarTagNo'] !== "") {
            $this->logHandler->log(3, $this->TAG, "checking if bull in db");
            $bullID = $this->general->getCowID($farmerID, $this->jsonObject['bullEarTagNo']);
            if ($bullID === -1) {
               $query = "INSERT INTO cow(`ear_tag_number`,`name`,`farmer_id`,`sex`) VALUES('{$this->jsonObject['bullEarTagNo']}','',{$farmerID},'Male')";
               $result = $this->database->runMySQLQuery($query, true);
               $bullID = $this->database->getLastInsertID();
            }
            if ($strawID !== -1) {
               $query = "UPDATE `straw` SET `sire_id` = $bullID WHERE id = $strawID";
               $this->database->runMySQLQuery($query, false);
            }
         }
         if ($strawID === -1)
            $strawID = "NULL";
         if ($vetID === -1)
            $vetID = "NULL";
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`event_date`,`date_added`,`straw_id`,`vet_id`, `app_used`, `no_used`)" .
                 " VALUES({$cowID},{$eventTypeID},STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',{$strawID},{$vetID}, '{$this->appUsed}', '{$numberUsed}')";
      }
      else if ($this->jsonObject['eventType'] == "Bull Servicing") {
         $bullID = $this->general->getCowID($farmerID, $this->jsonObject['bullEarTagNo'], "");//user provided with one input for both bull's ear tag number and name. In such a case we enter value in ear_tag_number
         //check if the bull already exists
         if ($bullID === -1) {
            $query = "INSERT INTO cow(`ear_tag_number`,`name`,`farmer_id`,`sex`, `bull_owner`, `owner_name`) VALUES('{$this->jsonObject['bullEarTagNo']}','',{$farmerID},'Male', '{$this->jsonObject['bullOwner']}', '{$this->jsonObject['bullOwnerName']}')";
            $result = $this->database->runMySQLQuery($query, true);
            $bullID = $this->database->getLastInsertID();
         }
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`event_date`,`date_added`,`bull_id`, `app_used`, `no_used`)" .
                 " VALUES({$cowID},{$eventTypeID},STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',$bullID, '{$this->appUsed}', '{$numberUsed}')";
      } 
      else if ($this->jsonObject['eventType'] == "Death") {
         $causeOfDeathID = $this->general->getCODID($this->jsonObject['causeOfDeath']);
         if ($causeOfDeathID != -1) {
            $query = "UPDATE cow SET old_farmer_id = farmer_id, farmer_id = null WHERE id = {$cowID}";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->alertHandler->sendDeathAlert($cowID);

            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`,`cod_id`, `app_used`, `no_used`)" .
                    " VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}',$causeOfDeathID, '{$this->appUsed}', '{$numberUsed}')";
         }
      } 
      else if ($this->jsonObject['eventType'] == "Sale") {
         $query = "UPDATE cow SET old_farmer_id = farmer_id, farmer_id = null WHERE id = {$cowID}";
         $this->database->runMySQLQuery($query, FALSE);

         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$numberUsed}')";
      }
      else if ($this->jsonObject['eventType'] == "Sickness"){
         $this->alertHandler->sendSicknessAlert($cowID);
         
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$numberUsed}')";
      }
      else if($this->jsonObject['eventType'] == "Start of Lactation"){
         $query = "UPDATE cow SET milking_status = 'adult_milking' WHERE id = {$cowID}";
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$numberUsed}')";
      }
      else if($this->jsonObject['eventType'] == "Dry Off"){
         $query = "UPDATE cow SET milking_status = 'adult_not_milking' WHERE id = {$cowID}";
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$numberUsed}')";
      }
      else if($this->jsonObject['eventType'] == "Signs of Heat"){
         $query = "UPDATE cow SET in_calf = 0 WHERE id = {$cowID}";
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$numberUsed}')";
      }
      else {
         $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$numberUsed}')";
      }

      $this->database->runMySQLQuery($query, false);
      $this->logHandler->log(3, $this->TAG, "returning response code " . $this->codes['acknowledge_ok']);
      echo $this->codes['acknowledge_ok'];
      $this->logHandler->log(3, $this->TAG, "gracefully exiting");
   }

   private function getTime($timeZone) {
      $time = new DateTime('now', new DateTimeZone($timeZone));
      return $time->format('Y-m-d H:i:s');
   }

   private function getSettings() {
      $this->logHandler->log(3, $this->TAG, "getting settings from: " . $this->settingsDir);
      if (file_exists($this->settingsDir)) {
         $settings = parse_ini_file($this->settingsDir);
         $mysqlCreds = parse_ini_file($settings['mysql_creds']);
         $settings['mysql_creds'] = $mysqlCreds;
         $this->settings = $settings;
         $this->logHandler->log(4, $this->TAG, "settings obtained: " . print_r($this->settings, true));
      } else {
         $this->logHandler->log(1, $this->TAG, "unable to get settings from " . $this->settingsDir . ", exiting");
         die();
      }
   }

   private function getCodes() {
      $responseCodesLocation = $this->ROOT . "config/" . $this->settings['response_codes'];
      $this->logHandler->log(3, $this->TAG, "getting response codes from: " . $responseCodesLocation);
      if (file_exists($responseCodesLocation)) {
         $this->codes = parse_ini_file($responseCodesLocation);
         $this->logHandler->log(4, $this->TAG, "response codes are: " . print_r($this->codes, true));
      } else {
         $this->logHandler->log(1, $this->TAG, "unable to get response codes from " . $responseCodesLocation . ", exiting");
         die();
      }
   }

   private function getEventTypeID() {
      $eventTypeName = $this->jsonObject['eventType'];
      $query = "SELECT `id` FROM `event` WHERE `name` = '{$eventTypeName}'";
      $result = $this->database->runMySQLQuery($query, true);
      if (sizeOf($result) == 1) {
         $this->logHandler->log(4, $this->TAG, "fetched event type ID for " . $eventTypeName . " which is " . $result[0]['id']);
         return $result[0]['id'];
      } else {
         $this->logHandler->log(1, $this->TAG, "it appears like there is no event type by the name '" . $eventTypeName . "' or more than one event type goes by this name, exiting");
         die();
      }
   }

   private function getPOSTJsonObject() {
      $this->logHandler->log(3, $this->TAG, "obtaining POST request");
      $this->jsonObject = json_decode($_POST["json"], true);
      $this->logHandler->log(4, $this->TAG, "json_decode returned: " . print_r($this->jsonObject, true));
   }

}

$obj = new CowEventHandler;
?>
