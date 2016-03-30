<?php

/* 
 * Gateway file to the ngombe_planner ussd application.
 *  - Please do not call echo command directly from anywhere
 *    else apart form the sendMessage function
 *  - Call the processUserResponse method when you are ready to handle
 *    the message recieved from the user
 */
class NgombePlannerUSSD {
   private $ROOT = "./";
   private $TAG = "NgombePlannerUSSD";
   private $settings;
   private $logHandler;
   private $database;
   private $translator;
   private $ussdPages;
   private $ussdPagesTest;
   private $testGroup;
   
   public function __construct(){
      include_once $this->ROOT.'php/common/database.php';
      include_once $this->ROOT.'php/common/log.php';
      include_once $this->ROOT.'php/common/translate.php';
      include_once $this->ROOT.'ussd/php/mod_ussd_pages.php';
      include_once $this->ROOT.'ussd/php/mod_ussd_pages_test.php';
      
      $this->database = new DatabaseHandler($this->ROOT);
      $this->logHandler = new LogHandler($this->ROOT);
      $this->translator = new Translator($this->logHandler, $this->database);
      
      $this->logHandler->log(3, $this->TAG, "Starting the USSD Handler");
      
      $this->loadSettings();
      
      $this->testGroup = array('715023805','726567797');
   }
   
   /**
    * This function sends the message to the client. Note that it does not translate
    * the text before sending. Use sendUTMessage if you want the text translated before it's sent
    * 
    * @param type $translatedText The text you want to send
    * @param type $phoneNumber The phone number associated with the current session
    * @param type $closeSession True if you want the session to end after message is sent
    */
   private function sendMessage($translatedText, $phoneNumber, $closeSession){
      if($closeSession === true){
         $prefix = "END ";
         $this->endSesssion($phoneNumber);
      }
      else{
         $prefix = "CON ";
      }
      
      $this->logHandler->log(3, $this->TAG, "About to send the following message to user ".$prefix.$translatedText);
      
      $escapedString = mysql_real_escape_string($prefix.$translatedText);
      $query = "INSERT INTO ussd_page_cache(phone_number, last_page) VALUES('$phoneNumber', '$escapedString') ON DUPLICATE KEY UPDATE last_page = '$escapedString'";
      $this->database->runMySQLQuery($query, false);
      
      $menuLength = strlen($prefix.$translatedText);
      $query = "UPDATE ussd_session SET menu_length = {$menuLength} WHERE phone_number = '{$phoneNumber}' AND is_active = 1";
      $this->database->runMySQLQuery($query, false);
      
      echo $prefix.$translatedText;
   }
   
   /**
    * This function is responsible for sending messages to the user.
    * Please do not use echo command else
    * 
    * @param type $message
    * @param type $closeSession
    */
   private function sendUTMessage($textContainer, $phoneNumber, $closeSession){
      $translatedMssg = $this->translator->getText($textContainer);
              
      $this->sendMessage($translatedMssg, $phoneNumber, $closeSession);
   }
   
