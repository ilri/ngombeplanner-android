<?php

include '../common/log.php';
include '../common/database.php';
include '../common/alerts.php';
include '../farmer/general.php';

class EditCowHandler {

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

      /*if ($this->jsonObject['mode'] === "initialRegistration") {
         $this->logHandler->log(3, $this->TAG, "Initial registration for farmer");
         $this->registerNewFarmer();
      } 
      else if ($this->jsonObject['mode'] === "newCowRegistration") {
         $this->logHandler->log(3, $this->TAG, "Farmer already registered but registering new cow");
         $this->addCowsToExistingFarmer();
      }*/
      
      if($this->jsonObject['earTagNumber'] == NULL)
         $this->jsonObject['earTagNumber'] = "";

      if($this->jsonObject['name'] == NULL)
         $this->jsonObject['name'] = "";

      $this->jsonObject['earTagNumber'] = preg_replace("/[^a-z0-9\s]/i", "", $this->jsonObject['earTagNumber']); 
      $this->jsonObject['name'] = preg_replace("/[^a-z0-9\s]/i", "", $this->jsonObject['name']); 
      //insert cow
      $currentCow  = $this->jsonObject;
      $this->updateCow($currentCow, $currentCow['farmer_id']);
      
      $this->updateParents($currentCow, $currentCow['farmer_id']);
      
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

   private function updateCow($currentCow, $farmerID) {
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
      else{
         if(!isset($currCowMilkingStatus) || strlen($currCowMilkingStatus) == 0) $currCowMilkingStatus = "adult_milking";
         if(!isset($currCowInCalf)) $currCowInCalf = 0;
      }
      
      if($countryID != -1) {
         $query = "UPDATE cow"
                 . " set `name` = '{$currentCow['name']}',"
                 . " `ear_tag_number` = '{$currentCow['earTagNumber']}',"
                 . " `date_of_birth` = STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),"
                 . " `age` = {$currentCow['age']},"
                 . " `age_type` = '{$currentCow['ageType']}',"
                 . " `sex` = '{$currentCow['sex']}',"
                 . " `service_type` = '{$currentCow['serviceType']}',"
                 . " `country_id` = {$countryID},"
                 . " `milking_status` = '$currCowMilkingStatus',"
                 . " `in_calf` = $currCowInCalf "
                 . " WHERE `id` = {$currentCow['id']}";
      }
      else {
         $query = "UPDATE cow"
            . " set `name` = '{$currentCow['name']}',"
            . " `ear_tag_number` = '{$currentCow['earTagNumber']}',"
            . " `date_of_birth` = STR_TO_DATE('{$currentCow['dateOfBirth']}', '%d/%m/%Y'),"
            . " `age` = {$currentCow['age']},"
            . " `age_type` = '{$currentCow['ageType']}',"
            . " `sex` = '{$currentCow['sex']}',"
            . " `service_type` = '{$currentCow['serviceType']}',"
            . " `milking_status` = '$currCowMilkingStatus',"
            . " `in_calf` = $currCowInCalf "
            . " WHERE `id` = {$currentCow['id']}";
      }
      
      $this->database->runMySQLQuery($query, false);
      $cowID = $currentCow['id'];
      
      $query = "DELETE FROM `cow_breed` WHERE `cow_id` = {$cowID}";
      $this->database->runMySQLQuery($query, false);
      
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
      
      $query = "DELETE FROM `cow_deformity` WHERE `cow_id` = {$cowID}";
      $this->database->runMySQLQuery($query, false);
      
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
   
   private function updateParents($currentCow,$farmerID) {
      $cowID = $currentCow['id'];
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

$obj = new EditCowHandler;
?>
