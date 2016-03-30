<?php

include '../../common/log.php';
include '../../common/database.php';
class MilkDataGetter{
   private $TAG = "get_milk_data.php";
   private $ROOT = "../../../";
   private $settingsDir;
   private $settings;
   private $logHandler;
   private $database;
   private $jsonObject;
   private $returnResult;
   
   public function __construct(){
      $this->settingsDir = $this->ROOT . "config/settings.ini";
      $this->logHandler = new LogHandler($this->ROOT);
      $this->logHandler->log(3, $this->TAG, "Starting MilkDataGetter");
      $this->getPOSTJsonObject();
      //echo "calling getSettings";
      $this->getSettings();
      $this->database = new DatabaseHandler($this->ROOT);
      var_dump($this->database);
      $this->returnResult = array();
      
      $this->sendMilkingDetails();
      die();
   }

   private function getSettings() {
      //echo "gettting setthings";
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
   
   private function getPOSTJsonObject() {
      $this->logHandler->log(3, $this->TAG, "obtaining POST request");
      $this->jsonObject = json_decode($_POST["json"], true);
      $this->logHandler->log(4, $this->TAG, "json_decode returned: " . print_r($this->jsonObject, true));
   }
   
   private function sendMilkingDetails() {
      $query = "SELECT * FROM farmer";
      $result = $this->database->runMySQLQuery($query, true);
      $this->returnResult = $result;
      //print_r($result);
      for($fIndex = 0; $fIndex < sizeof($this->returnResult); $fIndex++){
         $query = "SELECT * FROM cow WHERE farmer_id = {$this->returnResult[$fIndex]['id']}";
         $result = $this->database->runMySQLQuery($query, true);
         
         $this->returnResult[$fIndex]['cows'] = $result;
         for($cIndex = 0; $cIndex < sizeof($this->returnResult[$fIndex]['cows']); $cIndex++){
            $query = "SELECT * FROM milk_production WHERE cow_id = {$this->returnResult[$fIndex]['cows'][$cIndex]['id']}";
            $result = $this->database->runMySQLQuery($query, true);
            //$this->returnResult[$fIndex]['cows'][$cIndex]['milk_production'] = $result;
            
            $milkingDays = array();
            foreach($result as $currRes){
               if(!isset($milkingDays[$currRes['date']])){
                  $milkingDays[$currRes['date']] = $currRes;
                  $milkingDays[$currRes['date']]['Morning'] = 0;
                  $milkingDays[$currRes['date']]['Afternoon'] = 0;
                  $milkingDays[$currRes['date']]['Evening'] = 0;
                  $milkingDays[$currRes['date']]['Combined'] = 0;
                  $milkingDays[$currRes['date']][$currRes['time']] = $currRes['quantity'];
                  unset($milkingDays['quantity']);
               }
               else{
                  $milkingDays[$currRes['date']][$currRes['time']] = $currRes['quantity'];
                  $milkingDays[$currRes['date']][$currRes['time']."_t"] = $currRes['quantity_type'];
               }
            }
            
            $this->returnResult[$fIndex]['cows'][$cIndex]['milk_production'] = array();
            foreach($milkingDays as $currDay){
               array_push($this->returnResult[$fIndex]['cows'][$cIndex]['milk_production'], $currDay);
            }

            $query = "SELECT a.* FROM cow_event AS a INNER JOIN event AS b ON a.event_id = b.id WHERE b.name = 'Birth' AND a.birth_type = 'Normal'";
            $result = $this->database->runMySQLQuery($query, true);
            
            $this->returnResult[$fIndex]['cows'][$cIndex]['births'] = $result;
         }
      }
      
      $this->logHandler->log(3, $this->TAG, "Finished getting milking data. Sending to forntend");
      echo json_encode($this->returnResult);
   }
   
}
$obj = new MilkDataGetter();
?>
