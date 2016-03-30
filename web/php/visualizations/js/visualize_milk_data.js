function VisMilkData(){
   //constructor
   window.visMilkData = this;
      
   this.allFarmerData = this.getMilkData();
   //console.log(this.allFarmerData);
   $("#refresh").click(function(){
                  window.visMilkData.allFarmerData = window.visMilkData.getMilkData();
                  window.visMilkData.visualize();
               });
  
   //add farmers to farmer select 
   for(var i = 0; i < this.allFarmerData.length; i++){
      $("#farmerList").append("<option value='"+this.allFarmerData[i].id+"'>"+this.allFarmerData[i].name+"</option>");
   }
   
   $("#farmerList").change(function(){
                     window.visMilkData.refreshCowList();
                     window.visMilkData.visualize();
                  });
   $("#cowList").change(function(){
                  window.visMilkData.visualize();
               });  
   
   this.refreshCowList();
   this.visualize(); 
}

/**
 * Called whenever we want to refresh graph
 */
VisMilkData.prototype.visualize = function(){
   //console.log("visualize called");
   var graphLabels = new Array("Date","Morning", "Afternoon", "Evening", "Combined");
   var data = new Array();
   
   var selectedFI = $("#farmerList")[0].selectedIndex;//selected farmer index
   var selectedFData = window.visMilkData.allFarmerData[selectedFI];
   
   //get milk readings for selected cow [xn, y1n]
   var selectedCI = $("#cowList")[0].selectedIndex;//selected cow index
   var selectedCowData = selectedFData.cows[selectedCI];
   //console.log(selectedCowData);
   
   if(selectedCowData != null){

	   var milkData = new Array();
	   if(selectedCowData != null)//will be null if no cows available
	      milkData = selectedCowData.milk_production;
	   var prevTime = 0;
	   for(var i = 0; i < milkData.length; i++){
	      var date = new Date(milkData[i].date);
	      //TODO: consider quantity type
	      var morning = parseInt(milkData[i].Morning);
	      var afternoon = parseInt(milkData[i].Afternoon);
	      var evening = parseInt(milkData[i].Evening);
	      var combined = parseInt(milkData[i].Combined);
	      if(combined === 0)
	         combined = morning + afternoon + evening;
	      
	      //merge sort this b****
	      if(data.length === 0){
	         data.push(new Array(date, morning, afternoon, evening, combined));
	      }
	      else{
	         for(var j = 0; j < data.length; j++){
	            if(date.getTime() < data[j][0].getTime()){
	               data.splice(j, 0, new Array(date, morning, afternoon, evening, combined));
	               break;
	            }
	            else if(j === (data.length - 1)){//compared with the last element in data
	               data.push(new Array(date, morning, afternoon, evening, combined));
	               break;
	            }
	         }
	      }
	   }
	   console.log(data);
	
	   var births = new Array();
	   for(var i = 0; i < selectedCowData.births.length; i++){
	      births.push(new Date(selectedCowData.births[i].event_date));
	   }
	   
	   g = new Dygraph(
	            document.getElementById("div_g"), 
	            data,
	            {
	               title: "Milk production for " + selectedCowData.ear_tag_number + " (" + selectedCowData.name + ")",
	               ylabel: "Milk quantity (Litres)",
	               xlabel: "Date",
	               labels: graphLabels,
	               rollPeriod: 10,
	               showRoller: true,
	               drawPoints: true,
	               underlayCallback: function(canvas, area, g){
	                  function highlightArea(xStart, xEnd){
	                     console.log("xstart = "+xStart+" xEnd = "+xEnd);
	                     var canvasLeftX = g.toDomXCoord(xStart);
	                     var canvasRightX = g.toDomXCoord(xEnd);
	                     var canvasWidth = canvasRightX - canvasLeftX;
	                     canvas.fillRect(canvasLeftX, area.y, canvasWidth, area.h);
	                     console.log("filling canvas from "+canvasLeftX+" with area "+canvasWidth);
	                  }
	                  
                  for(var i = 0; i < births.length; i++){
                     var dayBBirth = new Date(births[i].getTime() - 43200000);
                     var dayABirth = new Date(births[i].getTime() + 43200000);
                     
                     highlightArea(dayBBirth, dayABirth);
                  }
               }
            }
         );
      }
      else{
         $('#div_g').empty();
      }
};
VisMilkData.prototype.getMilkData = function(){
   var responseString = $.ajax({
                           type: "POST",
                           url: "modules/mod_get_milk_data.php",
                           async: false
                        }).responseText;
   return $.parseJSON( responseString); 
};
VisMilkData.prototype.refreshCowList = function(){
   var selectedFI = $("#farmerList")[0].selectedIndex;
   var cowList = window.visMilkData.allFarmerData[selectedFI].cows;
   $("#cowList").empty();
   for(var i = 0; i < cowList.length; i++){
      $("#cowList").append("<option value='"+cowList[i].id+"'>"+cowList[i].ear_tag_number+"</option>"); 
   }
};
