<?php
/**
 * This module will have the general functions that appertains to the system
 *
 * @category   NgombePlanner
 * @package    Main
 * @author     Kihara Absolomon <a.kihara@cgiar.org>
 * @since      v0.1
 */
class NgombePlanner extends DBase{

   /**
    * @var Object An object with the database functions and properties. Implemented here to avoid having a million & 1 database connections
    */
   public $Dbase;

   public $addinfo;

   public $footerLinks = '';

   /**
    * @var  string   Just a string to show who is logged in
    */
   public $whoisme = '';

   /**
    * @var  string   A place to store any errors that happens before we have a valid connection
    */
   public $errorPage = '';

   /**
    * @var  bool     A flag to indicate whether we have an error or not
    */
   public $error = false;

   public function  __construct() {
      $this->Dbase = new DBase('mysql');
      $this->Dbase->InitializeConnection();
      if(is_null($this->Dbase->dbcon)) {
         ob_start();
         $this->LoginPage(OPTIONS_MSSG_DB_CON_ERROR);
         $this->errorPage = ob_get_contents();
         ob_end_clean();
         return;
      }
      $this->Dbase->InitializeLogs();

      //if we are looking to download a file, log in first
      if(Config::$downloadFile){
         $res = $this->Dbase->ConfirmUser($_GET['u'], $_GET['t']);
         if($res != 0) die('Permission Denied. You do not have permission to access this module');
      }
   }

