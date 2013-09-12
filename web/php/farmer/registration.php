<?php

include '../common/log.php';
include '../common/database.php';

class RegistrationHandler {

   private $TAG = "registration.php";
   private $ROOT = "../../";
   private $settingsDir;
   private $settings;
   private $codes;
   private $jsonObject;
   private $logHandler;
   private $database;

   public function __construct() {
      $this->settingsDir = $this->ROOT . "config/settings.ini";
      $this->logHandler = new LogHandler;
      $this->logHandler->log(3, $this->TAG, "Starting RegistrationHandler");
      $this->getPOSTJsonObject();
      $this->getSettings();
      $this->getCodes();
      $this->database = new DatabaseHandler;

      if ($this->jsonObject['mode'] === "initialRegistration") {
         $this->logHandler->log(3, $this->TAG, "Initial registration for farmer");
         $this->registerNewFarmer();
      } 
      else if ($this->jsonObject['mode'] === "newCowRegistration") {
         $this->logHandler->log(3, $this->TAG, "Farmer already registered but registering new cow");
         $this->addCowsToExistingFarmer();
      }
      $this->logHandler->log(3, $this->TAG, "sending acknowledge_ok response cod back to client");
      echo $this->codes['acknowledge_ok'];
   }

   private function getTime($format) {
      $timeZone = $this->settings['time_zone'];
      $time = new DateTime('now', new DateTimeZone($timeZone));
      $formatedTime = $time->format($format);
      $this->logHandler->log(4, $this->TAG, "returning time '" . $formatedTime . "' using format '" . $format . "'");
      return $formatedTime;
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

   private function getPOSTJsonObject() {
      $this->logHandler->log(3, $this->TAG, "obtaining POST request");
      $this->jsonObject = json_decode($_POST["json"], true);
      $this->logHandler->log(4, $this->TAG, "json_decode returned: " . print_r($this->jsonObject, true));
   }

   private function registerNewFarmer() {
      $timeEAT = $this->getTime('Y-m-d H:i:s');
      $this->logHandler->log(3, $this->TAG, "Registering the new farmer (" . $this->jsonObject['fullName'] . ") in the database");
      $query = "INSERT INTO `farmer`(name,`mobile_no`,`gps_longitude`,`gps_latitude`,`extension_personnel`,`sim_card_sn`,`date_added`) VALUES('{$this->jsonObject['fullName']}','{$this->jsonObject['mobileNumber']}','{$this->jsonObject['longitude']}','{$this->jsonObject['latitude']}','{$this->jsonObject['extensionPersonnel']}','{$this->jsonObject['simCardSN']}','{$timeEAT}')";
      $this->database->runMySQLQuery($query, false);
      $farmerID = mysql_insert_id();
      $this->logHandler->log(4, $this->TAG, "New farmer's ID is " . $farmerID);
      $cows = array();
      $cows = $this->jsonObject['cows'];
      for ($i = 0; $i < sizeof($cows); $i++) {
         //insert cow
         $currentCow = $cows[$i];
         $this->registerCow($currentCow,$farmerID);
      }
   }

   private function addCowsToExistingFarmer() {
      $simCardSN = $this->jsonObject['simCardSN'];
      $query = "SELECT `farmer`.`id` FROM `farmer` WHERE `farmer`.`sim_card_sn` = '{$simCardSN}'";
      $result = $this->database->runMySQLQuery($query, true);
      $farmerID = $result[0]['id'];
      $cows = array();
      $cows = $this->jsonObject['cows'];
      for ($i = 0; $i < sizeof($cows); $i++) {
         $currentCow = $cows[$i];
         $cowID = $this->registerCow($currentCow,$farmerID);
         //add an event and link it to the new cow
         if ($currentCow['mode' === "bornCalfRegistration"]) {
            //add event to database
            $eventTypeID = $this->getEventTypeID("Birth");
            $eventDate = $currentCow['dateOfBirth'];
            $remarks = "";
            $time = $this->getTime("EAT");
            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`) VALUES({$cowID},{$eventTypeID},'{$remarks}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}')";
            $this->database->runMySQLQuery($query, false);
         }
      }
   }

   private function getEventTypeID($eventTypeName) {
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

   private function registerCow($currentCow, $farmerID) {
      $timeEAT = $this->getTime('Y-m-d H:i:s');
      $this->logHandler->log(3, $this->TAG, "Registering the new cow (" . $currentCow['earTagNumber'] . ") in the database");

      //add the cow to the database
      $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`type`,`date_added`) VALUES({$farmerID},'{$currentCow['name']}','{$currentCow['earTagNumber']}',STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),{$currentCow['age']},{$currentCow['ageType']},{$currentCow['sex']},'cow','{$timeEAT}')";
      $this->database->runMySQLQuery($query, false);
      $cowID = mysql_insert_id();
      $this->logHandler->log(4, $this->TAG, "New cow's ID is " . $cowID);
      $breeds = $currentCow['breeds'];
      for ($j = 0; $j < sizeof($breeds); $j++) {
         $currentBreed = $breeds[$j];
         $query = "INSERT INTO `breed`(`cow_id`,`text`,`date_added`) VALUES($cowID,'$currentBreed','$timeEAT')";
         $this->database->runMySQLQuery($query, false);
      }
      $deformities = $currentCow['deformities'];
      for ($j = 0; $j < sizeof($deformities); $j++) {
         $currentDeformity = $deformities[$j];
         $query = "INSERT INTO `deformity`(`cow_id`,`text`,`date_added`) VALUES($cowID,'$currentDeformity','$timeEAT')";
         $this->database->runMySQLQuery($query, false);
      }
      $sire = $currentCow['sire'];
      if ($sire['earTagNumber'] != "" || $sire['strawNumber'] != "" || $sire['name'] != "") {
         
         if($sire['strawNumber']!= "") {
            $query = "SELECT `cow`.`id` FROM `cow` WHERE `straw_number` = '{$sire['strawNumber']}'";
         }
         else {
            $query = "SELECT `cow`.`id` FROM `cow` WHERE `name` = '{$sire['name']}' AND `ear_tag_number` = '{$sire['earTagNumber']}'";
         }
         
         $result = $this->database->runMySQLQuery($query, true);
         if(sizeof($result) == 0) {
            $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`type`,`service_type`,`straw_number`,`vet_used`,`date_added`) VALUES($farmerID,'{$sire['name']}','{$sire['earTagNumber']}',STR_TO_DATE('{$sire['dateOfBirth']}', '%d/%m/%Y'),{$sire['age']},{$sire['ageType']},{$sire['sex']},'sire',{$sire['serviceType']},'{$sire['strawNumber']}','{$sire['vetUsed']}','$timeEAT')";
            $this->database->runMySQLQuery($query, false);
            $sireID = mysql_insert_id();
         }
         else {
            $sireID = $result[0]['id'];
         }
         $this->logHandler->log(4, $this->TAG, "New sire's ID is " . $sireID);
         $query = "UPDATE `cow` SET `sire_id` = $sireID WHERE `id` = $cowID";
         $this->database->runMySQLQuery($query, false);
      }
      $dam = $currentCow['dam'];
      if ($dam['earTagNumber'] != "" || $dam['embryoNumber'] != "" || $dam['name'] != "") {
         
         if($dam['embryoNumber'] != "") {
            $query = "SELECT `cow`.`id` FROM `cow` WHERE `embryo_number` = '{$dam['embryoNumber']}'";
         }
         else {
            $query = "SELECT `cow`.`id` FROM `cow` WHERE `name` = '{$dam['name']}' AND `ear_tag_number` = '{$dam['earTagNumber']}'";
         }
         
         $result = $this->database->runMySQLQuery($query, true);
         if(sizeof($result) == 0) {
            $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`type`,`service_type`,`embryo_number`,`vet_used`,`date_added`) VALUES($farmerID,'{$dam['name']}','{$dam['earTagNumber']}',STR_TO_DATE('{$dam['dateOfBirth']}', '%d/%m/%Y'),{$dam['age']},{$dam['ageType']},{$dam['sex']},'dam',{$dam['serviceType']},'{$dam['embryoNumber']}','{$dam['vetUsed']}','$timeEAT')";
            $this->database->runMySQLQuery($query, false);
            $damID = mysql_insert_id();
         }
         else {
            $damID = $result[0]['id'];
         }
         $this->logHandler->log(4, $this->TAG, "New dam's ID is " . $damID);
         $query = "UPDATE `cow` SET `dam_id` = $damID WHERE `id` = $cowID";
         $this->database->runMySQLQuery($query, false);
      }
      return $cowID;
   }
}

$obj = new RegistrationHandler;
?>
