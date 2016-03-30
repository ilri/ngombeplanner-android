<?php

include '../common/log.php';
include '../common/database.php';
include '../common/alerts.php';
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
   private $alertHandler;
   private $appUsed;
   private $noUsed;

   public function __construct() {
      $this->settingsDir = $this->ROOT . "config/settings.ini";
      $this->logHandler = new LogHandler;
      $this->logHandler->log(3, $this->TAG, "Starting RegistrationHandler");
      $this->getPOSTJsonObject();
      $this->getSettings();
      $this->getCodes();
      $this->database = new DatabaseHandler;
      $this->general = new General();
      $this->alertHandler = new AlertHandler($this->ROOT, $this->database, $this->logHandler);

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
      $this->logHandler->log(4, $this->TAG, "Json from new farmer is ".print_r($this->jsonObject, true));
      
      $query = "SELECT * FROM farmer WHERE `mobile_no` = '{$this->jsonObject['mobileNumber']}'";
      
      $result = $this->database->runMySQLQuery($query, TRUE);
      
      if(is_array($result) && count($result) > 0){
         $this->logHandler->log(2, $this->TAG, "New farmer tried to register a mobile number (".$this->jsonObject['mobileNumber'].") that has already been registered under another farmer");
         die($this->codes['number_in_use']);
      }
      else{
         $extensionPID = $this->general->getExtensionPersonnelID($this->jsonObject['extensionPersonnel'], true);
         $this->jsonObject['fullName'] = preg_replace("/[^a-z0-9\s]/i","", $this->jsonObject['fullName']);
         if($extensionPID !== -1){
            $query = "INSERT INTO `farmer`(name,`mobile_no`,`gps_longitude`,`gps_latitude`,`extension_personnel_id`,`sim_card_sn`,`date_added`, `pref_locale`, `location_district`)".
                    " VALUES('{$this->jsonObject['fullName']}','{$this->jsonObject['mobileNumber']}','{$this->jsonObject['longitude']}','{$this->jsonObject['latitude']}',{$extensionPID},'{$this->jsonObject['simCardSN']}','{$timeEAT}', '{$this->jsonObject['preferredLocale']}', '{$this->jsonObject['site']}')";
         }

         else{
            $query = "INSERT INTO `farmer`(name,`mobile_no`,`gps_longitude`,`gps_latitude`,`sim_card_sn`,`date_added`, `pref_locale`, `location_district`)".
                    " VALUES('{$this->jsonObject['fullName']}','{$this->jsonObject['mobileNumber']}','{$this->jsonObject['longitude']}','{$this->jsonObject['latitude']}','{$this->jsonObject['simCardSN']}','{$timeEAT}', '{$this->jsonObject['preferredLocale']}', '{$this->jsonObject['site']}')";
         }
         $this->database->runMySQLQuery($query, false);
         $farmerID = mysql_insert_id();
         
         if(isset($this->jsonObject['simCardSN']) && strlen($this->jsonObject['simCardSN']) > 0){
            $query = "UPDATE farmer SET `sim_card_sn` = '' WHERE `id` != {$farmerID} AND `sim_card_sn` = '{$this->jsonObject['simCardSN']}'";
            $this->database->runMySQLQuery($query, false);
            $this->logHandler->log(2, $this->TAG, " Detached the serial number (".$this->jsonObject['simCardSN'].") to all farmer accounts linked to it except for the one with farmer id = ".$farmerID);
         }
         
         $this->logHandler->log(4, $this->TAG, "New farmer's ID is " . $farmerID);
         $cows = array();
         $cows = $this->jsonObject['cows'];
         for ($i = 0; $i < sizeof($cows); $i++) {
            //clean 
            if($cows[$i]['earTagNumber'] == NULL)
               $cows[$i]['earTagNumber'] = "";
      
            if($cows[$i]['name'] == NULL)
               $cows[$i]['name'] = "";
       
            $cows[$i]['earTagNumber'] = preg_replace("/[^a-z0-9\s]/i", "", $cows[$i]['earTagNumber']); 
            $cows[$i]['name'] = preg_replace("/[^a-z0-9\s]/i", "", $cows[$i]['name']); 
            //insert cow
            $currentCow = $cows[$i];
            $this->registerCow($currentCow,$farmerID);
         }
         for ($i = 0; $i < sizeof($cows); $i++) {
            $currentCow = $cows[$i];
            $this->registerParents($currentCow,$farmerID);
         }
         
         //$this->alertHandler->sendRegistrationAlert($farmerID);
      }
   }

   private function addCowsToExistingFarmer() {
        if(isset($this->jsonObject['simCardSN']) && $this->jsonObject['simCardSN']!==""){
            $simCardSN = $this->jsonObject['simCardSN'];
            $this->appUsed = "Android";
            $query = "SELECT `farmer`.`id`, `farmer`.`mobile_no` FROM `farmer` WHERE `farmer`.`sim_card_sn` = '{$simCardSN}'";
        }
        else if(isset ($this->jsonObject['mobileNumber']) && $this->jsonObject['mobileNumber']!==""){
           $this->appUsed = "JavaME";
            $mobileNumber = $this->jsonObject['mobileNumber'];
            $query = "SELECT `farmer`.`id`, `farmer`.`mobile_no` FROM `farmer` WHERE `farmer`.`mobile_no` = '{$mobileNumber}'";
        }
        
      $result = $this->database->runMySQLQuery($query, true);
      $farmerID = $result[0]['id'];
      $this->noUsed = $result[0]['mobile_no'];
      $cows = array();
      $cows = $this->jsonObject['cows'];
      for ($i = 0; $i < sizeof($cows); $i++) {
         $currentCow = $cows[$i];
         $cowID = $this->registerCow($currentCow,$farmerID);
         //add an event and link it to the new cow
         if ($currentCow['mode'] === "bornCalfRegistration") {
            $calvingId = $this->general->getEventID("Calving");
            
            $eventTypeID = $this->general->getEventID("Birth");
            $eventDate = $currentCow['dateOfBirth'];
            $remarks = "";
            $time = $this->getTime("Y-m-d H:i:s");
            $piggyBack = json_decode($currentCow['piggyBack'], true);
            
            if(strlen($eventDate) == 0 && isset($piggyBack['eventDate'])){
               $eventDate = $piggyBack['eventDate'];
               $this->logHandler->log(3, $this->TAG, "Date of birth for calf not set. Setting event date to '".$piggyBack['eventDate']."'");
            }
            else {
               $this->logHandler->log(1, $this->TAG, "Date of birth for calf not set and event date also not set");
            }
            
            $this->logHandler->log(3, $this->TAG, "piggy back " . $piggyBack['birthType']);
            
            $query = "SELECT id FROM cow WHERE ear_tag_number = '{$piggyBack['motherETN']}' AND name = '{$piggyBack['motherName']}' AND farmer_id = {$farmerID}";
            $result = $this->database->runMySQLQuery($query, TRUE);
            
            $motherId = $result[0]['id'];
            
            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`birth_type`,`event_date`,`date_added`, `app_used`, `no_used`)".
                    " VALUES({$motherId},{$calvingId},'{$piggyBack['birthType']}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$this->noUsed}')";
            $this->database->runMySQLQuery($query, false);
            
            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`birth_type`,`event_date`,`date_added`, `app_used`, `no_used`)".
                    " VALUES({$cowID},{$eventTypeID},'{$piggyBack['birthType']}',STR_TO_DATE('{$eventDate}', '%d/%m/%Y'),'{$time}', '{$this->appUsed}', '{$this->noUsed}')";
            $this->database->runMySQLQuery($query, false);
         }
         else if($currentCow['mode'] === "adultCowRegistration") {
            $eventTypeID = $this->general->getEventID("Acquisition");
            $eventDate = $this->getTime("Y-m-d");
            $piggyBack = json_decode($currentCow['piggyBack'], true);
            $remarks = $piggyBack['remarks'];
            $time = $this->getTime("Y-m-d H:i:s");
            $query = "INSERT INTO `cow_event`(`cow_id`,`event_id`,`remarks`,`event_date`,`date_added`, `app_used`, `no_used`) VALUES({$cowID},{$eventTypeID},'{$remarks}','{$eventDate}','{$time}', '{$this->appUsed}', '{$this->noUsed}')";
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
      
      $currCowMilkingStatus = $currentCow['milkingStatus'];
      $currCowInCalf = $currentCow['inCalf'];
      
      $this->logHandler->log(3, $this->TAG, "Current cow milking status" . $currCowMilkingStatus);
      $this->logHandler->log(3, $this->TAG, "Current cow in calf" . $currCowInCalf);
      if($currentCow['sex'] == "Male"){
         $currCowInCalf = "NULL";
         $currCowMilkingStatus = "NULL";
      }
      else if($currentCow['mode'] == "bornCalfRegistration"){//female calfs
         if(!isset($currCowMilkingStatus) || strlen($currCowMilkingStatus) == 0) $currCowMilkingStatus = "heifer";
         if(!isset($currCowInCalf) || strlen($currCowInCalf)) $currCowInCalf = 0;
      }
      else{
         if(!isset($currCowMilkingStatus) || strlen($currCowMilkingStatus) == 0) $currCowMilkingStatus = "adult_milking";
         if(!isset($currCowInCalf)) $currCowInCalf = 0;
      }
      
      if($countryID != -1) {
         $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`date_added`,`service_type`,`country_id`,`milking_status`, `in_calf`)".
              " VALUES({$farmerID},'{$currentCow['name']}','{$currentCow['earTagNumber']}',STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),{$currentCow['age']},'{$currentCow['ageType']}','{$currentCow['sex']}','{$timeEAT}','{$currentCow['serviceType']}',{$countryID},'$currCowMilkingStatus', $currCowInCalf)";
      }
      else {
         $query = "INSERT INTO `cow`(`farmer_id`,`name`,`ear_tag_number`,`date_of_birth`,`age`,`age_type`,`sex`,`date_added`,`service_type`,`milking_status`, `in_calf`)".
              " VALUES({$farmerID},'{$currentCow['name']}','{$currentCow['earTagNumber']}',STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),{$currentCow['age']},'{$currentCow['ageType']}','{$currentCow['sex']}','{$timeEAT}','{$currentCow['serviceType']}','$currCowMilkingStatus', $currCowInCalf)";
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
      $cowID = $this->general->getCowID($farmerID,$currentCow['earTagNumber'], $currentCow['name']);
      if($cowID != -1) {
         if($currentCow['serviceType'] == "Bull") {
            $sire = $currentCow['sire'];
            if($sire != "") {
               if(!isset($sire['name']) || $sire['name'] == NULL)
                  $sire['name'] = "";
               
               if(!isset($sire['earTagNumber']) || $sire['earTagNumber'] == NULL)
                  $sire['earTagNumber'] = "";
               
               $sire['name'] = preg_replace("/[^a-z0-9\s]/i", "", $sire['name']); 
               $sire['earTagNumber'] = preg_replace("/[^a-z0-9\s]/i", "", $sire['earTagNumber']); 
               $sireID = $this->general->getCowID($farmerID,$sire['earTagNumber'], $sire['name']);
               
               if($sireID == -1 && (strlen($sire['earTagNumber']) > 0 || strlen($sire['name']) > 0)){
                  $time = $this->getTime("Y-m-d H:i:s");
                  $query = "INSERT INTO `cow`(`name`, `ear_tag_number`, `farmer_id`, `sex`, `date_added`) VALUES('{$sire['name']}', '{$sire['ear_tag_number']}', $farmerID, 'Male', '$time')";
                  $this->database->runMySQLQuery($query, FALSE);
                  $sireID = $this->database->getLastInsertID();
                  $this->logHandler->log(3, $this->TAG, "Sire inserted to db. Id for sire is = "+$cowID);
               }
               
               if($sireID != -1){
                  $query = "UPDATE `cow` SET `sire_id` = {$sireID} WHERE `id` = {$cowID}";
                  $this->database->runMySQLQuery($query, false);

                  $query = "UPDATE `cow` SET `bull_owner` = '{$sire['ownerType']}', `owner_name` = '{$sire['owner']}' WHERE id = {$sireID}";
                  $this->database->runMySQLQuery($query, FALSE);
               }
            }
            
            $dam = $currentCow['dam'];
            if($dam != "") {
               if(!isset($dam['name']) || $dam['name'] == NULL)
                  $dam['name'] = "";
               
               if(!isset($dam['earTagNumber']) || $dam['earTagNumber'] == NULL)
                  $dam['earTagNumber'] = "";
               
               $dam['name'] = preg_replace("/[^a-z0-9\s]/i", "", $dam['name']); 
               $dam['earTagNumber'] = preg_replace("/[^a-z0-9\s]/i", "", $dam['earTagNumber']); 
               $damID = $this->general->getCowID($farmerID,$dam['earTagNumber'], $dam['name']);
               
               if($damID == -1 && (strlen($dam['earTagNumber']) > 0 || strlen($dam['name']) > 0)){
                  $time = $this->getTime("Y-m-d H:i:s");
                  $query = "INSERT INTO `cow`(`farmer_id`, `name`, `ear_tag_number`, `sex`, `date_added`, `milking_status`) VALUES($farmerID, '{$dam['name']}', '{$dam['earTagNumber']}', 'Female', '$time', 'adult_not_milking')";//assuming that if dam has not yet been registered then registering it now means it is not in physical herd
                  $this->database->runMySQLQuery($query, FALSE);
                  $damID = $this->database->getLastInsertID();
               }
               
               if($damID != -1){
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
               if(!isset($dam['name']) || $dam['name'] == NULL)
                  $dam['name'] = "";
               
               if(!isset($dam['earTagNumber']) || $dam['earTagNumber'] == NULL)
                  $dam['earTagNumber'] = "";
               
               $damID = $this->general->getCowID($farmerID,$dam['earTagNumber'],$dam['name']);
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
