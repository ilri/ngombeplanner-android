<?php

include '../common/log.php';
include '../common/database.php';

class CowFetchHandler {

   private $TAG = "fetch_cows.php";
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
      $this->logHandler->log(3, $this->TAG, "Starting CowFetchHandler");
      $this->getPOSTJsonObject();
      $this->getSettings();
      $this->getCodes();
      $this->database = new DatabaseHandler;

      
      $query = "SELECT * FROM cow WHERE farmer_id = {$this->jsonObject['id']} ORDER BY id";
      $cows = $this->database->runMySQLQuery($query, true);
      if (is_array($cows)) {
         for ($cowIndex = 0; $cowIndex < count($cows); $cowIndex++) {
            /*//get events
            $query = "SELECT a.*, b.name AS event_name, c.name as cause_of_death FROM cow_event AS a INNER JOIN event AS b ON a.event_id = b.id LEFT JOIN cause_of_death AS c ON a.cod_id = c.id WHERE a.cow_id = " . $cows[$cowIndex]['id'];
            $cows[$cowIndex]['events'] = $this->database->runMySQLQuery($query, true);

            //get milking data
            $query = "SELECT a.* FROM milk_production AS a WHERE a.cow_id = " . $cows[$cowIndex]['id'];
            $cows[$cowIndex]['milk_production'] = $this->database->runMySQLQuery($query, true);*/

            $query = "SELECT b.name FROM cow_breed AS a INNER JOIN breed AS b ON a.breed_id = b.id WHERE a.cow_id = {$cows[$cowIndex]['id']}";
            $breeds = $this->database->runMySQLQuery($query, true);
            $cBreeds = array();

            for($breedIndex = 0; $breedIndex < count($breeds); $breedIndex++){
               $cBreeds[$breedIndex] = $breeds[$breedIndex]['name'];
            }
            $cows[$cowIndex]['breed'] = $cBreeds;

            $query = "SELECT b.name, a.specification FROM cow_deformity AS a INNER JOIN deformity AS b ON a.deformity_id = b.id WHERE a.cow_id = {$cows[$cowIndex]['id']}";
            $deform = $this->database->runMySQLQuery($query, true);
            $cDeform = array();

            for($deformIndex = 0; $deformIndex < count($deform); $deformIndex++){
               $cDeform[$deformIndex] = $deform[$deformIndex]['name'];

               if($cDeform[$deformIndex] == "Other"){
                  $cows[$cowIndex]['other_deformity'] = $deform[$deformIndex]['specification'];
               }
            }
            if(!isset($cows[$cowIndex]['other_deformity'])){
               $cows[$cowIndex]['other_deformity'] = "";
            }
            $cows[$cowIndex]['deformity'] = $cDeform;

            $country = "";
            if($cows[$cowIndex]['country_id'] != NULL && strlen($cows[$cowIndex]['country_id']) > 0){
               $query = "SELECT name FROM country WHERE id = {$cows[$cowIndex]['country_id']}";
               $countries = $this->database->runMySQLQuery($query, true);
               if(count($countries) == 1) $country = $countries[0]['name'];
            }
            $cows[$cowIndex]['country'] = $country;

            $straw = "";
            if($cows[$cowIndex]['straw_id'] != NULL && strlen($cows[$cowIndex]['straw_id']) > 0){
               $query = "SELECT straw_number FROM straw WHERE id = {$cows[$cowIndex]['straw_id']}";
               $straws = $this->database->runMySQLQuery($query, true);
               if(count($straws) == 1)$straw = $straws[0]['straw_number'];
            }
            $cows[$cowIndex]['straw'] = $straw;

            $embryo = "";
            if($cows[$cowIndex]['embryo_id'] != null & strlen($cows[$cowIndex]['embryo_id']) > 0){
               $query = "SELECT embryo_no FROM embryo WHERE id = {$cows[$cowIndex]['embryo_id']}";
               $embryos = $this->database->runMySQLQuery($query, true);
               if(count($embryos) == 1)$embryo = $embryos[0]['embryo_no'];
            }
            $cows[$cowIndex]['embryo'] = $embryo;
         }
      } else {
         $cows = array();
      }

      echo json_encode($cows);
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

}

$obj = new CowFetchHandler;
?>
