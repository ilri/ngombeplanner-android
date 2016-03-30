<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class Pages{
   private $ROOT = "./";
   private $TAG = "Pages";
   
   public static $PAGE_HOME = "home";
   public static $PAGE_CHANGE_NUMBER = "changeNumber";
   public static $PAGE_MILKING_1 = "milking1";//show the cows
   public static $PAGE_MILKING_2 = "milking2";//show the milking times
   public static $PAGE_MILKING_3 = "milking3";//show milk quantity
   public static $PAGE_MILKING_4 = "milking4";//show confirmation if milk data is inconsistent
   public static $PAGE_CALVING_1 = "calving1";
   public static $PAGE_CALVING_2 = "calving2";
   public static $PAGE_REP_1 = "reprodution1";
   public static $PAGE_SERVICING_1 = "servicing1";
   public static $PAGE_SERVICING_2 = "servicing2";
   public static $PAGE_SICKNESS_1 = "sickness1";
   public static $PAGE_SICKNESS_2 = "sickness2";
   public static $PAGE_SOH_1 = "soh1";
   public static $PAGE_SOH_2 = "soh2";
   public static $PAGE_ACQ_1 = "acq1";
   public static $PAGE_ACQ_2 = "acq2";
   public static $PAGE_DISPOSAL_1= "disposal1";
   public static $PAGE_DISPOSAL_2= "disposal2";
   public static $PAGE_VACCINATION_1 = "vaccination1";
   public static $PAGE_PREG_CONF_1 = "pregConfirm1";
   public static $PAGE_RECORDS_1 = "records1";
   public static $PAGE_EVENTS_1 = "otherEvents1";
   public static $PAGE_P_MEASURES_1 = "prevMeasures1";
   public static $PAGE_START_OF_LACT_1 = "startOfLact1";
   public static $PAGE_DRY_OFF_1 = "dryOff1";
   public static $PAGE_INFO_PORTAL_1 = "infoPortal1";
   
   public static $R_HOME_MILKING1 = 1;
   public static $R_HOME_SICKNESS = 2;
   public static $R_HOME_REP = 3;
   //public static $R_HOME_SOH = 3;
   //public static $R_HOME_SERVICING = 4;   
   public static $R_HOME_RECORDS = 4;
   //public static $R_HOME_CALVING = 5;
   public static $R_HOME_EVENTS = 5;
   //public static $R_HOME_VACCINATION = 6;
   public static $R_HOME_P_MEASURES = 6;
   public static $R_HOME_ACQ = 7;
   public static $R_HOME_DISPOSAL = 8;
   
   public static $NEXT_OF_PAGE_CODE = 98;
   public static $PREV_OF_PAGE_CODE = 0;
   public static $MAX_PER_PAGE = 10; 
   
   private $database;
   private $farmersPhoneNumber;
   private $translator;
   private $logHandler;
   private $alertHandler;
   private $sessionID;
   
   /**
    * Constructor for the Pages class
    * 
    * @param type $database Database object to be used for all the transactions
    * @param type $translator Translator object to be used for all translations in this object
    * @param type $logHandler LogHandler object to be used in loggin thoughout this object
    * @param type $phoneNumber The phone number for the farmer in this session
    * @param type $sessionID The session id assigned by Africa's Talking to this session
    */
   public function __construct($database, $translator, $logHandler, $phoneNumber, $sessionID) {
      $this->database = $database;
      $this->translator = $translator;
      $this->logHandler = $logHandler;
      $this->farmersPhoneNumber = $phoneNumber;
      $this->sessionID = $sessionID;
      
      include_once $this->ROOT.'php/common/alerts.php';
      $this->alertHandler = new AlertHandler($this->ROOT, $this->database, $this->logHandler);
   }
   
   /**
    * Remove any meta characters from the message e.g 98* for 'more' from the USSD provider
    * 
    */
   public function sanitiseMessage($message){
      $newMessage = str_replace("98*", "", $message);
      $newMessage = str_replace("0*", "", $newMessage);
      return $newMessage;
   }
   
   /**
    * This function returns the text for the home page
    * 
    * @param type $text Any text you want shown on the top of the page
    * @return string Text for the home page
    */
   public function getHomePage($text = ""){
      $query = "SELECT name FROM farmer WHERE mobile_no LIKE '%{$this->farmersPhoneNumber}'";
      $names = $this->database->runMySQLQuery($query, true);
      if(is_array($names) && count($names) === 1){
         $name = explode(" ", $names[0]['name']);
         
         $message = "";
         if(strlen($text) == 0)
            $message .= $this->translator->getText(Translator::$WELCOME) . " ". $name[0] . " \n";
         else
            $message .= $text . " \n";
         
         $message .= " " . $this->translator->getText(Translator::$PRESS_1_MILK_DATA) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_2_SICKNESS) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_3_REP) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_4_INFO_PORTAL) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_5_EVENTS) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_6_P_MEASURES) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_7_ACQ) . " \n";
         $message .= " " . $this->translator->getText(Translator::$PRESS_8_DISPOSAL) . " \n";
         
         return $message;
      }
      else{
         $message = $this->translator->getText(Translator::$NOT_REGISTERED);
         return $message;
      }
   }
   
   /**
    * This function determines which page should be next based on what is stored in the db
    * for the current session. What it essentially does is redirect the current session to
    * functions that handle data from different pages
    * 
    * @param String $serviceCode The USSD service code e.g *384*4564#
    * @param String $message The message recieved from the user operating the USSD app
    * 
    * @return String returns the text for the next page
    */
   public function getNextPage($serviceCode, $message, $updateSession){
      //service code is always a constant
      $this->logHandler->log(3, $this->TAG, "Message from user is ".$message." trying to inteprate where the user wants to go");
      $phoneNumber = $this->farmersPhoneNumber;
      
      $query = "SELECT id FROM farmer WHERE mobile_no LIKE '%{$this->farmersPhoneNumber}'";
      $result = $this->database->runMySQLQuery($query, true);
      
      if(strlen($message) == 0 && is_array($result) && count($result) == 1){
         if($updateSession == TRUE) $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else{
         //get the previous page and give to it (this new message) - (old message)
         $query = "SELECT last_page,last_text FROM ussd_session WHERE phone_number = '{$phoneNumber}' AND session_id = '{$this->sessionID}' AND is_active = 1";
         $this->logHandler->log(3, $this->TAG, "query = ".$query);
         
         $result = $this->database->runMySQLQuery($query, TRUE);
         if(is_array($result) && count($result) == 1){
            $lastPage = $result[0]['last_page'];
            $lastMessage = $result[0]['last_text'];
            
            $this->logHandler->log(3, $this->TAG, "Last page for ".$this->farmersPhoneNumber." was ".$lastPage." ".Pages::$PAGE_P_MEASURES_1);
            
            $messageDiff = $message;
            
            if(strlen($lastMessage)>0){
               $this->logHandler->log(3, $this->TAG, "Last message was ".$lastMessage. " and current message = ".$message);
               $messageDiff = preg_replace("/^".preg_quote($lastMessage)."/", "", $message);
            }
            
            if($lastPage == Pages::$PAGE_HOME){
               return $this->homeWasLast($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_CHANGE_NUMBER){
               return $this->changeNumberPageLast($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_MILKING_1){
               return $this->milkingPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_MILKING_2){
               return $this->milkingPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_MILKING_3){
               return $this->milkingPage3Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_MILKING_4){
               return $this->milkingPage4Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_REP_1){
               return $this->repPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_RECORDS_1){
               return $this->recordsPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_EVENTS_1){
               return $this->eventsPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_SOH_1){
               return $this->sohPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_SERVICING_1){
               return $this->servicingPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_SERVICING_2){
               return $this->servicingPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_SICKNESS_1){
               return $this->sicknessPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_CALVING_1){
               return $this->calvingPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_PREG_CONF_1){
               return $this->pregConfPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_CALVING_2){
               return $this->calvingPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_VACCINATION_1){
               return $this->vaccinationPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_ACQ_1){
               return $this->acqPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_ACQ_2){
               return $this->acqPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_DISPOSAL_1){
               return $this->disposalPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_DISPOSAL_2){
               return $this->disposalPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_P_MEASURES_1){
               return $this->pMeasuresPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_START_OF_LACT_1){
               return $this->startOfLactPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_DRY_OFF_1){
               return $this->dryOffPage1Last($serviceCode, $lastMessage, $message);
            }
            /*else if($lastPage == Pages::$PAGE_INFO_PORTAL_1){
               return $this->infoPortalPage1Last($serviceCode, $lastMessage, $message);
            }*/
         }
         else {
            $this->logHandler->log(3, $this->TAG, "No data gotten for ".$phoneNumber);
         }
      }
      
   }
   
   /**
    * The home page was where the user was last. Process the data based on that 
    * context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function homeWasLast($serviceCode, $prevMessage, $message){
      if($prevMessage == $message){//means user directly typed the menu option in code eg *384*4564*1# for milk production
         $messageDiff = $message;
      }
      else{
         $messageDiff = $message;
         if(strlen($prevMessage) > 0)
            $messageDiff = preg_replace ("/^".preg_quote ($prevMessage)."/", "", $message);

         $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
         $messageDiff = preg_replace("/^\*/", "", $messageDiff);
         $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      }
      $messageDiff = $this->sanitiseMessage($messageDiff);
      
      $phoneNumber = $this->farmersPhoneNumber;
      
      $this->logHandler->log(3, $this->TAG, "Home Page was the last page. It's responsible for handling this next request with message diff as ".$messageDiff);
      if($messageDiff == Pages::$R_HOME_MILKING1){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$ADDING_MILK_RECORDS), "Female", "Milking");
      }
      /*else if($messageDiff == Pages::$R_HOME_SOH){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_SOH_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SOH), "Female", "Signs of Heat");
      }*/
      /*else if($messageDiff == Pages::$R_HOME_SERVICING){
         $this->logHandler->log(3, $this->TAG, "should go to servicing page");
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SERVICING), "Female", "Servicing");
      }*/
      else if($messageDiff == Pages::$R_HOME_RECORDS){
         $this->logHandler->log(3, $this->TAG, "should go to records page");
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
         return $this->getRecordsPage();
      }
      else if($messageDiff == Pages::$R_HOME_REP){
         $this->logHandler->log(3, $this->TAG, "should go to reproduction page");
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1, 1);
         return $this->getReproductionPage();
      }
      else if($messageDiff == Pages::$R_HOME_SICKNESS){
         $this->logHandler->log(3, $this->TAG, "should go to sickness page");
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_SICKNESS_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SICKNESS));
      }
      else if($messageDiff == Pages::$R_HOME_P_MEASURES){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_P_MEASURES_1, 1);
         return $this->getPMeasuresPage();
      }
      /*else if($messageDiff == Pages::$R_HOME_VACCINATION){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_VACCINATION_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_VACCINATION));
      }*/
      /*else if($messageDiff == Pages::$R_HOME_CALVING){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_CALVING), "Female", "Calving");
      }*/
      else if($messageDiff == Pages::$R_HOME_EVENTS){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage();
      }
      else if($messageDiff == Pages::$R_HOME_ACQ){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_1, 1);
         return $this->getAcqTypePage();
      }
      else if($messageDiff == Pages::$R_HOME_DISPOSAL){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DISPOSAL));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Was unable to determine where ".$messageDiff." will lead to. Sending user back to home");
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage($this->translator->getText(Translator::$SELECT_VALID_OPTION));
      }
   }
   
   /**
    * The change number page was last. Handle current request based on this context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function changeNumberPageLast($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT id FROM farmer WHERE mobile_no='{$messageDiff}'";
      $result = $this->database->runMySQLQuery($query, true);
      
      if(is_array($result) && count($result) == 1){
         $newNumber = '0'.$this->farmersPhoneNumber;
         $farmerId = $result[0]['id'];
         $query = "UPDATE farmer SET mobile_no = '{$newNumber}', sim_card_sn = '' WHERE id = {$farmerId}";//make sure you set sim card sn to ''
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "INSERT INTO number_change_log(farmer_id, old_number, new_number, app_used) VALUES({$farmerId}, '{$messageDiff}', '{$newNumber}', 'USSD')";
         $this->database->runMySQLQuery($query, FALSE);
         
         $this->alertHandler->sendNumberChangeAlert($farmerId, $messageDiff, $newNumber);
         
         return $this->translator->getText(Translator::$NUMBER_CHANGED);
      }
      else{
         return $this->translator->getText(Translator::$NUMBER_NOT_EXIST);
      }
   }

   /**
    * The first page in the milking sequence (Displaying of cow list for selection) was last.
    *  Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function milkingPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote($prevMessage)."/", "", $message);
      
      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      $phoneNumber = $this->farmersPhoneNumber;
      
      $this->logHandler->log(3, $this->TAG, "Milking Page 1 was the last page. It's responsible for handling this next request with message diff as ".$messageDiff);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user either wants to go back home or go back to previous list of animals
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff)){//user want to enter cow data for a particular cow
         //make sure you cache the index of the cow selected by the user
         //check if the number lies in the range of cows owned by farmer
         $query = "SELECT b.id FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$phoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $result = $this->getValidCows($fetchedCows, "Milking");
         $this->logHandler->log(4, $this->TAG, " *** Cows** = ".print_r($result, true));
         
         if($messageDiff <= count($result)){
            $cowIndex = $messageDiff - 1;
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_2, $messageDiff);
            return $this->getMilkingTimesPage($result[$cowIndex]['id']);
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Cow selected not in range");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Milking");
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Input provided by user is not numeric");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Milking");
      }
   }
   
   /**
    * Page displaying milking times was last. Process the data based on that 
    * context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function milkingPage2Last($serviceCode, $prevMessage, $message) {
      $phoneNumber = $this->farmersPhoneNumber;
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote($prevMessage)."/", "", $message);
      
      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1);
         return $this->getCowPage($this->translator->getText(Translator::$ADDING_MILK_RECORDS), "Female", "Milking");
      }
      else if(is_numeric($messageDiff)){
         
         $query = "SELECT b.id FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$phoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $result = $this->getValidCows($fetchedCows, "Milking");
         
         $cowIndex = $this->getDataFromLastPage();
         if($cowIndex == -1){	//there was some error... return to the cow selection page
            $this->logHandler->log(3, $this->TAG, "Couldn't find the pre-selected cow after resuming a session, presenting the list of cows.");
            return $this->milkingPage1Last($serviceCode, $prevMessage, $message);
         }
         
         $milkingTimes = $this->getMilkingTimes($result[$cowIndex - 1]['id']);
         
         $this->logHandler->log(4, $this->TAG, "Valid milking times for this cow is ".  print_r($milkingTimes, true));
         
         if($messageDiff>0 && $messageDiff <= count($milkingTimes)){
            $cachData = $cowIndex.".".$messageDiff;//append the milking time to the cow
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_3, $cachData);
            return $this->getMilkProdPage($cachData, $milkingTimes[$messageDiff - 1]);
         }
         else{
            $this->logHandler->log(3, $this->TAG, "User inputed an unknown option");
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_2, $cowIndex);
            
            return $this->getMilkingTimesPage($result[$cowIndex - 1]['id'],$this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1);
         return $this->getCowPage($this->translator->getText(Translator::$WRONG_FORMAT_COW_DATA), "Female", "Milking");
      }
   }
   
   /**
    * User last presented with page asking for quantity of milk. Process the data based on that 
    * context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function milkingPage3Last($serviceCode, $prevMessage, $message){
      //get the cached cow index from the database. Get the cow with that index
      $cachedData = $this->getDataFromLastPage();//should contain the index of the cow
      $xploded = explode(".", $cachedData);
      $cowIndex = $xploded[0];
      $time = $xploded[1];
      if($cachedData !== -1){
         
         $this->logHandler->log(3, $this->TAG, "About to insert data for cow with index = ".$cowIndex);
         
         $query = "SELECT cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $results = $this->getValidCows($fetchedCows, "Milking");
         
         if(is_array($results) && count($results) >= $cowIndex){
            $cowIndex = $cowIndex-1;//convert to 0 index
            
            $cowId = $results[$cowIndex]['id'];
            
            //validate the data from ussd
            //should be of the form [0-9],\s*[MAE]{1}
            $messageDiff = $message;
            if(strlen($prevMessage) > 0)
               $messageDiff = preg_replace ("/^".preg_quote ($prevMessage)."/", "", $message);

            $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
            $messageDiff = preg_replace("/^\*/", "", $messageDiff);
            $messageDiff = $this->sanitiseMessage($messageDiff);
            $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
            
            if(is_numeric($messageDiff)){//amount of milk correct
               $quantity = $messageDiff;
               
               $milkingTimes = $this->getMilkingTimes($cowId);
               $selectedT = $milkingTimes[$time - 1];
               
               if($selectedT == Translator::$MORNING){
                  $date = $this->getTime('Y-m-d');
                  $mTime = "Morning";
               }
               else if($selectedT == Translator::$AFTERNOON){
                  $date = $this->getTime('Y-m-d');
                  $mTime = "Afternoon";
               }
               else if($selectedT == Translator::$EVENING){
                  $date = $this->getTime('Y-m-d');
                  $mTime = "Evening";
               }
               else if($selectedT == Translator::$COMBINED){
                  $date = $this->getTime('Y-m-d');
                  $mTime = "Combined";
               }
               else if($selectedT == Translator::$Y_MORNING){
                  $yDayTime = strtotime("Yesterday");
                  $yDayDate = new DateTime();
                  $yDayDate->setTimestamp($yDayTime);
                  $date = $yDayDate->format('y-m-d');
                  $mTime = "Morning";
               }
               else if($selectedT == Translator::$Y_AFTERNOON){
                  $yDayTime = strtotime("Yesterday");
                  $yDayDate = new DateTime();
                  $yDayDate->setTimestamp($yDayTime);
                  $date = $yDayDate->format('y-m-d');
                  $mTime = "Afternoon";
               }
               else if($selectedT == Translator::$Y_EVENING){
                  $yDayTime = strtotime("Yesterday");
                  $yDayDate = new DateTime();
                  $yDayDate->setTimestamp($yDayTime);
                  $date = $yDayDate->format('y-m-d');
                  $mTime = "Evening";
               }
               else if($selectedT == Translator::$Y_COMBINED){
                  $yDayTime = strtotime("Yesterday");
                  $yDayDate = new DateTime();
                  $yDayDate->setTimestamp($yDayTime);
                  $date = $yDayDate->format('y-m-d');
                  $mTime = "Combined";
               }
               
               $this->logHandler->log(3, $this->TAG, "processing user input");
               
               $datetime = $this->getTime('Y-m-d H:i:s');
               
               $query = "SELECT `time` FROM `milk_production` WHERE `cow_id` = {$cowId} AND `date` = '{$date}'";
               $result = $this->database->runMySQLQuery($query, true);
               $dataThereFlag = false;
               if(sizeOf($result) > 0 && $mTime == "Combined") {
                  $dataThereFlag = true;
               }
               else {
                  for($i = 0; $i < sizeOf($result) ; $i++){
                     if($result[$i]["time"] == "Combined" || $result[$i]["time"] == $mTime) {
                        $dataThereFlag = true;
                     }
                  }
               }
               
               if($dataThereFlag === false){
                  if($this->checkMilkData($cowId, $mTime, $quantity, 'Litres')){
                     $query = "INSERT INTO milk_production(cow_id, time, quantity, date, date_added, quantity_type, app_used, no_used, ussd_session_id) VALUES($cowId, '$mTime', {$quantity}, '$date', '$datetime', 'Litres', 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
                     $this->logHandler->log(3, $this->TAG, "Query = ".$query);
                     $this->database->runMySQLQuery($query, false);

                     $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1);
                     return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Milking");
                  }
                  else{
                     $cachedData = $cachedData . "." . $quantity;
                     
                     $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_4, $cachedData);
                     return $this->getMilkConfirmPage();
                  }
               }
               else{
                  $this->logHandler->log(3, $this->TAG, "User already entered data for this cow for this time ". $messageDiff);
                  $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
                  return $this->getCowPage($this->translator->getText(Translator::$MILK_DATA_ALREADY_THERE), "Female", "Milking");
               }
               
            }
            else{
               $this->logHandler->log(3, $this->TAG, "Input from the user did not match what is expected. Sending user back to the cow list ". $messageDiff);
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
               return $this->getCowPage($this->translator->getText(Translator::$WRONG_FORMAT_COW_DATA), "Female", "Milking");
            }
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get cow with the index = ".$cowIndex." in the farmers cow list");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Milking");
         }
         //if data wrong send back to cow list
         //else send to main menu
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get the cached cow index of the selected cow. Sending user back to cow list page");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Milking");
      }
   }
   
   /**
    * User presented with a page asking him/her to confirm the quantity of milk entered.
    * Process the data based on that context. This page should only be presented when inconsistent
    * milk production entered 
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function milkingPage4Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      $cachedData = $this->getDataFromLastPage();
      $xploded = explode(".", $cachedData);
      $cowIndex = $xploded[0];
      $time = $xploded[1];
      $quantity = $xploded[2];

      $query = "SELECT cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);

      $results = $this->getValidCows($fetchedCows, "Milking");

      
      if(is_array($results) && count($results) >= $cowIndex){
         
         $cowIndex = $cowIndex-1;//convert to 0 index
         
         $this->logHandler->log(3, $this->TAG, "Cow index = ".$cowIndex);
         $this->logHandler->log(3, $this->TAG, "Cached data = ".$cachedData);
         $this->logHandler->log(4, $this->TAG, "Valid cows  = ".  print_r($results, true));

         $cowId = $results[$cowIndex]['id'];

         $milkingTimes = $this->getMilkingTimes($cowId);
         $selectedT = $milkingTimes[$time - 1];
         
         if($messageDiff == 1){//cancel button pressed

               $cachedData = ($cowIndex + 1) . "." . $time;

               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_3, $cachData);
               return $this->getMilkProdPage($cachData, $selectedT);

         }

         else if($messageDiff == 2){//okay button pressed
            if($selectedT == Translator::$MORNING){
               $date = $this->getTime('Y-m-d');
               $mTime = "Morning";
            }
            else if($selectedT == Translator::$AFTERNOON){
               $date = $this->getTime('Y-m-d');
               $mTime = "Afternoon";
            }
            else if($selectedT == Translator::$EVENING){
               $date = $this->getTime('Y-m-d');
               $mTime = "Evening";
            }
            else if($selectedT == Translator::$COMBINED){
               $date = $this->getTime('Y-m-d');
               $mTime = "Combined";
            }
            else if($selectedT == Translator::$Y_MORNING){
               $yDayTime = strtotime("Yesterday");
               $yDayDate = new DateTime();
               $yDayDate->setTimestamp($yDayTime);
               $date = $yDayDate->format('y-m-d');
               $mTime = "Morning";
            }
            else if($selectedT == Translator::$Y_AFTERNOON){
               $yDayTime = strtotime("Yesterday");
               $yDayDate = new DateTime();
               $yDayDate->setTimestamp($yDayTime);
               $date = $yDayDate->format('y-m-d');
               $mTime = "Afternoon";
            }
            else if($selectedT == Translator::$Y_EVENING){
               $yDayTime = strtotime("Yesterday");
               $yDayDate = new DateTime();
               $yDayDate->setTimestamp($yDayTime);
               $date = $yDayDate->format('y-m-d');
               $mTime = "Evening";
            }
            else if($selectedT == Translator::$Y_COMBINED){
               $yDayTime = strtotime("Yesterday");
               $yDayDate = new DateTime();
               $yDayDate->setTimestamp($yDayTime);
               $date = $yDayDate->format('y-m-d');
               $mTime = "Combined";
            }

            $this->logHandler->log(3, $this->TAG, "processing user input");

            $datetime = $this->getTime('Y-m-d H:i:s');

            $query = "SELECT `time` FROM `milk_production` WHERE `cow_id` = {$cowId} AND `date` = '{$date}'";
            $result = $this->database->runMySQLQuery($query, true);
            $dataThereFlag = false;
            if(sizeOf($result) > 0 && $mTime == "Combined") {
               $dataThereFlag = true;
            }
            else {
               for($i = 0; $i < sizeOf($result) ; $i++){
                  if($result[$i]["time"] == "Combined" || $result[$i]["time"] == $mTime) {
                     $dataThereFlag = true;
                  }
               }
            }

            if($dataThereFlag === false){
               $query = "INSERT INTO milk_production(cow_id, time, quantity, date, date_added, quantity_type, app_used, no_used, ussd_session_id) VALUES($cowId, '$mTime', {$quantity}, '$date', '$datetime', 'Litres', 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
               $this->logHandler->log(3, $this->TAG, "Query = ".$query);
               $this->database->runMySQLQuery($query, false);

               $this->alertHandler->sendMilkFluctuationAlert($cowId, $quantity, "Litres");
               
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1);
               return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Milking");
            }
            else{
               $this->logHandler->log(3, $this->TAG, "User already entered data for this cow for this time ". $messageDiff);
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
               return $this->getCowPage($this->translator->getText(Translator::$MILK_DATA_ALREADY_THERE), "Female", "Milking");
            }
         }
      }
   }
   
   /**
    * User last presented with a list of cows from which to select a cow with signs of heat.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function sohPage1Last($serviceCode, $prevMessage, $message){
      $this->logHandler->log(3, $this->TAG, "Processing data from soh page");
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote($prevMessage)."/", "", $message);
      
      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user either wants to go back to reproduction page
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1);
         return $this->getReproductionPage();
      }
      else{//record signs of heat for this cow and show the cow list
         $query = "SELECT cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
         $gottenCows = $this->database->runMySQLQuery($query, TRUE);
         
         $results = $this->getValidCows($gottenCows, "Signs of Heat");
         
         if(is_numeric($messageDiff) && is_array($results) && count($results) >= $messageDiff){
            $cowIndex = $messageDiff - 1;
            $cowId = $results[$cowIndex]['id'];
            $query = "SELECT id FROM event WHERE name = 'Signs of Heat'";
            $results = $this->database->runMySQLQuery($query, true);
            $eventId =$results[0]['id'];
            
            $date = $this->getTime('Y-m-d');
            $datetime = $this->getTime('Y-m-d H:i:s');
            
            $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES($cowId, $eventId, '$date', '$datetime', 'USSD', '{$this->farmersPhoneNumber}')";
            $this->database->runMySQLQuery($query, false);
            
            $this->logHandler->log(3, $this->TAG, "Signs of Heat successfully recorded");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SOH_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Signs of Heat");
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get cow with the index = ".$messageDiff." in the farmers cow list");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SOH_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Signs of Heat");
         }
      }
   }
   
   public function repPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user whats to go back home
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      
      else if(is_numeric($messageDiff)){
         if($messageDiff == 1){//signs of heat
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SOH_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SOH), "Female", "Signs of Heat");
         }
         else if($messageDiff == 2){//servicing
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SERVICING), "Female", "Servicing");
         }
         else if($messageDiff == 3){//calving
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_CALVING), "Female", "Calving");
         }
         else if($messageDiff == 4){//pregnancy confirmation
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_PREG_CONF_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_PREG_CONF), "Female", "Pregnancy Confirmation");
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1, 1);
            return $this->getReproductionPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1, 1);
         return $this->getReproductionPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function recordsPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user whats to go back home
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff)){
         if($messageDiff == 1){//yesterdays milk records
            //$yDayTime = time() - 86400;
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $yesterday = $yDayDate->format('Y-m-d');
            
            $query = "SELECT a.mobile_no, b.name, b.ear_tag_number, SUM(c.quantity) AS quantity"
                    . " FROM farmer AS a"
                    . " INNER JOIN cow AS b ON a.id = b.farmer_id"
                    . " INNER JOIN milk_production AS c ON b.id = c.cow_id"
                    . " WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}'"
                            . " AND b.sex = 'Female'"
                            . " AND c.date = '{$yesterday}'"
                    . " GROUP BY c.cow_id";
            $yDayMilk = $this->database->runMySQLQuery($query, true);
            
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $message = $this->translator->getText(Translator::$MILK_RECORDS_YDAY) . " \n";
               foreach($yDayMilk AS $currCow){
                  $message .= $this->getFormatedCowName($currCow['name'], $currCow['ear_tag_number']) . " - " . $currCow['quantity']."L \n";
               }
               
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            else{
               $this->logHandler->log(2, $this->TAG, "Unable to get milk records from yesterday for ".$this->farmersPhoneNumber);
               $message = $this->translator->getText(Translator::$NO_YDAY_MILK_RECORDS);
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            $numberToSend = "0".$this->farmersPhoneNumber;
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $numberToSend = $yDayMilk[0]['mobile_no'];
            }
            
            $query = "INSERT INTO sms_queue(text2send, number, text_status) VALUES('{$message}', '{$numberToSend}', 'not sent')";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$INFO_WILL_BE_SENT));
         }
         else if($messageDiff == 2){//weeks milk records
            //$yDayTime = time() - 86400;
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $yesterday = $yDayDate->format('Y-m-d');
            
            $wkFrmYdayTime = time() - (86400 * 8);
            $wkFrmYdayDate = new DateTime();
            $wkFrmYdayDate->setTimestamp($wkFrmYdayTime);
            $wkFromYDay = $wkFrmYdayDate->format('Y-m-d');
            
            $query = "SELECT a.mobile_no, b.name, b.ear_tag_number, SUM(c.quantity) AS quantity"
                    . " FROM farmer AS a"
                    . " INNER JOIN cow AS b ON a.id = b.farmer_id"
                    . " INNER JOIN milk_production AS c ON b.id = c.cow_id"
                    . " WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}'"
                            . " AND b.sex = 'Female'"
                            . " AND c.date <= '{$yesterday}'"
                            . " AND c.date >= '{$wkFromYDay}'"
                    . " GROUP BY c.cow_id";
            $yDayMilk = $this->database->runMySQLQuery($query, true);
            
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $message = $this->translator->getText(Translator::$MILK_RECORDS_WEEK) . " \n";
               foreach($yDayMilk AS $currCow){
                  $message .= $this->getFormatedCowName($currCow['name'], $currCow['ear_tag_number']) . " - " . $currCow['quantity']."L \n";
               }
               
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            else{
               $this->logHandler->log(2, $this->TAG, "Unable to get milk records from yesterday for ".$this->farmersPhoneNumber);
               $message = $this->translator->getText(Translator::$NO_WEEK_MILK_RECORDS);
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            $numberToSend = "0".$this->farmersPhoneNumber;
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $numberToSend = $yDayMilk[0]['mobile_no'];
            }
            
            $query = "INSERT INTO sms_queue(text2send, number, text_status) VALUES('{$message}', '{$numberToSend}', 'not sent')";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$INFO_WILL_BE_SENT));
         }
         else if($messageDiff == 3){//months milk record
            //$yDayTime = time() - 86400;
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $yesterday = $yDayDate->format('Y-m-d');
            
            $mthFrmYdayTime = time() - (86400 * 31);
            $mthFrmYdayDate = new DateTime();
            $mthFrmYdayDate->setTimestamp($mthFrmYdayTime);
            $mthFromYDay = $mthFrmYdayDate->format('Y-m-d');
            
            $query = "SELECT a.mobile_no, b.name, b.ear_tag_number, SUM(c.quantity) AS quantity"
                    . " FROM farmer AS a"
                    . " INNER JOIN cow AS b ON a.id = b.farmer_id"
                    . " INNER JOIN milk_production AS c ON b.id = c.cow_id"
                    . " WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}'"
                            . " AND b.sex = 'Female'"
                            . " AND c.date <= '{$yesterday}'"
                            . " AND c.date >= '{$mthFromYDay}'"
                    . " GROUP BY c.cow_id";
            $yDayMilk = $this->database->runMySQLQuery($query, true);
            
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $message = $this->translator->getText(Translator::$MILK_RECORDS_MONTH) . " \n";
               foreach($yDayMilk AS $currCow){
                  $message .= $this->getFormatedCowName($currCow['name'], $currCow['ear_tag_number']) . " - " . $currCow['quantity']."L \n";
               }
               
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            else{
               $this->logHandler->log(2, $this->TAG, "Unable to get milk records from yesterday for ".$this->farmersPhoneNumber);
               $message = $this->translator->getText(Translator::$NO_MONTH_MILK_RECORDS);
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            $numberToSend = "0".$this->farmersPhoneNumber;
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $numberToSend = $yDayMilk[0]['mobile_no'];
            }
            
            $query = "INSERT INTO sms_queue(text2send, number, text_status) VALUES('{$message}', '{$numberToSend}', 'not sent')";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$INFO_WILL_BE_SENT));
         }
         else if($messageDiff == 4){//months event records
            
            //$yDayTime = time() - 86400;
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $yesterday = $yDayDate->format('Y-m-d');
            
            $mthFrmYdayTime = time() - (86400 * 31);
            $mthFrmYdayDate = new DateTime();
            $mthFrmYdayDate->setTimestamp($mthFrmYdayTime);
            $mthFromYDay = $mthFrmYdayDate->format('Y-m-d');
            
            $query = "SELECT a.mobile_no, b.name, b.ear_tag_number, GROUP_CONCAT(DISTINCT(d.name) SEPARATOR ', ') AS events"
                    . " FROM farmer AS a"
                    . " INNER JOIN cow AS b ON a.id = b.farmer_id"
                    . " INNER JOIN cow_event AS c ON b.id = c.cow_id"
                    . " INNER JOIN event AS d ON c.event_id = d.id"
                    . " WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}'"
                            . " AND b.sex = 'Female'"
                            . " AND c.event_date <= '{$yesterday}'"
                            . " AND c.event_date >= '{$mthFromYDay}'"
                    . " GROUP BY c.cow_id"
                    . " ORDER BY c.event_date ASC";
            $yDayMilk = $this->database->runMySQLQuery($query, true);
            
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $message = $this->translator->getText(Translator::$EVENT_RECORDS_MONTH) . " \n";
               foreach($yDayMilk AS $currCow){
                  $message .= $this->getFormatedCowName($currCow['name'], $currCow['ear_tag_number']) . " - " . $currCow['events']." \n";
               }
               
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            else{
               $this->logHandler->log(2, $this->TAG, "Unable to get milk records from yesterday for ".$this->farmersPhoneNumber);
               $message = $this->translator->getText(Translator::$NO_MONTH_EVENT_RECORDS);
               $this->logHandler->log(4, $this->TAG, "About to send the following record to " . $this->farmersPhoneNumber . ": ".$message);
            }
            
            $numberToSend = "0".$this->farmersPhoneNumber;
            if(is_array($yDayMilk) && count($yDayMilk) > 0){
               $numberToSend = $yDayMilk[0]['mobile_no'];
            }
            
            $query = "INSERT INTO sms_queue(text2send, number, text_status) VALUES('{$message}', '{$numberToSend}', 'not sent')";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$INFO_WILL_BE_SENT));
         }
         else if($messageDiff == 5){//user wants to go to the weather forecast
            /*$this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_INFO_PORTAL_1, 1);
            return $this->getInfoPortalPage();*/
            
            //get weather
            include_once $this->ROOT.'php/common/weather_forecast.php';
            $wForecast = new WForecast($this->ROOT,$this->database, $this->logHandler);
            $result = $wForecast->getWeatherForecast($this->farmersPhoneNumber);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
            
            if($result == true){
               return $this->getRecordsPage($this->translator->getText(Translator::$FORECAST_SENT_SMS));
            }
            else {
               return $this->getRecordsPage($this->translator->getText(Translator::$FORECAST_UNAVAILABLE));
            }
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1, 1);
         return $this->getRecordsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function eventsPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user whats to go back home
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff)){
         if($messageDiff == 1){//start of lactionation
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_START_OF_LACT_1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_START_OF_LACT), "Female", "Start of Lactation");
         }
         else if($messageDiff == 2){//dry off
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_START_OF_LACT_1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DRY_OFF), "Female", "Milking");
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1, 1);
            return $this->getEventsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function pMeasuresPage1Last($serviceCode, $prevMessage, $message){
      $this->logHandler->log(3, $this->TAG, "pMeasuredPage1Last called ".$this->farmersPhoneNumber);
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user whats to go back home
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff)){
         if($messageDiff == 1){//dipping
            //assuming that dipping is done for the entire herd
            $query = "SELECT b.id FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}'";
            $cows = $this->database->runMySQLQuery($query, TRUE);
            if(is_array($cows)){
               $query = "SELECT id FROM event WHERE name = 'Dipping or Spraying'";
               $results = $this->database->runMySQLQuery($query, true);
               $eventId =$results[0]['id'];

               $date = $this->getTime('Y-m-d');
               $datetime = $this->getTime('Y-m-d H:i:s');
                  
               foreach($cows as $currCow){
                  $cowId = $currCow['id'];
                  
                  $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
                  $this->database->runMySQLQuery($query, FALSE);
               }
            }
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_P_MEASURES_1, 1);
            return $this->getPMeasuresPage($this->translator->getText(Translator::$INFO_RECORDED));
            
         }
         else if($messageDiff == 2){//deworming
            //assuming that deworming is done for the entire herd
            $query = "SELECT b.id FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}'";
            $cows = $this->database->runMySQLQuery($query, TRUE);
            if(is_array($cows)){
               $query = "SELECT id FROM event WHERE name = 'Deworming'";
               $results = $this->database->runMySQLQuery($query, true);
               $eventId =$results[0]['id'];

               $date = $this->getTime('Y-m-d');
               $datetime = $this->getTime('Y-m-d H:i:s');
                  
               foreach($cows as $currCow){
                  $cowId = $currCow['id'];
                  
                  $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
                  $this->database->runMySQLQuery($query, FALSE);
               }
            }
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_P_MEASURES_1, 1);
            return $this->getPMeasuresPage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else if($messageDiff == 3){//vaccination
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_VACCINATION_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_VACCINATION));
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_P_MEASURES_1, 1);
            return $this->getPMeasuresPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_P_MEASURES_1, 1);
         return $this->getPMeasuresPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function startOfLactPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
      $result = $this->getValidCows($fetchedCows, "Start of Lactation");
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1);
         return $this->getEventsPage();
      }
      
      else if(is_array($result) && is_numeric($messageDiff) && $messageDiff <= count($result)){
         $cowId = $result[$messageDiff - 1]['id'];
         
         $query = "SELECT id FROM event WHERE name = 'Start of Lactation'";
         $events = $this->database->runMySQLQuery($query, true);
         $eventId =$events[0]['id'];

         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         
         $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, false);
         
         $query = "UPDATE cow SET milking_status = 'adult_milking' WHERE id = {$cowId}";
         $this->database->runMySQLQuery($query, false);
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_START_OF_LACT_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Start of Lactation");
      }
   }
   
   
   public function dryOffPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
      $result = $this->getValidCows($fetchedCows, "Milking");
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1);
         return $this->getEventsPage();
      }
      
      else if(is_array($result) && is_numeric($messageDiff) && $messageDiff <= count($result)){
         $cowId = $result[$messageDiff - 1]['id'];
         
         $query = "SELECT id FROM event WHERE name = 'Dry Off'";
         $events = $this->database->runMySQLQuery($query, true);
         $eventId =$events[0]['id'];

         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         
         $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, false);
         
         $query = "UPDATE cow SET milking_status = 'adult_not_milking' WHERE id = {$cowId}";
         $this->database->runMySQLQuery($query, false);
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_START_OF_LACT_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Milking");
      }
   }

   public function infoPortalPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == 1){//user wants the weather forecast
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_INFO_PORTAL_1);
         //TODO: get weather
         include_once $this->ROOT.'php/common/weather_forecast.php';
         $wForecast = new WForecast($this->ROOT,$this->database, $this->logHandler);
         $result = $wForecast->getWeatherForecast($this->farmersPhoneNumber);
         
         if($result == true){
            return $this->getInfoPortalPage($this->translator->getText(Translator::$FORECAST_SENT_SMS));
         }
         else {
            return $this->getInfoPortalPage($this->translator->getText(Translator::$FORECAST_UNAVAILABLE));
         }
      }
      else if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_RECORDS_1);
         return $this->getRecordsPage();
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get the selected option in the info portal = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_INFO_PORTAL_1);
         return $this->getInfoPortalPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   /**
    * User presented with list of cows from which to select a cow that was serviced.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function servicingPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote($prevMessage)."/", "", $message);
      
      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      $phoneNumber = $this->farmersPhoneNumber;
      
      $this->logHandler->log(3, $this->TAG, "Servicing Page 1 was the last page. It's responsible for handling this next request with message diff as ".$messageDiff);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user either wants to go back home or go back to previous list of animals
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1);
         return $this->getReproductionPage();
      }
      else if(is_numeric($messageDiff)){//user want to enter cow data for a particular cow
         //make sure you cache the index of the cow selected by the user
         $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$phoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $result = $this->getValidCows($fetchedCows, "Servicing");
         
         if($messageDiff <= count($result)){
            $cowIndex = $messageDiff - 1;
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_2, $messageDiff);
            return $this->getServicingPage($this->getFormatedCowName($result[$cowIndex]['name'], $result[$cowIndex]['ear_tag_number']));
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Cow selected not in range");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Servicing");
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Input provided by user is not numeric");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Servicing");
      }
   }
   
   /**
    * User last presented with a list of service typese from which to select from.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function servicingPage2Last($serviceCode, $prevMessage, $message){
      //get the cached cow index from the database. Get the cow with that index
      $cowIndex = $this->getDataFromLastPage();//should contain the index of the cow
      
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote ($prevMessage)."/", "", $message);

      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      $query = "SELECT cow.id, cow.name, cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
      $results = $this->getValidCows($fetchedCows, "Servicing");
      
      if($messageDiff == 0){
         $this->logHandler->log(3, $this->TAG, "User want to go back");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SERVICING), "Female", "Servicing");
      }
      else if(is_numeric($messageDiff) && $messageDiff>0 && $messageDiff<3){
         if(is_array($results) && count($results) >= $cowIndex){
            $cowIndex = $cowIndex-1;//convert to 0 index
            
            $cowId = $results[$cowIndex]['id'];
            
            if($messageDiff == 1) $query = "INSERT INTO unregistered_servicing(cow_id) VALUES({$cowId})";
            if($messageDiff == 2) $query = "INSERT INTO unregistered_ai(cow_id) VALUES({$cowId})";
            
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Servicing");
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get cow with the index = ".$cowIndex." in the farmers cow list");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Servicing");
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Option selected by user does not exist");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_2, $cowIndex);
         
         $cowIndex = $cowIndex - 1 ;
         $cowName = $this->getFormatedCowName($results[$cowIndex]['name'], $results[$cowIndex]['ear_tag_number']);
         
         return $this->getServicingPage($cowName, $this->translator->getText(Translator::$UNKNOWN_OPTION_SELECTED));
      }
   }
   
   /**
    * User last presented with a list of cows from which to select the sick cow.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function sicknessPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote($prevMessage)."/", "", $message);
      
      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else{
         $query = "SELECT cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
         $result = $this->database->runMySQLQuery($query, TRUE);
         
         if(is_numeric($messageDiff) && $messageDiff <= count($result)){
            $cowIndex = $messageDiff - 1;
            $cowId = $result[$cowIndex]['id'];
            
            $query = "SELECT id FROM event WHERE name = 'Sickness'";
            $result = $this->database->runMySQLQuery($query, true);
            
            $eventId = $result[0]['id'];
            
            $date = $this->getTime('Y-m-d');
            $datetime = $this->getTime('Y-m-d H:i:s');
            
            $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->alertHandler->sendSicknessAlert($cowId);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SICKNESS_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Number not in range of cows");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_SICKNESS_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW));
         }
      }
   }
   
   /**
    * User last presented with a list of cows from which to select a cow that has calved.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function calvingPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $message;
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote($prevMessage)."/", "", $message);
      
      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      $phoneNumber = $this->farmersPhoneNumber;
      
      $this->logHandler->log(3, $this->TAG, "Calving Page 1 was the last page. It's responsible for handling this next request with message diff as ".$messageDiff);
      
      if($messageDiff == Pages::$PREV_OF_PAGE_CODE){//user either wants to go back home or go back to previous list of animals
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1);
         return $this->getReproductionPage();
      }
      else if(is_numeric($messageDiff)){//user want to enter cow data for a particular cow
         //make sure you save the index of the cow
         $query = "SELECT cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $results = $this->getValidCows($fetchedCows, "Calving");
         
         if($messageDiff <= count($results)){
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_2, $messageDiff);
            return $this->getCalvingPage($messageDiff);
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Number not in range of cows");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Calving");
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Input provided by user is not numeric");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Calving");
      }
   }
   
   /**
    * User last shown list of types of calving from which to select the type of calving.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function calvingPage2Last($serviceCode, $prevMessage, $message){
      //get the cached cow index from the database. Get the cow with that index
      $cowIndex = $this->getDataFromLastPage();//should contain the index of the cow
      
      $messageDiff = $message;
      
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote ($prevMessage)."/", "", $message);

      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);

      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_CALVING), "Female", "Calving");
      }
      
      else if($cowIndex !== -1){
         $this->logHandler->log(3, $this->TAG, "About to record ai for cow with index = ".$cowIndex);
         
         $query = "SELECT cow.id, cow.farmer_id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $results = $this->getValidCows($fetchedCows, "Calving");
         
         if(is_array($results) && count($results) >= $cowIndex){
            $cowIndex = $cowIndex-1;//convert to 0 index
            
            $cowId = $results[$cowIndex]['id'];
            $farmerId = $results[$cowIndex]['farmer_id'];
            
            //validate the data from ussd
            //should be of the form [0-9],\s*[MAE]{1}
           
            if(is_numeric($messageDiff) && $messageDiff < 5){
               $input = trim($messageDiff);
               
               if($input == 1) $input = "Normal";
               else if($input == 2) $input = "Still";
               else if($input == 3) $input = "Premature";
               else if($input == 4) $input = "Abortion";
               
               $date = $this->getTime('Y-m-d');
               $datetime = $this->getTime('Y-m-d H:i:s');
               if($input == "Premature" || $input == "Normal"){
                  $query = "INSERT INTO unregistered_calf(parent_cow, birth_type, date_added) VALUES($cowId, '{$input}', '{$datetime}')";
               }
               else{
                  $query = "SELECT id FROM event WHERE name = 'Calving'";
                  $result = $this->database->runMySQLQuery($query, true);
                  $eventId = $result[0]['id'];
                  
                  $query = "INSERT INTO cow_event(cow_id, event_id, birth_type, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$input}', '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
               }
               //$this->logHandler->log(3, $this->TAG, "Query = ".$query);
               $this->database->runMySQLQuery($query, false);
               
               $query = "UPDATE cow SET milking_status = 'adult_milking', in_calf = 0 WHERE id = {$cowId}";
               $this->database->runMySQLQuery($query, false);
               
               $this->alertHandler->sendBirthAlert($farmerId);
               
               $this->logHandler->log(3, $this->TAG, "Cow birth successfully recorded". $messageDiff);
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
               return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Calving");
            }
            else{
               $this->logHandler->log(3, $this->TAG, "Input from the user did not match what is expected. Sending user back to the cow list ". $messageDiff);
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
               return $this->getCowPage($this->translator->getText(Translator::$WRONG_FORMAT_COW_DATA), "Female", "Calving");
            }
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get cow with the index = ".$cowIndex." in the farmers cow list");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Calving");
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cached data on previous page");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Calving");
      }
   }
   
   public function pregConfPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
      $result = $this->getValidCows($fetchedCows, "Pregnancy Confirmation");
      
      if($messageDiff == 0){//user wants to go back to reproduction page
         $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_REP_1);
         return $this->getReproductionPage();
      }
      else if(is_array($result) && is_numeric($messageDiff) && $messageDiff <= count($result)){
         $cowIndex = $messageDiff - 1;
         
         $cowId = $result[$cowIndex]['id'];
         
         $query = "SELECT id FROM event WHERE name = 'Pregnancy Confirmation'";
         $events = $this->database->runMySQLQuery($query, true);

         $eventId = $events[0]['id'];

         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         
         $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, false);
         
         $query = "UPDATE cow SET in_calf = 1 WHERE id = {$cowId}";
         $this->database->runMySQLQuery($query, false);
         
         $this->logHandler->log(3, $this->TAG, "Pregnancy confirmation recorded for cow with id ".$cowId);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_PREG_CONF_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Pregnancy Confirmation");
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_PREG_CONF_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Pregnancy Confirmation");
      }
   }
   
   /**
    * User last presented with list from which to choose the type of acquisition.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function acqPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME, 1);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff < 4){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_2, $messageDiff);
         return $this->getAcqNumberPage();
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Wrong option selected by user");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_1, 1);
         return $this->getAcqTypePage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
      
   }
   
   /**
    * User last presented with a screen where he/she specifies the number of cows acquired.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function acqPage2Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $acqType = $this->getDataFromLastPage();
      
      if(is_numeric($messageDiff)){
         if($messageDiff > 0){
            $query = "SELECT id FROM farmer WHERE mobile_no LIKE '%{$this->farmersPhoneNumber}'";
            $result = $this->database->runMySQLQuery($query, true);
            $farmerId = $result[0]['id'];
            
            if($acqType == 1) $type = "Purchase";
            else if($acqType == 2) $type = "Dowry";
            else if($acqType == 3) $type = "Gift";
            
            $query = "INSERT INTO unregistered_acq(farmer_id, type, number) VALUES({$farmerId}, '{$type}', {$messageDiff})";
            $this->database->runMySQLQuery($query, FALSE);
            
            $this->alertHandler->sendAcquisitionAlert($farmerId, $messageDiff);
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_1, 1);
            return $this->getAcqTypePage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else{
            $this->logHandler->log(3, $this->TAG, "User entered 0");
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_2, $acqType);
            return $this->getAcqNumberPage($this->translator->getText(Translator::$NUMBER_GT_ZERO));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Non numeric character entered by user");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_2, $acqType);
         return $this->getAcqNumberPage($this->translator->getText(Translator::$ENTER_NUMBER));
      }
   }
   
   /**
    * User last presented with list of cows to select the cow that has been disposed.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function disposalPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT cow.name,cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $result = $this->database->runMySQLQuery($query, true);
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME, 1);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($result)){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_2, $messageDiff);
         return $this->getDisposalScreen($result[$messageDiff-1]['name'], $result[$messageDiff-1]['ear_tag_number']);
      }
      else{
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW));
      }
   }
   
   /**
    * User last presented with list of types of disposal from which to select one.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function disposalPage2Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      $cowIndex = $this->getDataFromLastPage();
      
      $query = "SELECT cow.name,cow.ear_tag_number,cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $result = $this->database->runMySQLQuery($query, true);
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DISPOSAL));
      }
      else if(is_numeric($messageDiff) && $messageDiff < 5){
         $cowId = $result[$cowIndex - 1]['id'];
         
         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         
         if($messageDiff == 1) $type = "Sale";
         else if($messageDiff == 2) $type = "Death";
         else if($messageDiff == 3) $type = "Dowry Out";
         else if($messageDiff == 4) $type = "Gift Out";
         
         $query = "SELECT id FROM event WHERE name = '{$type}'";
         $value = $this->database->runMySQLQuery($query, true);
         $eventId = $value[0]['id'];
         
         $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES ({$cowId}, {$eventId}, '$date', '$datetime', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "UPDATE cow SET old_farmer_id = farmer_id, farmer_id = null WHERE id = {$cowId}";
         $this->database->runMySQLQuery($query, false);
         //$query = "UPDATE cow SET farmer_id = 0 WHERE id = {$cowId}";
         //$this->database->runMySQLQuery($query, true);
         
         //send sms to site coordinators and farmer
         $this->alertHandler->sendDeathAlert($cowId);
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_2, $cowIndex);
         return $this->getDisposalScreen($result[$cowIndex-1]['name'], $result[$cowIndex-1]['ear_tag_number'], $this->translator->getText(Translator::$UNKNOWN_OPTION_SELECTED));
      }
   }
   
   /**
    * User last presented with a list of cows from which to select the cow that has been vaccinated.
    * Process the data based on that context
    * 
    * @param String $serviceCode The USSD service code
    * @param String $prevMessage Message from the user from the last request in this session
    * @param String $message Message from the user from the current request in this session
    * 
    * @return String Text for the next page to be displayed
    */
   public function vaccinationPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT cow.name,cow.ear_tag_number,cow.id FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $result = $this->database->runMySQLQuery($query, true);
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_P_MEASURES_1, 1);
         return $this->getPMeasuresPage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($result)){
         $cowIndex = $messageDiff -1;
         $cowId = $result[$cowIndex]['id'];
         
         $query = "SELECT id FROM event WHERE name = 'Vaccination'";
         $eventId= $this->database->runMySQLQuery($query, true);
         
         $eventId = $eventId[0]['id'];
         
         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');

         $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, FALSE);
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_VACCINATION_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_VACCINATION_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW));
      }
   }
   
   /**
    * This function returns the text for the page on which a user is supposed to confirm the quantity of milk
    * produced by a cow.
    * 
    * @return String The text for the confirm milk produced page
    */
   public function getMilkConfirmPage(){
      $message = $this->translator->getText(Translator::$MILK_DATA_INCONSISTENT) . " \n";
      $message .= " 1 - " . $this->translator->getText(Translator::$CANCEL) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$OKAY) . " \n";
      
      return $message;
   }

   /**
    * This function returns a list of cows to be displayed to the user
    * 
    * @param String $prependMessage Message to be prepended to the list of cows
    * @param type $sex Specify either Male or Female if you want to display only cattle of a specific sex
    * @param type $constraintEvent Type of event e.g Milking of which all the cows in the list quanify to 
    *                               be associated with eg specifying Milking will show only cows that can be
    *                               milked
    * @return string The list of cattle to be displayed
    */
   public function getCowPage($prependMessage = "", $sex =-1, $constraintEvent = -1){
      $phoneNumber = $this->farmersPhoneNumber;
      
      if($sex == -1)
         $query = "SELECT cow.id, cow.name,cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$phoneNumber}' ORDER BY cow.id";
      else
         $query = "SELECT cow.id, cow.name,cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$phoneNumber}' AND cow.sex = '{$sex}' ORDER BY cow.id";
         
      $this->logHandler->log(3, $this->TAG, "query = ".$query);
      
      $fetchedCows = $this->database->runMySQLQuery($query, true);
      if(is_array($fetchedCows)){
         if($constraintEvent != -1){
            $result = $this->getValidCows($fetchedCows, $constraintEvent);
         }
         else{
            $result = $fetchedCows;
         }
         
         if(count($result) > 0){
            $message  = $prependMessage;
            if(strlen($message) > 0)
               $message .= " \n";
            
            for($cowIndex = 0; $cowIndex < count($result); $cowIndex++){
               $readableI = $cowIndex + $limitOffset + 1;
               $message .= $readableI . " - " . $this->getFormatedCowName($result[$cowIndex]['name'], $result[$cowIndex]['ear_tag_number']) . " \n";
            }
            $this->logHandler->log(3, $this->TAG, "Gotten cows for ".$phoneNumber);
            
            $message .= Pages::$PREV_OF_PAGE_CODE. " - ". $this->translator->getText(Translator::$TO_GO_BACK);
                    
            return $message;
         }
         else{
            if(count($fetchedCows)>0){
               $message = $this->translator->getText(Translator::$COWS_TOO_YOUNG) . " \n";
               $message .= Pages::$PREV_OF_PAGE_CODE. " - ". $this->translator->getText(Translator::$TO_GO_BACK);
            }
            else{
               $message = $this->translator->getText(Translator::$NO_COWS) . " \n";
               $message .= Pages::$PREV_OF_PAGE_CODE. " - ". $this->translator->getText(Translator::$TO_GO_BACK);
            }
            $this->logHandler->log(2, $this->TAG, "No cows gotten for ".$phoneNumber);
            return $message;
         }
      }
      else{
         $this->logHandler->log(2, $this->TAG, "Something went wrong while trying to get the list of cows for ".$phoneNumber);
         return -1;
      }
   }
   
   /**
    * This function returns the valid milking times for the specified cow
    * 
    * @param String $cowId The id of the cow
    * @param String $message Prepended message to the list of milking times
    * 
    * @return String The list of milking times to be displayed
    */
   private function getMilkingTimesPage($cowId, $message = ""){
      if(strlen($message) > 0)
         $message .= " \n";
      
      $milkingTimes = $this->getMilkingTimes($cowId);
      
      for($index = 0; $index < count($milkingTimes); $index++){
         $message .= " " . ($index + 1) . " - " .$this->translator->getText($milkingTimes[$index]). " \n";
      }
      
      if(count($milkingTimes) == 0){
         $message .= $this->translator->getText(Translator::$MILK_DATA_FOR_COW_ENTERED) . " \n";
      }
      
      $message .= " 0 - " .$this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }
   
   /**
    * This function returns the text for the milk production page
    * 
    * @param String $cacheData Data cached from the previous page
    * @param String $milkingTime The milking time
    * 
    * @return String Text for the page
    */
   private function getMilkProdPage($cacheData, $milkingTime){
      $xploded = explode(".", $cacheData);
      $cowIndex = $xploded[0];
      $time = $xploded[1];
      $query = "SELECT cow.id, cow.name, cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
      $fetchedCows = $this->database->runMySQLQuery($query, true);
      
      $result = $this->getValidCows($fetchedCows, "Milking");
      
      if(is_array($result) && count($result) >= $cowIndex){
         $cowIndex = $cowIndex - 1;//convert this to 0 index
         $cowIdentity = $this->getFormatedCowName($result[$cowIndex]['name'], $result[$cowIndex]['ear_tag_number']);
         $message = $this->translator->getText(Translator::$MILK_PROD_INSTR) ." ". $cowIdentity ." \n";
         
         $message .= $this->translator->getText($milkingTime);
         
         return $message;
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index as from farmer's cow list'".$cowIndex);
      }
   }
   
   /**
    * This function returns text for the list of servicing types
    * 
    * @param String $cowName The name of the cow
    * @param String $message Message to prepend to the list
    * 
    * @return String the text for the list of servicing types
    */
   private function getServicingPage($cowName, $message = ""){
      if(strlen($message) == 0)
         $message = $this->translator->getText(Translator::$SELECT_SERVICE_TYPE) . " \n";
      else
         $message .= " \n";
      
      $message .= $cowName . " \n";
         
      $message .= " 1 - " . $this->translator->getText(Translator::$BULL_SERVICING) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$ARTIFICIAL_INSEMINATION) . " \n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      return $message;
   }
   
   /**
    * This function returns the list of calving types
    * 
    * @param Integer $cowIndex The index of the cow that has calved
    * 
    * @return String The text for the list
    */
   public function getCalvingPage($cowIndex){
      $query = "SELECT cow.name, cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) >= $cowIndex){
         $cowIndex = $cowIndex - 1;
         $cowIdentity = $this->getFormatedCowName($result[$cowIndex]['name'], $result[$cowIndex]['ear_tag_number']);
         $message = $cowIdentity . ". ";
         $message .= $this->translator->getText(Translator::$CALVING_INSTR) . " \n";
         $message .= " 1 - " . $this->translator->getText(Translator::$NORMAL) . " \n";
         $message .= " 2 - " . $this->translator->getText(Translator::$STILL) . " \n";
         $message .= " 3 - " . $this->translator->getText(Translator::$PREMATURE) . " \n";
         $message .= " 4 - " . $this->translator->getText(Translator::$ABORTION) . " \n";
         $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
         return $message;
      }
   }
   
   public function getReproductionPage($appenedMessage = ""){
      if(strlen($appenedMessage) > 0){
         $appenedMessage = $appenedMessage . " \n";
      }
      
      $appenedMessage .= " 1 - " . $this->translator->getText(Translator::$RECORD_SOH) . " \n";
      $appenedMessage .= " 2 - " . $this->translator->getText(Translator::$RECORD_SERVICING) . " \n";
      $appenedMessage .= " 3 - " . $this->translator->getText(Translator::$RECORD_CALVING) . " \n";
      $appenedMessage .= " 4 - " . $this->translator->getText(Translator::$RECORD_PREG_CONF) . " \n";
      $appenedMessage .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      return $appenedMessage;
   }
   
   public function getRecordsPage($message = ""){
      if(strlen($message) > 0) $message .= " \n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$YDAY_MILK_RECORDS) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$WEEK_MILK_RECORDS) . " \n";
      $message .= " 3 - " . $this->translator->getText(Translator::$MONTH_MILK_RECORDS) . " \n";
      $message .= " 4 - " . $this->translator->getText(Translator::$MONTH_EVENT_RECORDS) . " \n";
      $message .= " 5 - " . $this->translator->getText(Translator::$WEATHER_FORECAST) . " \n";
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      return $message;
   }
   
   public function getEventsPage($message = ""){
      if(strlen($message) > 0) $message .= " \n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$START_OF_LACT) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DRY_OFF) . " \n";
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }
   
   public function getPregConfPage($message = ""){
      if(strlen($message) > 0) $message = $message . " \n";
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
      $result = $this->getValidCows($fetchedCows, "Pregnancy Confirmation");
      
      if(is_array($result) && count($result) > 0){
         for($index = 0; $index < count($result); $index++){
            $corrIndex = $index + 1;
            $message .= " " . $corrIndex . " - " . $this->getFormatedCowName($result[$index]['name'], $result[$index]['ear_tag_number']) . " \n";
         }
      }
      else{
         $message .= $this->translator->getText(Translator::$COWS_TOO_YOUNG) . " \n";
      }
      
      $message .= $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }
   
   public function getPMeasuresPage($message = ""){
      if(strlen($message) > 0) $message = $message . " \n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$DIPPING_SPRAYING) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DEWORMING) . " \n";
      $message .= " 3 - " . $this->translator->getText(Translator::$VACCINATION) . " \n";
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }
   
   /**
    * This function saves current data for the session on the database
    * 
    * @param Interger $phoneNumber The user's phone number
    * @param String $serviceCode The USSD service code
    * @param String $text The current message from the user
    * @param String $page The page currently being displayed to user
    * @param String $dataToCache Optional data that should be cached and used later on
    */
   public function updateSession($phoneNumber, $serviceCode, $text, $page, $dataToCache = -1){
      $this->logHandler->log(3, $this->TAG, "Updating session for ".$phoneNumber);
      
      $query = "UPDATE ussd_session SET is_active = 0 WHERE phone_number = '{$phoneNumber}' AND session_id = '{$this->sessionID}'";
      $this->database->runMySQLQuery($query, FALSE);
      
      if($dataToCache === -1){
         //$query = "UPDATE ussd_session SET last_code = '{$serviceCode}', last_text = '{$text}', last_page = '{$page}' WHERE phone_number = '{$phoneNumber}'";
         $query = "INSERT INTO ussd_session(phone_number, session_id, last_code, last_text, last_page, is_active, locale) ".
                 "VALUES('{$phoneNumber}', '{$this->sessionID}', '{$serviceCode}', '{$text}', '{$page}', 1, '{$this->translator->getLocale()}')";
      }
      else{
         //$query = "UPDATE ussd_session SET last_code = '{$serviceCode}', last_text = '{$text}', last_page = '{$page}', last_page_data = '{$dataToCache}' WHERE phone_number = '{$phoneNumber}'";
         $query = "INSERT INTO ussd_session(phone_number, session_id, last_code, last_text, last_page, last_page_data, is_active, locale) ".
                 "VALUES('{$phoneNumber}', '{$this->sessionID}', '{$serviceCode}', '{$text}', '{$page}', '{$dataToCache}', 1, '{$this->translator->getLocale()}')";
      }
      
      $this->logHandler->log(3, $this->TAG, "Session update query = ".$query);
         
      $this->database->runMySQLQuery($query, FALSE);
   }
   
   /**
    * This function returns cached data for this session
    * 
    * @return Mixed Returns a string with the cached data or -1 if no cached data
    */
   private function getDataFromLastPage(){
      $query = "SELECT last_page_data FROM ussd_session WHERE phone_number = '{$this->farmersPhoneNumber}' AND session_id = '{$this->sessionID}' AND is_active = 1";
      $results = $this->database->runMySQLQuery($query, true);
      if(is_array($results) && count($results) == 1){
         if(!is_null($results[0]['last_page_data'])) return $results[0]['last_page_data'];
         else{
            $this->logHandler->log(3, $this->TAG, "The cache is missing the last cached data from last page in request by phone number ".$this->farmersPhoneNumber);
            return -1;
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cached data from last page in request by phone number ".$this->farmersPhoneNumber);
         return -1;
      }
   }
   
   /**
    * This function returns a list of the types of animal acquisition
    * 
    * @param String $message Any text that should be prepended to the list
    * 
    * @return String The text for the list
    */
   private function getAcqTypePage($message = ""){
      if(strlen($message)== 0)
         $message = $this->translator->getText(Translator::$ACQ_INSTR) . " \n";
      else
         $message .= " \n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$PURCHASE) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DOWRY) . " \n";
      $message .= " 3 - " . $this->translator->getText(Translator::$GIFT) . " \n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }
   
   /**
    * This function returns the text for the screen that asks the user for the number 
    * of animals acquired
    * 
    * @param String $message Any message that should be prepended to the actual page text
    * 
    * @return String text for the acquisition number page
    */
   private function getAcqNumberPage($message = ""){
      if(strlen($message) > 0)
         $message .= " \n";
      
      $message .= $this->translator->getText(Translator::$ACQ_NO_INSTR);
      
      return $message;
   }
   
   /*private function getInfoPortalPage($message = ""){
      if(strlen($message) > 0)
         $message .= " \n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$WEATHER_FORECAST) . " \n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }*/
   
   /**
    * This function returns the list of disposal types for a cow
    * 
    * @param type $cowName The name of the cow to be disposed
    * @param type $cowEarTagNumber The ear tag number for the animal to be disposed
    * @param type $message Any message that should be prepended to the list of disposal types
    * 
    * @return string The text for the list
    */
   private function getDisposalScreen($cowName, $cowEarTagNumber, $message = ""){
      $cow = $this->getFormatedCowName($cowName, $cowEarTagNumber);
      
      if(strlen($message) == 0 )
         $message .= $this->translator->getText(Translator::$DISPOSAL_INSTR) . " \n";
      else
         $message .= " \n";
      
      $message .= $cow . " \n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$SALE) . " \n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DEATH) . " \n";
      $message .= " 3 - " . $this->translator->getText(Translator::$DOWRY) . " \n";
      $message .= " 4 - " . $this->translator->getText(Translator::$GIFT) . " \n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . " \n";
      
      return $message;
   }
   
   /**
    * This function returns text for the current time depending on the format specified
    * 
    * @param String $format The format for the time
    * 
    * @return String The text for the current time in the specified format
    */
   private function getTime($format) {
		$time = new DateTime('now');
		$formatedTime = $time->format($format);
		$this->logHandler->log(4, $this->TAG,"returning time '".$formatedTime."' using format '".$format."'");
		return $formatedTime;
	}
   
   public function getMessageDiff($message, $prevMessage){
      $messageDiff = $message;
      
      if(strlen($prevMessage) > 0)
         $messageDiff = preg_replace ("/^".preg_quote ($prevMessage)."/", "", $message);

      $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
      $messageDiff = preg_replace("/^\*/", "", $messageDiff);
      $messageDiff = $this->sanitiseMessage($messageDiff);
      $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
      
      return $this->sanitiseMessage($messageDiff);
   }
   
   /**
    * This function returns a list of valid milking times for the specified cow.
    * Contents of the list will depend on the current time (i.e afternoon and evening 
    * options will not be displayed if it is still morning) and already entered data
    * (i.e if user has already entered quantity of milk for specified cow in the morning
    * then the morning option will not be displayed)
    * 
    * @param type $cowId
    * @return type
    */
   private function getMilkingTimes($cowId){
      //get possible times today eg if its 10am show return morning only
      //1. load the default milking times
      $dMilkingTimes = parse_ini_file($this->ROOT."config/milking_times.ini");
      $dMorning = $this->getTime("Y-m-d") . " " . $dMilkingTimes['morning'];
      $dAfternoon = $this->getTime("Y-m-d") . " " . $dMilkingTimes['afternoon'];
      $dEvening = $this->getTime("Y-m-d") . " " . $dMilkingTimes['evening'];
      
      $today = $this->getTime('Y-m-d');

      if($cowId == ''){
         $this->logHandler->log(3, $this->TAG, "Interested cow id '". $cowId . "'");
         //$this->logHandler->log(3, $this->TAG, print_r(debug_backtrace(true, 2), true));
      }

      $query = "SELECT time FROM milk_production WHERE cow_id = ".$cowId." AND date = '{$today}'";
      $result = $this->database->runMySQLQuery($query, true);
      
      $this->logHandler->log(3, $this->TAG, "Morning time = ".$dMorning." and now = ".  time());
      
      $times = array();
      if(time()>=strtotime($dMorning)) array_push ($times, Translator::$MORNING);
      if(time()>=strtotime($dAfternoon)) array_push ($times, Translator::$AFTERNOON);
      if(time()>=strtotime($dEvening)) array_push ($times, Translator::$EVENING);
      /*if(time()>=strtotime($dEvening)) array_push ($times, Translator::$COMBINED); */
      
      if(is_array($times)){
         foreach($result as $currResult){
            if($currResult['time'] == "Morning"){
               if(($index = array_search(Translator::$MORNING, $times)) !== false){
                  unset($times[$index]);
               }
               if(($index = array_search(Translator::$COMBINED, $times)) !== false){
                  unset($times[$index]);
               }
            }
            else if($currResult['time'] == "Afternoon"){
               if(($index = array_search(Translator::$AFTERNOON, $times)) !== false){
                  unset($times[$index]);
               }
               if(($index = array_search(Translator::$COMBINED, $times)) !== false){
                  unset($times[$index]);
               }
            }
            else if($currResult['time'] == "Evening"){
               if(($index = array_search(Translator::$EVENING, $times)) !== false){
                  unset($times[$index]);
               }
               if(($index = array_search(Translator::$COMBINED, $times)) !== false){
                  unset($times[$index]);
               }
            }
            else if($currResult['time'] == "Combined"){
               if(($index = array_search(Translator::$COMBINED, $times)) !== false){
                  $times = array();
                  break;
               }
            }
         }
      }
      
      $now = new DateTime('now');
      //$yesterday = $now->format('Y').'-'.$now->format('m').'-'.((int)$now->format('d') - 1);
      //$yDayTime = time() - 86400;
      $yDayTime = strtotime("Yesterday");
      $yDayDate = new DateTime();
      $yDayDate->setTimestamp($yDayTime);
      $yesterday = $yDayDate->format('Y-m-d');
      $this->logHandler->log(3, $this->TAG, "Yesterday's date is ".$yesterday);
      
      $query = "SELECT time FROM milk_production WHERE cow_id = ".$cowId." AND date = '{$yesterday}'";
      $result = $this->database->runMySQLQuery($query, TRUE);
      
      $yTimes = array(Translator::$Y_MORNING, Translator::$Y_AFTERNOON, Translator::$Y_EVENING);//, Translator::$Y_COMBINED);
      if(is_array($result)){
        foreach ($result as $currResult){
           if($currResult['time'] == "Morning"){
              if(($index = array_search(Translator::$Y_MORNING, $yTimes)) !== false){
                 unset($yTimes[$index]);
              }
              if(($index = array_search(Translator::$Y_COMBINED, $yTimes)) !== false){
                 unset($yTimes[$index]);
              }
           }
           else if($currResult['time'] == "Afternoon"){
              if(($index = array_search(Translator::$Y_AFTERNOON, $yTimes)) !== false){
                 unset($yTimes[$index]);
              }
              if(($index = array_search(Translator::$Y_COMBINED, $yTimes)) !== false){
                 unset($yTimes[$index]);
              }
           }
           else if($currResult['time'] == "Evening"){
              if(($index = array_search(Translator::$Y_EVENING, $yTimes)) !== false){
                 unset($yTimes[$index]);
              }
              if(($index = array_search(Translator::$Y_COMBINED, $yTimes)) !== false){
                 unset($yTimes[$index]);
              }
           }
           else if($currResult['time'] == "Combined"){
              $yTimes = array();
              break;
           }
        }
      }
      
      return array_merge($times, $yTimes);
   }
   
   /**
    * This function checks whether an event can occur on the specfied cow
    * 
    * @param type $cowId The id of the cow 
    * @param type $event The type of event
    * 
    * @return boolean True if the event can occur on the specified cow and False otherwise
    */
   private function validateEvent($cowId, $event){
      
      
      
      if($event == "Birth"){
         
         $prefered = $this->getCowAgeMilli($cowId);
         if($prefered >=0){
            $minimumAge = $this->getEventTimeMilli("Maturity");
            
            if($prefered > $minimumAge){
               return true;
            }
            return FALSE;
         }
      }
      if($event == "Servicing"){
         
         $prefered = $this->getCowAgeMilli($cowId);
         if($prefered >=0){
            $minimumAge = $this->getEventTimeMilli("Maturity");
            
            if($prefered > $minimumAge){
               return true;
            }
         }
         return FALSE;
      }
      else if($event == "Start of Lactation"){
         //cow must be of age (maxtimebirthlactation)
         //cow should be recorded as dry
         $cowAge = $this->getCowAgeMilli($cowId);
         
         $query = "SELECT milking_status FROM cow WHERE id = {$cowId} AND milking_status != 'adult_milking'";
         $cow = $this->database->runMySQLQuery($query, true);
         if($cowAge >=0){
            $minimumAge = $this->getEventTimeMilli("Milking");
            
            if($cowAge > $minimumAge){
               if(is_array($cow) && count($cow) == 1){
                  return true;
               }
            }
         }
         return false;
      }
      else if($event == "Milking"){
         //the cow must be at least 15 + 7 months
         /*$cowAge = $this->getCowAgeMilli($cowId);
         if($cowAge >=0){
            $minimumAge = $this->getEventTimeMilli("Milking");
            if($cowAge>$minimumAge){
               return true;
            }
         }*/
         $query = "SELECT milking_status FROM cow WHERE id = {$cowId}";
         $result = $this->database->runMySQLQuery($query, true);
         if(is_array($result) && count($result) == 1){
            if($result[0]['milking_status'] == "adult_milking"){
               return TRUE;
            }
         }
         return FALSE;
      }
      
      else if($event == "Signs of Heat"){
         //the cow must be old enough
         //the cow must not be pregnant
         //Heat detection must be at least 21 days from last calving
         
         //the cow giving birth must be old enough
         $prefered = $this->getCowAgeMilli($cowId);
         if($prefered >= 0){
            $minimumAge = $this->getEventTimeMilli("Maturity");
            if($prefered > $minimumAge){
               return true;
            }
         }
         return FALSE;
      }
      else if($event == "Calving"){
         $cowAge = $this->getCowAgeMilli($cowId);
         if($cowAge>=0){
            $minimumAge = $this->getEventTimeMilli("Calving");
            if($cowAge> $minimumAge)
               return true;
         }
         return FALSE;
      }
      else if($event == "Pregnancy Confirmation"){
         $cowAge = $this->getCowAgeMilli($cowId);
         if($cowAge >= 0){
            $minimumAge = $this->getEventTimeMilli("Maturity");
            if($cowAge > $minimumAge){
               return true;
            }
         }
         return false;
      }
   }
   
   /**
    * This function determines the valid age of a cow in milliseconds considering it's specified age
    * and data of birth
    * 
    * @param type $cowId The id of the cow
    * 
    * @return Long The age of the cow in milliseconds
    */
   private function getCowAgeMilli($cowId){
      $query = "SELECT age, age_type, date_of_birth,date_added FROM cow WHERE id = {$cowId}";
      $result = $this->database->runMySQLQuery($query, true);

      if(is_array($result) && count($result) == 1 ){
         $this->logHandler->log(3, $this->TAG, "Date of birth = ".$result[0]['date_of_birth']);
         $this->logHandler->log(3, $this->TAG, "Age = ".$result[0]['age']);
         $this->logHandler->log(3, $this->TAG, "Age type = ".$result[0]['age_type']);
         
         $ageFactor = 0;
         $age = $result[0]['age'];
         $ageType = $result[0]['age_type'];
         if($result[0]['date_of_birth'] != NULL && $result[0]['date_of_birth'] != "0000-00-00 00:00:00"){
            $this->logHandler->log(3, $this->TAG, "Date of birth is not null");
            $dateOfBirth = strtotime($result[0]['date_of_birth']);
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Date of birth IS NULL");
            $dateOfBirth = time();
         }
         
         $this->logHandler->log(3, $this->TAG, "*************** time  = ".time());

         if($ageType == "Years") $ageFactor = 86400000 * 365;
         else if($ageType == "Months") $ageFactor = 86400000 * 30;
         else if($ageType == "Days") $ageFactor = 86400000;

         $ageMilli = $age * $ageFactor;
         $addToAge = (time() - strtotime($result[0]['date_added'])) * 1000;//get the time in milliseconds from when cow was added to now to get the current age
         $ageMilli = $ageMilli + $addToAge;
         
         $dateOfBirthMilli = (time() - $dateOfBirth) * 1000;//convert from seconds to milliseconds
            
         $prefered = $dateOfBirthMilli;
         if($dateOfBirthMilli < $ageMilli)
            $prefered = $ageMilli;
         
         $this->logHandler->log(3, $this->TAG, "Cow age is ".$prefered);
         
         return $prefered;
      }
      
      return -1;
   }
   
   /**
    * This function returns the time in milliseconds for constaints.
    * Check event_vtime table on MySQL for more details.
    * 
    * @param type $event The name of the constraint
    * 
    * @return Long the time in milliseconds associated to an event
    */
   private function getEventTimeMilli($event){
      $query = "SELECT * FROM event_vtime";
      $eventTimes = $this->database->runMySQLQuery($query, true);
      
      $time = -1;
      
      foreach ($eventTimes as $currEType){
         if($currEType['event'] == $event){
            $unit = 0;

            if($currEType['time_units'] == "Days") $unit =  86400000;
            else if($currEType['time_units'] == "Months") $unit =  86400000 * 30;
            else if($currEType['time_units'] == "Years") $unit =  86400000 * 365;

            $time = $unit * $currEType['time'];
         }
      }
      
      $this->logHandler->log(3, $this->TAG, $event. " ".$time);
      
      return $time;
   }
   
   /**
    * This function filters out valid cows from the array of cows provided.
    * Validity of a cow depends on the type of event ($eventConstraint) specifed.
    * E.G if $eventConstraint specified is 'Milking' the an array of cows that can
    * be milked is returned
    * 
    * @param type $cows The array of cows to be considered
    * @param type $eventConstraint The constraint to be used for the filtering
    * 
    * @return Array An Array of the valid cows
    */
   private function getValidCows($cows, $eventConstraint){
      
      $originalSize = count($cows);
      
      for($index = 0; $index < $originalSize; $index++){
         $this->logHandler->log(4, $this->TAG, "Cows = ".print_r($cows, true));
         
         if($this->validateEvent($cows[$index]['id'], $eventConstraint)==false){
            unset($cows[$index]);
         }
         
      }
      
      return array_values($cows);
   }
   
   /**
    * This function checks for inconsistency in the specified milk quantity 
    * specified based on milk production history for the cow
    * 
    * @param type $cowID The id of the cow from which milking history is to be checked
    * @param type $time The milking time e.g 'Morning' from which the $quantity is of
    * @param type $quantity The quantity of milk produced by cow if id $cowID and of time $time
    * @param type $quantityType The quantity type for the quantity of milk specified
    * 
    * @return boolean True if milk quantity is consistent with history and False if not
    */
   private function checkMilkData($cowID, $time, $quantity, $quantityType){
      $query = "SELECT date, quantity, quantity_type FROM milk_production WHERE cow_id = {$cowID} AND time = '{$time}' ORDER BY date DESC LIMIT 1";//get latest milk production for cow for specified time
      $result = $this->database->runMySQLQuery($query, TRUE);
      
      if(is_array($result) && count($result) > 0){
         $prevQuantity = $result[0]['quantity'];
         $prevType = $result[0]['quantity_type'];//TODO: convert quantity based on type
         $prevDate = $result[0]['date'];
         
         $prevDateMilli = strtotime($prevDate) * 1000;//convert seconds to milliseconds
         $todayMilli = time() * 1000;
         
         $query = "SELECT time, time_units FROM event_vtime WHERE event = 'DeltaMilk'";
         $constraint = $this->database->runMySQLQuery($query, TRUE);
         $constraint = $constraint[0];
         
         $dayDiff = abs($prevDateMilli - $todayMilli)/86400000;
         
         $quantityDiff = abs($prevQuantity - $quantity);
         
         $diff = $quantityDiff / $dayDiff;
         
         if($diff > $constraint['time']){
            return FALSE;
         }
      }
      
      return TRUE;
   }
   
   /**
    * This function returns a formated cow name based on cow's actual name and 
    * ear tag number
    * 
    * @param type $name The name of the cow
    * @param type $earTagNumber The ear tag number of the cow
    * @return type The formated cow name
    */
   private function getFormatedCowName($name, $earTagNumber){
      if(strlen($earTagNumber) > 0 && strlen($name)> 0){
         return $name . " (" . $earTagNumber . ")"; 
      }
      else if(strlen($earTagNumber) == 0){
         return $name;
      }
      else if(strlen($name) == 0){
         return $earTagNumber;
      }
   }
}
?>
