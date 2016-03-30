<?php
include_once '../common/log.php';
include_once '../common/database.php';
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class API{
   private $TAG = "api.php";
   private $ROOT = "../../";
   private $settingsDir;
   private $settings;
   private $codes;
   private $jsonObject;
   private $logHandler;
   private $database;
   
   public function __construct(){
      $this->settingsDir = $this->ROOT."config/settings.ini";
      $this->logHandler = new LogHandler;
      $this->logHandler->log(3, $this->TAG,"Starting CachedDataSaver");
      $this->getSettings();
      $this->database = new DatabaseHandler;
      
      $json = "";
      if($_GET['api'] == 'sms'){
         if($_GET['do'] == 'get_unsent'){
         $json = $this->getUnsentSMSs();
         }
         else if($_GET['do'] == 'add_to_queue'){
            $json = $this->addSMSToQueue();
         }
         else if($_GET['do'] == 'set_sent'){
            $json = $this->setSMSSent();
         }
         else if($_GET['do'] == 'set_milk_sms'){
            $json = $this->setMilkSMS();
         }
         else if($_GET['do'] == 'get_last_milk_sms'){
            $json = $this->getLastMilkSMS();
         }
         else if($_GET['do'] == 'schedule_sms'){
            $json = $this->scheduleSMS();
         }
         else if($_GET['do'] == 'get_milk_message'){
            $json = $this->getMilkMessage();
         }
      }
      else if($_GET['api'] == 'farm'){
         if($_GET['do'] == 'get_all_farmers'){
            $json = $this->getAllFarmers();
         }
         else if($_GET['do'] == 'get_mature_cows'){
            $json = $this->getMatureCows();
         }
         else if($_GET['do'] == 'get_todays_milk_records'){
            $json = $this->getTodaysMilkRecords();
         }
         else if($_GET['do'] == 'get_cow_estrus'){
            $json = $this->getCowEstus();
         }
         else if($_GET['do'] == 'get_cow_details'){
            $json = $this->getCowDetails();
         }
      }
      else if($_GET['api'] == 'settings'){
         if($_GET['do'] == 'get_milk_settings'){
            $json = $this->getMilkSettings();
         }
         else if($_GET['do'] == 'get_event_settings'){
            $json = $this->getEventSettings();
         }
      }
      echo $json;
   }
   
   /**
    * This function gets settings from the .ini files
    */
   private function getSettings() {
		$this->logHandler->log(3, $this->TAG,"getting settings from: ".$this->settingsDir);
		if(file_exists($this->settingsDir)) {
			$settings = parse_ini_file($this->settingsDir);
			$mysqlCreds = parse_ini_file($settings['mysql_creds']);
         $settings['mysql_creds'] = $mysqlCreds;
			$this->settings = $settings;
			$this->logHandler->log(4, $this->TAG,"settings obtained: ".print_r($this->settings, true));
		}
		else {
         $this->logHandler->log(1, $this->TAG,"unable to get settings from ".$this->settingsDir.", exiting");
			die();
		}
   }
   
   /**
    * This function returns a json object with unsent SMSs
    * 
    * @return JSON
    */
   private function getUnsentSMSs(){
      $query = "select id, number, text2send, text_status"
              . " from sms_queue"
              . " where text_status = 'not sent' and (schedule_time < now() or schedule_time = '0000-00-00 00:00:00') and number rlike '^[\+0-9]+$' and in_queue = 0 and project = '{$this->settings['sms_api_project']}'";
      $result = $this->database->runMySQLQuery($query, true);
      
      if($result == null){
         $json['data'] = array();
      }
      $json = array("error" => false, "data" => $result);
      return json_encode($json);
   }
   
   /**
    * This functions adds all unsent SMSs to a queue
    * @return type
    */
   private function addSMSToQueue(){
      $query = "update sms_queue set in_queue = 1"
              . " where text_status = 'not sent' and (schedule_time < now() or schedule_time = '0000-00-00 00:00:00') and number rlike '^[\+0-9]+$' and project = '{$this->settings['sms_api_project']}'";
      $this->database->runMySQLQuery($query, false);
      
      return json_encode(array("error" => false, "data" => array()));
   }
   
   /**
    * This function sets the SMS with the given ID to status = sent
    * 
    * @return JSON json object showing wheter the status change was successfully changed in the database
    */
   private function setSMSSent(){
      $id = $_GET['id'];
      if(is_numeric($id)){
         $query = "update sms_queue set text_status = 'sent', sending_time = now()"
              . " where id = ".$id;
         $this->database->runMySQLQuery($query, false);
         return json_encode(array("error" => false, "data" => array()));
      }
      else {
         return json_encode(array("error" => true, "data" => array()));
      }
   }
   
   /**
    * This function records a milk SMS sent to a farmer
    * 
    * @return JSON a json object showing if the record was successfully added
    */
   private function setMilkSMS(){
      $farmerID = $_GET['farmer'];
      $smsID = $_GET['sms'];
      
      if(is_numeric($farmerID) && is_numeric($smsID)){
         $query = "insert into milk_sms(farmer_id, sms_id) values(".$farmerID.", ".$smsID.")";
         $this->database->runMySQLQuery($query, false);
         return json_encode(array("error" => false, "data" => array()));
      }
      else {
         return json_encode(array("error" => true, "data" => array()));
      }
   }
   
   /**
    * This function gets the last milk SMS sent to a farmer
    * 
    * @return JSON a json object with the last sent milk SMS sent to the given farmer
    */
   private function getLastMilkSMS(){
      $farmerID = $_GET['farmer'];
      
      if(is_numeric($farmerID)){
         $query = "select sending_time from notifications_sent where farmer_id = $farmerID order by sending_time desc limit 1";
         $result = $this->database->runMySQLQuery($query, true);
         if($result == null){
            $result = array();
         }
         return json_encode(array("error" => true, "data" => $result));
      }
      else {
         return json_encode(array("error" => true, "data" => array()));
      }
   }
   
   /**
    * This function adds the given SMS into the SMS queue
    * 
    * @return JSON a json object indicating whetere the SMS was successfully scheduled or not
    */
   private function scheduleSMS(){
      $number = $_GET['number'];
      $sms = mysql_real_escape_string($_GET['sms']);
      $smsType = $_GET['sms_type'];
      $time = $_GET['time'];
      
      if(is_numeric($number) && is_numeric($smsType)){
         $query = "insert into sms_queue(number, text2send, text_status, sms_type, schedule_time, project) values('$number', '$sms', 'not sent', $smsType, '$time', '{$this->settings['sms_api_project']}')";
         $this->database->runMySQLQuery($query, false);
         return json_encode(array("error" => false, "data" => array("id" => $this->database->getLastInsertID())));
      }
      else {
         return json_encode(array("error" => true, "data" => array("id" => 0)));
      }
      
   }
   
   /**
    * This function returns an array of milk specific SMSs with the sms_code as the index
    * 
    * @return JSON
    */
   private function getMilkMessage(){
      $query = "select id, sms_code, locale, sms_text from sms_types where sms_code in ('fm_no_rec', 'cw_no_rec', 'cw_no_tm_rec', 'fm_cow_est', 'sc_cow_est')";
      $result = $this->database->runMySQLQuery($query, true);
      if($result == null){
         return json_encode(array("error" => false, "data" => array()));
      }
      else {
         $return = array();
         foreach($result as $currRes){
            $return[$currRes['sms_code']] = $currRes;
         }
         return json_encode(array("error" => false, "data" => $return));
      }
   }
   
   /**
    * This function returns an json object with all active farmers and their ids as indexes
    * 
    * @return JSON
    */
   private function getAllFarmers(){
      $query = "select id, name, mobile_no from farmer where is_active = 1";
      $result = $this->database->runMySQLQuery($query, true);
      if($result == null){
         return json_encode(array("error" => false, "data" => array(), "count" => 0));
      }
      else {
         $return = array();
         foreach($result as $currRes){
            $return[$currRes['id']] = $currRes;
         }
         return json_encode(array("error" => false, "data" => $return, "count" => count($result)));
      }
   }
   
   /**
    * This function returns a json object with all the mature cows and their ids an indexes
    * 
    * @return JSON
    */
   private function getMatureCows(){
      $farmer = $_GET['farmer'];
      $age = $_GET['age'];
      if(is_numeric($farmer) && is_numeric($age)){
         $query = "select id, name, ear_tag_number from cow where farmer_id = $farmer and sex = 'Female' and if(age_type = 'Years', 12*age, if(age_type='Days', age/30, age)) >= $age and milking_status = 'adult_milking'";
         $result = $this->database->runMySQLQuery($query, true);
         if($result == null){
            json_encode(array("error" => false, "data" => array()));
         }
         else {
            $return = array();
            foreach($result as $currRes){
               $return[$currRes['id']] = $currRes;
            }
            return json_encode(array("error" => false, "data" => $return));
         }
      }
      else {
         return json_encode(array("error" => true, "data" => array()));
      }
   }
   
   /**
    * This function returns milk records present for a farmer for the given day
    * 
    * @return JSON an object with the 
    */
   private function getTodaysMilkRecords(){
      $farmer = $_GET['farmer'];
      $cow = $_GET['cow'];
      $date = $_GET['date'];
      //check date
      if(is_numeric($farmer) && is_numeric($cow) && preg_match("/\d{4}-\d{2}-\d{2}/", $date) == 1){
         $query = "select `time` from farmer_milk where farmer_id = $farmer and cow_id = $cow and date = '$date'";
         $result = $this->database->runMySQLQuery($query, true);
         if($result == null){
            return json_encode(array("error" => false, "data" => array()));
         }
         else {
            return json_encode(array("error" => false, "data" => $result));
         }
      }
      else {
         return json_encode(array("error" => true, "data" => array()));
      }
   }
   
   /**
    * This function returns a json object with an array of estrus events recorded and the id's of the associated cows as indexes
    * @return type
    */
   private function getCowEstus(){
      $date = $_GET['date'];
      if(preg_match("/\d{4}-\d{2}-\d{2}/", $date) == 1){
         $query = "SELECT a.cow_id, a.event_date FROM cow_event AS a INNER JOIN event AS b ON a.event_id = b.id WHERE b.name  = 'Calving' AND a.event_date = '$date' GROUP BY a.cow_id";
         $result = $this->database->runMySQLQuery($query, true);
         if($result == null){
            return json_encode(array("error" => false, "data" => array(), "count" => 0));
         }
         else {
            $return = array();
            foreach($result as $currRes){
               $return[$currRes['cow_id']] = $currRes;
            }
            return json_encode(array("error" => false, "data" => $return, "count" => count($result)));
         }
      }
      else {
         return json_encode(array("error" => true, "data" => array(), "count" => 0));
      }
   }
   
   /**
    * This function returns a json object with cow details
    * @return type
    */
   private function getCowDetails(){
      $cowID = $_GET['cow'];
      
      if(is_numeric($cowID)){
         $query = "SELECT a.ear_tag_number, a.name as cow_name, b.name as farmer_name, b.mobile_no AS farmer_number, c.name as vet_name, c.mobile_no AS vet_number"
                  . " FROM cow AS a"
                  . " INNER JOIN farmer AS b ON a.farmer_id = b.id"
                  . " LEFT JOIN vet AS c ON b.extension_personnel_id = c.id"
                  . " WHERE a.id = $cowID";
         $result = $this->database->runMySQLQuery($query, true);
         if($result == null){
            return json_encode(array("error" => true, "data" => array()));
         }
         else {
            if(count($result) == 1){
               return json_encode(array("error" => false, "data" => $result[0]));
            }
            else {
               return json_encode(array("error" => true, "data" => array()));
            }
         }
      }
      else {
         return json_encode(array("error" => true, "data" => array()));
      }
   }
   
   /**
    * This function returns an array of milki settings from the database with settings_name as the key
    * 
    * @return JSON
    */
   private function getMilkSettings(){
      $query = " select settings_name, value from settings where settings_name in ('sms_sending_period', 'milk_rec_period', 'sms_sending_time')";
      $result = $this->database->runMySQLQuery($query, true);
      if($result == null){
         return json_encode(array("error" => true, "data" => array()));
      }
      else {
         $return = array();
         foreach($result as $currRes){
            $return[$currRes['settings_name']] = $currRes;
         }
         return json_encode(array("error" => false, "data" => $return));
      }
   }
   
   /**
    * This function returns an array of event settings with the event name as the key
    * 
    * @return JSON
    */
   private function getEventSettings(){
      $query = "select id, event, time, time_units from event_vtime";
      $result = $this->database->runMySQLQuery($query, true);
      if($result == null){
         return json_encode(array("error" => true, "data" => array()));
      }
      else {
         $return = array();
         foreach($result as $currRes){
            $return[$currRes['event']] = $currRes;
         }
         return json_encode(array("error" => false, "data" => $return));
      }
   }
}

$sms = new API();
?>