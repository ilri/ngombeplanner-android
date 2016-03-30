<?php

/* 
 * This class is responsible for fetching weather forecasts for some area
 */
class WForecast{
   private $TAG = "weather_forecast.php";
   private $ROOT;
   
   private $database;
   private $logHandler;
   private $translator;
   private $settings;
   
   private $mobileNumber;
   private $farmerID;
   private $locale;
   
   public function __construct($rootDir = "../../",$database, $logHandler) {
      $this->ROOT = $rootDir;
      
      $this->database = $database;
      $this->logHandler = $logHandler;
      
      include_once $this->ROOT.'php/common/translate.php';
      $this->translator = new Translator($this->logHandler, $this->database);
      
      $this->settings = parse_ini_file($this->ROOT."config/settings.ini");
   }
   
   public function getWeatherForecast($farmerNumber) {
      $query = "select id, mobile_no, location_district, gps_latitude, gps_longitude,pref_locale"
              . " from farmer"
              . " where mobile_no like '%$farmerNumber'";
      $result = $this->database->runMySQLQuery($query, true);
      
      if(is_array($result) && count($result) == 1){
         $this->mobileNumber = $result[0]['mobile_no'];
         $this->farmerID = $result[0]['id'];
         $this->locale = $result[0]['pref_locale'];
         
         $lat = $result[0]['gps_latitude'];
         $lon = $result[0]['gps_longitude'];
         $district = $result[0]['location_district'];
         
         $nearestStation = $this->getFarmerStationName();
         
         if($nearestStation == null){//get nearest station to farmer
            if(strlen($lat) == 0 || strlen($lon) == 0){//user's gps is not set, get gps from another farmer in the same district
               $this->logHandler->log(2, $this->TAG, "Unable to get GPS location for farmer. Getting GPS from a farmer close by");

               $latLon = $this->getLatLonInDistrict($district);
               if($latLon != null){
                  $lat = $latLon['gps_latitude'];
                  $lon = $latLon['gps_longitude'];
               }
               else {
                  $this->logHandler->log(2, $this->TAG, "Unable to get GPS from an farmer close by. Unable to send weather forecast back to farmer");
               }
            }
            
            if(strlen($lat) > 0 && strlen($lon) > 0){
               $latLon = array("lat" => $lat, "lon" => $lon);
               $nearestStation = $this->getNearestWStation($latLon);
            }
         }
         
         if($nearestStation != null){
            //TODO: check if we have data for the weather station in the database
            $weather = $this->getStoredWeather($nearestStation);
            if($weather == null){
               $weather = $this->getStationWeather($nearestStation);
            }

            if($weather != null){
               return $this->addForecastToSMSQueue($weather);
            }
            else {
               $this->logHandler->log(2, $this->TAG, "Unable to obtain the weather forecast from both the weather API and the database");
            }
         }
      }
      
      return false;
	}
   
