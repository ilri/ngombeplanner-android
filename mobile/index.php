<?php
/**
 * Currently only being called by the ussd application.
 * Redirect accordingly when other applications/website will be using the url
 */
header('Content-type: text/plain');
include_once '../np_ussd/php/mod_kerberos.php';
date_default_timezone_set ('Africa/Nairobi');
$ussdHandlerObject = new NgombePlannerUSSD();
$ussdHandlerObject->processUserResponse();
?>