   /**
    * This function recieves message from the user. Also handle the session.
    * Similar to TrafficController in Repository project
    */
   public function processUserResponse(){
      $this->logHandler->log(3, $this->TAG, "Processing USSD request " .  print_r($_REQUEST, true));
      $sessionId = $_REQUEST["sessionId"];
      $serviceCode = $_REQUEST["serviceCode"];//*384*4564#
      $pNumber = $_REQUEST["phoneNumber"];//appears as +2547NNNNNNNN
      $phoneNumber = str_replace("+254", "", $pNumber);
      $text = $_REQUEST["text"];
      if(strlen($sessionId) === 0 || strlen($serviceCode) === 0 || strlen($phoneNumber) === 0){
         $this->logHandler->log(1, $this->TAG, "Either session id or service code or phone number not provided in USSD request. Exiting");
         $this->sendUTMessage(Translator::$SOMETHING_WRONG_NGOMBE_PLANNER, $phoneNumber, true);
      }
      else{
         $this->translator->setLocale($phoneNumber);
         $this->ussdPages = new Pages($this->database, $this->translator, $this->logHandler, $phoneNumber, $sessionId);
         $this->ussdPagesTest = new PagesT($this->database, $this->translator, $this->logHandler, $phoneNumber, $sessionId);
         
         //auth the user
         $this->logHandler->log(3, $this->TAG, "All session variables provided. About to authenticate user");
         $query = "SELECT id FROM farmer WHERE mobile_no LIKE '%{$phoneNumber}'";
         $result = $this->database->runMySQLQuery($query, true);
         
         //clean up the sessions
         $cleanSessionId = $this->isSessionClean($phoneNumber, $text); //TODO: uncomment
         if($cleanSessionId != -1){//session is not clean
            if($this->updateUncleanSession($cleanSessionId, $phoneNumber, $sessionId)){//session updated successfully
               //no need to continue
               return;
            }
         }
         
         //do even more house cleaning
         $sessionStatus = $this->isInSession($phoneNumber, $sessionId);
         if($sessionStatus === -1){//phone number is in another session
            $this->logHandler->log(3, $this->TAG, $phoneNumber . " initiated more than one session. Checking if user can continue with the last session");
            
            //check time for last session
            //
            $prevSessionId = $this->canUserContinueLastSession($phoneNumber, $sessionId); //TODO: uncomment
            if($prevSessionId != -1){//user can continue with previous session
               //resume last session
               
               $this->startSession($sessionId, $serviceCode, $phoneNumber, "", $prevSessionId);
               $query = "SELECT last_page FROM ussd_page_cache WHERE phone_number = '$phoneNumber'";
               $pageCache = $this->database->runMySQLQuery($query, true);
               if(count($pageCache) > 0){
                  $this->logHandler->log(3, $this->TAG, "Sending ".$phoneNumber." text from the last session");//TODO: switch to info
                  $this->logHandler->log(3, $this->TAG, "Text from the last session = ".$pageCache[0]['last_page']);//TODO: switch to debug
                  
                  $menuLength = strlen($pageCache[0]['last_page']);
                  $query = "UPDATE ussd_session SET menu_length = {$menuLength} WHERE phone_number = '{$phoneNumber}' AND is_active = 1";
                  $this->database->runMySQLQuery($query, FALSE);
                  
                  echo $pageCache[0]['last_page'];
                  return;
               }
               else{
                  $this->logHandler->log(2, $this->TAG, "Unable to resume last session with client. There was no page in cache available for ".$phoneNumber);
               }
            }
            else{
               //$this->logHandler->log(2, $this->TAG, "Unable to resume from previous session probably because it was too old"); //TODO: uncomment
               $this->endOtherSesssions($phoneNumber, $sessionId);
               $sessionStatus = -2;
            }
         }
         
         if(is_array($result) && count($result) === 1){//number registered
            $this->logHandler->log(3, $this->TAG, "User authenticated as farmer with id = ".$result[0]['id']);
            $updateSession = TRUE;
            if($sessionStatus === -2){
               $this->startSession($sessionId, $serviceCode, $phoneNumber);
               $this->logHandler->log(3, $this->TAG, "First time for user in this session. Taking him/her to homepage");
               $updateSession = FALSE;
            }
            
            if(in_array($phoneNumber, $this->testGroup)){
               $this->logHandler->log(3, $this->TAG, $phoneNumber . " is in test group");
               $nextPage = $this->ussdPagesTest->getNextPage($serviceCode, $text, $updateSession);
            }
            else{
               $nextPage = $this->ussdPages->getNextPage($serviceCode, $text, $updateSession);
            }
            $this->sendMessage($nextPage, $phoneNumber, false);
         }
         else{//number not registered
            if($sessionStatus === -2){
               $this->startSession($sessionId, $serviceCode, $phoneNumber, Pages::$PAGE_CHANGE_NUMBER);
               $this->logHandler->log(3, $this->TAG, "First time for user in this session. Taking him/her to change number page");
            }
            
            $this->logHandler->log(3, $this->TAG, $phoneNumber . " is not registered with Ngombe Planner but dialed the shortcode");
            
            $query = "SELECT last_page,last_page_data FROM ussd_session WHERE phone_number = '{$phoneNumber}' AND is_active = 1";
            $result = $this->database->runMySQLQuery($query, true);
            
            $this->logHandler->log(3, $this->TAG, " result = " . print_r($result, TRUE));
            
            if(is_array($result) && $result[0]['last_page'] == Pages::$PAGE_CHANGE_NUMBER){
               $lastMessage = $result[0]['last_page_data'];
               
               if(in_array($phoneNumber, $this->testGroup)){
                  $messageDiff = $this->ussdPagesTest->getMessageDiff($text, $lastMessage);
               }
               else{
                  $messageDiff = $this->ussdPages->getMessageDiff($text, $lastMessage);
               }
               
               if(strlen($messageDiff) > 0){//user has provided an input
                  if(in_array($phoneNumber, $this->testGroup)){
                     $nextPage = $this->ussdPagesTest->getNextPage($serviceCode, $text, TRUE);
                  }
                  else{
                     $nextPage = $this->ussdPages->getNextPage($serviceCode, $text, TRUE);
                  }
                  $this->sendMessage($nextPage, $phoneNumber, TRUE);
               }
               else{
                  if(in_array($phoneNumber, $this->testGroup)){
                     $this->ussdPagesTest->updateSession($phoneNumber, $serviceCode, $text, Pages::$PAGE_CHANGE_NUMBER);
                  }
                  else{
                     $this->ussdPages->updateSession($phoneNumber, $serviceCode, $text, Pages::$PAGE_CHANGE_NUMBER);
                  }
                  
                  $this->sendUTMessage(Translator::$NOT_REGISTERED, $phoneNumber, FALSE);
               }
            }
            else{
               if(in_array($phoneNumber, $this->testGroup)){
                  $this->ussdPagesTest->updateSession($phoneNumber, $serviceCode, $text, Pages::$PAGE_CHANGE_NUMBER);
               }
               else{
                  $this->ussdPages->updateSession($phoneNumber, $serviceCode, $text, Pages::$PAGE_CHANGE_NUMBER);
               }
               $this->sendUTMessage(Translator::$NOT_REGISTERED, $phoneNumber, FALSE);
            }
         }
      }
   }
   
