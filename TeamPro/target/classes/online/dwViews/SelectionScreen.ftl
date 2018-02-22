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
				<a href = "http://localhost:7777/toptrumps"><p style="padding-top:10px; color: white; margin-left: 20px;">Top Trumps Game</p></a>
			</div>
		<div style="margin: auto;
				clear: both;
				background-color: LightGray;
				width:1000px;
				height: 350px;
				background-size: 950px,200px;
				text-align:left;
	            font-size: 20px;">
			<div class="start1" style = "margin: auto; margin-top: 50px; margin-left: 20px;
				width: 45%;
				height:80px;
				background-color: white;
				float: left;
				">
				<!--
            	作者：lyxsusie1226@126.com
            	时间：2018-02-06
            	描述：在这里更换超链接
           		 -->
           		 <a href="http://localhost:7777/toptrumps/game">
           		 <div style="background-color: MediumSeaGreen; height:30px; margin-top: -20px;">
           		 	<p style=" color: white; margin-left: 20px;">New Game</p>
           		 </div>
           		 </a>
				<p style=" color: MediumSeaGreen; margin-left: 20px; font-size: 14px;">start
					a new Top Trump Game</p>
			</div>
			<div class="start2" style = "margin: auto;  margin-top: 50px;  margin-left: 40px;
				width: 45%;
				height: 80px;
				background-color: white;
				float: left;
				">
				<!--
            	作者：lyxsusie1226@126.com
            	时间：2018-02-06
            	描述：在这里更换超链接
           		 -->
           		<div style="background-color: MediumSeaGreen; height:30px; margin-top: -20px;">
           		<a href="http://localhost:7777/toptrumps/stats"><p style=" color: white; margin-left: 20px;">Game Statistics</p></a>
				</div>
				
				<p style=" color: MediumSeaGreen; margin-left: 20px; font-size: 14px;">get statistics from
				past Games</p>
			</div>	
		</div>
			
		
		
		<script type="text/javascript">
		
			// Method that is called on page load
			function initalize() {
			
				// --------------------------------------------------------------------------
				// You can call other methods you want to run when the page first loads here
				// --------------------------------------------------------------------------
				
				// For example, lets call our sample methods
				//helloJSONList();
				//helloWord("Stan");
				
			}
			
			// -----------------------------------------
			// Add your other Javascript methods Here
			// -----------------------------------------
			
			
			
			
			
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
		<script type="text/javascript">
		
			// This calls the helloJSONList REST method from TopTrumpsRESTAPI
			function helloJSONList() {
			
				// First create a CORS request, this is the message we are going to send (a get request in this case)
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/helloJSONList"); // Request type and URL
				
				// Message is not sent yet, but we can check that the browser supports CORS
				if (!xhr) {
  					alert("CORS not supported");
				}

				// CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
				// to do when the response arrives 
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
					alert(responseText); // lets produce an alert
				};
				
				// We have done everything we need to prepare the CORS request, so send it
				xhr.send();		
			}
			
			// This calls the helloJSONList REST method from TopTrumpsRESTAPI
			function helloWord(word) {
			
				// First create a CORS request, this is the message we are going to send (a get request in this case)
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/helloWord?Word="+word); // Request type and URL+parameters
				
				// Message is not sent yet, but we can check that the browser supports CORS
				if (!xhr) {
  					alert("CORS not supported");
				}

				// CORS requests are Asynchronous, i.e. we do not wait for a response, instead we define an action
				// to do when the response arrives 
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
					alert(responseText); // lets produce an alert
				};
				
				// We have done everything we need to prepare the CORS request, so send it
				xhr.send();		
			}

		</script>
		
		</body>
</html>