   /**
    * This function returns station name for station assigned to farmer
    * 
    * @return Station ID if station found and null if nothing found
    */
   private function getFarmerStationName(){
      $query = "select b.name"
              . " from farmer_station as a"
              . " inner join station as b on a.station_id = b.id"
              . " where a.farmer_id = {$this->farmerID}";
              
      $result = $this->database->runMySQLQuery($query, true);
      if(count($result) == 1){
         return $result[0]['name'];
      }
      return null;
   }
   
   
   /**
    * This function gets the closest weather station (in an airport) to the provided
    * Latitude and Longitude
    * 
    * @param type $latLon
    * @return null
    */
   private function getNearestWStation($latLon){
      
      $data = $this->getFromAPI('geolookup', $latLon);
      
      if($data != null){
         if(isset($data['location']) && isset($data['location']['nearby_weather_stations']['airport']) && isset($data['location']['nearby_weather_stations']['airport']['station'])){
            if(count($data['location']['nearby_weather_stations']['airport']['station']) > 0){
               $stations = $data['location']['nearby_weather_stations']['airport']['station'];
               
               //get the nearest station
               $nearest = null;
               $nearestIndex = -1;
               $distance = -1;
               for($index = 0 ; $index < count($stations); $index++){
                  if(strlen($stations[$index]['lat']) > 0 && strlen($stations[$index]['lon']) > 0){
                     $from = $latLon;
                     $to = array("lat" => $stations[$index]['lat'], "lon" => $stations[$index]['lon']);
                     
                     $currDistance = $this->calculateDistance($from, $to);
                     if($distance == -1 || $distance > $currDistance){
                        $distance = $currDistance;
                        $nearest = $stations[$index]['city'];
                        $nearestIndex = $index;
                     }
                  }
               }
               
               if($nearest == null){
                  $this->logHandler->log(2, $this->TAG, "geolookup API returned a list of close Stations but none had geo-coordinates attached to them. Sending user nothing");
               }
               if($distance > 10){
                  $this->logHandler->log(2, $this->TAG, "The distance to the closest weather station is more than 10KM. Sending user nothing");
                  $nearest = null;
               }
               
               $nearestRow = $stations[$nearestIndex];
               //if we've reached this far, nearest weather station deserves to be used as farmer's station
               $query = "insert into station(`name`, `type`, `country`, `state`, `icao`, `gps_latitude`, `gps_longitude`)"
                       . " values('{$nearestRow['city']}', 'airport','{$nearestRow['country']}', '{$nearestRow['state']}', '{$nearestRow['icao']}','{$nearestRow['lat']}','{$nearestRow['lon']}')"
                       . " on duplicate key update country = '{$nearestRow['country']}', `state` = '{$nearestRow['state']}', icao = '{$nearestRow['icao']}', gps_latitude = '{$nearestRow['lat']}', gps_longitude = '{$nearestRow['lon']}'";
                       
               $this->database->runMySQLQuery($query, false);
               
               $stationID = $this->getStationID($nearestRow['city']);
               
               if($stationID == null){
                  $this->logHandler->log(1, $this->TAG, "Unable to get id for the station just inserted");
                  return null;
               }
               
               $query = "insert into farmer_station(farmer_id, station_id)"
                       . " values({$this->farmerID}, $stationID)"
                       . " on duplicate key update station_id = $stationID";
               $this->database->runMySQLQuery($query, false);
               
               return $nearestRow['city'];
            }
            else {
               $this->logHandler->log(1, $this->TAG, "No airports found close to user. Sending nothing to user");
            }
         }
         else {
            $this->logHandler->log(1, $this->TAG, "Data returned from the geolookup API is mulformed. Sending nothing to the user");
         }
      }
      
      return null;
   }
   
   /**
    * This function gets the id for a station from the database
    * 
    * @param String $stationName
    * 
    * @return ID of the station if found or null if not
    */
   private function getStationID($stationName){
      $query = "select id from station where name = '$stationName'";
      $result = $this->database->runMySQLQuery($query, true);
      
      if(count($result) == 1){
         return $result[0]['id'];
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Did not find unique station with name = '$stationName' in the database");
         return null;
      }
   }
   
   /**
    * This function gets the specified station's weather forcast for the next 4 days (including today) from the database
    * 
    * @param type $stationName
    * 
    * @return Array Returns an array with the weather forecasts or null if an error occurs
    */
   private function getStoredWeather($stationName){
      //get station id
      $stationID = $this->getStationID($stationName);
      
      if($stationID == null){
         $this->logHandler->log(1, $this->TAG, "Unable to find station in database with name = '$stationName'");
         return null;
      }
      
      $today = date('Y-m-d');
      $forthDay = date('Y-m-d', time() + 86400*3);
      $threeHoursAgo = date('Y-m-d H:i:s', time() - (3600 * 3));
      
      $query = "select a.*,b.name as station_name"
              . " from station_weather as a"
              . " inner join station as b on a.station_id = b.id"
              . " where a.station_id = $stationID && a.date >= '$today' && a.date <= '$forthDay' && a.time_added >= '$threeHoursAgo'";
      $result = $this->database->runMySQLQuery($query, true);
      
      if(count($result) == 4){//if we get 3 days, data is valid
         return $result;
      }
      else {
         $this->logHandler->log(2, $this->TAG, "Database does not have the forecast for the next four days (including today). It has ".  count($result));
         return null;
      }
   }
   
