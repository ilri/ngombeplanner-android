<?php
/**
 *
 */
class AlertHandler{
   private $TAG = "alerts.php";
   private $ROOT;

   private $database;
   private $logHandler;
   private $translator;
   private $settings;
   private $sender;
   private $smsUsername;
   private $smsPassword;
   private $prefix;
   private $apiURL;
   private $project;

   public function __construct($rootDir = "../../",$database, $logHandler, $project = null) {
      $this->ROOT = $rootDir;

      $this->database = $database;
      $this->logHandler = $logHandler;
      $this->prefix = "256";
      $this->apiURL = "http://sms.smsone.co.ug:8866/cgi‐bin/sendsms";

      include_once $this->ROOT.'php/common/translate.php';
      $this->translator = new Translator($this->logHandler, $this->database);

      $this->settings = parse_ini_file($this->ROOT."config/settings.ini");
      $this->sender = $this->settings['sms_sender'];
      $this->smsUsername = $this->settings['sms_api_user'];
      $this->smsPassword = $this->settings['sms_api_pass'];
      if($project == null) $project = $this->settings['sms_api_project'];
      $this->project = $project;
   }

   public function sendDeathAlert($cowID){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn, cow.name AS cow_name, cow.ear_tag_number AS cow_etn".
              " FROM cow INNER JOIN farmer ON cow.old_farmer_id = farmer.id LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE cow.id = $cowID";

      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) == 1){
         //send farmer instructions
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("fm_cow_dth");
         $fMessage = $messageInfo['sms_text'];
         $cow = $result[0]['cow_name'] . " - " . $result[0]['cow_etn'];

         $this->sendSMS($result[0]['farmer_mn'], sprintf($fMessage, $cow), $messageInfo['id']);

         $cowIDMessage = "";
         if(strlen($result[0]['cow_name']) == 0) $cowIDMessage = "with the ear tag number ".$result[0]['cow_etn'];
         else if(strlen($result[0]['cow_etn']) == 0) $cowIDMessage = "going by the name ".$result[0]['cow_name'];
         else $cowIDMessage = "going by the name ".$result[0]['cow_name']." and ear tag number ".$result[0]['cow_etn'];

         $vName = explode(" ", $result[0]['extension_personnel_name']);

         $messageInfo = $this->getMessage("sc_cow_dth", "en");
         $vMessage = $messageInfo['sms_text'];

         if(strlen($result[0]['extension_personnel_mn']) > 0)
            $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $vName[0], $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowIDMessage), $messageInfo['id']);
         else{
            $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowIDMessage), $messageInfo['id']);
         }
      }
   }

   public function sendSicknessAlert($cowID){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn, cow.name AS cow_name, cow.ear_tag_number AS cow_etn".
              " FROM cow INNER JOIN farmer ON cow.farmer_id = farmer.id LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE cow.id = $cowID";

      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) == 1){
         //send farmer instructions
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("fm_cow_sc");
         $fMessage = $messageInfo['sms_text'];
         $cow = $result[0]['cow_name'] . " - " . $result[0]['cow_etn'];
         $this->sendSMS($result[0]['farmer_mn'], sprintf($fMessage, $result[0]['farmer_name'], $cow), $messageInfo['id']);

         $cowIDMessage = "";
         if(strlen($result[0]['cow_name']) == 0) $cowIDMessage = "with the ear tag number ".$result[0]['cow_etn'];
         else if(strlen($result[0]['cow_etn']) == 0) $cowIDMessage = "going by the name ".$result[0]['cow_name'];
         else $cowIDMessage = "going by the name ".$result[0]['cow_name']." and ear tag number ".$result[0]['cow_etn'];

         $vName = explode(" ", $result[0]['extension_personnel_name']);

         $messageInfo = $this->getMessage("sc_cow_sc", "en");
         $vMessage = $messageInfo['sms_text'];

         if(strlen($result[0]['extension_personnel_mn']) > 0)
            $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $vName[0], $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowIDMessage), $messageInfo['id']);
         else{
            $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowIDMessage), $messageInfo['id']);
         }
      }
      else{
         $this->logHandler->log(4, $this->TAG, "No farmer fetched");
      }
   }

   public function sendMiscarriageAlert($cowID){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn, cow.name AS cow_name, cow.ear_tag_number AS cow_etn".
              " FROM cow INNER JOIN farmer ON cow.farmer_id = farmer.id LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE cow.id = $cowID";

      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) == 1){
         //send farmer instructions
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("fm_cow_mc");
         $fMessage = $messageInfo['sms_text'];
         $cow = $result[0]['cow_name'] . " - " . $result[0]['cow_etn'];
         $this->sendSMS($result[0]['farmer_mn'], sprintf($fMessage, $cow), $messageInfo['id']);

         $cowIDMessage = "";
         if(strlen($result[0]['cow_name']) == 0) $cowIDMessage = "with the ear tag number ".$result[0]['cow_etn'];
         else if(strlen($result[0]['cow_etn']) == 0) $cowIDMessage = "going by the name ".$result[0]['cow_name'];
         else $cowIDMessage = "going by the name ".$result[0]['cow_name']." and ear tag number ".$result[0]['cow_etn'];

         $vName = explode(" ", $result[0]['extension_personnel_name']);

         $messageInfo = $this->getMessage("sc_cow_mc", "en");
         $vMessage = $messageInfo['sms_text'];

         if(strlen($result[0]['extension_personnel_mn']) > 0)
            $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $vName[0], $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowIDMessage), $messageInfo['id']);
         else{
            $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowIDMessage), $messageInfo['id']);
         }
      }
      else{
         $this->logHandler->log(4, $this->TAG, "No farmer fetched");
      }
   }

   public function sendMilkFluctuationAlert($cowID, $quantity, $units){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn, cow.name AS cow_name, cow.ear_tag_number AS cow_etn".
              " FROM cow INNER JOIN farmer ON cow.farmer_id = farmer.id LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE cow.id = $cowID";

      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) == 1){
         //send farmer instructions
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("fm_cow_flc");
         $fMessage = $messageInfo['sms_text'];
         $cow = $result[0]['cow_name'] . " - " . $result[0]['cow_etn'];
         $this->sendSMS($result[0]['farmer_mn'], sprintf($fMessage, $result[0]['farmer_name'], $cow), $messageInfo['id']);

         $cowIDMessage = "";
         if(strlen($result[0]['cow_name']) == 0) $cowIDMessage = "with the ear tag number ".$result[0]['cow_etn'];
         else if(strlen($result[0]['cow_etn']) == 0) $cowIDMessage = "going by the name ".$result[0]['cow_name'];
         else $cowIDMessage = "going by the name ".$result[0]['cow_name']." and ear tag number ".$result[0]['cow_etn'];

         $vName = explode(" ", $result[0]['extension_personnel_name']);

         $messageInfo = $this->getMessage("sc_cow_flc", "en");
         $vMessage = $messageInfo['sms_text'];

         if(strlen($result[0]['extension_personnel_mn']) > 0)
            $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $vName[0], $result[0]['farmer_name'], $result[0]['farmer_mn'], $quantity, $units, $cowIDMessage), $messageInfo['id']);
         else{
            $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $result[0]['farmer_mn'], $quantity, $units, $cowIDMessage), $messageInfo['id']);
         }
      }
      else{
         $this->logHandler->log(4, $this->TAG, "No farmer fetched");
      }
   }

   public function sendAcquisitionAlert($farmerID, $cowNumber){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn".
              " FROM farmer LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE farmer.id = $farmerID";
      $result = $this->database->runMySQLQuery($query, true);

      if(count($result) == 1){
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("fm_cow_acq");
         $fMessage = $messageInfo['sms_text'];
         $this->sendSMS($result[0]['farmer_mn'], sprintf($fMessage, $result[0]['farmer_name']), $messageInfo['id']);

         $cows = "a new cow.";
         if($cowNumber>1) $cows = $cowNumber." new cows.";

         $vName = explode(" ", $result[0]['extension_personnel_name']);

         $messageInfo = $this->getMessage("sc_cow_acq", "en");
         $vMessage = $messageInfo['sms_text'];

         if(strlen($result[0]['extension_personnel_mn']) > 0)
            $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $vName[0], $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowNumber), $messageInfo['id']);
         else{
            $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $result[0]['farmer_mn'], $cowNumber), $messageInfo['id']);
         }
      }
   }

   public function sendBirthAlert($farmerID){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn".
              " FROM farmer LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE farmer.id = $farmerID";
      $result = $this->database->runMySQLQuery($query, true);

      if(count($result) == 1){
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("sc_cow_bth", "en");
         $vMessage = $messageInfo['sms_text'];

         if(strlen($result[0]['extension_personnel_mn']) > 0){
            $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $result[0]['extension_personnel_name'], $result[0]['farmer_name'], $result[0]['farmer_mn']), $messageInfo['id']);
         }
         else{
            $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $oldNumber, $newNumber), $messageInfo['id']);
         }
      }
   }

   public function sendRegistrationAlert($farmerID){
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn".
              " FROM farmer LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE farmer.id = $farmerID";
      $result = $this->database->runMySQLQuery($query, true);

      if(count($result) == 1){
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

         $messageInfo = $this->getMessage("fm_scs_reg");
         $fMessage = $messageInfo['sms_text'];
         $this->sendSMS($result[0]['farmer_mn'], sprintf($fMessage, $result[0]['farmer_name']), $messageInfo['id']);
      }
   }

   public function sendNumberChangeAlert($farmerID, $oldNumber, $newNumber){
      $query = "SELECT farmer.name as farmer_name, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn".
              " FROM farmer LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE farmer.id = $farmerID";

      $result = $this->database->runMySQLQuery($query, true);

      $messageInfo = $this->getMessage("sc_fm_chng_no", "en");
      $vMessage = $messageInfo['sms_text'];

      if(strlen($result[0]['extension_personnel_mn']) > 0){
         $this->sendSMS($result[0]['extension_personnel_mn'], sprintf($vMessage, $result[0]['extension_personnel_name'], $result[0]['farmer_name'], $oldNumber, $newNumber), $messageInfo['id']);
      }
      else{
         $this->sendSMS($this->settings['admin_sms_number'], sprintf($vMessage, "admin", $result[0]['farmer_name'], $oldNumber, $newNumber), $messageInfo['id']);
      }
   }

   private function sendSMS($number, $message, $smsType = -1, $record = true){
      if($this->project == "np_kenya") {
         if(substr($number, 0, 1) == "0"){
            $number = "+254" . substr($number, 1);
         }

         $this->logHandler->log(3, $this->TAG, "About to queue NP KE SMS for ".$number);
         $this->logHandler->log(3, $this->TAG, "SMS looks like ".print_r($message, true));

         $message = mysql_real_escape_string($message);

         if($smsType == -1){
            $query = "INSERT INTO sms_queue (number, text2send, text_status, project) VALUES('{$number}','{$message}', 'not sent', '{$this->project}')";
         }
         else{
            $query = "INSERT INTO sms_queue (number, text2send, text_status, sms_type, project) VALUES('{$number}','{$message}', 'not sent', {$smsType}, '{$this->project}')";
         }
         $this->database->runMySQLQuery($query, FALSE);
      }
      else if($this->project == "eadd_ug") {
         if(substr($number, 0, 1) == "0"){
            $number = $this->prefix . substr($number, 1);
         }

         $this->logHandler->log(3, $this->TAG, "About to queue EADD UG SMS for ".$number);
         $this->logHandler->log(3, $this->TAG, "SMS looks like ".$message);

         //send the message to SMSOne API
         $result = $this->sendRawSMSHTTPCall($number, $message);
         parse_str($result, $resultParts);
         $sent = "eadd_ug not sent";
         if($resultParts['status'] == 0) $sent = "eadd_ug sent";
         //record the message in the database
         $message = mysql_real_escape_string($message);
         if($record == true) {
             if($smsType == -1){
                 $query = "INSERT INTO sms_queue (number, text2send, text_status, project) VALUES('{$number}','{$message}', '$sent', '{$this->project}')";
             }
             else{
                 $query = "INSERT INTO sms_queue (number, text2send, text_status, sms_type, project) VALUES('{$number}','{$message}', '$sent', {$smsType}, '{$this->project}')";
             }
             $this->database->runMySQLQuery($query, FALSE);
         }
         if($sent == "sent") return true;
         return false;
      }
   }

   public function sendRawSMSHTTPCall($number, $message) {
      $request = "";
      $request .= urlencode("Mocean-Username") . "=" . urlencode($this->smsUsername) .
      "&";
      $request .= urlencode("Mocean-Password") . "=" . urlencode($this->smsPassword) .
      "&";
      $request .= urlencode("Mocean-From") . "=" . urlencode($this->sender) .
      "&";
      $request .= urlencode("Mocean-To") . "=" . urlencode($number) .
      "&";
      $request .= urlencode("Mocean-Coding") . "=" . urlencode("1") . "&";
      $request .= urlencode("Mocean-Url-Text") . "=" . urlencode($message);
      // Build the header
      $host = "sms.smsone.co.ug";
      $script = "/cgi-bin/sendsms";
      $request_length = 0;
      $method = "POST";

      //Now construct the headers.
      $header = "$method $script HTTP/1.1\r\n";
      $header .= "Host: $host\r\n";
      $header .= "Content-Type: application/x-www-form-urlencoded\r\n";
      $header .= "Content-length: " . strlen($request) . "\r\n";
      $header .= "Connection: close\r\n\r\n";
      $this->logHandler->log(3, $this->TAG, "Headers for sending SMS = ".$header);
      // Open the connection
      $port = 8866;
      $socket = @fsockopen($host, $port, $errno, $errstr);
      if ($socket){
         //open socket
         fputs($socket, $header . $request);
         // Get the response
         while (!feof($socket)) {
            $output[] = fgets($socket); //get the results
         }
         fclose($socket);
      }
      else{
         die ("connection failed....\r\n");
      }
      $this->logHandler->log(3, $this->TAG, "Raw response from SMS API = ".print_r($output, true));
      return $output[8];
   }

   public function sendAllUnsentSMSs() {
      if($this->project == "eadd_ug") {
         $query = "select number, text2send, id from sms_queue where text_status != 'sent' and project = '{$this->project}' and (schedule_time = '0000-00-00 00:00:00' or schedule_time < NOW())";
         $result = $this->database->runMySQLQuery($query, true);
         foreach ($result as $currSMS) {
             $sent = $this->sendSMS($currSMS['number'], $currSMS['text2send'], -1, false);
             if($sent == true) {
                 $query = "update sms_queue set text_status = 'sent' where id = {$currSMS['id']}";
                 $this->database->runMySQLQuery($query, false);
             }
         }
      }
      else{
         $this->logHandler->log(2, $this->TAG, "Context project is not eadd_ug. Don't know how to handle that");
      }
   }

   private function getMessage($code, $locale = -1){
      if($locale == -1) $locale = $this->translator->getLocale();
      $query = "SELECT * FROM sms_types where sms_code = '{$code}' AND locale = '{$locale}'";

      $result = $this->database->runMySQLQuery($query, TRUE);

      return $result[0];
   }

   /**
    * Send a message to the site coordinator when we get an estrus event
    * @param type $cowId
    */
   public function sendSCEstrusMessage($cowId){
      $this->logHandler->log(3, $this->TAG, "Adding a SC notification message for a cow which is on heat");
      $query = "SELECT farmer.name as farmer_name, farmer.mobile_no as farmer_mn, extension_personnel.name AS extension_personnel_name, extension_personnel.mobile_no AS extension_personnel_mn, if(cow.name != '', cow.name, cow.ear_tag_number) AS cow_name ".
              "FROM cow INNER JOIN farmer ON cow.farmer_id = farmer.id LEFT JOIN extension_personnel ON farmer.extension_personnel_id = extension_personnel.id WHERE cow.id = $cowId";
      $result = $this->database->runMySQLQuery($query, true);

      if(count($result) == 1){
         if($this->translator->isLocaleSet() == FALSE)
            $this->translator->setLocale($result[0]['farmer_mn']);

            $vName = explode(" ", $result[0]['extension_personnel_name']);
            $fName = explode(" ", $result[0]['farmer_name']);

         $messageInfo = $this->getMessage("sc_cow_est_now");
         $fMessage = $messageInfo['sms_text'];

         $mssg = sprintf($fMessage, $vName[0], $fName[0], $result[0]['farmer_mn'], $result[0]['cow_name']);
         $this->sendSMS($result[0]['extension_personnel_mn'], $mssg, $messageInfo['id']);
      }
      $this->logHandler->log(3, $this->TAG, "SC notification added successfully");
   }
}

?>
