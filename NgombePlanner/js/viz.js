
function Vizualization(){
   window.viz = this;

   // initialize the main variables
//   window.viz.sub_module = Common.getVariable('do', document.location.search.substring(1));
//   window.viz.module = Common.getVariable('page', document.location.search.substring(1));

   window.viz.graphs = [
      {module: 'graphs', sub_module: 'users', tab_name: 'users'},
      {module: 'graphs', sub_module: 'milk_prod', tab_name: 'milk_prod'},
      {module: 'graphs', sub_module: 'milk_usage', tab_name: 'milk_usage'},
      {module: 'graphs', sub_module: 'milk_buyers', tab_name: 'milk_buyers'},
      {module: 'graphs', sub_module: 'watering', tab_name: 'watering'},
      {module: 'graphs', sub_module: 'feeding', tab_name: 'feeding'}
   ];
};

Vizualization.prototype.initiateVizTabs = function(){
    $('#tabs').jqxTabs({ width: '90%', height: '95%'});
    if(viz.curTabIndex === viz.graphs.length-1){
       viz.curTabIndex = 0;
    }
    else{
       viz.curTabIndex++;
    }
    viz.curTab = viz.graphs[viz.curTabIndex];
    viz.updateGraphs();
    setTimeout(viz.initiateVizTabs, 10000);
};

Vizualization.prototype.updateGraphs = function(){
   $.ajax({
      // get the stats for this tab
       type:"POST", url: 'mod_ajax.php?page='+viz.curTab.module+'&do='+viz.curTab.sub_module, async: false, dataType:'json',
       success: function (data) {
          if(data.error === true){
             $('#animal_id').val('').focus();
             return;
          }
          else {
             $('#tabs').jqxTabs('select', viz.curTabIndex);
             if(viz.curTab.tab_name === 'users') viz.initiateUserPie(viz.curTab.tab_name, data.users);
             else if(viz.curTab.tab_name === 'milk_prod') viz.initiateMilkProd(viz.curTab.tab_name, data.milk);
             else if(viz.curTab.tab_name === 'milk_usage') viz.initiateMilkUsage(viz.curTab.tab_name, data.milk_usage);
             else if(viz.curTab.tab_name === 'milk_buyers') viz.initiateSellingRegimes(viz.curTab.tab_name, data.milk_selling);
             else if(viz.curTab.tab_name === 'watering') viz.initiateWaterIntake(viz.curTab.tab_name, data.water);
             else if(viz.curTab.tab_name === 'feeding') viz.initiateFeedIntake(viz.curTab.tab_name, data.feed);
          }
      }
   });
};

Vizualization.prototype.initiateWaterIntake = function(tabName, data){
   var settings = {
       title: "Animal Water Intake",
       description: "The watering regimes for the cattle",
       enableAnimations: true,
       showLegend: true,
       showBorderLine: true,
       legendLayout: { height: 200, flow: 'horizontal' },
       padding: { left: 5, top: 5, right: 5, bottom: 5 },
       titlePadding: { left: 0, top: 0, right: 0, bottom: 10 },
       source: data,
       colorScheme: 'scheme01',
       xAxis: {
           dataField: 'project',
           tickMarks: { visible: true, interval: 1 },
           gridLines: { visible: true, interval: 1 },
           flip: false,
           valuesOnTicks: false
       },
       seriesGroups: [{
          type: 'column',
          showLabels: true,
          skipOverlappingPoints: false,
          valueAxis:{ unitInterval: 10, title: {text: 'Water'}},
          series: [
             { dataField: 'Throughout the day', displayText: 'Throughout the day' },
             { dataField: 'Twice or more', displayText: 'Twice or more' },
             { dataField: 'Once', displayText: 'Once' },
             { dataField: 'Not provided', displayText: 'Not provided' }
          ]
      }]
   };

   // setup the chart
   $('#'+tabName).jqxChart(settings);
};

Vizualization.prototype.initiateFeedIntake = function(tabName, data){
   var settings = {
       title: "Animal Fodder Crops",
       description: "The fodder being given to the the cattle",
       enableAnimations: true,
       showLegend: true,
       showBorderLine: true,
       legendLayout: { height: 200, flow: 'horizontal' },
       padding: { left: 5, top: 5, right: 5, bottom: 5 },
       titlePadding: { left: 0, top: 0, right: 0, bottom: 10 },
       source: data,
       colorScheme: 'scheme01',
       xAxis: {
           dataField: 'project',
           tickMarks: { visible: true, interval: 1 },
           gridLines: { visible: true, interval: 1 },
           flip: false,
           valuesOnTicks: false
       },
       seriesGroups: [{
          type: 'column',
          showLabels: true,
          skipOverlappingPoints: false,
          valueAxis:{ unitInterval: 10, title: {text: 'Fodder'}},
          series: [
             { dataField: 'Concentrates', displayText: 'Concentrates' },
             { dataField: 'Hay/Dry crop residue', displayText: 'Hay/Dry Crop Residue' },
             { dataField: 'Legumes', displayText: 'Legumes' },
             { dataField: 'Mineral licks', displayText: 'Mineral Licks' },
             { dataField: 'Napier/Green crop residue', displayText: 'Napier/Green Crop Residue' },
             { dataField: 'Natural grass', displayText: 'Natural Grass' },
             { dataField: 'Silage', displayText: 'Silage' }
          ]
      }]
   };

   // setup the chart
   $('#'+tabName).jqxChart(settings);
};