   /**
    * This function gets the specified station's weather forecast for the next 3 days
    * 
    * @param type $stationName
    * 
    * @return Array Returns an array with the weather forecasts or null if an error occurs
    */
   private function getStationWeather($stationName){
      $data = $this->getFromAPI('forecast', array('station' => $stationName));
      
      if($data != null){
         if(isset($data['forecast']) && isset($data['forecast']['simpleforecast']) && isset($data['forecast']['simpleforecast']) && isset($data['forecast']['simpleforecast']['forecastday'])){
            $days = $data['forecast']['simpleforecast']['forecastday'];
            
            //add/update the days in the database
            $formatted = array();
            for($index = 0; $index < count($days); $index++){
               $date = $days[$index]['date']['day']."-".$days[$index]['date']['month']."-".$days[$index]['date']['year'];
               
               $stationID = $this->getStationID($stationName);
               
               if($stationID == null){
                  $this->logHandler->log(1, $this->TAG, "Unable to get station ID for station with name = '$stationName'. Unable to send data to user");
                  return null;
               }
               
               $currDay = array(
                   "station_id" => $stationID,
                   "station_name" => $stationName,
                   "date" => $date,
                   "pop" => $days[$index]['pop'],
                   "temp_high" => $days[$index]['high']['celsius'],
                   "temp_low" => $days[$index]['low']['celsius'],
                   "conditions" => $days[$index]['conditions']
               );
               
               
               $query = "insert into station_weather (station_id, date, pop, temp_high, temp_low, conditions)"
                       . " values('{$stationID}', STR_TO_DATE('$date','%d-%m-%Y'), {$currDay['pop']}, {$currDay['temp_high']}, {$currDay['temp_low']}, '{$currDay['conditions']}')"
                       . " on duplicate key update pop = {$currDay['pop']}, temp_high = {$currDay['temp_high']}, temp_low = {$currDay['temp_low']}, conditions = '{$currDay['conditions']}'";
               $this->database->runMySQLQuery($query, false);
               
               $formatted[] = $currDay;
               if($index == 3) break;//do not add any weather forecast for days more than 3 from today
            }
            
            return $formatted;
         }
         else {
            $this->logHandler->log(1, $this->TAG, "Data returned from the forecast API is mulformed. Sending nothing to the user");
         }
      }
      
      return null;
   }
   
   /**
    * This function gets GPS coordinates from a farmer in the specified district
    * if any.
    * 
    * @param type $district The district to get GPS coordinates from
    * 
    * @return Array returns an object with gps_latitude and gps_longitude or null
    *          if no coordinates found
    */
   private function getLatLonInDistrict($district){
      $query = "select gps_latitude, gps_longitude from farmer where location_district = '$district' and gps_latitude is not null and gps_latitude != '' and gps_longitude is not null and gps_longitude != '' limit 1";
      $result = $this->database->runMySQLQuery($query, true);
      if(is_array($result) && count($result) == 1){
         return $result[0];
      }
      return null;
   }
   
   /**
    * This function gets data from the Weather API
    * 
    * @param String $uri The URI in the API to use e.g geolookup or forecast e.t.c
    * @param Array $data Array with the data to be sent to API
    * 
    * @return Array JSON object with the response or null if something happened
    */
   private function getFromAPI($uri, $data){
      $url = $this->settings['weather_api_url'];
      $key = $this->settings['weather_api_key'];
      
      $completeURL = "";
      if($uri == "geolookup"){
         if(isset($data['lat']) && isset($data['lon'])){
            $completeURL = $url.$key."/".$uri."/q/".$data['lat'].",".$data['lon'].".json";
         }
         else {
            $this->logHandler->log(1, $this->TAG, "Data provided for the geolookup URI is incorrect");
         }
      }
      else if($uri == "forecast"){
         if(isset($data['station'])){
            $completeURL = $url.$key."/".$uri."/q/".str_replace(" ", "%20", $data['station'])."/".str_replace(" ", "%20", $data['station']).".json";
         }
      }
      else {
         $this->logHandler->log(1, $this->TAG, "Unknown URI provided for the Weather API");
      }
      
      if(strlen($completeURL) > 0){
         $this->logHandler->log(3, $this->TAG, "About to access the weather API using the following URL ".$completeURL);
         $ch = curl_init($completeURL);

         curl_setopt($ch, CURLOPT_USERAGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:26.0) Gecko/20100101 Firefox/26.0");
         curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);
         curl_setopt($ch, CURLOPT_FOLLOWLOCATION, TRUE);

         $result = curl_exec($ch);
         
         $http_status = curl_getinfo($ch, CURLINFO_HTTP_CODE);
         curl_close($ch);
         
         if($http_status == 200){
            if(strlen($result) > 0){
               return json_decode($result, true);
            }
            else {
               $this->logHandler->log(1, $this->TAG, "The API returned nothing with the status code $http_status for the uri $uri. Unable to sent anything to the user");
            }
         }
         else {
            $this->logHandler->log(1, $this->TAG, "The API returned with a status code of $http_status for the uri $uri. Unable to sent anything to the user");
         }
      }
      