   private function loadSettings(){
      $this->logHandler->log(3, $this->TAG, "importing settings from settings.ini");
      $settingsDir = $this->ROOT."config/";
      if(file_exists($settingsDir)){
         $settings = parse_ini_file($settingsDir."settings.ini");
         $mysqlCreds = parse_ini_file($settings['mysql_creds']);
         $settings['mysql_creds'] = $mysqlCreds;
         $this->settings = $settings;
         //$this->logHandler->log(4, $this->TAG,"settings loaded: ".print_r($this->settings, true));
      }
      else{
         $this->logHandler->log(1, $this->TAG, "Unable to load settings directory. Exiting");
         //$this->sendMessage("Under maintenance. Come again soon!", TRUE);
      }
   }
   
   private function startSession($sessionId, $serviceCode, $phoneNumber, $page = "", $previousSession = -1){
      $this->logHandler->log(3, $this->TAG, "Starting session with session id as ".$sessionId." for ".$phoneNumber);
      
      if(strlen($page) == 0)
         $homePage = Pages::$PAGE_HOME;
      else
         $homePage = $page;
      
      if($previousSession == -1){//session is starting clean
         $query = "INSERT INTO ussd_session(phone_number, session_id, last_code, last_text, last_page, is_active, locale) "
                 . "VALUES('{$phoneNumber}', '{$sessionId}', '{$serviceCode}', '', '{$homePage}', 1, '{$this->translator->getLocale()}')";
         $this->database->runMySQLQuery($query, FALSE);
      }
      else{//session is resuming from another session
         $query = "INSERT INTO ussd_session(phone_number, session_id, last_code, last_page, last_page_data, prev_session) "
                 . "SELECT phone_number, session_id, last_code, last_page, last_page_data, id as prev_session FROM ussd_session WHERE id = {$previousSession}";
         $this->database->runMySQLQuery($query, FALSE);
         $id = $this->database->getLastInsertID();
         $query = "UPDATE ussd_session SET last_text = '', session_id = '{$sessionId}', locale = '{$this->translator->getLocale()}' WHERE id = $id";
         $this->database->runMySQLQuery($query, false);
      }
   }
   
   /**
    * This function ends a session associated to a phone number. If sessionId is not
    * provided, it will end all the active sessions assiciated with the number
    * 
    * @param type $phoneNumber The phone number you want the sessions terminated
    * @param type $sessionId The id of the session you want terminated
    */
   private function endSesssion($phoneNumber, $sessionId = -1){
      if($sessionId != -1){
         $query = "UPDATE ussd_session SET is_active = 0 WHERE session_id = '{$sessionId}' AND phone_number = '{$phoneNumber}'";
      }
      else {
         $query = "UPDATE ussd_session SET is_active = 0 WHERE phone_number = '{$phoneNumber}'";
      }
      
      $this->database->runMySQLQuery($query, false);
   }
   
