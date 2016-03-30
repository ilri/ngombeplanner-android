<?php
    //setting the date settings
    date_default_timezone_set ('Africa/Nairobi');

    /**
     * @var string    What the user wants
     */
    define('OPTIONS_REQUESTED_MODULE', $argv[1]);
    define('OPTIONS_REQUESTED_SUB_MODULE', $argv[2]);
    chdir('/var/www/html/azizi.ilri.org/ngombeplanner/');
    require_once 'modules/mod_startup.php';
    $NgombePlanner->TrafficController();
?>
