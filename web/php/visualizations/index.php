<html>
  <head>
    <script type="text/javascript" src="js/dygraph-combined.js"></script>
    <script type="text/javascript" src="js/visualize_milk_data.js"></script>
    <script type="text/javascript" src="js/jquery-2.0.3.js"></script>
    <script type="text/javascript">
      $(document).ready(function(){
         console.log("Document loaded");
         var visObject = new VisMilkData();
      });
    </script>
  </head>
  <body>
      <div>
         <div style="width:15%;float:left;">
            <h3>Data source</h3>
            <select id="farmerList"></select><br />
            <select id="cowList"></select><br />
            <input type="button" id="refresh" value="refresh"></input><br />
         </div>
         <div id="div_g" style="width:80%; height:500px;vertical-align: top; float: right; margin-top: 20px; margin-right: 20px;"></div>
      </div>
  </body>
</html>
