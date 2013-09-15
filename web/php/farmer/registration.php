<?php

include '../common/log.php';
include '../common/database.php';
include 'general.php';

class RegistrationHandler {

   private $TAG = "registration.php";
   private $ROOT = "../../";
   private $settingsDir;
   private $settings;
   private $codes;
   private $jsonObject;
   private $logHandler;
   private $database;
   private $general;

   public function __construct() {
      $this->settingsDir = $this->ROOT . "config/settings.ini";
      $this->logHandler = new LogHandler;
      $this->logHandler->log(3, $this->TAG, "Starting RegistrationHandler");
      $this->getPOSTJsonObject();
      $this->getSettings();
      $this->getCodes();
      $this->database = new DatabaseHandler;
      $this->general = new General();

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
      $extensionPID = $this->general->getExtensionPersonnelID($this->jsonObject['extensionPersonnel'], true);
      $query = "INSERT INTO `farmer`(name,`mobile_no`,`gps_longitude`,`gps_latitude`,`extension_personnel_id`,`sim_card_sn`,`date_added`)".
              " VALUES('{$this->jsonObject['fullName']}','{$this->jsonObject['mobileNumber']}','{$this->jsonObject['longitude']}','{$this->jsonObject['latitude']}',{$extensionPID},'{$this->jsonObject['simCardSN']}','{$timeEAT}')";
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
      for ($i = 0; $i < sizeof($cows); $i++) {
         $currentCow = $cows[$i];
         $this->registerParents($currentCow,$farmerID);
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
         if ($currentCow['mode'] === "bornCalfRegistration") {
            //add event to database
            $eventTypeID = $this->general->getEventID("Birth");
            $eventDate = $currentCow['dateOfBirth'];
            $remarks = "";
            $time = $this->getTime("Y-m-d H:i:s");
            $piggyBack = $currentCow['piggyBack'];
            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`birth_type`,`event_date`,`date_added`) VALUES({$cowID},{$eventTypeID},'{$piggyBack['birthType']}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}')";
            $this->database->runMySQLQuery($query, false);
         }
         else if($currentCow['mode'] === "adultCowRegistration") {
            $eventTypeID = $this->general->getEventID("Acquisition");
            $eventDate = $this->getTime("Y-m-d");
            $remarks = $currentCow['piggyBack']['remarks'];
            $time = $this->getTime("Y-m-d H:i:s");
            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`) VALUES({$cowID},{$eventTypeID},'{$remarks}','{$eventDate}','{$time}')";
            $this->database->runMySQLQuery($query, false);
         }
      }
      for ($i = 0; $i < sizeof($cows); $i++) {
         $currentCow = $cows[$i];
         $this->registerParents($currentCow,$farmerID);
      }
   }

   private function registerCow($currentCow, $farmerID) {
      $timeEAT = $this->getTime('Y-m-d H:i:s');
      $this->logHandler->log(3, $this->TAG, "Registering the new cow (" . $currentCow['earTagNumber'] . ") in the database");
      $countryID = $this->general->getCountryID($currentCow['countryOfOrigin']);
      if($countryID != -1) {
         $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`date_added`,`service_type`,`country_id`)".
              " VALUES({$farmerID},'{$currentCow['name']}','{$currentCow['earTagNumber']}',STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),{$currentCow['age']},'{$currentCow['ageType']}','{$currentCow['sex']}','{$timeEAT}','{$currentCow['serviceType']}',{$countryID})";
      }
      else {
         $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`date_added`,`service_type`)".
              " VALUES({$farmerID},'{$currentCow['name']}','{$currentCow['earTagNumber']}',STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),{$currentCow['age']},'{$currentCow['ageType']}','{$currentCow['sex']}','{$timeEAT}','{$currentCow['serviceType']}')";
      }
      
      $this->database->runMySQLQuery($query, false);
      $cowID = mysql_insert_id();
      $this->logHandler->log(4, $this->TAG, "New cow's ID is " . $cowID);
      $breeds = $currentCow['breeds'];
      for ($j = 0; $j < sizeof($breeds); $j++) {
         $currentBreed = $breeds[$j];
         $breedID = $this->general->getBreedID($currentBreed);
         if($breedID != -1) {
            $query = "INSERT INTO `cow_breed`(`cow_id`,`breed_id`,`date_added`) VALUES($cowID,$breedID,'$timeEAT')";
            $this->database->runMySQLQuery($query, false);
         }
         else {
            $this->logHandler->log(2, $this->TAG, "No breed with the name '".$currentBreed."' found, not inserting breed into database");
         }
         
      }
      $deformities = $currentCow['deformities'];
      for ($j = 0; $j < sizeof($deformities); $j++) {
         $currentDeformity = $deformities[$j];
         $deformityID = $this->general->getDeformityID($currentDeformity);
         if ($deformityID != -1) {
            if ($currentDeformity != "Other") {
               $query = "INSERT INTO `cow_deformity`(`cow_id`,`deformity_id`,`date_added`) VALUES($cowID,$deformityID,'$timeEAT')";
            } else {
               $query = "INSERT INTO `cow_deformity`(`cow_id`,`deformity_id`,`date_added`,`specification`) VALUES($cowID,$deformityID,'$timeEAT','{$currentCow['otherDeformity']}')";
            }
            $this->database->runMySQLQuery($query, false);
         }
      }
      return $cowID;
   }
   
   private function registerParents($currentCow,$farmerID) {
      $cowID = $this->general->getCowID($farmerID,$currentCow['earTagNumber']);
      if($cowID != -1) {
         if($currentCow['serviceType'] == "Bull") {
            $sire = $currentCow['sire'];
            if($sire != "") {
               $sireID = $this->general->getCowID($farmerID,$sire['earTagNumber']);
               if($sireID != -1) {
                  $query = "UPDATE `cow` SET `sire_id` = {$sireID} WHERE `id` = {$cowID}";
                  $this->database->runMySQLQuery($query, false);
               }
            }
            
            $dam = $currentCow['dam'];
            if($dam != "") {
               $damID = $this->general->getCowID($farmerID,$dam['earTagNumber']);
               if($damID != -1) {
                  $query = "UPDATE `cow` SET `dam_id` = {$damID} WHERE `id` = {$cowID}";
                  $this->database->runMySQLQuery($query, false);
               }
            }
         }
         else if($currentCow['serviceType'] == "AI") {
            $sire = $currentCow['sire'];
            if($sire != "") {
               $strawID = $this->general->getStrawID($sire['strawNumber'], true);
               if ($strawID != -1) {
                  $query = "UPDATE `cow` SET `straw_id` = {$strawID} WHERE `id` = {$cowID}";
                  $this->database->runMySQLQuery($query, false);
               }
            }
            
            $dam = $currentCow['dam'];
            if($dam != "") {
               $damID = $this->general->getCowID($farmerID,$dam['earTagNumber']);
               if($damID != -1) {
                  $query = "UPDATE `cow` SET `dam_id` = {$damID} WHERE `id` = {$cowID}";
                  $this->database->runMySQLQuery($query, false);
               }
            }
         }
         else if($currentCow['serviceType'] == "ET") {
            $dam = $currentCow['dam'];
            if($dam != "") {
               $embryoID = $this->general->getEmbryoID($dam['embryoNumber'],true);
               if($embryoID != -1) {
                  $query = "UPDATE `cow` SET `embryo_id` = {$embryoID} WHERE `id` = {$cowID}";
                  $this->database->runMySQLQuery($query, false);
               }
            }
         }
      }
   }
}

$obj = new RegistrationHandler;
?>