Vizualization.prototype.initiateMilkUsage = function(tabName, data){
   var settings = {
       title: "Milk Usage in the Household",
       description: "How the milk is used in the household",
       enableAnimations: true,
       showLegend: true,
       showBorderLine: true,
       legendLayout: { height: 200, flow: 'horizontal' },
       padding: { left: 5, top: 5, right: 5, bottom: 5 },
       titlePadding: { left: 0, top: 0, right: 0, bottom: 10 },
       source: data,
       colorScheme: 'scheme01',
       xAxis: {
           dataField: 'project',
           tickMarks: { visible: true, interval: 1 },
           gridLines: { visible: true, interval: 1 },
           flip: false,
           valuesOnTicks: false
       },
       seriesGroups: [{
          type: 'column',
          showLabels: true,
          skipOverlappingPoints: false,
          valueAxis: {unitInterval: 10, title: {text: 'Milk Sold'}},
          series: [
             { dataField: 'Sale', displayText: 'Sale' },
             { dataField: 'Consumed', displayText: 'Consumed' },
             { dataField: 'Reserved', displayText: 'Reserved' }
          ]
      }]
   };

   // setup the chart
   $('#'+tabName).jqxChart(settings);
};

Vizualization.prototype.initiateSellingRegimes = function(tabName, data){
   var settings = {
       title: "Milk Buyers",
       description: "To who does the farmer sell their milk to?",
       enableAnimations: true,
       showLegend: true,
       showBorderLine: true,
       legendLayout: { height: 200, flow: 'horizontal' },
       padding: { left: 5, top: 5, right: 5, bottom: 5 },
       titlePadding: { left: 0, top: 0, right: 0, bottom: 10 },
       source: data,
       colorScheme: 'scheme01',
       xAxis: {
           dataField: 'project',
           tickMarks: { visible: true, interval: 1 },
           gridLines: { visible: true, interval: 1 },
           flip: false,
           valuesOnTicks: false
       },
       seriesGroups: [{
          type: 'column',
          showLabels: true,
          skipOverlappingPoints: false,
          valueAxis:{ unitInterval: 10, title: {text: 'Fodder'}},
          series: [
             { dataField: 'EADD HUB/PO', displayText: 'Sale' },
             { dataField: 'Individual customer', displayText: 'Individual Customer' },
             { dataField: 'Private milk trader', displayText: 'Private Milk Trader' },
             { dataField: 'processor', displayText: 'Processor' }
          ]
      }]
   };

   // setup the chart
   $('#'+tabName).jqxChart(settings);
};

Vizualization.prototype.initiateMilkProd = function(tabName, data){
   var settings = {
       title: "Average Farmer Milk Production",
       description: "The average milk production per cow",
       enableAnimations: true,
       showLegend: true,
       showBorderLine: true,
       legendLayout: { height: 200, flow: 'horizontal' },
       padding: { left: 5, top: 5, right: 5, bottom: 5 },
       titlePadding: { left: 0, top: 0, right: 0, bottom: 10 },
       source: data,
       colorScheme: 'scheme01',
       xAxis: {
           dataField: 'project',
           tickMarks: { visible: true, interval: 1 },
           gridLines: { visible: true, interval: 1 },
           flip: false,
           valuesOnTicks: false
       },
       seriesGroups: [{
          type: 'column',
          showLabels: true,
          skipOverlappingPoints: false,
          valueAxis:{ unitInterval: 10, title: {text: 'Milk Production'}},
          series: [
             { dataField: 'Morning', displayText: 'Morning Milk' },
             { dataField: 'Afternoon', displayText: 'Afternoon Milk' },
             { dataField: 'Evening', displayText: 'Evening Milk' }
          ]
      }]
   };

   // setup the chart
   $('#'+tabName).jqxChart(settings);
};

Vizualization.prototype.initiateUserPie = function(tabName, data){
   var settings = {
       title: "Farmers Enrolled in Ngombe Planner",
       description: "The number of farmers who have used Ngombe Planner",
       enableAnimations: true,
       showLegend: true,
       showBorderLine: true,
       legendLayout: { height: 200, flow: 'horizontal' },
       padding: { left: 5, top: 5, right: 5, bottom: 5 },
       titlePadding: { left: 0, top: 0, right: 0, bottom: 10 },
       source: data,
       colorScheme: 'scheme01',
       seriesGroups: [{
          type: 'pie',
          showLabels: true,
          series: [{
             dataField: 'count',
             displayText: 'project',
             labelRadius: 170,
             initialAngle: 15,
             radius: $('#tabs')[0].offsetHeight*0.40,
             centerOffset: 0,
             formatFunction: function (value) {
                 if (isNaN(value))
                     return value;
                 return parseFloat(value);
             }
         }]
      }]
   };

   // setup the chart
   $('#'+tabName).jqxChart(settings);

};

var viz = new Vizualization();
viz.curTabIndex = -1;
viz.initiateVizTabs();
