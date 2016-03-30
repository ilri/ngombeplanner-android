var Main = {
   ajaxParams: {successMssg: undefined, div2Update: undefined}, successMssg: undefined, theme: '',
   contact: ' Please contact the system administrator.', uploadedFiles: new Array(),
   reEscape: new RegExp('(\\' + ['/', '.', '*', '+', '?', '|', '(', ')', '[', ']', '{', '}', '\\'].join('|\\') + ')', 'g')
};

var ngombePlanner = {
   serverData: undefined, reportsData: undefined,

   /**
    * Initiate the farmer graphs
    */
   initiateFarmers: function(){
      var totalSettings = {
         title: "Farmers Enrollment",
         description: "Cumulative farmer enrollment day by day",
         enableAnimations: true,
         showLegend: true,
         padding: {left: 10, top: 5, right: 10, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.enrollmentPerDay,
         categoryAxis: {
            text: 'Category Axis',
            textRotationAngle: 0,
            dataField: 'date',
            showTickMarks: true,
            valuesOnTicks: false,
            tickMarksInterval: 10,
            tickMarksColor: '#888888',
            gridLinesInterval: 10,
            gridLinesColor: '#888888',
            axisSize: 'auto'
         },
         colorScheme: 'scheme05',
         seriesGroups: [{
            type: 'line',
            showLabels: true,
            symbolType: 'circle',
            valueAxis:{minValue: 0, description: 'Farmer Enrollment', tickMarksColor: '#888888'},
            series: [{dataField: 'count', displayText: 'Enrollment'}]
         }]
      };
      // create the farmer enrollment line
      $('#total_farmers').jqxChart(totalSettings);

      //Enrollment per site
      var siteFarmers = {
         title: "Farmers per Site",
         description: "Enrolled farmers per site",
         showLegend: true,
         enableAnimations: true,
         padding: {left: 20, top: 5, right: 20, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.siteFarmers,
         categoryAxis:{ dataField: 'site', showGridLines: true, flip: false },
         colorScheme: 'scheme01',
         seriesGroups: [{
            type: 'column',
            orientation: 'horizontal',
            columnsGapPercent: 100,
            toolTipFormatSettings: { thousandsSeparator: ',' },
            valueAxis: { flip: true, unitInterval: 5, minValue: 0, displayValueAxis: true, description: '' },
            series: [{dataField: 'count', displayText: 'Farmer Nos'}]
         }]
      };

     $('#site_farmers').jqxChart(siteFarmers);
   },

   /**
    * Initiate the stats for active records
    */
   initiateActiveRecords: function() {
      var months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

      // prepare jqxChart settings
      var farmer_settings = {
         title: "Farmer Records",
         description: "Active farmers with milk records",
         enableAnimations: true,
         showLegend: true,
         padding: {left: 10, top: 5, right: 10, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.milkRecords,
         categoryAxis: {
            dataField: 'date',
            formatFunction: function(value) {
               return months[value.getMonth()]; },
            toolTipFormatFunction: function(value) {
               return value.getDate() + '-' + months[value.getMonth()];
            },
            type: 'date',
            baseUnit: 'day',
            showTickMarks: true,
            tickMarksInterval: 1,
            tickMarksColor: '#888888',
            showGridLines: true,
            gridLinesInterval: 3,
            gridLinesColor: '#888888',
            valuesOnTicks: false
         },
         colorScheme: 'scheme04',
         seriesGroups:[{
            type: 'line',
            valueAxis:{ minValue: 0, displayValueAxis: true, description: 'Number of Records', axisSize: 'auto', tickMarksColor: '#888888' },
            series: [
               {dataField: 'farmers', displayText: 'Active Farmers'},
               {dataField: 'totalFarmers', displayText: 'Total Farmers'}
            ]}
         ]
      };

      // prepare jqxChart settings
      var cow_settings = {
         title: "Cow Records",
         description: "Active cows with milk records",
         enableAnimations: true,
         showLegend: true,
         padding: {left: 10, top: 5, right: 10, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.milkRecords,
         categoryAxis: {
            dataField: 'date',
            formatFunction: function(value) {
               return months[value.getMonth()]; },
            toolTipFormatFunction: function(value) {
               return value.getDate() + '-' + months[value.getMonth()];
            },
            type: 'date',
            baseUnit: 'day',
            showTickMarks: true,
            tickMarksInterval: 10,
            tickMarksColor: '#888888',
            showGridLines: true,
            gridLinesInterval: 30,
            gridLinesColor: '#888888',
            valuesOnTicks: false
         },
         colorScheme: 'scheme04',
         seriesGroups:[{
            type: 'line',
            valueAxis:{ minValue: 0, displayValueAxis: true, description: 'Number of Records', axisSize: 'auto', tickMarksColor: '#888888' },
            series: [
               {dataField: 'cows', displayText: 'Active Cows'},
               {dataField: 'totalCows', displayText: 'Total Cows'}
            ]}
         ]
      };

      // setup the charts
      $('#active_farmers').jqxChart(farmer_settings);
      $('#active_cows').jqxChart(cow_settings);
   },

   /**
    * Initiate the stats display for the events and the times milk events were recorded
    */
   initiateEvents_MilkTimes: function() {
      var settings = {
         title: "Milk Recording Patterns",
         description: "The times when milk data is recorded",
         enableAnimations: true,
         showLegend: true,
         padding: {left: 5, top: 5, right: 5, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.recordPatterns,
         categoryAxis:{ dataField: 'site', showGridLines: true },
         colorScheme: 'scheme01',
         seriesGroups: [{
            type: 'column',
            columnsGapPercent: 50,
            seriesGapPercent: 0,
            valueAxis: {
               minValue: 0,
               displayValueAxis: true,
               description: 'No of Instances',
               axisSize: 'auto',
               tickMarksColor: '#888888'
            },
            series: [
               {dataField: 'Morning', displayText: 'Morning'},
               {dataField: 'Afternoon', displayText: 'Afternoon'},
               {dataField: 'Evening', displayText: 'Evening'},
               {dataField: 'Combined', displayText: 'Combined'}
            ]
         }]
      };

      // setup the chart
      $('#milk_times').jqxChart(settings);

      //get the time recording patterns
      var time_settings = {
         title: "Recording Time",
         description: "The time when the farmers are recording milk data",
         enableAnimations: true,
         showLegend: true,
         padding: {left: 10, top: 5, right: 10, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.timePatterns,
         categoryAxis: {
            dataField: 'hour',
            showTickMarks: true,
            tickMarksInterval: 1,
            unitInterval: 1,
            showGridLines: true,
            gridLinesInterval: 3,
            valuesOnTicks: false
         },
         colorScheme: 'scheme04',
         seriesGroups:[{
            type: 'line',
            valueAxis:{ minValue: 0, displayValueAxis: true, description: 'Number of Records', axisSize: 'auto', tickMarksColor: '#888888' },
            series: [{dataField: 'count', displayText: 'Hour'}]
         }]
      };
      $('#record_times').jqxChart(time_settings);
   },

   /**
    * Initiate the graphs for the USSD params
    */
   initiateUSSD: function(){
      //Sessions per day
      var daySessions = {
         title: "USSD Sessions",
         description: "Initiated USSD sessions per Week",
         showLegend: true,
         enableAnimations: true,
         padding: {left: 20, top: 5, right: 20, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.daySessions,
         categoryAxis:{ dataField: 'date', showGridLines: true, flip: false },
         colorScheme: 'scheme01',
         seriesGroups: [{
            type: 'column',
            toolTipFormatSettings: { thousandsSeparator: ',' },
            valueAxis: { unitInterval: 2000, minValue: 0, displayValueAxis: true, description: 'No of Sessions' },
            series: [{dataField: 'count', displayText: 'Week No'}]
         }]
      };
     $('#day_sessions').jqxChart(daySessions);

      //hop costs per day
      var hopCosts = {
         title: "USSD Costs per Week",
         description: "Incurred costs per Week (KES 1.8 per menu)",
         showLegend: true,
         enableAnimations: true,
         padding: {left: 20, top: 5, right: 20, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.serverData.dayCosts,
         categoryAxis:{ dataField: 'date', showGridLines: true, flip: false },
         colorScheme: 'scheme02',
         seriesGroups: [{
            type: 'column',
            toolTipFormatSettings: { thousandsSeparator: ',' },
            valueAxis: { unitInterval: 5000, minValue: 0, displayValueAxis: true, description: 'Cost in KShs (\'000)', formatFunction: function (value) { return parseInt(value/1000); }
 },
            series: [{dataField: 'count', displayText: "Week No"}]
         }]
      };
     $('#day_costs').jqxChart(hopCosts);
   },

   initiateEvents: function(){},

   initiateSMS: function(){},

   /**
    * Fetch the data needed to generate the charts
    */
   fetchData: function(){
      $.ajax({
         type:"POST", url:'mod_ajax.php?page=data&do=fetch', dataType:'json', data: {}, async: false,
		  error:function(x, y, z){
           Notification.show({create:true, hide:true, updateText:true, text:'There was an error while communicating with the server', error:true});
		  },
		  success: function(data){
           ngombePlanner.serverData = data;
        }
       });
    },

    showLoginPad: function(){
       $('#login_div').toggle();
       $('[name=login]').click(ngombePlanner.submitCredentials);
    },

    submitCredentials: function(){
       //get the username and password and create a query to log in to the system
       var login = $('#usernameId').val(), psswd = $('#passwordId').val();
       if(login === '' || login === 'undefined'){
          Notification.show({create: true, hide: true, updateText: false, text: 'Please specify a username to use to login', error:true});
          return;
       }
       if(psswd === '' || psswd === 'undefined'){
          Notification.show({create: true, hide: true, updateText: false, text: 'Please specify a password to use to login', error:true});
          return;
       }
       var params = sprintf('login=%s&psswd=%s', login, psswd);

       //get the list of farmers from the server
       Notification.show({create: true, hide: false, updateText: false, text: 'Validating username and password', error:false});
       $.ajax({
            type:"POST", url:'mod_ajax.php?page=farmersList&do=fetch', dataType:'json', data: params,
            error: ngombePlanner.communicationError,
            success: function(data){
               if(data.error) {
                  Notification.show({create: false, hide: true, updateText: true, text: 'There was an error while communicating with the server', error:true});
                  return;
               }
               else{
                  if(data.error){
                     Notification.show({create: false, hide: true, updateText: true, text: 'Access Denied! Invalid username or password.', error:true});
                     return;
                  }
                  else{
                     Notification.show({create: false, hide: true, updateText: true, text: 'Access granted', error:false});
                     ngombePlanner.reportsData = data;

                     // show the divs to display the summary
                     $('#summary').css({display: 'block'});
                     $('#print').css({display: 'block'});

                     //get the scripts for loading the tree
                     ngombePlanner.treeFiles();
                     //display the interface
                     ngombePlanner.toggleFarmerBrowsingInterface();
                  }
               }
            }
       });
    },

    treeFiles: function(){},

    toggleFarmerBrowsingInterface: function(){
       //hide the main interface
       $('section, #header, #login_div').css({display: 'none' });
       $('farmer_panel').css({ display: 'block'});
       ngombePlanner.initiateFarmersTree();
    },

    /**
     * Create the tree with all the farmers from the diffferent sites
     */
    initiateFarmersTree: function() {

      // prepare the data
      var source = { datatype: "json", datafields: [ {name: 'id'}, {name: 'parentid'}, {name: 'text'} ], id: 'id', localdata: ngombePlanner.reportsData.site_farmers };

      // create data adapter.
      var dataAdapter = new $.jqx.dataAdapter(source);
      // perform Data Binding.
      dataAdapter.dataBind();
      // get the tree items. The first parameter is the item's id. The second parameter is the parent item's id. The 'items' parameter represents
      // the sub items collection name. Each jqxTree item has a 'label' property, but in the JSON data, we have a 'text' field. The last parameter
      // specifies the mapping between the 'text' and 'label' fields.
      var records = dataAdapter.getRecordsHierarchy('id', 'parentid', 'items', [{name: 'text', map: 'label'}]);
      $('#sites_tree').jqxTree({source: records, width: '300px', theme: '', checkboxes: true });
      $('#sites_tree').on('select', function (event) {
          var args = event.args;
          var item = $('#sites_tree').jqxTree('getItem', args.element);
          if(item.parentId == 0) return;
          ngombePlanner.curFarmerId = item.id;
          ngombePlanner.getFarmerData();
       });
    },

    /**
     * Get the selected farmer details
     * @returns {undefined}
     */
    getFarmerData: function(){
        $.ajax({
            type:"POST", url:'mod_ajax.php?page=farmerData&do=fetch', dataType:'json', data: {farmer_id: ngombePlanner.curFarmerId}, async: false,
            error: ngombePlanner.communicationError,
            success: function(data){
               if(data.error) {
                  Notification.show({create: true, hide: true, updateText: false, text: 'There was an error while communicating with the server', error:true});
                  return;
               }
               else{
                  ngombePlanner.curFarmerData = data;
                  ngombePlanner.initiateCowsGrid();
                  ngombePlanner.populateFarmerDetails();
                  ngombePlanner.initiateMilkGraphs();
               }
            }
       });
    },
    /**
     * Initiate te grid that will show the details of the farmers cows
     * @returns {undefined}
     */
    initiateCowsGrid: function() {
      var source = {
         datatype: 'json', localdata: ngombePlanner.curFarmerData.cows,
         datafields: [ {name: 'name'}, {name: 'age'}, {name: 'breed'}, {name: 'totalMilk'}, {name: 'totalRecords'}, {name: 'rating'}, {name: 'cow_rank'}, {name: 'cow_average'}]
      };
      var cowsAdapter = new $.jqx.dataAdapter(source);
      $("#cows").jqxGrid({
         width: 940,
         source: cowsAdapter,
         theme: '',
         autoheight: true,
         columns: [
            {text: 'Cow Name', datafield: 'name', width: 150},
            {text: 'Age (Years)', datafield: 'age', width: 100, cellsalign: 'center'},
            {text: 'Breed', datafield: 'breed', width: 150},
            {text: 'Total Milk (L)', datafield: 'totalMilk', width: 100, cellsalign: 'center'},
            {text: 'No of Records', datafield: 'totalRecords', width: 120, cellsalign: 'center'},
            {text: 'Average', datafield: 'cow_average', width: 100, cellsalign: 'center'},
            {text: 'Rank', datafield: 'cow_rank', width: 150, cellsalign: 'center',
               cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
                  var val2return = '';
                  if(value === 'Not Ranked') val2return = value;
                  else{
                     var stars = '', site_average = parseFloat(ngombePlanner.reportsData.sitesAverage[ngombePlanner.curFarmerData.farmer.location_district]);
                     var site_stddev = parseFloat(ngombePlanner.reportsData.sitesStddev[ngombePlanner.curFarmerData.farmer.location_district]);
                     value = parseFloat(value);

                     if(value === 0) val2return = 'Not Ranked';
                     else if(value < site_average - (2*site_stddev)) val2return = '1 star';
                     else if(value <= site_average - site_stddev) val2return = '2 stars';
                     else if(value <= site_average) val2return = '3 stars';
                     else if(value > site_average - (2*site_stddev)) val2return = '6 stars';
                     else if(value > site_average - site_stddev) val2return = '5 stars';
                     else if(value > site_average) val2return = '4 stars';

                     return "<div class='center'>"+ val2return +"</div>";
                  }
               }
            },
            {text: 'Rating', datafield: 'rating', width: 70, cellsalign: 'center'}
         ]
      });
    },

    /**
     * Initiate the farmers details
     */
    populateFarmerDetails: function(){
       $('#details .farmer_name').html(ngombePlanner.curFarmerData.farmer.name);
       $('#details .enroll_date').html(ngombePlanner.curFarmerData.farmer.date_added);
       $('#details .enrolled_cows').html(ngombePlanner.curFarmerData.cows.length);

       var totalMilkRecords = parseInt(ngombePlanner.curFarmerData.farmer.totalMilkRecords);
       var totalEventsRecords = parseInt(ngombePlanner.curFarmerData.farmer.totalEventsRecords);

       // show the rankings
       $.fn.raty.defaults.path = 'img/';
       $.fn.raty.defaults.readOnly = true;
       $.fn.raty.defaults.number = 10;
       $('#rating').raty({score: parseInt(ngombePlanner.reportsData.farmersByRanking[ngombePlanner.curFarmerId].decile)});

       var records = totalMilkRecords +' Milk, '+ totalEventsRecords + ' Events';

       $('#details .records').html(records);
       $('#details .site_average').html(parseFloat(ngombePlanner.reportsData.sitesAverage[ngombePlanner.curFarmerData.farmer.location_district]).toFixed(2));

       // populate the top 5 farmers
       var top5 = '<table><tr><th>Farmer</th><th>No of Cows</th><th>Records Avg</th><th>Rank</th></tr>';
       $.each(ngombePlanner.reportsData.farmersOrderedByRanking, function(index, farmer){
          top5 += '<tr><td>'+ farmer.farmer_name +"</td><td class='center'>"+ farmer.no_cows +"</td><td class='center'>"+ farmer.av_rec_cow +"</td><td class='center'>"+ farmer.rank +'</td></tr>';
          if(parseInt(index) > 5) return false;   // once we have 5 farmers, exit from the loop
          else return true;
       });
       top5 += '</table>';
       $('#tops').html(top5);

       //populate all records and events
       var eventsTemplate = "<table><tr><th>#</th><th>Cow</th><th>Event Date</th><th>Event</th><th>Amount</th></tr>";
       var right_events = eventsTemplate, left_events = eventsTemplate, eventsCount = ngombePlanner.curFarmerData.allEvents.length;
       $.each(ngombePlanner.curFarmerData.allEvents, function(i, event){
          if(i <= eventsCount/2){
             left_events += sprintf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", i, event.cow_name, event.event_date, event.event, event.amount);
          }
          else{
             right_events += sprintf("<tr><td>%d</td><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", i, event.cow_name, event.event_date, event.event, event.amount);
          }
       });
       right_events += '</table>'; left_events += '</table>';
       var allEvents = sprintf("<table><tr><td>%s</td><td>%s</td></tr></table>", left_events, right_events);
       $('#all-records').html(allEvents);

       var lat = ngombePlanner.curFarmerData.farmer.gps_latitude;
       var longt = ngombePlanner.curFarmerData.farmer.gps_longitude;
       if(lat !== ''){
          $('#map').html("<div><img alt='This sample was collected from Lat:"+ lat +", Long:"+ longt +"' src='http://maps.googleapis.com/maps/api/staticmap?center="+ lat +","+ longt +"&zoom=9&size=300x150&markers=color:blue%7Clabel:S%7C"+ lat +","+ longt +"&sensor=false' /><div>");
       }
       else{
          $('#map').html("<div style='border: 1px dotted black; width: 300px; height: 150px;'>Latitude & Longitude not defined</div>");
       }
    },

    /**
     * Initiate the cows productivity graphs
     */
    initiateMilkGraphs: function(){
       var series = [];
       $.each(ngombePlanner.curFarmerData.cows, function(){
          series[series.length] = { dataField: this.id, displayText: this.name, emptyPointsDisplay: 'connect', lineWidth: 1 };
       });
      // prepare jqxChart settings
      var farmer_settings = {
         title: "Cow Milk Production",
         description: "The recorded milk production per cow",
         enableAnimations: true,
         showLegend: true,
         padding: {left: 10, top: 5, right: 10, bottom: 5},
         titlePadding: {left: 90, top: 0, right: 0, bottom: 10},
         source: ngombePlanner.curFarmerData.milkCurves,
         categoryAxis: {
            dataField: 'date',
            type: 'basic',
            showTickMarks: true,
            tickMarksInterval: 2,
            tickMarksColor: '#888888',
            unitInterval: 4,
            showGridLines: true,
            verticalTextAlignment: 'right',
            gridLinesInterval: 3,
            gridLinesColor: '#888888',
            valuesOnTicks: false
         },
         colorScheme: 'scheme04',
         seriesGroups:[{
            type: 'spline',
            valueAxis:{ unitInterval: 1, minValue: 0, maxValue: ngombePlanner.curFarmerData.maxVal+3, displayValueAxis: true, description: 'Number of Records', axisSize: 'auto', tickMarksColor: '#888888' },
            series: series
         }]
      };

       var grid = $('#curves').children()[0];

       if(grid != undefined){
         // Remove all child nodes of the set of matched elements from the DOM.  This clears out the old chart and its values.
         $('#curves').children().remove();
         // Remove the attribute that gets created by jqxChart
         var attribs = $('#curves').getAttributes();
         for (var key in attribs) {
            if (key.toLowerCase().startsWith('jquery')) {
               $('#curves').removeAttr(key);
               break;
            }
         }
       }
      $('#curves').jqxChart(farmer_settings);
    },

    /**
     * Create a report from the latest usage stats
     * @returns {undefined}
     */
    fetchLatestStats: function(){
        $.ajax({
            type:"POST", url:'mod_ajax.php?page=farmerData&do=stats', dataType:'json', data: {project: 'eadd'}, async: false,
            error: ngombePlanner.communicationError,
            success: function(data){
               if(data.error) {
                  Notification.show({create: true, hide: true, updateText: false, text: 'There was an error while communicating with the server', error:true});
                  return;
               }
               else{
                  ngombePlanner.curStatsData = data;
                  ngombePlanner.initiateReport();
               }
            }
       });
    }
};

$(document).ready(function(){
    $('.slides').each(function(){
        var $bgobj = $(this); // assigning the object
        $(window).scroll(function() {
            var yPos = -($(window).scrollTop() / $bgobj.data('speed'));
            // Put together our final background position
            var coords = '50% '+ yPos + 'px';
            // Move the background
            $bgobj.css({ backgroundPosition: coords });
            if($(window).scrollTop() > 1025){ $('#header').css({ display: 'block' }); }
            else{ $('#header, #login_div').css({display: 'none'}); }
        });
    });
    ngombePlanner.fetchData();
    ngombePlanner.initiateFarmers();
    ngombePlanner.initiateActiveRecords();
    ngombePlanner.initiateEvents_MilkTimes();
    ngombePlanner.initiateUSSD();
    $('.nav .login').on('click', ngombePlanner.showLoginPad);
    $('.latest_stats').on('click', ngombePlanner.fetchLatestStats);

//    function replace(){$(this).after($(this).text()).remove()}

    $('#print').live('click', function(event){
        var print_window = window.open('', 'Printing Current Farmer', 'height=948,width=1050'), print_document = $('#right_panel').clone();
        print_window.document.open();
        print_window.document.write('<html><head><title>'+ ngombePlanner.curFarmerData.farmer.name +' - Records</title>');
        print_window.document.write('<link rel="stylesheet" href="/common/bootstrap/css/bootstrap.css" type="text/css" />');
        print_window.document.write('<link rel="stylesheet" href="/azizi/css/bootstrap.min.css" type="text/css" />');
        print_window.document.write('<link rel="stylesheet" href="css/ngombe_planner.css" type="text/css" />');
        print_window.document.write('<link rel="stylesheet" href="http://fonts.googleapis.com/css?family=Open+Sans:400,800" type="text/css" />');
        print_window.document.write('<link rel="stylesheet" href="/common/jquery/jqwidgets/styles/jqx.base.css" type="text/css" />');
        print_window.document.write(print_document.html());
        print_window.print();
    });

    //testing purposes
//    ngombePlanner.submitCredentials();
});

//enable smooth scrolling
$(function() {
  $('a[href*=#]:not([href=#])').click(function() {
    if (location.pathname.replace(/^\//,'') == this.pathname.replace(/^\//,'') && location.hostname == this.hostname) {
      var target = $(this.hash);
      target = target.length ? target : $('[name=' + this.hash.slice(1) +']');
      if (target.length) {
        $('html,body').animate({ scrollTop: target.offset().top }, 1000);
        return false;
      }
    }
  });
});

(function ($) {
	$.fn.getAttributes = function () {
		var elem = this,
            attr = {};

		if (elem.length) $.each(elem.get(0).attributes, function (v, n) {
			n = n.nodeName || n.name;
			v = elem.attr(n); // relay on $.fn.attr, it makes some filtering and checks
			if (v != undefined && v !== false) attr[n] = v
		})

		return attr
	}
})(jQuery);

String.prototype.startsWith = function(thing){
    return(this.indexOf(thing) == 0);
};
