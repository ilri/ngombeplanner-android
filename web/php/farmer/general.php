<?php

class General {

   private $TAG = "general.php";
   private $ROOT = "../../";
   private $settingsDir;
   private $settings;
   private $codes;
   private $logHandler;
   private $database;

   public function __construct() {
      $this->settingsDir = $this->ROOT . "config/settings.ini";
      $this->logHandler = new LogHandler;
      $this->logHandler->log(3, $this->TAG, "Starting MilkProductionHistoryHandler");
      $this->getSettings();
      $this->getCodes();
      $this->database = new DatabaseHandler;
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

   public function getBreeds() {
      $this->logHandler->log(3, $this->TAG, "Getting all breeds from database");
      $query="SELECT * FROM `breed`";
      $result = $this->database->runMySQLQuery($query, true);
      return $result;
   }
   
   public function getBreedID($breed) {
      $this->logHandler->log(3, $this->TAG, "Getting id of breed with name as ".$breed);
      $query = "SELECT `id` FROM `breed` WHERE `name` = '{$breed}'";
      $result = $this->database->runMySQLQuery($query, true);
      if(sizeof($result) == 1) {
         $id = $result[0]['id'];
         $this->logHandler->log(4, $this->TAG, "ID of Breed".$breed." gotten as ".$id);
         return $id;
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Either more than one breed with the name '".$breed."' gotten or none at all, returning -1");
         return -1;
      }
   }
   
   public function getEventID($event) {
      $this->logHandler->log(3, $this->TAG, "Getting id of event with name as ".$event);
      $query = "SELECT `id` FROM `event` WHERE `name` = '{$event}'";
      $result = $this->database->runMySQLQuery($query, true);
      if(sizeof($result) == 1) {
         $id = $result[0]['id'];
         $this->logHandler->log(4, $this->TAG, "ID of Event".$event." gotten as ".$id);
         return $id;
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Either more than one event with the name '".$event."' gotten or none at all, returning -1");
         return -1;
      }
   }
   
   public function getDeformityID($deformity) {
      $this->logHandler->log(3, $this->TAG, "Getting id of deformity with name as ".$deformity);
      $query = "SELECT `id` FROM `deformity` WHERE `name` = '{$deformity}'";
      $result = $this->database->runMySQLQuery($query, true);
      if(sizeof($result) == 1) {
         $id = $result[0]['id'];
         $this->logHandler->log(4, $this->TAG, "ID of Deformity".$deformity." gotten as ".$id);
         return $id;
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Either more than one deformity with the name '".$deformity."' gotten or none at all, returning -1");
         return -1;
      }
   }

}
?>