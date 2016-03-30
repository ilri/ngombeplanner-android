<?php

/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class PagesT{
   private $ROOT = "./";
   private $TAG = "Pages";
   public static $DELIMITER = "_*_";
   public static $PAGE_HOME = "home";
   public static $PAGE_CHANGE_NUMBER = "changeNumber";
   public static $PAGE_MILKING_1 = "milking1";//show the cows
   public static $PAGE_MILKING_2 = "milking2";//show the milking times
   public static $PAGE_MILKING_3 = "milking3";//show milk quantity
   public static $PAGE_MILKING_4 = "milking4";//show confirmation if milk data is inconsistent
   public static $PAGE_MILKING_5 = "milking5";//show confirmation if milk data is inconsistent
   public static $PAGE_CALVING_1 = "calving1";
   public static $PAGE_CALVING_2 = "calving2";
   public static $PAGE_REP_1 = "reprodution1";
   public static $PAGE_SERVICING_1 = "servicing1";
   public static $PAGE_SERVICING_2 = "servicing2";
   public static $PAGE_SERVICING_3 = "servicing3";
   public static $PAGE_SICKNESS_1 = "sickness1";
   public static $PAGE_SICKNESS_2 = "sickness2";
   public static $PAGE_SOH_1 = "soh1";
   public static $PAGE_SOH_2 = "soh2";
   public static $PAGE_ACQ_1 = "acq1";
   public static $PAGE_ACQ_2 = "acq2";
   public static $PAGE_DISPOSAL_1= "disposal1";
   public static $PAGE_DISPOSAL_2= "disposal2";
   public static $PAGE_DISPOSAL_3= "disposal3";
   public static $PAGE_VACCINATION_1 = "vaccination1";
   public static $PAGE_PREG_CONF_1 = "pregConfirm1";
   public static $PAGE_PREG_CONF_2 = "pregConfirm2";
   public static $PAGE_RECORDS_1 = "records1";
   public static $PAGE_EVENTS_1 = "otherEvents1";
   public static $PAGE_P_MEASURES_1 = "prevMeasures1";
   public static $PAGE_START_OF_LACT_1 = "startOfLact1";
   public static $PAGE_DRY_OFF_1 = "dryOff1";
   public static $PAGE_INFO_PORTAL_1 = "infoPortal1";
   public static $PAGE_FEEDING_1 = "feeding1";
   public static $PAGE_FEEDING_2 = "feeding2";
   public static $PAGE_FEEDING_3 = "feeding3";
   public static $PAGE_FEEDING_4 = "feeding4";
   public static $PAGE_DEATH_1 = "death1";
   public static $PAGE_DEATH_2 = "death2";
   public static $PAGE_WATER_1 = "water1";
   public static $PAGE_WATER_2 = "water2";
   public static $PAGE_WATER_3 = "water3";
   public static $PAGE_MILK_CONSUMED_1 = "milkConsumed1";
   public static $PAGE_MILK_RESERVED_1 = "milkResereved1";
   public static $PAGE_MILK_SOLD_1 = "milkSold1";//time
   public static $PAGE_MILK_SOLD_2 = "milkSold2";//category
   public static $PAGE_MILK_SOLD_3 = "milkSold3";//litres sold
   public static $PAGE_MILK_SOLD_4 = "milkSold4";//litres rejected
   public static $PAGE_MILK_SOLD_5 = "milkSold5";//price litre
   public static $PAGE_MILK_SOLD_6 = "milkSold6";//transport cost
   
   public static $R_HOME_MILKING1 = 1;
   public static $R_HOME_FEEDING = 2;
   public static $R_HOME_WATER = 3;
   public static $R_HOME_SICKNESS = 7;
   public static $R_HOME_REP = 15;
   public static $R_HOME_INSEMINATION = 4;
   //public static $R_HOME_SOH = 3;
   //public static $R_HOME_SERVICING = 4;   
   public static $R_HOME_CALVING = 5;
   public static $R_HOME_RECORDS = 12;
   //public static $R_HOME_CALVING = 5;
   public static $R_HOME_DEATH = 6;
   public static $R_HOME_EVENTS = 13;
   //public static $R_HOME_VACCINATION = 6;
   public static $R_HOME_P_MEASURES = 12;
   public static $R_HOME_ACQ = 14;
   public static $R_HOME_DISPOSAL = 8;
   public static $R_HOME_MILK_CONSUMED = 9;
   public static $R_HOME_MILK_RESERVED = 10;
   public static $R_HOME_MILK_SOLD = 11;
   
   public static $NEXT_OF_PAGE_CODE = 98;
   public static $PREV_OF_PAGE_CODE = 0;
   public static $MAX_PER_PAGE = 10;
   
   public static $INTVL_MILK_PRODUCTION = 7;//interval in days
   public static $INTVL_WATER = 14;//interval in days
   public static $INTVL_MILK_SALE = 14;//interval in days
   public static $INTVL_MILK_RESERVED = 14;//interval in days
   public static $INTVL_MILK_CONSUMED = 14;//interval in days
   public static $INTVL_FEEDING = 14;//interval in days
   
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
      $query = "SELECT name,id FROM farmer WHERE mobile_no LIKE '%{$this->farmersPhoneNumber}'";
      $names = $this->database->runMySQLQuery($query, true);
      if(is_array($names) && count($names) === 1){
         $name = explode(" ", $names[0]['name']);
         $farmerId = $names[0]['id'];
         $message = "";
         if(strlen($text) == 0)
            $message .= $this->translator->getText(Translator::$WELCOME) . " ". $name[0] . "\n";
         else
            $message .= $text . "\n";
         $menuItems = $this->getMainMenuItems($farmerId);
         $index = 1;
         foreach($menuItems as $currMenuItem) {
            $message .= "$index. ".$this->translator->getText($currMenuItem)."\n";
            $index++;
         }
         return $message;
      }
      else{
         $message = $this->translator->getText(Translator::$NOT_REGISTERED);
         return $message;
      }
   }
   
   private function getMainMenuItems($farmerId = -1){
      if($farmerId == -1) {
         $query = "SELECT name,id FROM farmer WHERE mobile_no LIKE '%{$this->farmersPhoneNumber}'";
         $names = $this->database->runMySQLQuery($query, true);
         if(count($names) == 1) {
            $farmerId = $names[0]['id'];
         }
      }
      $menuItems = array();
      $query = "SELECT b.id FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.id = $farmerId ORDER BY b.id";
      $cows = $this->database->runMySQLQuery($query, TRUE);
      $validMilkingCows = $this->getValidCows($cows, "Milking");
      if(count($validMilkingCows) > 0) {
         $menuItems[] = Translator::$MILK_DATA;
      }
      $validWaterCows = $this->getValidCows($cows, "Water");
      if(count($validWaterCows) > 0) {
         $menuItems[] = Translator::$WATER;
      }
      $validFeedingCows = $this->getValidCows($cows, "Feeding");
      if(count($validFeedingCows) > 0) {
         $menuItems[] = Translator::$FEEDING;
      }
      $validPregnancyCows = $this->getValidCows($cows, "Pregnancy Confirmation");
      if(count($validPregnancyCows) > 0) {
         $menuItems[] = Translator::$INSEMINATION_RESULTS;
      }
      if($this->validateEvent($farmerId, "Milk consumed")) {
         $menuItems[] = Translator::$MILK_CONSUMED;
      }
      if($this->validateEvent($farmerId, "Milk reserved")) {
         $menuItems[] = Translator::$MILK_RESERVED;
      }
      if($this->validateEvent($farmerId, "Milk sold")) {
         $menuItems[] = Translator::$MILK_SOLD;
      }
      $menuItems[] = Translator::$INSEMINATION;
      $menuItems[] = Translator::$CALVING;
      $menuItems[] = Translator::$SICKNESS;
      $menuItems[] = Translator::$DEATH;
      $menuItems[] = Translator::$EXIT;
      return $menuItems;
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
            
            $this->logHandler->log(3, $this->TAG, "Last page for ".$this->farmersPhoneNumber." was ".$lastPage." ".PagesT::$PAGE_P_MEASURES_1);
            
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
            else if($lastPage == PagesT::$PAGE_MILKING_5){
               return $this->milkingPage5Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_REP_1){
               return $this->repPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_RECORDS_1){
               return $this->recordsPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_EVENTS_1){
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
            else if($lastPage == PagesT::$PAGE_SERVICING_3){
               return $this->servicingPage3Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_SICKNESS_1){
               return $this->sicknessPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == Pages::$PAGE_CALVING_1){
               return $this->calvingPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_PREG_CONF_1){
               return $this->pregConfPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_PREG_CONF_2){
               return $this->pregConfPage2Last($serviceCode, $lastMessage, $message);
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
            else if($lastPage == PagesT::$PAGE_DISPOSAL_3){
               return $this->disposalPage3Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_P_MEASURES_1){
               return $this->pMeasuresPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_START_OF_LACT_1){
               return $this->startOfLactPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_DRY_OFF_1){
               return $this->dryOffPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_FEEDING_1) {
               return $this->feedingPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_FEEDING_2) {
               return $this->feedingPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_FEEDING_3) {
               return $this->feedingPage3Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_FEEDING_4) {
               return $this->feedingPage4Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_DEATH_1) {
               return $this->deathPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_DEATH_2) {
               return $this->deathPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_WATER_1) {
               return $this->waterPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_WATER_2) {
               return $this->waterPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_WATER_3) {
               return $this->waterPage3Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_CONSUMED_1) {
               return $this->milkConsumedPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_RESERVED_1) {
               return $this->milkReservedPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_SOLD_1) {
               return $this->milkSoldPage1Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_SOLD_2) {
               return $this->milkSoldPage2Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_SOLD_3) {
               return $this->milkSoldPage3Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_SOLD_4) {
               return $this->milkSoldPage4Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_SOLD_5) {
               return $this->milkSoldPage5Last($serviceCode, $lastMessage, $message);
            }
            else if($lastPage == PagesT::$PAGE_MILK_SOLD_6) {
               return $this->milkSoldPage6Last($serviceCode, $lastMessage, $message);
            }
            /*else if($lastPage == PagesT::$PAGE_INFO_PORTAL_1){
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
      $mainMenuItems = $this->getMainMenuItems();
      if(is_numeric($messageDiff) && $messageDiff <= count($mainMenuItems)) {
         if($mainMenuItems[$messageDiff - 1] == Translator::$MILK_DATA){
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
         /*else if($messageDiff == PagesT::$R_HOME_RECORDS){
            $this->logHandler->log(3, $this->TAG, "should go to records page");
            $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage();
         }*/
         /*else if($mainMenuItems[$messageDiff - 1] == Translator::$INSEMINATION){
            $this->logHandler->log(3, $this->TAG, "should go to reproduction page");
            $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_REP_1, 1);
            return $this->getReproductionPage();
         }*/
         else if($mainMenuItems[$messageDiff - 1] == Translator::$SICKNESS){
            $this->logHandler->log(3, $this->TAG, "should go to sickness page");
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_SICKNESS_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SICKNESS));
         }
         /*else if($messageDiff == PagesT::$R_HOME_P_MEASURES){
            $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_P_MEASURES_1, 1);
            return $this->getPMeasuresPage();
         }*/
         /*else if($messageDiff == Pages::$R_HOME_VACCINATION){
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_VACCINATION_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_VACCINATION));
         }*/
         /*else if($messageDiff == Pages::$R_HOME_CALVING){
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_CALVING), "Female", "Calving");
         }*/
         /*else if($messageDiff == PagesT::$R_HOME_EVENTS){
            $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1, 1);
            return $this->getEventsPage();
         }*/
         /*else if($messageDiff == PagesT::$R_HOME_ACQ){
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_ACQ_1, 1);
            return $this->getAcqTypePage();
         }*/
         else if($mainMenuItems[$messageDiff - 1] == Translator::$EXIT){
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DISPOSAL));
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$FEEDING) {
            $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_1, 1);
            return $this->getFeedingPage();
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$INSEMINATION) {
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_SERVICING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_SERVICING), "Female", "Servicing");
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$CALVING) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_CALVING_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_CALVING), "Female", "Calving");
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$DEATH) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DEATH_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DEATH));
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$WATER) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_WATER), -1, "Water");
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$MILK_CONSUMED) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_CONSUMED_1, 1);
            return $this->getMilkConsumedPage();
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$MILK_RESERVED) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_RESERVED_1, 1);
            return $this->getMilkReservedPage();
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$MILK_SOLD) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_2, 1);
            return $this->getMilkCategoryPage();
         }
         else if($mainMenuItems[$messageDiff - 1] == Translator::$INSEMINATION_RESULTS) {
            $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_PREG_CONF_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$INSEMINATION_RESULTS_INSTRUCTIONS), "Female", "Pregnancy Confirmation");
         }
         else {
            $this->logHandler->log(3, $this->TAG, "Was unable to determine where ".$messageDiff." will lead to. Sending user back to home");
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
            return $this->getHomePage($this->translator->getText(Translator::$SELECT_VALID_OPTION));
         }
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
         $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$phoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
         $result = $this->getValidCows($fetchedCows, "Milking");
         $this->logHandler->log(4, $this->TAG, " *** Cows** = ".print_r($result, true));
         
         if($messageDiff <= count($result)){
            $cowIndex = $messageDiff - 1;
            $this->updateSession($phoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_2, $messageDiff);
            return $this->getMilkingTimesPage($result[$cowIndex]['id'], $this->getFormatedCowName($result[$cowIndex]['name'], $result[$cowIndex]['ear_tag_number']));
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
         
         $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$phoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
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
            
            return $this->getMilkingTimesPage($result[$cowIndex - 1]['id'], $this->getFormatedCowName($result[$cowIndex - 1]['name'], $result[$cowIndex - 1]['ear_tag_number']),$this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
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
               $query = "select name, ear_tag_number from cow where id = $cowId";
               $result = $this->database->runMySQLQuery($query, true);
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILKING_5, $cowId);
               //return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Milking");
               return $this->getFeedingQuantityPage($this->getFormatedCowName($result[0]['name'], $result[0]['ear_tag_number']), $this->translator->getText(Translator::$CONCENTRATE));
            }
            else{
               $this->logHandler->log(3, $this->TAG, "User already entered data for this cow for this time ". $messageDiff);
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
               return $this->getCowPage($this->translator->getText(Translator::$MILK_DATA_ALREADY_THERE), "Female", "Milking");
            }
         }
      }
   }
   
   public function milkingPage5Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//quantity
      $cowId = $this->getDataFromLastPage();//cow id
      if(is_numeric($messageDiff)){//user selected a valid feed option
         $feed = $this->translator->getText(Translator::$CONCENTRATES, "en");
         $yDayTime = strtotime("Yesterday");
         $yDayDate = new DateTime();
         $yDayDate->setTimestamp($yDayTime);
         $date = $yDayDate->format('y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         $query = "INSERT INTO feeding(cow_id, date, date_added, food, quantity, app_used, no_used, ussd_session_id) VALUES($cowId, '$date', '$datetime', '$feed', $messageDiff, 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
         $this->database->runMySQLQuery($query, false);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_MILKING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED), "Female", "Milking");
      }
      else{//user selected an unknown option
         $query = "select name, ear_tag_number from cow where id = $cowId";
         $cows = $this->database->runMySQLQuery($query, true);
         $cowName = "";
         if(count($cows)  == 1){
            $cowName = $this->getFormatedCowName($cows[0]['name'], $cows[0]['ear_tag_number']);
         }
         $this->logHandler->log(3, $this->TAG, "User entered a non integer while recording feeding quantity");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILKING_5, $cowId);
         return $this->getFeedingQuantityPage($cowName, $this->translator->getText(Translator::$CONCENTRATES), $this->translator->getText(Translator::$ENTER_NUMBER));
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
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_REP_1);
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
      
      if($messageDiff == PagesT::$PREV_OF_PAGE_CODE){//user whats to go back home
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
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_PREG_CONF_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_PREG_CONF), "Female", "Pregnancy Confirmation");
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_REP_1, 1);
            return $this->getReproductionPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_REP_1, 1);
         return $this->getReproductionPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function recordsPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == PagesT::$PREV_OF_PAGE_CODE){//user whats to go back home
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
               $message = $this->translator->getText(Translator::$MILK_RECORDS_YDAY) . "\n";
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
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
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
               $message = $this->translator->getText(Translator::$MILK_RECORDS_WEEK) . "\n";
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
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
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
               $message = $this->translator->getText(Translator::$MILK_RECORDS_MONTH) . "\n";
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
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
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
               $message = $this->translator->getText(Translator::$EVENT_RECORDS_MONTH) . "\n";
               foreach($yDayMilk AS $currCow){
                  $message .= $this->getFormatedCowName($currCow['name'], $currCow['ear_tag_number']) . " - " . $currCow['events']."\n";
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
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$INFO_WILL_BE_SENT));
         }
         else if($messageDiff == 5){//user wants to go to the weather forecast
            /*$this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_INFO_PORTAL_1, 1);
            return $this->getInfoPortalPage();*/
            
            //get weather
            include_once $this->ROOT.'php/common/weather_forecast.php';
            $wForecast = new WForecast($this->ROOT,$this->database, $this->logHandler);
            $result = $wForecast->getWeatherForecast($this->farmersPhoneNumber);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
            
            if($result == true){
               return $this->getRecordsPage($this->translator->getText(Translator::$FORECAST_SENT_SMS));
            }
            else {
               return $this->getRecordsPage($this->translator->getText(Translator::$FORECAST_UNAVAILABLE));
            }
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
            return $this->getRecordsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1, 1);
         return $this->getRecordsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function eventsPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == PagesT::$PREV_OF_PAGE_CODE){//user whats to go back home
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff)){
         if($messageDiff == 1){//start of lactionation
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_START_OF_LACT_1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_START_OF_LACT), "Female", "Start of Lactation");
         }
         else if($messageDiff == 2){//dry off
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_START_OF_LACT_1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DRY_OFF), "Female", "Milking");
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1, 1);
            return $this->getEventsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function pMeasuresPage1Last($serviceCode, $prevMessage, $message){
      $this->logHandler->log(3, $this->TAG, "pMeasuredPage1Last called ".$this->farmersPhoneNumber);
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == PagesT::$PREV_OF_PAGE_CODE){//user whats to go back home
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
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_P_MEASURES_1, 1);
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
            
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_P_MEASURES_1, 1);
            return $this->getPMeasuresPage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else if($messageDiff == 3){//vaccination
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_VACCINATION_1, 1);
            return $this->getCowPage($this->translator->getText(Translator::$RECORDING_VACCINATION));
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_P_MEASURES_1, 1);
            return $this->getPMeasuresPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
         }
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_P_MEASURES_1, 1);
         return $this->getPMeasuresPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function startOfLactPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
      $result = $this->getValidCows($fetchedCows, "Start of Lactation");
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1);
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
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_START_OF_LACT_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Start of Lactation");
      }
   }
   
   
   public function dryOffPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         
      $result = $this->getValidCows($fetchedCows, "Milking");
      
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1);
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
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_EVENTS_1, 1);
         return $this->getEventsPage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_START_OF_LACT_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW), "Female", "Milking");
      }
   }
   
   public function feedingPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//message diff is feed index
      $feeds = $this->getFeeds();
      if($messageDiff == 0){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($feeds)){
         if($feeds[$messageDiff - 1] == Translator::$CONCENTRATES) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_2, $feeds[$messageDiff - 1]);
            return $this->getCowPage(sprintf($this->translator->getText(Translator::$FEEDING_COW_INSTRUCTIONS), strtoupper($this->translator->getText($feeds[$messageDiff - 1]))), -1, "Feeding-Concentrates");
         }
         else {
            $feed = $this->translator->getText($feeds[$messageDiff - 1], "en");
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $date = $yDayDate->format('y-m-d');
            $datetime = $this->getTime('Y-m-d H:i:s');
            $query = "INSERT INTO feeding(cow_id, date, date_added, food, app_used, no_used, ussd_session_id) select cow.id as cow_id, '$date', '$datetime', '$feed', 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}' from cow inner join farmer on cow.farmer_id = farmer.id where farmer.mobile_no like '%{$this->farmersPhoneNumber}'";
            $this->database->runMySQLQuery($query, false);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_1, 1);
            return $this->getFeedingPage($this->translator->getText(Translator::$INFO_RECORDED));
         }
      }
      else{
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_1, 1);
         return $this->getFeedingPage($this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   public function feedingPage2Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//cow index
      $cachedData = $this->getDataFromLastPage();//feed type
      $query = "SELECT cow.id as id, cow.name as name,cow.ear_tag_number as ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $allCows = $this->database->runMySQLQuery($query, true);
      $cows = $this->getValidCows($allCows, "Feeding-Concentrates");
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_1, 1);
         return $this->getFeedingPage();
      }
      else if(is_numeric($messageDiff) && count($cows) >= $messageDiff){//user has selected a feed
         $cowName = $this->getFormatedCowName($cows[$messageDiff - 1]['name'], $cows[$messageDiff - 1]['ear_tag_number']);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_3, $cachedData.PagesT::$DELIMITER.$cows[$messageDiff - 1]['id']);
         return $this->getFeedingQuantityPage($cowName, $this->translator->getText($cachedData));
      }
      else{//user entered an options that is unknown
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_2, $cachedData);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW)."\n".sprintf($this->translator->getText(Translator::$FEEDING_COW_INSTRUCTIONS), strtoupper($this->translator->getText($cachedData))), -1, "Feeding-Concentrates");
      }
   }
   
   public function feedingPage3Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//quantity
      $cachedData = $this->getDataFromLastPage();//feed type and cow id split by PagesT::$DELIMITER
      $parts = explode(PagesT::$DELIMITER, $cachedData);
      $type = $parts[0];
      $cowId = $parts[1];
      if(is_numeric($messageDiff)){//user selected a valid feed option
         $feed = $this->translator->getText($type, "en");
         $yDayTime = strtotime("Yesterday");
         $yDayDate = new DateTime();
         $yDayDate->setTimestamp($yDayTime);
         $date = $yDayDate->format('y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         $query = "INSERT INTO feeding(cow_id, date, date_added, food, quantity, app_used, no_used, ussd_session_id) VALUES($cowId, '$date', '$datetime', '$feed', $messageDiff, 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
         $this->database->runMySQLQuery($query, false);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_2, $type);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED)."\n".sprintf($this->translator->getText(Translator::$FEEDING_COW_INSTRUCTIONS), strtoupper($this->translator->getText($type))), -1, "Feeding-Concentrates");
      }
      else{//user selected an unknown option
         $query = "select name, ear_tag_number from cow where id = $cowId";
         $cows = $this->database->runMySQLQuery($query, true);
         $cowName = "";
         if(count($cows)  == 1){
            $cowName = $this->getFormatedCowName($cows[0]['name'], $cows[0]['ear_tag_number']);
         }
         $this->logHandler->log(3, $this->TAG, "User entered a non integer while recording feeding quantity");
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_3, $cachedData);
         return $this->getFeedingQuantityPage($cowName, $this->translator->getText($type));
      }
   }
   
   /*public function feedingPage4Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//amount in Kg
      $cachedData = $this->getDataFromLastPage();//combination of cow id, time and type split by PagesT::$DELIMITER
      $parts = explode(PagesT::$DELIMITER, $cachedData);
      $cowId = $parts[0];
      $feedingTimes = $this->getFeedingTimes($cowId);
      $selectedT = $feedingTimes[$parts[1] - 1];
      $feedType = $parts[2];
      $query = "SELECT name, ear_tag_number FROM cow WHERE id = $cowId";
      $cows = $this->database->runMySQLQuery($query, true);
      $cowName = $this->getFormatedCowName($cows[0]['name'], $cows[0]['ear_tag_number']);
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_3, $parts[0].PagesT::$DELIMITER.$parts[1]);
         return $this->getFeedingPage($cowName);
      }
      else if(is_numeric($messageDiff)){//user entered a correct amount
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
         $datetime = $this->getTime('Y-m-d H:i:s');
         $query = "INSERT INTO feeding(cow_id, time, date, date_added, food, quantity, app_used, no_used, ussd_session_id) VALUES($cowId, '$mTime', '$date', '$datetime', '$feedType', $messageDiff, 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
         $this->database->runMySQLQuery($query, false);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED)."\n".$this->translator->getText(Translator::$RECORDING_FEEDING), -1, "Feeding");
      }
      else{//user entered an invalid number
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_FEEDING_4, $cachedData);
         return $this->getFeedingQuantityPage($cowName, $this->translator->getText($selectedT), $this->translator->getText(Translator::$$CONCENTRATES));
      }
   }*/
   
   public function deathPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//cow index
      $query = "SELECT cow.id as id, cow.name as name,cow.ear_tag_number as ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $cows = $this->database->runMySQLQuery($query, true);
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($cows)) {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DEATH_2, $cows[$messageDiff - 1]['id']);
         $cowName = $this->getFormatedCowName($cows[$messageDiff - 1]['name'], $cows[$messageDiff - 1]['ear_tag_number']);
         return $this->getCauseOfDeathPage($cowName);
      }
      else{//user entered an invalid number
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DEATH_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW));
      }
   }
   
   public function deathPage2Last($serviceCode, $prevMessage, $message) {
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//cause of death index
      $cowId = $this->getDataFromLastPage();//cow id
      $causes = $this->getCausesOfDeath();
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DEATH_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_DEATH));
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($causes)){//user selected a valid cow index
         $cause = "";
         if($causes[$messageDiff - 1] == Translator::$NATURAL) {
            $cause = "Natural Causes";
         }
         else if($causes[$messageDiff - 1] == Translator::$SICKNESS) {
            $cause = "Sickness";
         }
         else if($causes[$messageDiff - 1] == Translator::$INJURY) {
            $cause = "Injury";
         }
         $query = "select id from cause_of_death where name = '$cause'";
         $result = $this->database->runMySQLQuery($query, true);
         $codId = $result[0]['id'];
         $query = "SELECT id FROM event WHERE name = 'Death'";
         $value = $this->database->runMySQLQuery($query, true);
         $eventId = $value[0]['id'];
         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         $query = "INSERT INTO cow_event(cow_id, event_id, cod_id, event_date, date_added, app_used, no_used) VALUES ({$cowId}, {$eventId}, $codId, '$date', '$datetime', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "UPDATE cow SET old_farmer_id = farmer_id, farmer_id = null WHERE id = {$cowId}";
         $this->database->runMySQLQuery($query, false);
         
         $this->alertHandler->sendDeathAlert($cowId);
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{//user entered an invalid number
         $query = "select name, ear_tag_number from cow where id = $cowId";
         $result = $this->database->runMySQLQuery($query, TRUE);
         $cowName = $this->getFormatedCowName($result[0]['name'], $result[0]['ear_tag_number']);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DEATH_2, $cowId);
         return $this->getCauseOfDeathPage($cowName, $this->translator->getText(Translator::$UNKNOWN_OPTION_SELECTED));
      }
   }
   
   public function waterPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//cow index
      $cowIndex = $messageDiff - 1;
      $query = "SELECT cow.id as id, cow.name as name,cow.ear_tag_number as ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $allCows = $this->database->runMySQLQuery($query, true);
      $cows = $this->getValidCows($allCows, "Water");
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($cows)){//user selected a valid cow index
         $cowId = $cows[$cowIndex]['id'];
         $cowName = $this->getFormatedCowName($cows[$cowIndex]['name'], $cows[$cowIndex]['ear_tag_number']);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_2, $cowId);
         return $this->getWaterTypesPage($cowName);
      }
      else{//user entered an invalid number
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW)."\n".$this->translator->getText(Translator::$RECORDING_WATER), -1, "Water");
      }
   }
   
   public function waterPage2Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//water type index
      $cachedData = $this->getDataFromLastPage();//cow id
      $query = "SELECT name, ear_tag_number from cow where id = $cachedData";
      $cows = $this->database->runMySQLQuery($query, true);
      $cowName = $this->getFormatedCowName($cows[0]['name'], $cows[0]['ear_tag_number']);
      $wateringTypes = $this->getWaterTypes();
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_WATER), -1, "Water");
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($wateringTypes)){//user selected a valid time index
         $adminType = $this->translator->getText($wateringTypes[$messageDiff - 1], "en");
         $yDayTime = strtotime("Yesterday");
         $yDayDate = new DateTime();
         $yDayDate->setTimestamp($yDayTime);
         $date = $yDayDate->format('y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         $query = "INSERT INTO water(cow_id, date, date_added, admin_type, app_used, no_used, ussd_session_id) VALUES($cachedData, '$date', '$datetime', '$adminType', 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
         $this->database->runMySQLQuery($query, false);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$RECORDING_WATER), -1, "Water");
      }
      else{//user entered an invalid number
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_2, $cachedData);
         return $this->getWaterTypesPage($cowName, $this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }
   
   /*public function waterPage3Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//amount in Kg
      $cachedData = $this->getDataFromLastPage();//combination of cow id and time index split by PagesT::$DELIMITER
      $parts = explode(PagesT::$DELIMITER, $cachedData);
      $cowId = $parts[0];
      $wateringTimes = $this->getWaterTimes($cowId);
      $selectedT = $wateringTimes[$parts[1] - 1];
      $query = "SELECT name, ear_tag_number FROM cow WHERE id = $cowId";
      $cows = $this->database->runMySQLQuery($query, true);
      $cowName = $this->getFormatedCowName($cows[0]['name'], $cows[0]['ear_tag_number']);
      if($messageDiff == 0){//user wants to go back
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_2, $cowId);
         return $this->getWaterTimesPage($cowId, $cowName);
      }
      else if(is_numeric($messageDiff)){//user entered a correct amount
         if($selectedT == Translator::$TODAY){
            $date = $this->getTime('Y-m-d');
         }
         else if($selectedT == Translator::$YESTERDAY){
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $date = $yDayDate->format('y-m-d');
         }
         $datetime = $this->getTime('Y-m-d H:i:s');
         $query = "INSERT INTO water(cow_id, date, date_added, quantity, app_used, no_used, ussd_session_id) VALUES($cowId, '$date', '$datetime', $messageDiff, 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
         $this->database->runMySQLQuery($query, false);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED)."\n".$this->translator->getText(Translator::$RECORDING_WATER), -1, "Water");
      }
      else{//user entered an invalid number
         $this->logHandler->log(3, $this->TAG, "Unable to get option with index = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_WATER_3, $cachedData);
         return $this->getWaterQuantityPage($cowName, $this->translator->getText($selectedT), $this->translator->getText(Translator::$WRONG_OPTION_SELECTED));
      }
   }*/
   
   public function milkConsumedPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//milk consumed litres
      $query = "select id from farmer where mobile_no like '%{$this->farmersPhoneNumber}'";
      $farmers = $this->database->runMySQLQuery($query, true);
      if(count($farmers) == 1) {
         if(is_numeric($messageDiff)) {
            $farmerId = $farmers[0]['id'];
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $date = $yDayDate->format('y-m-d');
            $datetime = $this->getTime('Y-m-d H:i:s');
            $query = "insert into milk_usage (farmer_id, usage_type, date_added, date, quantity, app_used, no_used, ussd_session_id)"
                    . " values($farmerId, 'Consumed', '$datetime', '$date', $messageDiff, 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
            $this->database->runMySQLQuery($query, false);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
            return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_CONSUMED_1, 1);
            return $this->getMilkConsumedPage($this->translator->getText(Translator::$ENTER_NUMBER));
         }
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Unable to get unique farmer with mobile number = ".$this->farmersPhoneNumber);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$ERROR_OCCURRED));
      }
   }
   
   public function milkReservedPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//milk consumed litres
      $query = "select id from farmer where mobile_no like '%{$this->farmersPhoneNumber}'";
      $farmers = $this->database->runMySQLQuery($query, true);
      if(count($farmers) == 1) {
         if(is_numeric($messageDiff)) {
            $farmerId = $farmers[0]['id'];
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $date = $yDayDate->format('y-m-d');
            $datetime = $this->getTime('Y-m-d H:i:s');
            $query = "insert into milk_usage (farmer_id, usage_type, date_added, date, quantity, app_used, no_used, ussd_session_id)"
                    . " values($farmerId, 'Reserved', '$datetime', '$date', $messageDiff, 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
            $this->database->runMySQLQuery($query, false);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
            return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_RESERVED_1, 1);
            return $this->getMilkReservedPage($this->translator->getText(Translator::$ENTER_NUMBER));
         }
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Unable to get unique farmer with mobile number = ".$this->farmersPhoneNumber);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$ERROR_OCCURRED));
      }
   }
   
   /*public function milkSoldPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//time index
      $milkSaleTimes = $this->getMilkSellingTimes();
      if($messageDiff == 0) {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($milkSaleTimes)) {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_2, $messageDiff);
         return $this->getMilkCategoryPage($milkSaleTimes[$messageDiff - 1]);
      }
      else {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_1, 1);
         return $this->getMilkSellingTimesPage($this->translator->getText(Translator::$UNKNOWN_OPTION_SELECTED));
      }
   }*/
   
   public function milkSoldPage2Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//buyer category index
      //$cachedData = $this->getDataFromLastPage();//time index
      $categories = $this->getMilkSaleCategories();
      if($messageDiff == 0) {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage();
      }
      else if(is_numeric($messageDiff) && $messageDiff <= count($categories)) {
         //$times = $this->getMilkSellingTimes();
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_3, $messageDiff);
         return $this->getMilkLitersSoldPage($categories[$messageDiff - 1]);
      }
      else {
         //$milkSaleTimes = $this->getMilkSellingTimes();
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_2, 1);
         return $this->getMilkCategoryPage($this->translator->getText(Translator::$UNKNOWN_OPTION_SELECTED));
      }
   }
   
   public function milkSoldPage3Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//amount of milk sold in litres
      $cachedData = $this->getDataFromLastPage();//category index
      if(is_numeric($messageDiff)) {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_5, $cachedData.PagesT::$DELIMITER.$messageDiff);
         $categories = $this->getMilkSaleCategories();
         return $this->getMilkLitersRejectedPage($categories[$cachedData - 1]);
      }
      else {
         $categories = $this->getMilkSaleCategories();
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_3, $cachedData);
         return $this->getMilkLitersSoldPage($categories[$cachedData - 1], $this->translator->getText(Translator::$ENTER_NUMBER));
      }
   }
   
   /*public function milkSoldPage4Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//amount of milk rejected in litres
      $cachedData = $this->getDataFromLastPage();//time index, category index and amount of milk sold seperated by DELIMITER
      if(is_numeric($messageDiff)) {
         $categories = $this->getMilkSaleCategories();
         $times = $this->getMilkSellingTimes();
         $parts = explode(PagesT::$DELIMITER, $cachedData);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_5, $cachedData.PagesT::$DELIMITER.$messageDiff);
         return $this->getMilkPricePerLiterPage($categories[$parts[1] - 1], $times[$parts[0] - 1]);
      }
      else {
         $categories = $this->getMilkSaleCategories();
         $times = $this->getMilkSellingTimes();
         $parts = explode(PagesT::$DELIMITER, $cachedData);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_4, $cachedData);
         return $this->getMilkLitersRejectedPage($categories[$parts[1] - 1], $times[$parts[0] - 1], $this->translator->getText(Translator::$ENTER_NUMBER));
      }
   }*/
   
   public function milkSoldPage5Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//price per liter
      $cachedData = $this->getDataFromLastPage();//category index and amount of milk sold seperated by DELIMITER
      if(is_numeric($messageDiff)) {
         $categories = $this->getMilkSaleCategories();
         $parts = explode(PagesT::$DELIMITER, $cachedData);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_6, $cachedData.PagesT::$DELIMITER.$messageDiff);
         return $this->getMilkTransportCostPage($categories[$parts[0] - 1]);
      }
      else {
         $categories = $this->getMilkSaleCategories();
         $times = $this->getMilkSellingTimes();
         $parts = explode(PagesT::$DELIMITER, $cachedData);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_5, $cachedData);
         return $this->getMilkPricePerLiterPage($categories[$parts[0] - 1], $this->translator->getText(Translator::$ENTER_NUMBER));
      }
   }
   
   public function milkSoldPage6Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);//transport cost
      //$cachedData = $this->getDataFromLastPage();//time index, category index, amount of milk sold, milk rejected and price per liter seperated by DELIMITER
      $cachedData = $this->getDataFromLastPage();//category index, amount of milk sold and price per liter seperated by DELIMITER
      $query = "select id from farmer where mobile_no like '%{$this->farmersPhoneNumber}'";
      $farmers = $this->database->runMySQLQuery($query, true);
      $parts = explode(PagesT::$DELIMITER, $cachedData);
      if(count($farmers) == 1 && count($parts) == 5) {
         if(is_numeric($messageDiff)) {
            $categoryIndex = $parts[0] - 1;
            $amountSold = $parts[1];
            $pricePerLiter = $parts[2];
            $yDayTime = strtotime("Yesterday");
            $yDayDate = new DateTime();
            $yDayDate->setTimestamp($yDayTime);
            $date = $yDayDate->format('y-m-d');
            $categories = $this->getMilkSaleCategories();
            $category = $this->translator->getText($categories[$categoryIndex], "en");
            $farmerId = $farmers[0]['id'];
            $datetime = $this->getTime('Y-m-d H:i:s');
            $query = "insert into milk_usage (farmer_id, usage_type, date_added, date, quantity, tranport_cost, price, buyer, app_used, no_used, ussd_session_id)"
                    . " values($farmerId, 'Sale', '$datetime', '$date', $amountSold, $messageDiff, $pricePerLiter, '$category', 'USSD', '{$this->farmersPhoneNumber}', '{$this->sessionID}')";
            $this->database->runMySQLQuery($query, false);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_2, 1);
            return $this->getMilkCategoryPage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else {
            $categories = $this->getMilkSaleCategories();
            $parts = explode(PagesT::$DELIMITER, $cachedData);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_MILK_SOLD_6, $cachedData);
            return $this->getMilkTransportCostPage($categories[$parts[1] - 1], $this->translator->getText(Translator::$ENTER_NUMBER));
         }
      }
      else {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$ERROR_OCCURRED));
      }
   }

   public function infoPortalPage1Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      
      if($messageDiff == 1){//user wants the weather forecast
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_INFO_PORTAL_1);
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
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_RECORDS_1);
         return $this->getRecordsPage();
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get the selected option in the info portal = ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_INFO_PORTAL_1);
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
         $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME);
         return $this->getHomePage();
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
            $serviceType = Translator::$BULL_SERVICING;
            if($messageDiff == 2) $serviceType = Translator::$ARTIFICIAL_INSEMINATION;
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_SERVICING_3, $cowIndex.PagesT::$DELIMITER.$messageDiff);
            return $this->getServicingCostPage($this->getFormatedCowName($results[$cowIndex-1]['name'], $results[$cowIndex-1]['ear_tag_number']), $this->translator->getText($serviceType));
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
   
   public function servicingPage3Last($serviceCode, $prevMessage, $message){
      $cachedData = $this->getDataFromLastPage();//should contain the index of the cow and the index for the service type
      $parts = explode(PagesT::$DELIMITER, $cachedData);
      if(count($parts) == 2) {
         $messageDiff = $message;
         if(strlen($prevMessage) > 0)
            $messageDiff = preg_replace ("/^".preg_quote ($prevMessage)."/", "", $message);

         $this->logHandler->log(3, $this->TAG, "Message diff before ".$messageDiff);
         $messageDiff = preg_replace("/^\*/", "", $messageDiff);
         $messageDiff = $this->sanitiseMessage($messageDiff);
         $this->logHandler->log(3, $this->TAG, "Message diff after ".$messageDiff);
         $cowIndex = $parts[0];
         $serviceType = $parts[1];
         $query = "SELECT cow.id, cow.name, cow.ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND cow.sex = 'Female' ORDER BY cow.id";
         $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
         $results = $this->getValidCows($fetchedCows, "Servicing");
         if(is_numeric($messageDiff)){//correct price stated
            $cowIndex = $cowIndex-1;//convert to 0 index
            $cowId = $results[$cowIndex]['id'];
            if($serviceType == 1) $query = "INSERT INTO unregistered_servicing(cow_id, cost) VALUES($cowId, $messageDiff)";
            else if($serviceType == 2) $query = "INSERT INTO unregistered_ai(cow_id, cost) VALUES($cowId, $messageDiff)";
            $this->database->runMySQLQuery($query, FALSE);
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME, 1);
            return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
         }
         else{
            $this->logHandler->log(3, $this->TAG, "Option selected by user does not exist");
            $cowIndex = $cowIndex - 1 ;
            $cowName = $this->getFormatedCowName($results[$cowIndex]['name'], $results[$cowIndex]['ear_tag_number']);
            $serviceTypeText = Translator::$BULL_SERVICING;
            if($serviceType == 2) $serviceTypeText = Translator::$ARTIFICIAL_INSEMINATION;
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_SERVICING_3, $cowIndex);
            return $this->getServicingCostPage($cowName, $this->translator->getText($serviceTypeText), $this->translator->getText(Translator::$ENTER_NUMBER));
         }
      }
      else {
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$ERROR_OCCURRED));
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
            return $this->getCowPage($this->translator->getText(Translator::$INFO_RECORDED)."\n".$this->translator->getText(Translator::$RECORDING_SICKNESS));
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
         $this->updateSession($phoneNumber, $serviceCode, $message, PagesT::$PAGE_REP_1);
         return $this->getHomePage();
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
           $calvingTypes = $this->getCalvingTypes();
            if(is_numeric($messageDiff) && $messageDiff <= count($calvingTypes)){
               if($calvingTypes[$messageDiff - 1] == Translator::$NORMAL) $input = "Normal";
               else if($calvingTypes[$messageDiff - 1] == Translator::$PROBLEMATIC_FULL_TERM) $input = "Problematic Full-term";
               else if($calvingTypes[$messageDiff - 1] == Translator::$STILL) $input = "Still";
               else if($calvingTypes[$messageDiff - 1] == Translator::$PREMATURE) $input = "Premature";
               else if($calvingTypes[$messageDiff - 1] == Translator::$ABORTION) $input = "Abortion";
               
               $date = $this->getTime('Y-m-d');
               $datetime = $this->getTime('Y-m-d H:i:s');
               if($input == "Premature" || $input == "Normal" || $input == "Problematic Full-term"){
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
               $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME, 1);
               return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
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
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME);
         return $this->getHomePage();
      }
      else if(is_array($result) && is_numeric($messageDiff) && $messageDiff <= count($result)){
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_PREG_CONF_2, $messageDiff);
         return $this->getPregConfPage($this->getFormatedCowName($result[$messageDiff - 1]['name'], $result[$messageDiff - 1]['ear_tag_number']));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_PREG_CONF_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$UNABLE_GET_SELECTED_COW)."\n".$this->translator->getText(Translator::$INSEMINATION_RESULTS_INSTRUCTIONS), "Female", "Pregnancy Confirmation");
      }
   }
   
   public function pregConfPage2Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      $cachedData = $this->getDataFromLastPage();
      $query = "SELECT b.id, b.name, b.ear_tag_number FROM farmer AS a INNER JOIN cow AS b ON a.id = b.farmer_id WHERE a.mobile_no LIKE '%{$this->farmersPhoneNumber}' AND b.sex = 'Female' ORDER BY b.id";
      $fetchedCows = $this->database->runMySQLQuery($query, TRUE);
      $result = $this->getValidCows($fetchedCows, "Pregnancy Confirmation");
      if($messageDiff == 0){//user wants to go back to reproduction page
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_PREG_CONF_1, 1);
         return $this->getCowPage($this->translator->getText(Translator::$INSEMINATION_RESULTS_INSTRUCTIONS), "Female", "Pregnancy Confirmation");
      }
      else if(is_array($result) && is_numeric($messageDiff) && $messageDiff <= 2){
         $cowIndex = $cachedData - 1;
         $cowId = $result[$cowIndex]['id'];
         if($messageDiff == 1) {//pregnancy confirmed
            $query = "SELECT id FROM event WHERE name = 'Pregnancy Confirmation'";
            $events = $this->database->runMySQLQuery($query, true);
            $eventId = $events[0]['id'];
            $date = $this->getTime('Y-m-d');
            $datetime = $this->getTime('Y-m-d H:i:s');
            $query = "INSERT INTO cow_event(cow_id, event_id, event_date, date_added, app_used, no_used) VALUES({$cowId}, {$eventId}, '{$date}', '{$datetime}', 'USSD', '{$this->farmersPhoneNumber}')";
            $this->database->runMySQLQuery($query, false);
            $query = "UPDATE cow SET in_calf = 1 WHERE id = {$cowId}";
            $this->database->runMySQLQuery($query, false);
         }
         else if($messageDiff == 2) {//animal not pregnant
            $query = "UPDATE cow SET in_calf = 0 WHERE id = {$cowId}";
            $this->database->runMySQLQuery($query, false);
         }
         $this->logHandler->log(3, $this->TAG, "Pregnancy confirmation recorded for cow with id ".$cowId);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->logHandler->log(3, $this->TAG, "Unable to get cow with index ".$messageDiff);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_PREG_CONF_1, 1);
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
         $cowName = $this->getFormatedCowName($result[$cowIndex - 1]['name'], $result[$cowIndex - 1]['ear_tag_number']);
         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         
         if($messageDiff == 1) {
            $type = "Sale";
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DISPOSAL_3, $cowId.PagesT::$DELIMITER.$type);
            return $this->getCowPricePage($cowName);
         }
         else if($messageDiff == 2) {
            $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DEATH_2, $cowId);
            return $this->getCauseOfDeathPage($cowName);
         }
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
         
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_DISPOSAL_2, $cowIndex);
         return $this->getDisposalScreen($result[$cowIndex-1]['name'], $result[$cowIndex-1]['ear_tag_number'], $this->translator->getText(Translator::$UNKNOWN_OPTION_SELECTED));
      }
   }
   
   public function disposalPage3Last($serviceCode, $prevMessage, $message){
      $messageDiff = $this->getMessageDiff($message, $prevMessage);
      $cachedData = $this->getDataFromLastPage();//cow id and type seperated using delimiter
      $parts = explode(PagesT::$DELIMITER, $cachedData);
      $cowId = $parts[0];
      $type = $parts[1];
      if(is_numeric($messageDiff)){
         $date = $this->getTime('Y-m-d');
         $datetime = $this->getTime('Y-m-d H:i:s');
         
         $query = "SELECT id FROM event WHERE name = '{$type}'";
         $value = $this->database->runMySQLQuery($query, true);
         $eventId = $value[0]['id'];
         
         $query = "INSERT INTO cow_event(cow_id, event_id, cost, event_date, date_added, app_used, no_used) VALUES ({$cowId}, {$eventId}, $messageDiff, '$date', '$datetime', 'USSD', '{$this->farmersPhoneNumber}')";
         $this->database->runMySQLQuery($query, FALSE);
         
         $query = "UPDATE cow SET old_farmer_id = farmer_id, farmer_id = null WHERE id = {$cowId}";
         $this->database->runMySQLQuery($query, false);
         //$query = "UPDATE cow SET farmer_id = 0 WHERE id = {$cowId}";
         //$this->database->runMySQLQuery($query, true);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, Pages::$PAGE_HOME, 1);
         return $this->getHomePage($this->translator->getText(Translator::$INFO_RECORDED));
      }
      else{
         $query = "select name, ear_tag_number from cow where id = $cowId";
         $result = $this->database->runMySQLQuery($query, true);
         $name = $this->getFormatedCowName($result[0]['name'], $result[0]['ear_tag_number']);
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_DISPOSAL_3, $cachedData);
            return $this->getCowPricePage($name);
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
         $this->updateSession($this->farmersPhoneNumber, $serviceCode, $message, PagesT::$PAGE_P_MEASURES_1, 1);
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
      $message = $this->translator->getText(Translator::$MILK_DATA_INCONSISTENT) . "\n";
      $message .= " 1 - " . $this->translator->getText(Translator::$CANCEL) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$OKAY) . "\n";
      
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
               $message .= "\n";
            
            for($cowIndex = 0; $cowIndex < count($result); $cowIndex++){
               $readableI = $cowIndex /*+ $limitOffset*/ + 1;
               $message .= $readableI . " - " . $this->getFormatedCowName($result[$cowIndex]['name'], $result[$cowIndex]['ear_tag_number']) . "\n";
            }
            $this->logHandler->log(3, $this->TAG, "Gotten cows for ".$phoneNumber);
            
            $message .= Pages::$PREV_OF_PAGE_CODE. " - ". $this->translator->getText(Translator::$TO_GO_BACK);
                    
            return $message;
         }
         else{
            if(count($fetchedCows)>0){
               $message = $this->translator->getText(Translator::$NO_COWS_AVAILABLE) . "\n";
               $message .= Pages::$PREV_OF_PAGE_CODE. " - ". $this->translator->getText(Translator::$TO_GO_BACK);
            }
            else{
               $message = $this->translator->getText(Translator::$NO_COWS) . "\n";
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
   private function getMilkingTimesPage($cowId, $formattedName, $message = ""){
      if(strlen($message) > 0)
         $message .= "\n";
      //what time was %s milked
      $message .= sprintf($this->translator->getText(Translator::$MILKING_TIME_INSTRUCTIONS), strtoupper($formattedName)). "\n";
      $milkingTimes = $this->getMilkingTimes($cowId);
      
      for($index = 0; $index < count($milkingTimes); $index++){
         $message .= " " . ($index + 1) . " - " .$this->translator->getText($milkingTimes[$index]). "\n";
      }
      
      if(count($milkingTimes) == 0){
         $message .= $this->translator->getText(Translator::$MILK_DATA_FOR_COW_ENTERED) . "\n";
      }
      
      $message .= " 0 - " .$this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
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
         //How much milk did %s produce %s
         $message = sprintf($this->translator->getText(Translator::$MILK_PROD_INSTR), strtoupper($cowIdentity), strtoupper($this->translator->getText($milkingTime)));
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
      if(strlen($message) > 0)
         $message .= "\n";
      $message .= sprintf($this->translator->getText(Translator::$SERVICING_INSTRUCTIONS), strtoupper($cowName)) . "\n";
      $message .= " 1 - " . $this->translator->getText(Translator::$BULL_SERVICING) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$ARTIFICIAL_INSEMINATION) . "\n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   private function getServicingCostPage($cowName, $serviceType, $message = "") {
      if(strlen($message) > 0)
         $message .= "\n";
      $message .= sprintf($this->translator->getText(Translator::$SERVICING_COST_INSTRUCTIONS), strtoupper($serviceType), strtoupper($cowName))."\n";
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
         $message .= sprintf($this->translator->getText(Translator::$CALVING_INSTR), strtoupper($cowIdentity)) . "\n";
         $types = $this->getCalvingTypes();
         $index = 1;
         foreach($types as $currType) {
            $message .= " $index - " . $this->translator->getText($currType)."\n";
            $index++;
         }
         $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
         return $message;
      }
   }
   
   private function getCalvingTypes() {
      $types = array();
      $types[] = Translator::$NORMAL;
      $types[] = Translator::$PROBLEMATIC_FULL_TERM;
      $types[] = Translator::$STILL;
      $types[] = Translator::$PREMATURE;
      $types[] = Translator::$ABORTION;
      return $types;
   }
   
   public function getReproductionPage($appenedMessage = ""){
      if(strlen($appenedMessage) > 0){
         $appenedMessage = $appenedMessage . "\n";
      }
      
      $appenedMessage .= " 1 - " . $this->translator->getText(Translator::$RECORD_SOH) . "\n";
      $appenedMessage .= " 2 - " . $this->translator->getText(Translator::$RECORD_SERVICING) . "\n";
      $appenedMessage .= " 3 - " . $this->translator->getText(Translator::$RECORD_CALVING) . "\n";
      $appenedMessage .= " 4 - " . $this->translator->getText(Translator::$RECORD_PREG_CONF) . "\n";
      $appenedMessage .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $appenedMessage;
   }
   
   public function getRecordsPage($message = ""){
      if(strlen($message) > 0) $message .= "\n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$YDAY_MILK_RECORDS) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$WEEK_MILK_RECORDS) . "\n";
      $message .= " 3 - " . $this->translator->getText(Translator::$MONTH_MILK_RECORDS) . "\n";
      $message .= " 4 - " . $this->translator->getText(Translator::$MONTH_EVENT_RECORDS) . "\n";
      $message .= " 5 - " . $this->translator->getText(Translator::$WEATHER_FORECAST) . "\n";
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   public function getEventsPage($message = ""){
      if(strlen($message) > 0) $message .= "\n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$START_OF_LACT) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DRY_OFF) . "\n";
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
      return $message;
   }
   
   public function getPregConfPage($cowName, $message = ""){
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$INSEMINATION_RESULTS_TYPE_INSTRUCTIONS), strtoupper($cowName))."\n";
      $message .= " 1 - ".$this->translator->getText(Translator::$YES)."\n";
      $message .= " 2 - ".$this->translator->getText(Translator::$NO)."\n";
      $message .= " 0 - ".$this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   public function getPMeasuresPage($message = ""){
      if(strlen($message) > 0) $message = $message . "\n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$DIPPING_SPRAYING) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DEWORMING) . "\n";
      $message .= " 3 - " . $this->translator->getText(Translator::$VACCINATION) . "\n";
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
      return $message;
   }
   
   /**
    * This function returns a list of valid feeding times for the specified cow.
    * Contents of the list will depend on the current time (i.e afternoon and evening 
    * options will not be displayed if it is still morning) and already entered data
    * (i.e if user has already entered quantity of milk for specified cow in the morning
    * then the morning option will not be displayed)
    * 
    * @param type $cowId
    * @return type
    */
   private function getFeedingTimes($cowId){
      //get possible times today eg if its 10am show return morning only
      //1. load the default feeding times
      $dMilkingTimes = parse_ini_file($this->ROOT."config/feeding_times.ini");
      $dMorning = $this->getTime("Y-m-d") . " " . $dMilkingTimes['morning'];
      $dAfternoon = $this->getTime("Y-m-d") . " " . $dMilkingTimes['afternoon'];
      $dEvening = $this->getTime("Y-m-d") . " " . $dMilkingTimes['evening'];
      
      $today = $this->getTime('Y-m-d');

      /*if($cowId == ''){
         $this->logHandler->log(3, $this->TAG, "Interested cow id '". $cowId . "'");
         //$this->logHandler->log(3, $this->TAG, print_r(debug_backtrace(true, 2), true));
      }

      $query = "SELECT time FROM milk_production WHERE cow_id = ".$cowId." AND date = '{$today}'";
      $result = $this->database->runMySQLQuery($query, true);
      
      $this->logHandler->log(3, $this->TAG, "Morning time = ".$dMorning." and now = ".  time());*/
      
      $times = array();
      if(time()>=strtotime($dMorning)) array_push ($times, Translator::$MORNING);
      if(time()>=strtotime($dAfternoon)) array_push ($times, Translator::$AFTERNOON);
      if(time()>=strtotime($dEvening)) array_push ($times, Translator::$EVENING);
      /*if(time()>=strtotime($dEvening)) array_push ($times, Translator::$COMBINED); */
      
      /*if(is_array($times)){
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
      }*/
      
      /*$now = new DateTime('now');
      //$yesterday = $now->format('Y').'-'.$now->format('m').'-'.((int)$now->format('d') - 1);
      //$yDayTime = time() - 86400;
      $yDayTime = strtotime("Yesterday");
      $yDayDate = new DateTime();
      $yDayDate->setTimestamp($yDayTime);
      $yesterday = $yDayDate->format('Y-m-d');
      $this->logHandler->log(3, $this->TAG, "Yesterday's date is ".$yesterday);
      
      $query = "SELECT time FROM milk_production WHERE cow_id = ".$cowId." AND date = '{$yesterday}'";
      $result = $this->database->runMySQLQuery($query, TRUE);*/
      
      $yTimes = array(Translator::$Y_MORNING, Translator::$Y_AFTERNOON, Translator::$Y_EVENING);//, Translator::$Y_COMBINED);
      /*if(is_array($result)){
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
      }*/
      
      return array_merge($times, $yTimes);
   }
   
   public function getFeedingPage($message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$FEEDING_INSTRUCTIONS))."\n";
      $feeds = $this->getFeeds();
      $index = 1;
      foreach($feeds as $currFeed) {
         $message .= " $index - ".$this->translator->getText($currFeed)."\n";
         $index++;
      }
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
      return $message;
   }
   
   private function getFeeds() {
      //get all feeds recorded to be fed to animals for yesterday
      $query = "select distinct(food) as feed from feeding where cow_id in (select cow.id from cow inner join farmer on cow.farmer_id = farmer.id where farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}') and date = date(NOW() - interval 1 day)";
      $result = $this->database->runMySQLQuery($query, true);
      $recorded = array();
      foreach($result as $currFeedType) {
         $recorded[] = $currFeedType['feed'];
      }
      $feeds = array();
      if(array_search($this->translator->getText(Translator::$NAPIER, "en"), $recorded) === false){
         $feeds[] = Translator::$NAPIER;
      }
      if(array_search($this->translator->getText(Translator::$NATURAL_GRASS, "en"), $recorded) === false){
         $feeds[] = Translator::$NATURAL_GRASS;
      }
      if(array_search($this->translator->getText(Translator::$HAY, "en"), $recorded) === false){
         $feeds[] = Translator::$HAY;
      }
      if(array_search($this->translator->getText(Translator::$LEGUMES, "en"), $recorded) === false){
         $feeds[] = Translator::$LEGUMES;
      }
      if(array_search($this->translator->getText(Translator::$MINERAL_LICKS, "en"), $recorded) === false){
         $feeds[] = Translator::$MINERAL_LICKS;
      }
      if(array_search($this->translator->getText(Translator::$SILAGE, "en"), $recorded) === false){
         $feeds[] = Translator::$SILAGE;
      }
      $query = "SELECT cow.id as id, cow.name as name,cow.ear_tag_number as ear_tag_number FROM farmer INNER JOIN cow ON farmer.id = cow.farmer_id WHERE farmer.mobile_no LIKE '%{$this->farmersPhoneNumber}' ORDER BY cow.id";
      $allCows = $this->database->runMySQLQuery($query, true);
      $cows = $this->getValidCows($allCows, "Feeding-Concentrates");
      if(count($cows) > 0) {
         $feeds[] = Translator::$CONCENTRATES;
      }
      return $feeds;
   }
   
   public function getFeedingTimesPage($cowId, $cowName, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$FEEDING_TIME_INSTRUCTIONS), strtoupper($cowName))."\n";
      //What time was % fed?
      $feedingTimes = $this->getFeedingTimes($cowId);
      $index = 1;
      foreach ($feedingTimes as $currFeedingTime) {
         $message .= " $index - ".$this->translator->getText($currFeedingTime). "\n";
         $index++;
      }
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   public function getFeedingQuantityPage($cowName, $type, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$FEEDING_QUANTITY_INSTRUCTIONS), strtoupper($type), strtoupper($cowName));
      return $message;
   }
   
   public function getWaterTimesPage($cowId, $cowName, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$WATER_TIME_INSTRUCTIONS), strtoupper($cowName)) . "\n";
      $waterTimes = $this->getWaterTimes($cowId);
      $index = 1;
      foreach ($waterTimes as $currWaterTime) {
         $message .= " $index - ".$this->translator->getText($currWaterTime). "\n";
         $index++;
      }
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   public function getWaterTypesPage($cowName, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$WATER_TYPE_INSTRUCTIONS), strtoupper($cowName)) . "\n";
      $waterTypes = $this->getWaterTypes();
      $index = 1;
      foreach ($waterTypes as $currWaterType) {
         $message .= " $index - ".$this->translator->getText($currWaterType). "\n";
         $index++;
      }
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   public function getWaterTypes() {
      $waterTypes = array();
      $waterTypes[] = Translator::$THROUGHOUT_DAY;
      $waterTypes[] = Translator::$TWICE_OR_MORE;
      $waterTypes[] = Translator::$ONCE;
      $waterTypes[] = Translator::$NOT_PROVIDED;
      return $waterTypes;
   }
   
   private function getWaterTimes($cowId){
      return array(Translator::$YESTERDAY, Translator::$TODAY);
   }
   
   private function getMilkSellingTimesPage($message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= $this->translator->getText(Translator::$RECORD_MILK_SALE)."\n";
      $milkSellingTimes = $this->getMilkSellingTimes();
      $index = 1;
      foreach ($milkSellingTimes as $currMilkSellingTime) {
         $message .= " $index - ".$this->translator->getText($currMilkSellingTime). "\n";
         $index++;
      }
      
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   private function getMilkLitersSoldPage($customer, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$RECORD_MILK_SOLD), strtoupper($this->translator->getText($customer)), strtoupper($this->translator->getText(Translator::$YESTERDAY)));
      return $message;
   }
   
   private function getMilkLitersRejectedPage($customer, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$RECORD_MILK_REJECTED), strtoupper($this->translator->getText($customer)), strtoupper($this->translator->getText(Translator::$YESTERDAY)));
      return $message;
   }
   
   private function getMilkPricePerLiterPage($customer, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$RECORD_SALE_PRICE_PER_LITER), strtoupper($this->translator->getText($customer)), strtoupper($this->translator->getText(Translator::$YESTERDAY)));
      return $message;
   }
   
   private function getMilkTransportCostPage($customer, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$RECORD_TRANSPORT_COST), strtoupper($this->translator->getText($customer)));
      return $message;
   }
   
   private function getMilkCategoryPage($message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$RECORD_MILK_SALE_CATEGORY), $this->translator->getText(Translator::$YESTERDAY))."\n";
      $categories = $this->getMilkSaleCategories();
      $index = 1;
      foreach($categories as $currCategory) {
         $message .= " $index - " . $this->translator->getText($currCategory) . "\n";
         $index++;
      }
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
      return $message;
   }
   
   private function getMilkSaleCategories() {
      $categories = array();
      $categories[] = Translator::$INDIVIDUAL_CUSTOMER;
      $categories[] = Translator::$PRIVATE_MILK_TRADER;
      $categories[] = Translator::$PROCESSOR;
      $categories[] = Translator::$EADD_HUB;
      return $categories;
   }
   
   private function getMilkSellingTimes(){
      //get possible times today eg if its 10am show return morning only
      //1. load the default feeding times
      $dMilkingTimes = parse_ini_file($this->ROOT."config/feeding_times.ini");
      $dMorning = $this->getTime("Y-m-d") . " " . $dMilkingTimes['morning'];
      $dAfternoon = $this->getTime("Y-m-d") . " " . $dMilkingTimes['afternoon'];
      $dEvening = $this->getTime("Y-m-d") . " " . $dMilkingTimes['evening'];
      $today = $this->getTime('Y-m-d');
      $times = array();
      if(time()>=strtotime($dMorning)) array_push ($times, Translator::$MORNING);
      if(time()>=strtotime($dAfternoon)) array_push ($times, Translator::$AFTERNOON);
      if(time()>=strtotime($dEvening)) array_push ($times, Translator::$EVENING);
      $yTimes = array(Translator::$Y_MORNING, Translator::$Y_AFTERNOON, Translator::$Y_EVENING);//, Translator::$Y_COMBINED);
      return array_merge($times, $yTimes);
   }
   
   public function getWaterQuantityPage($cowName, $time, $message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= sprintf($this->translator->getText(Translator::$WATER_QUANTITY_INSTRUCTIONS), strtoupper($cowName), strtoupper($time))."\n";
      return $message;
   }
   
   public function getMilkConsumedPage($message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= $this->translator->getText(Translator::$RECORDING_MILK_CONSUMED)."\n";
      return $message;
   }
   
   public function getMilkReservedPage($message = "") {
      if(strlen($message) > 0) $message = $message . "\n";
      $message .= $this->translator->getText(Translator::$RECORDING_MILK_RESERVED)."\n";
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
            $this->logHandler->log(2, $this->TAG, "The cache is missing the last cached data from last page in request by phone number ".$this->farmersPhoneNumber);
            return -1;
         }
      }
      else{
         $this->logHandler->log(2, $this->TAG, "Unable to get cached data from last page in request by phone number ".$this->farmersPhoneNumber);
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
         $message = $this->translator->getText(Translator::$ACQ_INSTR) . "\n";
      else
         $message .= "\n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$PURCHASE) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DOWRY) . "\n";
      $message .= " 3 - " . $this->translator->getText(Translator::$GIFT) . "\n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
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
         $message .= "\n";
      
      $message .= $this->translator->getText(Translator::$ACQ_NO_INSTR);
      
      return $message;
   }
   
   /*private function getInfoPortalPage($message = ""){
      if(strlen($message) > 0)
         $message .= "\n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$WEATHER_FORECAST) . "\n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
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
         $message .= "\n";
      $message .= sprintf($this->translator->getText(Translator::$DISPOSAL_INSTR), strtoupper($cow)) . "\n";
      
      $message .= " 1 - " . $this->translator->getText(Translator::$SALE) . "\n";
      $message .= " 2 - " . $this->translator->getText(Translator::$DEATH) . "\n";
      $message .= " 3 - " . $this->translator->getText(Translator::$DOWRY) . "\n";
      $message .= " 4 - " . $this->translator->getText(Translator::$GIFT) . "\n";
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      
      return $message;
   }
   
   public function getCauseOfDeathPage($cowName, $message = "") {
      if(strlen($message) == 0 )
         $message .= "\n";
      $message .= sprintf($this->translator->getText(Translator::$CAUSE_OF_DEATH_INSTRUCTIONS), strtoupper($cowName)) . "\n";
      $cod = $this->getCausesOfDeath();
      $index = 1;
      foreach($cod as $currCOD) {
         $message .= " $index - " . $this->translator->getText($currCOD)."\n";
         $index++;
      }
      $message .= " 0 - " . $this->translator->getText(Translator::$TO_GO_BACK) . "\n";
      return $message;
   }
   
   public function getCowPricePage($cowName, $message = "") {
      if(strlen($message) == 0 )
         $message .= "\n";
      $message .= sprintf($this->translator->getText(Translator::$COW_COST_INSTRUCTIONS), strtoupper($cowName)) . "\n";
      return $message;
   }
   
   private function getCausesOfDeath() {
      $causes = array();
      $causes[] = Translator::$NATURAL;
      $causes[] = Translator::$SICKNESS;
      $causes[] = Translator::$INJURY;
      return $causes;
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
      /*if(time()>=strtotime($dAfternoon)) array_push ($times, Translator::$AFTERNOON);*/
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
      
      $yTimes = array(Translator::$Y_MORNING, /*Translator::$Y_AFTERNOON,*/ Translator::$Y_EVENING);//, Translator::$Y_COMBINED);
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
               //check if we have at least 14 days of milk records for the current cow
               $query = "select id from milk_production where cow_id = $cowId group by time having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_MILK_PRODUCTION." DAY";
               $milkProd = $this->database->runMySQLQuery($query, true);
               if(count($milkProd) < 2){//cow only valid if we have less than 2 time types (e.g morning and evening) of milk data points (type here being either morning or evening)
                  return TRUE;
               }
            }
         }
         return FALSE;
      }
      else if($event == "Water"){
         $query = "select id from water where cow_id = $cowId group by date having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_WATER." DAY";
         $milkProd = $this->database->runMySQLQuery($query, true);
         if(count($milkProd) < 1){//cow only valid if we have no water record in the last $INTVL_WATER days
            return TRUE;
         }
         return FALSE;
      }
      else if($event == "Feeding"){
         $query = "select id from feeding where cow_id = $cowId group by date having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_FEEDING." DAY";
         $milkProd = $this->database->runMySQLQuery($query, true);
         if(count($milkProd) < 1){//cow only valid if we have no water record in the last $INTVL_FEEDING days
            return TRUE;
         }
         return FALSE;
      }
      else if($event == "Feeding-Concentrates"){
         $query = "select id from feeding where cow_id = $cowId and food = '".$this->translator->getText(Translator::$CONCENTRATES,"en")."' group by date having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_FEEDING." DAY";
         $milkProd = $this->database->runMySQLQuery($query, true);
         if(count($milkProd) < 1){//cow only valid if we have no water record in the last $INTVL_FEEDING days
            return TRUE;
         }
         return FALSE;
      }
      else if($event == "Milk consumed"){
         $query = "select id from milk_usage where farmer_id = $cowId and usage_type = 'Consumed' group by date having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_MILK_CONSUMED." DAY";
         $milkProd = $this->database->runMySQLQuery($query, true);
         if(count($milkProd) < 1){//cow only valid if we have less no record in the last $INTVL_MILK_CONSUMED days
            return TRUE;
         }
         return FALSE;
      }
      else if($event == "Milk reserved"){
         $query = "select id from milk_usage where farmer_id = $cowId and usage_type = 'Reserved' group by date having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_MILK_RESERVED." DAY";
         $milkProd = $this->database->runMySQLQuery($query, true);
         if(count($milkProd) < 1){//cow only valid if we have less no record in the last $INTVL_MILK_RESERVED days
            return TRUE;
         }
         return FALSE;
      }
      else if($event == "Milk sold"){
         $query = "select id from milk_usage where farmer_id = $cowId and usage_type = 'Sale' group by date having max(date_added) > NOW() - INTERVAL ".PagesT::$INTVL_MILK_SALE." DAY";
         $milkProd = $this->database->runMySQLQuery($query, true);
         if(count($milkProd) < 2){//cow only valid if we have less than 2 time types (e.g morning and evening) of milk sale data points
            return TRUE;
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
               $query = "select in_calf from cow where id = $cowId";
               $inCalf = $this->database->runMySQLQuery($query, true);
               if($inCalf[0]['in_calf'] == 0){//animal has not yet been recorded as in-calf
                  $query = "select count(id) as scount from unregistered_servicing where cow_id = $cowId";
                  $bull = $this->database->runMySQLQuery($query, true);
                  if(count($bull) == 1) {
                     $bull = $bull[0]['scount'];
                  }
                  else {
                     $bull = 0;
                  }
                  $query = "select count(id) as scount from unregistered_ai where cow_id = $cowId";
                  $ai = $this->database->runMySQLQuery($query, true);
                  if(count($ai) == 1) {
                     $ai = $ai[0]['scount'];
                  }
                  else {
                     $ai = 0;
                  }
                  if($bull > 0 || $ai > 0){//cow only valid if we have less no water record in the last $INTVL_WATER days
                     return TRUE;
                  }
               }
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