   /**
    * Controls the program execution
    */
   public function TrafficController(){
      if(OPTIONS_REQUESTED_MODULE == 'data' && OPTIONS_REQUESTED_SUB_MODULE == 'fetch') $this->fetchData();
      elseif(OPTIONS_REQUESTED_MODULE == 'farmersList' && OPTIONS_REQUESTED_SUB_MODULE == 'fetch'){
         // lets confirm that we have a valid user
         $confirmQ = "SELECT lower(location_district) `site` FROM `extension_personnel` as a inner join farmer as b on b.extension_personnel_id = a.id WHERE a.mobile_no=:mobile_no limit 1";
         $confirm = $this->Dbase->ExecuteQuery($confirmQ, array('mobile_no' => $_POST['psswd']));
         if($confirm == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

         if(count($confirm) == 0) die(json_encode(array('error' => true, 'data' => 'Invalid username or password!'), TRUE));
         else if($confirm[0]['site'] != $_POST['login']) die(json_encode(array('error' => true, 'data' => 'Invalid username!'), TRUE));

         $this->fetchFarmersData();
      }
      elseif(OPTIONS_REQUESTED_MODULE == 'farmerData' && OPTIONS_REQUESTED_SUB_MODULE == 'fetch') $this->getFarmerData();
      elseif(OPTIONS_REQUESTED_MODULE == 'farmerData' && OPTIONS_REQUESTED_SUB_MODULE == 'stats') $this->generateUsageStats();
      elseif(OPTIONS_REQUESTED_MODULE == 'graphs'){
         if(OPTIONS_REQUESTED_SUB_MODULE == 'users') $this->getUserStats();
         else if(OPTIONS_REQUESTED_SUB_MODULE == 'milk_prod') $this->getMilkProd();
         else if(OPTIONS_REQUESTED_SUB_MODULE == 'watering') $this->getWaterRegimes ();
         else if(OPTIONS_REQUESTED_SUB_MODULE == 'milk_usage') $this->getMilkUsageRegimes();
         else if(OPTIONS_REQUESTED_SUB_MODULE == 'feeding') $this->getFeedRegimes();
         else if(OPTIONS_REQUESTED_SUB_MODULE == 'milk_buyers') $this->getMilkSellingRegimes ();
      }
   }

   /**
    * Fetch different pieces of data for the analytics part
    */
   private function fetchData(){
      //get the farmer enrollment per day
      $query = "select count(*) as cnt, date_format(date_added, '%d/%m') as date from farmer where id != 0 group by date(date_added) order by date(date_added)";
      $farmers = $this->Dbase->ExecuteQuery($query);
      if($farmers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $count = 0;
      foreach($farmers as $key => $dt){
         $count += $dt['cnt'];
         $farmers[$key]['count'] = $count;
      }

      //get the farmer enrollment per site
      $query = "select location_district as site, count(*) as count from farmer where location_district is not null group by location_district";
      $site_farmers = $this->Dbase->ExecuteQuery($query);
      if($site_farmers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $query = "select date, count(distinct farmer_id) as count from farmer_milk where date > '2014-06-03' group by date order by date";
      $recording_farmers = $this->Dbase->ExecuteQuery($query);
      if($recording_farmers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $farmersCount = "select count(*) as count from farmer where date(date_added) <= :cur_date";
      foreach($recording_farmers as $key => $farmer){
         $fc = $this->Dbase->ExecuteQuery($farmersCount, array('cur_date' => $farmer['date']));
         if($fc == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
         $recording_farmers[$key]['total_farmers'] = $fc[0]['count'];
      }

      //milk recording per cow
      $query = "select date, count(distinct cow_id) as count from farmer_milk where date > '2014-06-03' group by date order by date";
      $recording_cows = $this->Dbase->ExecuteQuery($query);
      if($recording_cows == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $cowCount = "select count(*) as count from cow where date(date_added) <= :cur_date and sex='Female' and milking_status='adult_milking'";
      foreach($recording_cows as $key => $cow){
         $cc = $this->Dbase->ExecuteQuery($cowCount, array('cur_date' => $cow['date']));
         if($cc == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
         $recording_cows[$key]['total_cows'] = $cc[0]['count'];
      }

      $milkRecords = array();
      foreach($recording_cows as $key => $dt){
         $milkRecords[] = array('cows' => $dt['count']+0, 'date' => $dt['date'], 'farmers' => $recording_farmers[$key]['count']+0, 'totalCows' => $dt['total_cows']+0, 'totalFarmers' => $recording_farmers[$key]['total_farmers']+0);
      }

      //get the events stats
      $eventsQ = "select b.name as event_name, count(distinct cow_id) as event_count from cow_event as a inner join event as b on a.event_id=b.id group by b.id";
      $events = $this->Dbase->ExecuteQuery($eventsQ);
      if($events == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      //Milk recording patterns
      $rec_patternsQ = "select a.time, c.location_district, count(*) as count from farmer_milk as a inner join farmer as c on a.farmer_id=c.id where location_district is not null and time !='' group by time, location_district order by location_district, time";
      $recPatterns = $this->Dbase->ExecuteQuery($rec_patternsQ);
      if($recPatterns == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $rec_pat = array();
      $sites = array();
      foreach($recPatterns as $rec){
         if(!isset($sites[$rec['location_district']])) $sites[$rec['location_district']] = array();
         $sites[$rec['location_district']][$rec['time']] = $rec['count']+0;
      }
      foreach($sites as $st_name => $st) $rec_pat[] = array_merge(array('site' => $st_name), $st);

      $timeRecPatternsQ = "select hour(date_added) as hour, count(*) as count from farmer_milk group by hour(date_added) order by hour(date_added);";
      $timeRecPatterns = $this->Dbase->ExecuteQuery($timeRecPatternsQ);
      if($timeRecPatterns == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      //costs incurred per week based on the number of hops
/**      $dayCostsQ = "select d as date, (all_cnt-cnt)*1.8 as count from (select *, week(start_time) as date, count(*) as all_cnt from ussd_session group by week(start_time)) as a inner join (select week(start_time) as d, count(*) as cnt from ussd_session where last_code='*384*4564#' and last_page='home' group by week(start_time)) as b on week(start_time)=b.d";
      //$dayCostsQ = "select week(start_time) as date, count(*)*1.8 as count from ussd_session group by week(start_time)";
      $dayCosts = $this->Dbase->ExecuteQuery($dayCostsQ);
      if($dayCosts == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
*/

      //initiated sessions per week
//      $daySessionsQ = "select week(start_time) as date, count(distinct session_id) as count from ussd_session group by week(start_time)";
/**      $daySessionsQ = "select d as date, (all_cnt-cnt) as count from (select *, week(start_time) as date, count(*) as all_cnt from ussd_session group by week(start_time)) as a inner join (select week(start_time) as d, count(*) as cnt from ussd_session where last_code='*384*4564#' and last_page='home' group by week(start_time)) as b on week(start_time)=b.d";
      $daySessions = $this->Dbase->ExecuteQuery($daySessionsQ);
      if($daySessions == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
*/

      header("Content-type: application/json");
      $jsonString = '{"enrollmentPerDay":'. json_encode($farmers)
              .', "siteFarmers":'. json_encode($site_farmers)
              .', "milkRecords":'. json_encode($milkRecords)
              .', "timePatterns":'. json_encode($timeRecPatterns)
              .', "recordPatterns":'. json_encode($rec_pat)
   //           .', "dayCosts":'. json_encode($dayCosts)
   //           .', "daySessions":'. json_encode($daySessions)
              .', "events":'. json_encode($events) .'}';

      die($jsonString);
   }

   /**
    * Get all the site farmers grouped by site and ordered by rank
    */
   private function fetchFarmersData(){
      $allSitesQ = 'select 600+extension_personnel_id as id, location_district as text, "-1" as parentid  from farmer where extension_personnel_id not in(13,2) group by extension_personnel_id order by location_district';
      $farmerBysiteQ = 'select id as id, name as text, 600+extension_personnel_id as parentid from farmer where location_district is not null and location_district != "" and extension_personnel_id not in (13,2) order by name';
      $sitesAverageQ = "SELECT site, avg(`daily average`) as `average`, stddev(`daily average`) as `stddev` FROM `cow_daily_average` where site is not null group by site";

      /**
       * Crazy SQL query which ranks the farmers based on the average recordings and groups them in deciles based on the average recording per cow
       *
       * 1. We get the total number of milk records per farmer
       *  -- select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
       *
       * 2. We get the total number of event records per farmer
       *  -- select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id
       *
       * 3. We add these 2 records per farmer to get the total number of records per farmer
       *  -- select farmer_id, sum(cnt) `ttl_recs` from (select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
       *  -- union all
       *  -- select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id) rec
       *  -- group by farmer_id
       *
       * 4. We get the number of cows per farmer
       *  -- SELECT farmer_id, count(*) `no_cows` FROM cow where farmer_id != 0 group by farmer_id
       *
       * 5. Using the total records per farmer and the number of cows per farmer, we get the average number of recordings per farmer
       *  -- select tr.*, no_cows, (ttl_recs/no_cows) `av_rec_cow` from (select farmer_id, sum(cnt) `ttl_recs` from (select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
       *  -- union all
       *  -- select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id) rec
       *  -- group by farmer_id) tr
       *  -- inner join
       *  -- (SELECT farmer_id, count(*) `no_cows` FROM cow where farmer_id != 0 group by farmer_id) nc on tr.farmer_id=nc.farmer_id
       *
       * 6. Rank the farmers based on the average records per cow
       *  -- select farmer_id, ttl_recs, no_cows, av_rec_cow, @rank := @rank + 1 `rank` from (
       *  -- select tr.*, no_cows, (ttl_recs/no_cows) `av_rec_cow`, @rank := 0 from (select farmer_id, sum(cnt) `ttl_recs` from (select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
       *  -- union all
       *  -- select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id) rec
       *  -- group by farmer_id) tr
       *  -- inner join
       *  -- (SELECT farmer_id, count(*) `no_cows` FROM cow where farmer_id != 0 group by farmer_id) nc on tr.farmer_id=nc.farmer_id order by av_rec_cow desc) rk
       *
       * 7. Get the number of participating farmers. Some farmers only recorded milk records, others recorded only cow events, while others recorded both types of records
       *  -- select count(distinct(farmer_id)) `ttl_farms` from (select farmer_id, sum(cnt) `ttl_recs` from (select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
       *  -- union all
       *  -- select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id) rec
       *  -- group by farmer_id) recs
       *
       * 8. Finally group the farmers in deciles taking into account their rank and the total number of participating farmers. This is the final resulting query
       */
      $farmersByRankingQ = "select name `farmer_name`, farmer_id, ttl_recs, no_cows, format(av_rec_cow,1) `av_rec_cow`, rank, ttl_farms, round(10*(ttl_farms-rank+1)/ttl_farms,0) `decile`, location_district `site` from (select farmer_id, ttl_recs, no_cows, av_rec_cow, @rank := @rank + 1 `rank`, ttl_farms from (
         select tr.*, no_cows, (ttl_recs/no_cows) `av_rec_cow`, @rank := 0 from (select farmer_id, sum(cnt) `ttl_recs` from (select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
         union all
         select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id) rec
         group by farmer_id) tr
         inner join
         (SELECT farmer_id, count(*) `no_cows` FROM cow where farmer_id != 0 group by farmer_id) nc on tr.farmer_id=nc.farmer_id order by av_rec_cow desc) rk, (select count(distinct(farmer_id)) `ttl_farms` from (select farmer_id, sum(cnt) `ttl_recs` from (select farmer_id, count(*) `cnt` from farmer_milk where farmer_id != 0 group by farmer_id
         union all
         select farmer_id, count(*) `cnt` from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id != 0 group by farmer_id) rec
         group by farmer_id) recs) as ct) rnk inner join farmer as b on rnk.farmer_id=b.id order by rank";

      //get all the districts first
      $allSites = $this->Dbase->ExecuteQuery($allSitesQ);
      if($allSites == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      //get the farmers by site
      $farmerBysite = $this->Dbase->ExecuteQuery($farmerBysiteQ);
      if($farmerBysite == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      //get the site average production
      $sitesAverage = $this->Dbase->ExecuteQuery($sitesAverageQ);
      if($sitesAverage == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $sitesAverageO = array();
      $sitesStddev = array();
      foreach($sitesAverage as $site) {
         $sitesAverageO[$site['site']] = $site['average'];
         $sitesStddev[$site['site']] = $site['stddev'];
      }

      // get farmers ranking and their corresponding deciles
      $farmersByRanking = $this->Dbase->ExecuteQuery($farmersByRankingQ);
      if($farmersByRanking == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $farmersRanking = array();
      foreach($farmersByRanking as $farmer){
         $farmersRanking[$farmer['farmer_id']] = $farmer;
      }

      header("Content-type: application/json");
      $jsonString = '{"testing":'. json_encode(array())
              .', "site_farmers":'. json_encode(array_merge($allSites, $farmerBysite))
              .', "sitesAverage":'. json_encode($sitesAverageO)
              .', "sitesStddev":'. json_encode($sitesStddev)
              .', "sitesStddev":'. json_encode($sitesStddev)
              .', "farmersByRanking":'. json_encode($farmersRanking)
              .', "farmersOrderedByRanking":'. json_encode($farmersByRanking) .'}';

      die($jsonString);
   }

   /**
    * Get the data necessary to create the farmer visualization
    */
   private function getFarmerData(){
      $farmerQ = 'select name, mobile_no, location_district, gps_longitude, gps_latitude, date_added from farmer where id = :farmer_id';      //get the farmer information
      $cowsQ = 'select id, if(name != "" and ear_tag_number != "", concat(name, " (",ear_tag_number,")"), if(name != "", name, ear_tag_number)) as name, age, age_type, sex from cow where farmer_id = :farmer_id';     //get the farmer cows
      $breedQ = 'select b.name from cow_breed as a inner join breed as b on a.breed_id=b.id where a.cow_id = :cow_id';                          //get the cows' breed
      $milkCurveQ = "select date_format(milk_date, '%d-%m') as milk_date, sum, no_recs, time from cow_daily_sum where cow_id = :cow_id";
      $milkDatesQ = "select date_format(milk_date, '%d-%m') as milk_date from cow_daily_sum where farmer_id = :farmer_id";
      $noOfRecordsQ = 'select count(*) as no_recs from milk_production where cow_id=:cow_id';
      $farmerNoOfRecordsQ = 'select count(*) as no_recs from farmer_milk where farmer_id=:farmer_id';
      $eventsNoOfRecordsQ = 'select count(*) as no_recs from cow_event as a inner join cow as b on a.cow_id=b.id where farmer_id=:farmer_id';
      $cowAverageQ = "SELECT b.name as farmer_name, c.name as cow_name, count(*) as no_days_recorded, avg(`daily average`) as `average` FROM `cow_daily_average` as a inner join farmer as b on a.farmer_id=b.id inner join cow as c on a.cow_id=c.id where cow_id=:cow_id group by cow_id";
      $allEventsQ = "select * from (
            SELECT b.name as `cow_name`, a.date `event_date`, concat('Milking: ', time) `event`, concat(quantity, ' ', quantity_type) `amount` FROM `farmer_milk` as a inner join cow as b on a.cow_id=b.id where a.farmer_id=:farmer_id
            union all
            SELECT c.name `cow_name`, a.event_date `event_date`, b.name `event`, 'N/A' `amount` FROM `cow_event` as a inner join event as b on a.event_id=b.id inner join cow as c on a.cow_id=c.id where c.farmer_id=:farmer_id) a
            order by `event_date`";

      //farmer info
      $farmer = $this->Dbase->ExecuteQuery($farmerQ, array('farmer_id' => $_POST['farmer_id']));
      if($farmer == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      // all recorded events
      $allEvents = $this->Dbase->ExecuteQuery($allEventsQ, array('farmer_id' => $_POST['farmer_id']));
      if($allEvents == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      // no of milk records per farmer
      $farmerNoOfRecords = $this->Dbase->ExecuteQuery($farmerNoOfRecordsQ, array('farmer_id' => $_POST['farmer_id']));
      if($farmerNoOfRecords == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $farmer[0]['totalMilkRecords'] = $farmerNoOfRecords[0]['no_recs'];

      // no of other events records per farmer
      $eventsNoOfRecords = $this->Dbase->ExecuteQuery($eventsNoOfRecordsQ, array('farmer_id' => $_POST['farmer_id']));
      if($eventsNoOfRecords == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      $farmer[0]['totalEventsRecords'] = $eventsNoOfRecords[0]['no_recs'];

      //milk dates
      $milkDates = $this->Dbase->ExecuteQuery($milkDatesQ, array('farmer_id' => $_POST['farmer_id']));
      if($milkDates == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      //cows
      $cows = $this->Dbase->ExecuteQuery($cowsQ, array('farmer_id' => $_POST['farmer_id']));
      if($cows == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      //loop through all the cows and get the breed and milk curve
      foreach($cows as $i => $cow){
         //breed
         $breed = $this->Dbase->ExecuteQuery($breedQ, array('cow_id' => $cow['id']));
         if($breed == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
         if(count($breed) == 1) $cowBreed = $breed[0]['name'];
         else{
            $cowBreed = '';
            foreach($breed as $bd) $cowBreed .= ($cowBreed == '') ? $bd['name'] : ", {$bd['name']}";
         }

         if($cow['sex'] != 'Female') continue;

         //milk curve
         $milkCurve = $this->Dbase->ExecuteQuery($milkCurveQ, array('cow_id' => $cow['id']));
         if($milkCurve == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

         // no of records per cow
         $noOfRecords = $this->Dbase->ExecuteQuery($noOfRecordsQ, array('cow_id' => $cow['id']));
         if($noOfRecords == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
         $cows[$i]['totalRecords'] = $noOfRecords[0]['no_recs'];

         // cow average
         $cowAverage = $this->Dbase->ExecuteQuery($cowAverageQ, array('cow_id' => $cow['id']));
         if($cowAverage == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

         $cows[$i]['breed'] = $cowBreed;
         $cows[$i]['milk_curve'] = $milkCurve;
         $cows[$i]['cow_average'] = number_format($cowAverage[0]['average'],2);
         $cows[$i]['cow_rank'] = number_format($cowAverage[0]['average'],2);
      }

      //lets formart the milk curves well
      $cowMilkData = array(); $maxVal = 0;
      foreach($cows as $i => $cow){
         $mcurve = $cow['milk_curve'];
         $totalMilk = 0; $totalDays = array();
         foreach($mcurve as $mprod){
            $cowMilkData[$mprod['milk_date']][$cow['id']] = $mprod['sum']+0;
            $cowMilkData[$mprod['milk_date']]['date'] = $mprod['milk_date'];
            if($mprod['sum'] > $maxVal) $maxVal = $mprod['sum'];
            $totalMilk += $mprod['sum'];
            $totalDays[] = $mprod['milk_date'];
         }
         $cows[$i]['totalMilk'] = $totalMilk;
         $cows[$i]['totalDays'] = count($totalDays);
         $cows[$i]['rating'] = 'TBD';
      }
      $milkCurves = array();
      foreach($cowMilkData as $dt) $milkCurves[] = $dt;

      header("Content-type: application/json");
      $jsonString = '{"farmer":'. json_encode($farmer[0])
              .', "allEvents":'. json_encode($allEvents)
              .', "milkCurves":'. json_encode($milkCurves)
              .', "cows":'. json_encode($cows)
              .', "maxVal":'. json_encode($maxVal) .'}';

      die($jsonString);
   }

   /**
    * Get the enrolled users stats
    */
   private function getUserStats(){
      $allUsersQ = "select count(*) as count from farmer where project is not NULL and project not in ('eadd_ug_test')";
      $allusers = $this->Dbase->ExecuteQuery($allUsersQ);
      if($allusers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $usersQ = "select project, count(*) as count from farmer where project is not NULL and project not in ('eadd_ug_test') group by project";
      $users = $this->Dbase->ExecuteQuery($usersQ);
      if($users == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));
      foreach($users as $i => $u){
         if($u['project'] == 'eadd_ke') $users[$i]['project'] = 'EADD Kenya';
         else if($u['project'] == 'eadd_ug') $users[$i]['project'] = 'EADD Uganda';
         else if($u['project'] == 'np_kenya') $users[$i]['project'] = 'NP Test Kenya';
         else if($u['project'] == 'gfia2015_sa') $users[$i]['project'] = 'GFIA 2015 - Durban';
      }

      header("Content-type: application/json");
      $jsonString = '{"users":'. json_encode($users).'}';
      die($jsonString);
   }

   private function getMilkProd(){
      $milkQ = "select project, time, format(avg(quantity), 1) as avg_q from milk_production as a inner join cow as b on a.cow_id=b.id inner join farmer as c on b.farmer_id=c.id "
       . "where project is not NULL and project not in ('eadd_ug_test') and `time` not in ('Combined', '') group by project, time";
      $milk = $this->Dbase->ExecuteQuery($milkQ);
      if($milk == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $data = array();
      foreach($milk as $i => $u){
         if($u['project'] == 'eadd_ke') $project = 'EADD Kenya';
         else if($u['project'] == 'eadd_ug') $project = 'EADD Uganda';
         else if($u['project'] == 'np_kenya') $project = 'NP Test Kenya';
         else if($u['project'] == 'gfia2015_sa') $project = 'GFIA 2015 - Durban';

         $data[$project][$u['time']] = $u['avg_q'];
         $data[$project]['project'] = $project;
      }

      $users = array();
      foreach($data as $d){
         $users[] = $d;
      }

      header("Content-type: application/json");
      $jsonString = '{"milk":'. json_encode($users).'}';
      die($jsonString);
   }

   private function getWaterRegimes(){
      $allUsersQ = "select count(*) as count from farmer as a inner join cow as b on a.id=b.farmer_id inner join water as c on b.id=c.cow_id "
          . "where project is not NULL and project not in ('eadd_ug_test')";
      $allusers = $this->Dbase->ExecuteQuery($allUsersQ);
      if($allusers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $waterQ = "select project, admin_type, format(count(*), 1) as avg_w from water as a inner join cow as b on a.cow_id=b.id inner join farmer as c on b.farmer_id=c.id "
       . "where project is not NULL and project not in ('eadd_ug_test') group by project, admin_type";
      $water = $this->Dbase->ExecuteQuery($waterQ);
      if($water == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $data = array();
      foreach($water as $i => $u){
         if($u['project'] == 'eadd_ke') $project = 'EADD Kenya';
         else if($u['project'] == 'eadd_ug') $project = 'EADD Uganda';
         else if($u['project'] == 'np_kenya') $project = 'NP Test Kenya';
         else if($u['project'] == 'gfia2015_sa') $project = 'GFIA 2015 - Durban';

         $data[$project][$u['admin_type']] = $u['avg_w'];
         $data[$project]['project'] = $project;
      }

      $users = array();
      foreach($data as $d){
         $users[] = $d;
      }

      header("Content-type: application/json");
      $jsonString = '{"water":'. json_encode($users).'}';
      die($jsonString);
   }

   private function getFeedRegimes(){
//      $allUsersQ = "select count(*) as count from farmer as a inner join cow as b on a.id=b.farmer_id inner join feeding as c on b.id=c.cow_id "
//          . "where project is not NULL and project not in ('eadd_ug_test')";
//      $allusers = $this->Dbase->ExecuteQuery($allUsersQ);
//      if($allusers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $waterQ = "select project, food, count(*) as avg_f from feeding as a inner join cow as b on a.cow_id=b.id inner join farmer as c on b.farmer_id=c.id "
       . "where project is not NULL and project not in ('eadd_ug_test') group by project, food";
      $water = $this->Dbase->ExecuteQuery($waterQ);
      if($water == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $data = array();
      foreach($water as $i => $u){
         if($u['project'] == 'eadd_ke') $project = 'EADD Kenya';
         else if($u['project'] == 'eadd_ug') $project = 'EADD Uganda';
         else if($u['project'] == 'np_kenya') $project = 'NP Test Kenya';
         else if($u['project'] == 'gfia2015_sa') $project = 'GFIA 2015 - Durban';

         $data[$project][$u['food']] = $u['avg_f'];
         $data[$project]['project'] = $project;
      }

      $users = array();
      foreach($data as $d){
         $users[] = $d;
      }

      header("Content-type: application/json");
      $jsonString = '{"feed":'. json_encode($users).'}';
      die($jsonString);
   }

   private function getMilkUsageRegimes(){
//      $allUsersQ = "select count(*) as count from farmer as a inner join milk_usage as c on a.id=c.farmer_id "
//          . "where project is not NULL and project not in ('eadd_ug_test')";
//      $allusers = $this->Dbase->ExecuteQuery($allUsersQ);
//      if($allusers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $waterQ = "select project, usage_type, avg(quantity) as milk_u from milk_usage as a inner join farmer as c on a.farmer_id=c.id "
       . "where project is not NULL and project not in ('eadd_ug_test') group by project, usage_type";
      $water = $this->Dbase->ExecuteQuery($waterQ);
      if($water == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $data = array();
      foreach($water as $i => $u){
         if($u['project'] == 'eadd_ke') $project = 'EADD Kenya';
         else if($u['project'] == 'eadd_ug') $project = 'EADD Uganda';
         else if($u['project'] == 'np_kenya') $project = 'NP Test Kenya';
         else if($u['project'] == 'gfia2015_sa') $project = 'GFIA 2015 - Durban';

         $data[$project][$u['usage_type']] = $u['milk_u'];
         $data[$project]['project'] = $project;
      }

      $users = array();
      foreach($data as $d){
         $users[] = $d;
      }

      header("Content-type: application/json");
      $jsonString = '{"milk_usage":'. json_encode($users).'}';
      die($jsonString);
   }

   private function getMilkSellingRegimes(){
//      $allUsersQ = "select count(*) as count from farmer as a inner join milk_usage as c on a.id=c.farmer_id "
//          . "where project is not NULL and project not in ('eadd_ug_test')";
//      $allusers = $this->Dbase->ExecuteQuery($allUsersQ);
//      if($allusers == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $waterQ = "select project, buyer, count(*) as milk_buyer from milk_usage as a inner join farmer as c on a.farmer_id=c.id "
       . "where project is not NULL and project not in ('eadd_ug_test') and buyer is not NULL and buyer !='individual_customer' group by project, buyer";
      $water = $this->Dbase->ExecuteQuery($waterQ);
      if($water == 1) die(json_encode(array('error' => true, 'data' => $this->Dbase->lastError)));

      $data = array();
      foreach($water as $i => $u){
         if($u['project'] == 'eadd_ke') $project = 'EADD Kenya';
         else if($u['project'] == 'eadd_ug') $project = 'EADD Uganda';
         else if($u['project'] == 'np_kenya') $project = 'NP Test Kenya';
         else if($u['project'] == 'gfia2015_sa') $project = 'GFIA 2015 - Durban';

         $data[$project][$u['buyer']] = $u['milk_buyer'];
         $data[$project]['project'] = $project;
      }

      $users = array();
      foreach($data as $d){
         $users[] = $d;
      }

      header("Content-type: application/json");
      $jsonString = '{"milk_selling":'. json_encode($users).'}';
      die($jsonString);
   }

   private function generateUsageStats(){
      // lets generate some usage stats
      $query = "select project, location_district from farmer where project in ('eadd_ke', 'eadd_ug') group by location_district";
      $locations = $this->Dbase->ExecuteQuery($query);
      if($locations == 1) die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
      $locations[] = array('project' => 'eadd_ke', 'location_district' => '%%');
      $locations[] = array('project' => 'eadd_ug', 'location_district' => '%%');

      // lets get the months
      $query = "select left(a.date, 7) month from milk_production as a inner join cow as b on a.cow_id=b.id inner join farmer as c on b.farmer_id=c.id "
            . "where project in ('eadd_ke', 'eadd_ug') and a.date != 0 group by left(a.date, 7)";
      $months = $this->Dbase->ExecuteQuery($query);
      if($months == 1) die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
      $monthsCsv = array();
      foreach($months as $m) $monthsCsv[] = "{$m['month']}";
      $monthsCsv = implode(',', $monthsCsv);

      $noFarmersQuery = 'select month, count(*) as records from farmer as a
         inner join (select distinct(farmer_id) as farmer_id, left(a.date, 7) as month
            from milk_production as a inner join cow as b on a.cow_id=b.id
            where farmer_id != 0 and farmer_id is not null and a.date != 0) as b on a.id=b.farmer_id
        where project like :project and location_district like :location
        group by month order by month';

      $noCowsQuery = 'select month, count(*) as records
        from cow as a inner join farmer as b on a.farmer_id=b.id
        inner join (SELECT distinct(cow_id) as cow_id, left(date, 7) as month FROM `milk_production` where date != 0) as c on a.id=c.cow_id
        where project like :project and location_district like :location group by month';

      $noRecordsQuery = 'select left(a.date, 7) as month, count(*) as records '
         . 'from milk_production as a inner join cow as b on a.cow_id = b.id inner join farmer as c on b.farmer_id = c.id '
         . 'where date != 0 and farmer_id != 0 and project like :project and location_district like :location  group by month';

      $avgMilkProdQuery = 'select left(a.date, 7) as month, format(avg(quantity),2) as records '
         . 'from milk_production as a inner join cow as b on a.cow_id = b.id inner join farmer as c on b.farmer_id = c.id '
         . 'where farmer_id != 0 and project like :project and location_district like :location and a.date != 0 group by month';

      $stddevMilkProdQuery = 'select left(a.date, 7) as month, format(stddev(quantity), 2) as records '
         . 'from milk_production as a inner join cow as b on a.cow_id = b.id inner join farmer as c on b.farmer_id = c.id '
         . 'where farmer_id != 0 and project like :project and location_district like :location and a.date != 0 group by month';

      $districts = array();
      foreach($locations as $loc){
         if(!isset($districts[$loc['project']])) $districts[$loc['project']] = array();
         $districts[$loc['project']][$loc['location_district']] = array();

         $noFarmers = $this->Dbase->ExecuteQuery($noFarmersQuery, array('project' => $loc['project'], 'location' => $loc['location_district']));
         if($noFarmers == 1) die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
         $tmp = array();
         foreach($noFarmers as $f) $tmp[$f['month']] = $f['records'];
         $districts[$loc['project']][$loc['location_district']]['Number of Farmers'] = $tmp;

         $noCows = $this->Dbase->ExecuteQuery($noCowsQuery, array('project' => $loc['project'], 'location' => $loc['location_district']));
         if($noCows == 1)  die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
         $tmp = array();
         foreach($noCows as $c) $tmp[$c['month']] = $c['records'];
         $districts[$loc['project']][$loc['location_district']]['Number of Cows'] = $tmp;

         $noRecords = $this->Dbase->ExecuteQuery($noRecordsQuery, array('project' => $loc['project'], 'location' => $loc['location_district']));
         if($noRecords == 1) die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
         $tmp = array();
         foreach($noRecords as $r) $tmp[$r['month']] = $r['records'];
         $districts[$loc['project']][$loc['location_district']]['Number of Records'] = $tmp;

         $avgMilkProd = $this->Dbase->ExecuteQuery($avgMilkProdQuery, array('project' => $loc['project'], 'location' => $loc['location_district']));
         if($avgMilkProd == 1) die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
         $tmp = array();
         foreach($avgMilkProd as $a) $tmp[$a['month']] = $a['records'];
         $districts[$loc['project']][$loc['location_district']]['Average Milk Production'] = $tmp;

         $stddevMilkProd = $this->Dbase->ExecuteQuery($stddevMilkProdQuery, array('project' => $loc['project'], 'location' => $loc['location_district']));
         if($stddevMilkProd == 1) die(json_encode(array('error' => true, 'mssg' => $this->Dbase->lastError)));
         $tmp = array();
         foreach($stddevMilkProd as $s) $tmp[$s['month']] = $s['records'];
         $districts[$loc['project']][$loc['location_district']]['StdDev'] = $tmp;
      }

      // lets create the csv with this data
      // header
      $out = '';
      foreach($districts as $country => $d){
         $out .= "\n\nCountry, Hub, Parameters, $monthsCsv\n";
         $writeCountry = true;
         foreach($d as $hub => $data){
            $hub = ($hub == '%%') ? 'Country Summary' : $hub;
            $writeHub = true;
            foreach($data as $param => $vals){
               $out .= ($writeCountry) ? "$country" : '';
               $out .= ($writeHub) ? ",$hub" : ',';
               $out .= ",$param";
               foreach($months as $m){
                  if(!isset($vals[$m['month']])) $out .= ',';
                  else $out .= ",{$vals[$m['month']]}";
               }
               $out .= "\n";
               $writeCountry = false; $writeHub = false;
            }
         }
      }

      $outFile = 'FarmerParticipationStatistics.csv';
      $fp = fopen($outFile, 'wt');
      if(!$fp) die('Error while opening the file for writing');
      fputs($fp, $out);

      $settings = parse_ini_file(OPTIONS_MODULE_EMAIL_CONFIG_FILE);
      $addressedTo = 'i.baltenweck@cgiar.org';

      $content = "Dear all,<br /><br />Please find attached a csv file with a summary of farmer participation and milk records for the EADD project.<br /><br />";
      $content .= "Regards<br />The Biorepository team";

      $this->Dbase->CreateLogEntry("sending an email to $addressedTo with the daily digest\n\n$content", 'info');
      shell_exec("echo '$content' | {$settings['mutt_bin']} -a '$outFile' -e 'set content_type=text/html' -c 'e.oyieng@cgiar.org' -c 'a.kihara@cgiar.org' -F {$settings['mutt_config']} -s 'EADD participation digest' -- $addressedTo");
      unlink($outFile);
   }
}
?>