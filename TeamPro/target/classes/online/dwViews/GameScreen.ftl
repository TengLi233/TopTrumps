<html>

	<head>
		<meta charset="UTF-8">
		<!-- Web page title -->
    	<title>Top Trumps</title>
    	
    	<!-- Import JQuery, as it provides functions you will probably find useful (see https://jquery.com/) -->
    	<script src="https://code.jquery.com/jquery-2.1.1.js"></script>
    	<script src="https://code.jquery.com/ui/1.11.1/jquery-ui.js"></script>
    	

	</head>

    <body onload="initalize();" style = "background-color: LightGray;"> <!-- Call the initalize method when the page loads -->
    	
    	<div class="top"  style="margin: auto;
				clear: both;
				background-color: rgb(82,82,82);
				width: 1000px;
				height: 50px;
				background-size: 950px,200px;
				text-align:left;
	            font-size: 20px;">
				<a href = "http://localhost:7777/toptrumps"><p style="padding-top:10px; color: white; margin-left: 20px;">Top Trumps Game</p></a>
		</div>
		<div   style="margin: auto;
				clear: both;
				background-color: DodgerBlue;
				width: 1000px;
				height: 50px;
				background-size: 950px,200px;
				text-align:left;
	            font-size: 20px;">
				<p id="topMsg" style="padding-top:10px; color: white; margin-left: 20px;">Please select the number of players in this game.</p>
			</div>
		<div style="margin: auto;
				clear: both;
				background-color: LightGray;
				width: 1000px;
				height: 800px;
				background-size: 950px,200px;
				text-align:left;
	            font-size: 20px;">
	        
	        <div id = "leftWindow" style = "height:300px; width: 20%; float: left;">    
				
			</div>
			
			<div id = "window" style = "width: 80%; float: left;">
			
			</div>
		</div>
		
		<script type="text/javascript">
		
			// Method that is called on page load
			function initalize() {
			
				// --------------------------------------------------------------------------
				// You can call other methods you want to run when the page first loads here
				// --------------------------------------------------------------------------
								
				selectButton();
				
			}
			
			// -----------------------------------------
			// Add your other Javascript methods Here
			// -----------------------------------------
			
			function selectButton() {
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/selectButton");
				if (!xhr) {
	  				alert("CORS not supported");
				}
				xhr.onload = function(e) {
	 					var responseText = xhr.response; 
						document.getElementById("leftWindow").innerHTML = responseText; 
				};
				xhr.send();
			}
			
			function startGame(number){
				var playerNum = number;
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/startGame?playerNum="+playerNum);
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
					firstMsg();
				};
				
				xhr.send();	
			}
			
			function firstMsg(){
				
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/firstMsg");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("topMsg").innerHTML = responseText; // lets produce an alert
 					showUserCard();
				};
				
				xhr.send();	
			}
					
			function showUserCard(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/showUserCard");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("window").innerHTML = responseText; // lets produce an alert
 					
 					firstButton();
				};
				
				xhr.send();	
			}
			
			function firstButton(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/firstButton");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("leftWindow").innerHTML = responseText; // lets produce an alert
				};
				
				xhr.send();	
			}
			
			function waitMsg(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/waitMsg");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("topMsg").innerHTML = responseText; // lets produce an alert
 					userSelectButton();
				};
				
				xhr.send();	
			}
			
			function userSelectButton(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/userSelectButton");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("leftWindow").innerHTML = responseText; // lets produce an alert
				};
				
				xhr.send();	
			}
			
			function cardCompare(attributeKey){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/cardCompare?attributeKey="+attributeKey);
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					secondMsg();
				};
				
				xhr.send();	
			}
			
			function secondMsg(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/secondMsg");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
					var responseText = xhr.response; // the text of the response
 					document.getElementById("topMsg").innerHTML = responseText; // lets produce an alert
 					showAllCards();
				};
				
				xhr.send();	
			}
						
			function showAllCards(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/showAllCards");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("window").innerHTML = responseText; // lets produce an alert
 					secondButton();
				};
				
				xhr.send();	
			}
			
			function secondButton(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/secondButton");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
 					var responseText = xhr.response; // the text of the response
 					document.getElementById("leftWindow").innerHTML = responseText; // lets produce an alert
				};
				
				xhr.send();	
			}
			
			function thirdMsg(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/thirdMsg");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
					var responseText = xhr.response; // the text of the response
 					document.getElementById("topMsg").innerHTML = responseText; // lets produce an alert
 					thirdButton();
				};
				
				xhr.send();	
			}
			
			function thirdButton(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/thirdButton");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
					var responseText = xhr.response; // the text of the response
 					document.getElementById("leftWindow").innerHTML = responseText; // lets produce an alert
				};
				
				xhr.send();	
			}
			
			function endMsg(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/endMsg");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
					var responseText = xhr.response; // the text of the response
 					document.getElementById("topMsg").innerHTML = responseText; // lets produce an alert
 					endButton();
				};
				
				xhr.send();	
			}
			
			function endButton(){
				var xhr = createCORSRequest('GET', "http://localhost:7777/toptrumps/endButton");
				
				if (!xhr) {
  					alert("CORS not supported");
				}
				
				xhr.onload = function(e) {
					var responseText = xhr.response; // the text of the response
 					document.getElementById("leftWindow").innerHTML = responseText; // lets produce an alert
				};
				
				xhr.send();	
			}

			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
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