<html>

	<head>
		<meta charset="UTF-8">
		<!-- Web page title -->
    	<title>Top Trumps</title>
    	
    	<!-- Import JQuery, as it provides functions you will probably find useful (see https://jquery.com/) -->
    	<script src="https://code.jquery.com/jquery-2.1.1.js"></script>
    	<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.js"></script>
    	

	</head>

    <body onload="initalize()" style = "background-color: LightGray;"> <!-- Call the initalize method when the page loads -->
    	
    	<div class="top"  style="margin: auto;
				clear: both;
				background-color: rgb(82,82,82);
				width:1000px;
				height: 50px;
				background-size: 950px,200px;
				text-align:left;
	            font-size: 20px;">
				<a href = "http://localhost:7777/toptrumps"><p style="padding-top:10px; color: white; margin-left: 20px;">Home Page</p></a>
		</div>
		<div style="margin: auto;
				clear: both;
				background-color: LightGray;
				width:1000px;
				height: 350px;
				background-size: 950px,200px;
				text-align:left;
	            font-size: 20px;">
			<div class="start1" style = "margin: auto; margin-top: 50px; margin-left: 200px;
				width: 50%;
				height: 150px;
				background-color: white;
				float: left;
				">
				<!--
            	作者：lyxsusie1226@126.com
            	时间：2018-02-06
            	描述：在这里更换超链接
           		 -->
           		
           		 <div style="background-color: MediumSeaGreen; height:30px; margin-top: -20px;">
           		 	<p style=" color: white; margin-left: 20px;">Game Statistic</p>
           		 </div>
           		<div id = "demo" class="container" style=" color: black; padding-top: 20px; margin-left: 20px; font-size: 14px;">
				
				</div>
				
			</div>
			
		</div>
    	

		
	
			
			
		
		
		<script type="text/javascript">
		
			// Method that is called on page load
			function initalize() {
			
				// --------------------------------------------------------------------------
				// You can call other methods you want to run when the page first loads here
				// --------------------------------------------------------------------------
				history();
				
				
			}
			
			// -----------------------------------------
			// Add your other Javascript methods Here
			// -----------------------------------------
			
			function history(){
				document.getElementById("demo").innerHTML = "Trying to update";
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/history");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("demo").innerHTML = responseText;
				};
				
				xhr.send();	
			}
			
			
			
			
			// This is a reusable method for creating a CORS request. Do not edit this.
			function createCORSRequest(method, url) {
  				var xhr = new XMLHttpRequest();
  				if ("withCredentials" in xhr) {

    				// Check if the XMLHttpRequest object has a "withCredentials" property.
    				// "withCredentials" only exists on XMLHTTPRequest2 objects.
    				xhr.open(method, url, true);

  				} else if (typeof XDomainRequest != "undefined") {

    				// Otherwise, check if XDomainRequest.
    				// XDomainRequest only exists in IE, and is IE's way of making CORS requests.
    				xhr = new XDomainRequest();
    				xhr.open(method, url);

 				 } else {

    				// Otherwise, CORS is not supported by the browser.
    				xhr = null;

  				 }
  				 return xhr;
			}
		
		</script>
		
		<!-- Here are examples of how to call REST API Methods -->
		
		
		</body>
</html>