      return null;
   }
   
   /**
    * This function calculates the distance between two GPS points
    * 
    * @param type $from
    * @param type $to
    * 
    * @return The distance in KMs
    */
   private function calculateDistance($from, $to){
      $rad = pi() / 180;
      
      $R = 6371;
      $latDiff = ($from['lat'] - $to['lat']) * $rad;
      $lonDiff = ($from['lon'] - $to['lon']) * $rad;
      $fromLat = $from['lat'] * $rad;
      $toLat = $to['lat'] * $rad;
      
      $a = sin($latDiff/2) * sin($latDiff/2) + 
              sin($lonDiff/2) * sin($lonDiff/2) * cos($fromLat) * cos($toLat);
      
      $c = 2 * atan2(sqrt($a), sqrt(1- $a));
      $d = $R * $c;
      return $d;
   }
   
   /**
    * This function adds the weather to the SMS queue as a tranlated string
    * 
    * @param type $weather The forecast to be sent to the farmer
    */
   private function addForecastToSMSQueue($weather){
      if(count($weather) == 4){
         $template = $this->database->runMySQLQuery("select id, sms_text from sms_types where sms_code = 'fm_wth_fcst' and locale = '{$this->locale}'",true);
         $smsType = -1;
         if(count($template) == 1){
            $smsType = $template[0]['id'];
            $template = $template[0]['sms_text'];
         }
         else {
            $this->logHandler->log(1, $this->TAG, "Unable to get sms text for weather forecasts for farmer with id = ".$this->farmerID);
            $template = null;
         }
         
         $allGood = true;
         for($index = 0; $index < count($weather); $index++){
            $day = "";
            if($index == 0) $day = $this->translator->getText (Translator::$TODAY, $this->locale);
            else if($index == 1 ) $day = $this->translator->getText (Translator::$TOMORROW, $this->locale);
            else if($index == 2 || $index == 3){
               $dw = date('l', time() + (86400*$index));
               if($dw == "Monday") $day = $this->translator->getText (Translator::$MONDAY, $this->locale);
               else if($dw == "Tuesday") $day = $this->translator->getText (Translator::$TUESDAY, $this->locale);
               else if($dw == "Wednesday") $day = $this->translator->getText (Translator::$WEDNESDAY, $this->locale);
               else if($dw == "Thursday") $day = $this->translator->getText (Translator::$THURSDAY, $this->locale);
               else if($dw == "Friday") $day = $this->translator->getText (Translator::$FRIDAY, $this->locale);
               else if($dw == "Saturday") $day = $this->translator->getText (Translator::$SATURDAY, $this->locale);
               else if($dw == "Sunday") $day = $this->translator->getText (Translator::$SUNDAY, $this->locale);
            }
            else {
               $this->logHandler->log(2, $this->TAG, "Unable to get the day using its index");
            }
            
            $sms = null;
            if($template != null){
               //%d% chance of rain %s in %s. Highs of %dC and lows of %d.C
               $sms = sprintf($template, $weather[$index]['pop'], $day, $weather[$index]['station_name'], $weather[$index]['temp_high'], $weather[$index]['temp_low']);
               
               $query = "insert into sms_queue(number, text2send, text_status, sms_type, schedule_time)"
                       . " values('{$this->mobileNumber}', '$sms', 'not sent', $smsType, NOW())";
               $this->database->runMySQLQuery($query, false);
               
               $this->logHandler->log(3, $this->TAG, "Queued weather forecast for farmer with number = ".$this->mobileNumber);
            }
            else $allGood = false;
         }
         
         return $allGood;
      }
      else {
         $this->logHandler->log(1, $this->TAG, "Expected weather forecast for 3 days, got ".  count($weather).". Sending nothing to user");
      }
      
      return false;
   }
}
?>

