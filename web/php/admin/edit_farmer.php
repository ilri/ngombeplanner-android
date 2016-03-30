<?php

include '../common/log.php';
include '../common/database.php';
include '../common/alerts.php';
include '../farmer/general.php';

class FarmerEditHandler {

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

      if ($this->jsonObject['mode'] === "editFarmer") {
         $this->logHandler->log(3, $this->TAG, "Editing a farmer");
         $this->editFarmer();
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

   private function editFarmer() {
      $timeEAT = $this->getTime('Y-m-d H:i:s');
      $this->logHandler->log(3, $this->TAG, "Editing farmer (" . $this->jsonObject['fullName'] . ") in the database");
      $this->logHandler->log(4, $this->TAG, "Json for new farmer is ".print_r($this->jsonObject, true));
      
      $query = "SELECT * FROM farmer WHERE `mobile_no` = '{$this->jsonObject['mobileNumber']}' AND id != {$this->jsonObject['id']}";
      
      $result = $this->database->runMySQLQuery($query, TRUE);
      
      if(is_array($result) && count($result) > 0){
         $this->logHandler->log(2, $this->TAG, "Admin tried to set farmer's mobile number to ".$this->jsonObject['mobileNumber']." which has already been registered under a different farmer");
         die($this->codes['number_in_use']);
      }
      
      $extensionPID = $this->general->getExtensionPersonnelID($this->jsonObject['extensionPersonnel'], true);
      $this->jsonObject['fullName'] = preg_replace("/[^a-z0-9\s]/i","", $this->jsonObject['fullName']);
      if($extensionPID !== -1){
         $query = "UPDATE `farmer` SET"
                 . " name = '{$this->jsonObject['fullName']}',"
                 . " `mobile_no` = '{$this->jsonObject['mobileNumber']}',"
                 . " `gps_longitude` = '{$this->jsonObject['longitude']}',"
                 . " `gps_latitude` = '{$this->jsonObject['latitude']}',"
                 . " `extension_personnel_id` = {$extensionPID},"
                 . " `sim_card_sn` = '{$this->jsonObject['simCardSN']}',"
                 . " `pref_locale` = '{$this->jsonObject['preferredLocale']}',"
                 . " `is_active` = {$this->jsonObject['isActive']},"
                 . " `location_district` = '{$this->jsonObject['site']}'"
                 . " WHERE id = {$this->jsonObject['id']}";
      }

      else{
         $query = "UPDATE `farmer` SET"
                 . " name = '{$this->jsonObject['fullName']}',"
                 . " `mobile_no` = '{$this->jsonObject['mobileNumber']}',"
                 . " `gps_longitude` = '{$this->jsonObject['longitude']}',"
                 . " `gps_latitude` = '{$this->jsonObject['latitude']}',"
                 . " `extension_personnel_id` = NULL,"
                 . " `sim_card_sn` = '{$this->jsonObject['simCardSN']}',"
                 . " `pref_locale` = '{$this->jsonObject['preferredLocale']}',"
                 . " `is_active` = {$this->jsonObject['isActive']},"
                 . " `location_district` = '{$this->jsonObject['site']}'"
                 . " WHERE id = {$this->jsonObject['id']}";
      }
      $this->database->runMySQLQuery($query, false);
      $farmerID = $this->jsonObject['id'];

      if(isset($this->jsonObject['simCardSN']) && strlen($this->jsonObject['simCardSN']) > 0){
         $query = "UPDATE farmer SET `sim_card_sn` = '' WHERE `id` != {$farmerID} AND `sim_card_sn` = '{$this->jsonObject['simCardSN']}'";
         $this->database->runMySQLQuery($query, false);
         $this->logHandler->log(2, $this->TAG, " Detached the serial number (".$this->jsonObject['simCardSN'].") to all farmer accounts linked to it except for the one with farmer id = ".$farmerID);
      }
   }
}

$obj = new FarmerEditHandler;
?>
