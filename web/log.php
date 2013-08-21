<?php
function log_error($tag,$time,$message,$query)
{
	if($message!="")
	{
		$fh = fopen("../log/error.log","a");
		fputs($fh,$time." ".$tag." ".$message." ".$query."\n");
		echo $time." ".$tag." ".$message." ".$query."\n";
		fclose($fh);
	}
}
?>
