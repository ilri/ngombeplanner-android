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
      $this->logHandler->log(3, $this->TAG, "Starting General");
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
   
   public function getExtensionPersonnelID($name, $insert) {
      if($name != "") {
         $time = $this->getTime('Y-m-d H:i:s');
         $this->logHandler->log(3, $this->TAG, "Getting id of Extension Personnel with name as " . $name);
         $query = "SELECT `id` FROM `extension_personnel` WHERE `name` = '{$name}'";
         $result = $this->database->runMySQLQuery($query, true);
         if (sizeof($result) == 1) {
            $id = $result[0]['id'];
            $this->logHandler->log(4, $this->TAG, "ID of Extension Personnel " . $name . " gotten as " . $id);
            return $id;
         } 
         else if(sizeof($result) == 0 && $insert == true) {
            $query = "INSERT INTO `extension_personnel`(`name`,`date_added`) VALUES ('$name','$time')";
            $result = $this->database->runMySQLQuery($query, false);
            $id = mysql_insert_id();
            $this->logHandler->log(3, $this->TAG, "Created a new Extension Personnel record with the name " . $name . " and id as " . $id);
            return $id;
         }
         else {
            $this->logHandler->log(2, $this->TAG, "There is more than one Extension Personnel with the name '" . $name . "' gotten, returning -1");
            return -1;
         }
      }
      else {
         return -1;
      }
      
   }
   
   public function getCountryID($name) {
      if($name != -1){
         $this->logHandler->log(3, $this->TAG, "Getting id of country with name as ".$name);
         $query = "SELECT `id` FROM `country` WHERE `name` = '{$name}'";
         $result = $this->database->runMySQLQuery($query, true);
         if (sizeof($result) == 1) {
            $id = $result[0]['id'];
            $this->logHandler->log(4, $this->TAG, "ID of Country " . $name . " gotten as " . $id);
            return $id;
         } else {
            $this->logHandler->log(2, $this->TAG, "Either more than one Country with the name '" . $name . "' gotten or none at all, returning -1");
            return -1;
         }
      }
      else {
         return -1;
      }
   }
   
   public function getCowID($farmerID,$name,$earTagNumber) {
      $this->logHandler->log(3, $this->TAG, "Getting id of cow with name as ".$name);
      if($farmerID != -1) {
         $query = "SELECT `id` FROM `cow` WHERE `name` = '{$name}' AND `ear_tag_number` = '{$earTagNumber}' AND `farmer_id` = {$farmerID}";
      }
      else {
         $query = "SELECT `id` FROM `cow` WHERE `name` = '{$name}' AND `ear_tag_number` = '{$earTagNumber}'";
      }
      
      $result = $this->database->runMySQLQuery($query, true);
      if(sizeof($result) == 1) {
         $id = $result[0]['id'];
         $this->logHandler->log(4, $this->TAG, "ID of Cow ".$name." gotten as ".$id);
         return $id;
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Either more than one cow with the name '".$name."' and ear_tag_number '".$earTagNumber."' gotten or none at all, returning -1");
         return -1;
      }
   }
   
   public function getEmbryoID($embryoNumber, $insert) {
      if($embryoNumber != "") {
         $time = $this->getTime('Y-m-d H:i:s');
         $this->logHandler->log(3, $this->TAG, "Getting id of embryo with number as " . $embryoNumber);
         $query = "SELECT `id` FROM `embryo` WHERE `embryo_no` = '{$embryoNumber}'";
         $result = $this->database->runMySQLQuery($query, true);
         if (sizeof($result) == 1) {
            $id = $result[0]['id'];
            $this->logHandler->log(4, $this->TAG, "ID of embryo " . $embryoNumber . " gotten as " . $id);
            return $id;
         } 
         else if(sizeof($result) == 0 && $insert == true) {
            $query = "INSERT INTO `embryo`(`embryo_no`,`date_added`) VALUES ('$embryoNumber','$time')";
            $result = $this->database->runMySQLQuery($query, false);
            $id = mysql_insert_id();
            $this->logHandler->log(3, $this->TAG, "Created a new embryo record with the embryo number " . $embryoNumber . " and id as " . $id);
            return $id;
         }
         else {
            $this->logHandler->log(2, $this->TAG, "There is more than one embryo with the number '" . $embryoNumber . "' gotten, returning -1");
            return -1;
         }
      }
      else {
         return -1;
      }
      
   }
   public function getStrawID($strawNumber, $insert) {
      if($strawNumber != "") {
         $time = $this->getTime('Y-m-d H:i:s');
         $this->logHandler->log(3, $this->TAG, "Getting id of straw with number as " . $strawNumber);
         $query = "SELECT `id` FROM `straw` WHERE `straw_number` = '{$strawNumber}'";
         $result = $this->database->runMySQLQuery($query, true);
         if (sizeof($result) == 1) {
            $id = $result[0]['id'];
            $this->logHandler->log(4, $this->TAG, "ID of straw " . $strawNumber . " gotten as " . $id);
            return $id;
         } 
         else if(sizeof($result) == 0 && $insert == true) {
            $query = "INSERT INTO `straw`(`straw_number`,`date_added`) VALUES ('$strawNumber','$time')";
            $result = $this->database->runMySQLQuery($query, false);
            $id = mysql_insert_id();
            $this->logHandler->log(3, $this->TAG, "Created a new straw record with the straw number " . $strawNumber . " and id as " . $id);
            return $id;
         }
         else {
            $this->logHandler->log(2, $this->TAG, "There is more than one straw with the number '" . $strawNumber . "' gotten, returning -1");
            return -1;
         }
      }
      else {
         return -1;
      }
      
   }
   
   private function getTime($format) {
      $timeZone = $this->settings['time_zone'];
      $time = new DateTime('now', new DateTimeZone($timeZone));
      $formatedTime = $time->format($format);
      $this->logHandler->log(4, $this->TAG, "returning time '" . $formatedTime . "' using format '" . $format . "'");
      return $formatedTime;
   }
}
?>