<?php

/* 
 * This file sends cow fertility notifications to Ngombe Planner users.
 * This file was made with the assumption that it will be run as a cron
 * job
 * 
 * Notifications sent by this file include:
 *    - When to expect estrus notification
 *    - Pregnancy confirmation notification
 */
class Notification {
   
   private $KANNEL_URL = "http://127.0.0.1:13013/cgi-bin/sendsms";
   private $db;
   
   public function __construct($kannelURL) {
      $this->KANNEL_URL = $kannelURL;
      
      $dbDriver = "mysql:dbname=azizi_mistro;host=192.168.5.7:3306";
      $dbUser = "azizi_scriptsrw";
      $dbPassword = "*R&\$skUV*E8a";
      $this->initDBConnection($dbDriver, $dbUser, $dbPassword);
      
      $this->getEstrusCows();
   }
   
   private function initDBConnection($dbDriver, $dbUser, $dbPassword){
      $this->db = new PDO($dbDriver, $dbUser, $dbPassword);
   }
   
   private function getEstrusCows(){
      $timeOfBirth = time() - (86400 * 38);//today is the 38th day after birth
      $dateOfBirth = date('Y-m-d', $timeOfBirth);
      
      $query = "SELECT a.cow_id, a.event_date".
              " FROM cow_event AS a".
              " INNER JOIN event AS b ON a.event_id = b.id".
              " WHERE b.name  = 'Calving' AND a.event_date = '{$dateOfBirth}'".
              " GROUP BY a.cow_id";//get only one calving for a cow on that day
              
      $estrusCows = $this->runQuery($query);
      
      if(is_array($estrusCows)){
         for($index = 0; $index < count($estrusCows); $index++){//get the associated farmer and 
            $query = "SELECT a.ear_tag_number, a.name as cow_name, b.name as farmer_name, b.mobile_no AS farmer_number, c.name as vet_name, c.mobile_no AS vet_number".
                     " FROM cow AS a".
                     " INNER JOIN farmer AS b ON a.farmer_id = b.id".
                     " LEFT JOIN vet AS c ON b.extension_personnel_id = c.id".
                     " WHERE a.id = {$estrusCows[$index]['cow_id']} ";

            $result = $this->runQuery($query);

            if(is_array($result) && count($result) == 1){
               $estrusCows[$index] = array_merge($estrusCows[$index], $result[0]);
            }
            else{
               echo "Something went wrong while trying to fetch extra information on the calving event ".print_r($estrusCows[$index], TRUE);
            }
         }
         
         if(count($estrusCows) == 0){
            echo "No calvings recorded on ".$dateOfBirth;
         }
         
         foreach ($estrusCows as $currCow){
            $this->send1stEstrusSMS($currCow['farmer_name'], $currCow['farmer_number'], $currCow['cow_name'], $currCow['ear_tag_number'], $currCow['vet_name'], $currCow['vet_number']);
         }
      }
      else{
         echo "Error occured while trying to fetch data from the database";
      }
      
      
   }
   
   private function send1stEstrusSMS($farmerName, $farmerNumber, $cowName, $cowETN, $vetName, $vetNumber){
      
      $cowIDMessage = "";
      if(strlen($cowName) == 0) $cowIDMessage = "with the ear tag number ".$cowETN;
      else if(strlen($cowETN) == 0) $cowIDMessage = "going by the name ".$cowName;
      else $cowIDMessage = "going by the name ".$cowName." and ear tag number ".$cowETN;
      
      $cow = $cowName . " (" . $cowETN . ")";
      $smsToFarmer = $cow . " should be ready for servicing in 7 days. Start checking for signs of heat";
      
      $this->sendSMS($farmerNumber, $smsToFarmer);
      
      $smsToSC = $farmerName . " has a cow " . $cowIDMessage . " that should be ready for servicing in 7 days. Please lias with the farmer as preparation for the servicing";
      $this->sendSMS($vetNumber, $smsToSC);
   }
   
   private function sendSMS($number, $text){
      if(substr($number, 0, 1) == "0"){
         $number = "+254" . substr($number, 1);
      }
      
      $query = "INSERT INTO sms_queue(number, text2send, text_status) values('$number', '$text', 'not sent')";
      $this->runQuery($query);
   }
   
   private function runQuery($query){
      $stmt = $this->db->query($query);
      return $stmt->fetch(PDO::FETCH_ASSOC);
   }
}
?>