   /**
    * This function checks if it is fine to continue with previous session
    * 
    * @param String $phoneNumber The farmer's phone number
    * @param String $sessionId Session id for the current session
    * @return Integer -1 If session should not continue or the id of the previous session if It's fine to continue
    */
   private function canUserContinueLastSession($phoneNumber, $sessionId){
      //do not resume from session where last page is the home page
      $query = "SELECT * FROM ussd_session WHERE session_id != '{$sessionId}' AND phone_number = '{$phoneNumber}' AND last_page != '".Pages::$PAGE_HOME."' AND is_active = 1 ORDER BY start_time DESC LIMIT 1";
      $result = $this->database->runMySQLQuery($query, true);
      if(count($result) > 0){
         $currTime = time();
         $lastSessionTime = strtotime($result[0]['start_time']);
         
         $timeDiff = ($currTime - $lastSessionTime)/60;//convert seconds to minutes
         
         if($timeDiff < 5){
            //set all other sessions that are not the last one to not active
            $query = "UPDATE ussd_session SET is_active = 0 WHERE session_id != '{$sessionId}' AND phone_number = '{$phoneNumber}'";
            $this->database->runMySQLQuery($query, false);
            
            return $result[0]['id'];
         }
         else{
            return -1;
         }
      }
      else{
         return -1;
      }
      //check if 
   }
   
   /**
    * This function ends all sessions associated with a phone number except for the one specified
    * 
    * @param String $phoneNumber The phone number you want sessions associated to ended
    * @param String $sessionId Id of the session you dont want to end
    */
   private function endOtherSesssions($phoneNumber, $sessionId){
      $this->logHandler->log(3, $this->TAG, "Deleting all other sessions by ".$phoneNumber." apart from ".$sessionId);
      $query = "UPDATE ussd_session SET is_active = 0 WHERE session_id != '{$sessionId}' AND phone_number = '{$phoneNumber}'";
      
      $this->database->runMySQLQuery($query, false);
   }
   
   private function isInSession($phoneNumber, $sessionId){
      $query = "SELECT id,session_id FROM ussd_session WHERE phone_number = '{$phoneNumber}' AND is_active = 1";
      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) > 0){
         if($sessionId == $result[0]['session_id'])
            return $result[0]['id'];
         else
            return -1;
      }
      else{
         return -2;//phone number is not in any session
      }
   }
   
   /**
    * This function determines if session is clean.
    * i.e if the session is being called redundantly by the USSD provider
    * 
    * @param String $phoneNumber The phone number in the session
    * @param String $text The text from the client
    * @return Integer Returns -1 if session is clean or the id of the session in the ussd_session
    *         table if the session is a redundant call associated to the specified session
    */
   private function isSessionClean($phoneNumber, $text){
      $seconds = 3;
      $query = "SELECT id FROM ussd_session WHERE is_active = 1 AND phone_number = '{$phoneNumber}' AND start_time > DATE_SUB(NOW(), INTERVAL $seconds SECOND) ORDER BY id DESC LIMIT 1";
      $result = $this->database->runMySQLQuery($query, true);
      if(count($result) > 0){
         if(strlen($text) == 0){
            $this->logHandler->log(2, $this->TAG, "This session with ".$phoneNumber." appears to be a redundant call from USSD provider. Resending the text from session with id = ".$result[0]['id']);
            return $result[0]['id'];
         }
         else{
            $this->logHandler->log(3, $this->TAG, "User just replied to request in less than ".$seconds." second. Respect ".$phoneNumber);
            return -1;
         }
      }
      else{
         return -1;//means that the session is already clean
      }
   }
   
   private function updateUncleanSession($oldSessionId, $phoneNumber, $newUSSDSesisonId){
      $query = "UPDATE ussd_session set session_id = '{$newUSSDSesisonId}', start_time = NOW() WHERE id = {$oldSessionId}";
      $this->database->runMySQLQuery($query, false);
      
      $query  = "SELECT last_page FROM ussd_page_cache WHERE phone_number = '{$phoneNumber}'";
      $pageCache = $this->database->runMySQLQuery($query, true);
      if(count($pageCache) > 0){
         $this->logHandler->log(3, $this->TAG, "Redundant call to USSD app ignored but resending data back to client ".$phoneNumber." just in case");
         
         $menuLength = strlen($pageCache[0]['last_page']);
         $query = "UPDATE ussd_session SET menu_length = {$menuLength} WHERE phone_number = '{$phoneNumber}' AND is_active = 1";
         $this->database->runMySQLQuery($query, false);
         
         echo $pageCache[0]['last_page'];
         return true;
      }
      else{
         $this->logHandler->log(2, $this->TAG, "Unable to obtain page cache for  ".$phoneNumber." thus unable to resend last page.");
         return false;
      }
   }
}
?>
