package online.dwResources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import commandline.function.CMDDatabase;
import commandline.function.CMDLog;
import commandline.function.CMDStatistic;
import commandline.vo.CMDGameModelCard;
import commandline.vo.CMDGameModelPlayer;
import online.configuration.TopTrumpsJSONConfiguration;

@Path("/toptrumps") // Resources specified here should be hosted at http://localhost:7777/toptrumps
@Produces(MediaType.APPLICATION_JSON) // This resource returns JSON content
@Consumes(MediaType.APPLICATION_JSON) // This resource can take JSON content as input
/**
 * This is a Dropwizard Resource that specifies what to provide when a user
 * requests a particular URL. In this case, the URLs are associated to the
 * different REST API methods that you will need to expose the game commands
 * to the Web page.
 * 
 * Below are provided some sample methods that illustrate how to create
 * REST API methods in Dropwizard. You will need to replace these with
 * methods that allow a TopTrumps game to be controled from a Web page.
 */

//selectButton() -> startGame(playNum) -> firstMsg() -> showUserCard() -> firstButton() -> ((waitMsg)userMsg)compareCard(attributeKey) -> 
//Input attribute-> topMsg() -> showAllCards() -> secondButton() ->
public class TopTrumpsRESTAPI{
	private boolean beginFlag;

	/** A Jackson Object writer. It allows us to turn Java objects
	 * into JSON strings easily. */
	ObjectWriter oWriter = new ObjectMapper().writerWithDefaultPrettyPrinter();
	
	/**
	 * database and statistic function objects.
	 */
	private CMDDatabase Db;
	private CMDStatistic StatsFunc;
	private CMDLog testLog;
	private OnlineTopTrumps gameController; 
	private int roundCnt;
	private int stepCnt;
	private int playerNum;
	private  CMDGameModelPlayer[] originPlayers;
	
	/**
	 * Contructor method for the REST API. This is called first. It provides
	 * a TopTrumpsJSONConfiguration from which you can get the location of
	 * the deck file and the number of AI players.
	 * @param conf
	 */
	public TopTrumpsRESTAPI(TopTrumpsJSONConfiguration conf) {
		// ----------------------------------------------------
		// Add relevant initalization here
		// running when program is staring
		// ----------------------------------------------------
		this.Db = new CMDDatabase();
		this.StatsFunc = new CMDStatistic(Db);
		this.testLog = new CMDLog();
		this.beginFlag = false;
		
	}
	
	// ----------------------------------------------------
	// Add relevant API methods here
	// ----------------------------------------------------
	
	@GET
	@Path("/startGame")
	public String startGame(@QueryParam("playerNum") String playerNum) throws IOException {
		System.err.println("Hello "+playerNum);
//		return "nidaye "+playerNum;
		int players = Integer.parseInt(playerNum);
		gameController = new OnlineTopTrumps(Db, players);
		this.playerNum = players;
		this.originPlayers = new CMDGameModelPlayer[this.gameController.getPlayers().size()];
		for(int i = 0; i < this.gameController.getPlayers().size(); i++)
		{
			this.originPlayers[i] = (CMDGameModelPlayer) this.gameController.getPlayers().get(i);
		}
		System.out.println(this.gameController.getPlayers().size() + "................");
		return " ";
	}
	
	@GET
	@Path("/history")
	public String updateStats() throws IOException {
		String msg = this.StatsFunc.get();
		return msg;
	}
	
	
	@GET
	@Path("/showAllCards")
	public String showAllCards() throws IOException {
		String msg = "";
		for(int i = 0; i < this.gameController.getPlayers().size(); i++) {
			int id = this.gameController.getPlayers().get(i).getID();
			int numOfCard = this.gameController.getPlayers().get(i).size();
			String player;
			if (id == OnlineTopTrumps.USER) {
				player = "You";
			}
			else {
				player = "AI Player " + id;
			}
			
			
			CMDGameModelCard card = this.gameController.getCardsPerRound().get(i);
			msg += "<div id = \"player "+ id +"\" style = \"margin: auto;  margin-top: 50px; color: black;  margin-left: 2%;\n" + 
					"				width: 25%;\n" + 
					"				height: 300px;\n" + 
					"				background-color: white;\n" + 
					"				float: left;\n" + 
					"				\">\n" + 
					"				\n" + 
					"           		<div style=\"background-color: "+ (id == this.gameController.getLastWinner() ? "Tomato" : "#99CC33") +  "; color: white; height:30px; margin-top: -20px;\">\n" + 
										 player +"(" + numOfCard + ") \n" + 
					"				</div>\n" + 
					"				<div> <img src=\"http://dcs.gla.ac.uk/~richardm/TopTrumps/" + card.getDescription() + ".jpg\" width=200 height=130/></div>\n" +
					"				<div>\n" + "Description  "+ card.getDescription()  +"</br>Size  "+ card.getSize() +"</br>Speed  "+ card.getSpeed() +"</br>Range  "+ card.getRange() +"</br>Firepower  "+ card.getFirepower() +"</br>Cargo  "+ card.getCargo() +"</br>" +
					"				</div>\n" + 
					"			</div>\n";
		}
		return msg;
	}
	
	@GET
	@Path("/showUserCard")
	public String showUserCard() throws IOException {
		String msg = "";
		this.gameController.prepared();
		if(this.gameController.isUserOut()) {
			msg += "<div id = \"player\" style = \"margin: auto; margin-top: 50px; margin-left: 2%; color: rgb(255, 56, 15)\"> You are out of card!</div>\n";
		} else {
			CMDGameModelCard card = this.gameController.getUserCard();
			int id = OnlineTopTrumps.USER;
			msg += "<div id = \"player "+ id +"\"  style = \"margin: auto;  margin-top: 50px; color: black; margin-left: 2%;\n" + 
						"				width: 25%;\n" + 
						"				height: 300px;\n" + 
						"				background-color: white;\n" + 
						"				float: left;\n" + 
						"				\">\n" + 
						"				\n" + 
						"           		<div style=\"background-color: "+ (this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "Tomato" : "#99CC33") + "; color: white; height:30px; margin-top: -20px;\">\n" + 
						"           			You(" + this.gameController.getPlayers().get(OnlineTopTrumps.USER).size() + ")\n" + 
						"				</div>\n" + 
						"				<div> <img src=\"http://dcs.gla.ac.uk/~richardm/TopTrumps/" + card.getDescription() + ".jpg\" width=200 height=130/></div>\n" +
						"				<div>\n" + "Description  "+ card.getDescription()  +"</br>Size  "+ card.getSize() +"</br>Speed  "+ card.getSpeed() +"</br>Range  "+ card.getRange() +"</br>Firepower  "+ card.getFirepower() +"</br>Cargo  "+ card.getCargo() +"</br>" +
						"				</div>\n" + 
						"			</div>\n";
		}
		return msg;
	}
	
	@GET
	@Path("/updates")
	public String updates() throws IOException {
		String msg = this.StatsFunc.get();
		return msg;
	}
	
	@GET
	@Path("/selectButton")
	public String selectButton() throws IOException {
		String msg = "";
			msg = "<div style = \"margin: auto; margin-top: 30px; margin-left: 2%;\n" + 
					"					width: 100%;\n" + 
					"					height: 230px;\n" + 
					"					background-color: white;\n" + 
					"					float: left;\n" + 
					"					\">\n" + 
					"	           		\n" + 
					"	           		 <div style=\"background-color: DodgerBlue; height:70px; margin-top: -20px;\">\n" + 
					"	           		 	<p style=\" color: white; margin-left: 20px;\">Player Number Select</p>\n" + 
					"	           		 </div>\n" + 
					"					<div id = \"panelContent\">\n" + 
					"						<button type = \"button\" onclick=\"startGame(2);\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">2 Players</button>\n" + 
					"						<button type = \"button\" onclick=\"startGame(3);\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">3 Players</button>\n" + 
					"						<button type = \"button\" onclick=\"startGame(4);\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">4 Players</button>\n" + 
					"						<button type = \"button\" onclick=\"startGame(5);\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">5 Players</button>\n" + 
					"					</div>\n" + 
					"				</div>";
		return msg;
	}
	
	@GET
	@Path("/firstButton")
	public String firstButton() throws IOException {
		String msg = "";
		int id = this.gameController.getLastWinner();
		msg = "<div style = \"margin: auto; margin-top: 30px; margin-left: 2%;\n" + 
				"					width: 100%;\n" + 
				"					height: 110px;\n" + 
				"					background-color: white;\n" + 
				"					float: left;\n" + 
				"					\">\n" + 
				"	           		\n" + 
				"	           		 <div style=\"background-color: DodgerBlue; height:70px; margin-top: -20px;\">\n" + 
				"	           		 	<p style=\" color: white; margin-left: 20px;\">The active player is " + (id == OnlineTopTrumps.USER? "You" : "AI Player " + id) + "</p>\n" + 
				"	           		 </div>\n" + 
				"					<div id = \"panelContent\">\n" + 
				"						<button type = \"button\" onclick=\"" + (id == OnlineTopTrumps.USER ? "waitMsg()" : "cardCompare(5)") + " ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">NEXT: CATEGORY SELECTION</button>\n" + 
				"					</div>\n" + 
				"				</div>";
	return msg;
	}
	
	@GET
	@Path("/waitMsg")
	public String waitMsg() {
		String msg = "Waiting on you to select a category.";
		
		return msg;
	}
	
	@GET
	@Path("/userSelectButton")
	public String userSelectButton() {
		String msg = "";
		msg = "<div style = \"margin: auto; margin-top: 30px; margin-left: 2%;\n" + 
					"					width: 100%;\n" + 
					"					height: 270px;\n" + 
					"					background-color: white;\n" + 
					"					float: left;\n" + 
					"					\">\n" + 
					"	           		\n" + 
					"	           		 <div style=\"background-color: DodgerBlue; height:70px; margin-top: -20px;\">\n" + 
					"	           		 	<p style=\" color: white; margin-left: 20px;\">You Select</p>\n" + 
					"	           		 </div>\n" + 
					"					<div id = \"panelContent\">\n" + 
					"						<button type = \"button\" onclick=\"cardCompare(0) ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">Size</button></br>\n" + 
					"						<button type = \"button\" onclick=\"cardCompare(1) ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">Speed</button></br>\n" +
					"						<button type = \"button\" onclick=\"cardCompare(2) ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">Range</button></br>\n" +
					"						<button type = \"button\" onclick=\"cardCompare(3) ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">Firepower</button></br>\n" +
					"						<button type = \"button\" onclick=\"cardCompare(4) ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">Cargo</button></br>\n" +
					"					</div>\n" + 
					"				</div>";
		System.err.println("xixixixixixiixix");
		return msg;
	}
	
	@GET
	@Path("/cardCompare")
	public String cardCompare(@QueryParam("attributeKey") String attributeKey) throws IOException {
		int key = Integer.parseInt(attributeKey);
		CMDGameModelCard temp = this.gameController.getCurrentPlayerCard();

		int max = 0;
		for(int i = 0; i < 5 ; i++) {
			if(temp.getAttribute(i) >= temp.getAttribute(max)) {
				max = i;
			}
		}
		
		if(key == 5)
		{
			this.gameController.setAttributeKey(max);
		} else {
			this.gameController.setAttributeKey(key);
		}
		
		switch(this.gameController.getAttributeKey()) {
			case 0: this.gameController.setCurrentAttribute("Size"); break;
			case 1: this.gameController.setCurrentAttribute("Speed"); break;
			case 2: this.gameController.setCurrentAttribute("Range"); break;
			case 3: this.gameController.setCurrentAttribute("Firepower"); break;
			case 4: this.gameController.setCurrentAttribute("Cargo"); break;
		}
		
		return " ";
	}
	
	@GET
	@Path("/secondButton")
	public String secondButton() throws IOException {
		String msg = "";
		int id = this.gameController.getLastWinner();
		this.gameController.result();
		
		msg = "<div style = \"margin: auto; margin-top: 30px; margin-left: 2%;\n" + 
				"					width: 100%;\n" + 
				"					height: 160px;\n" + 
				"					background-color: white;\n" + 
				"					float: left;\n" + 
				"					\">\n" + 	
				"	           		\n" + 
				"	           		 <div style=\"background-color: DodgerBlue; height:70px; margin-top: -20px;\">\n" + 
				"	           		 	<p style=\" color: white; margin-left: 20px;\">The active player is " + (id == OnlineTopTrumps.USER? "You" : "AI Player " + id) + "</p>\n" + 
				"	           		 </div>\n" + 
				"					<div id = \"panelContent\">\n" + 
				"						<p>" + (id == OnlineTopTrumps.USER? "You" : "They") + " selected " + this.gameController.getCurrentAttribute() + "</p>"+
				"						<button type = \"button\" onclick=\"" + (this.gameController.isEnd() ? " endMsg() " : " thirdMsg() ") + " ;\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">SHOW WINNER</button>\n" + 
				"					</div>\n" + 
				"				</div>";
	return msg;
	}
	
	@GET
	@Path("/thirdButton")
	public String thirdButton() {
		String msg = "";
		int id = this.gameController.getLastWinner();
		this.gameController.setRoundCnt(this.gameController.getRoundCnt() + 1);
		msg = "<div style = \"margin: auto; margin-top: 30px; margin-left: 2%;\n" + 
				"					width: 100%;\n" + 
				"					height: 40px;\n" + 
				"					background-color: white;\n" + 
				"					float: left;\n" + 
				"					\">\n" + 
				"	           		\n" + 
				"					<div id = \"panelContent\">\n" + 
				"						<button type = \"button\" onclick=\"firstMsg();\" style=\"background-color: #99CC33; color: white; height:40px; width:200px;\">NEXT ROUND</button>\n" + 
				"					</div>\n" + 
				"				</div>";
		return msg;
	}
	
	@GET
	@Path("/endButton")
	public String endButton() {
		String msg;
		int winRound = 0;
		int winCnt[] = this.gameController.getWinnerArr();
		for(int i=1; i < winCnt.length; i++) {
			winRound += winCnt[i];
		}
		
		String text = "The winner was " + (this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "You " : ("AI Player No." + this.gameController.getLastWinner())) + ", they won " + winRound + " rounds.</br></br>";
		for(int i = 0; i < this.originPlayers.length; i++) {
			if(this.originPlayers[i].getID() != this.gameController.gameWinner()) {
//				loserId[i] = this.gameController.getPlayers().get(i).getID();
				text += "AI Player " + this.originPlayers[i].getID() + " lost overall, but won " + winCnt[this.originPlayers[i].getID()] + " rounds. </br></br>";
			}
		}
		
		msg = "<div style = \"margin: auto; margin-top: 30px; margin-left: 2%;\n" + 
				"					width: 100%;\n" + 
				"					height: 500px;\n" + 
				"					background-color: white;\n" + 
				"					float: left;\n" + 
				"					\">\n" + 
				"	           		\n" + 
				"	           		 <div style=\"background-color: DodgerBlue; height:70px; margin-top: -20px;\">\n" + 
				"	           		 	<p style=\" color: black; margin-left: 20px;\">The game is over</p>\n" + 
				"	           		 </div>\n" + 
				"					<div id = \"panelContent\">\n" + 
				"					<button type = \"button\" onclick=\"location.href='http://localhost:7777/toptrumps';\" style=\"background-color: #99CC33; height:20px; width:200px;\">BACK TO MENU</button></br>\n" +
				"					</div><div>" + text +
				"						\n" +
				"					</div>\n" + 
				"				</div>";
		
		return msg;
	}
	

	
	
//	@GET
//	@Path("/topMsg")
//	public String topMsg() throws IOException {
//		int round = this.gameController.getRoundCnt();
//		String msg = "Round " + round + ": ";
//	
//		switch(this.stepCnt++ % 3) {
//			case 0: msg += "Players have drawn their cards.";  break;
//			case 1: {
//				msg += (this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "You " : ("AI Player " + this.gameController.getLastWinner())) + " selected " + this.gameController.getCurrentAttribute();
//			} break;
//			case 2: {
//				int cardNum = this.gameController.getCardsAfterDraw().size();
//				msg += this.gameController.getDrawFlag() ? 
//					("This round was a Draw, common pile now has " + cardNum + (cardNum == 1 ? " card" : " cards")) :
//					("Player " + (this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "You " : ("AI Player " + this.gameController.getLastWinner())) + " won this round");
//			}
//		}
//		
//		return msg;
//	}
	
	@GET
	@Path("/firstMsg")
	public String firstMsg() throws IOException {
		int round = this.gameController.getRoundCnt();
		String msg = "Round " + round + ": Players have drawn their cards."; 
		return msg;
	}
	
	@GET
	@Path("/secondMsg")
	public String secondMsg() throws IOException {
		int round = this.gameController.getRoundCnt();
		String msg = "Round " + round + ": ";
		msg += (this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "You " : ("AI Player " + this.gameController.getLastWinner())) + " selected " + this.gameController.getCurrentAttribute();
		return msg;
	}
	
	@GET
	@Path("/thirdMsg")
	public String thirdMsg() throws IOException {
		int round = this.gameController.getRoundCnt();
		String msg = "Round " + round + ": ";
		int cardNum = this.gameController.getCardsAfterDraw().size();
		msg += this.gameController.getDrawFlag() ? 
			("This round was a Draw, common pile now has " + cardNum + (cardNum == 1 ? " card" : " cards")) :
			((this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "You " : ("AI Player No." + this.gameController.getLastWinner())) + " won this round");
		return msg;
	}
	
	
	@GET
	@Path("/endMsg")
	public String endMsg() throws IOException {
		int round = this.gameController.getRoundCnt();
		String msg = "The winner of the game is " + (this.gameController.getLastWinner() == OnlineTopTrumps.USER ? "You " : ("AI Player No." + this.gameController.getLastWinner()));
		return msg;
	}

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GET
	@Path("/")
	public String expample() throws IOException {
		List<String> listOfWords = new ArrayList<String>();
		
		String listAsJSONString = oWriter.writeValueAsString(listOfWords);
		return listAsJSONString;
	}
	
	@GET
	@Path("/helloJSONList")
	/**
	 * Here is an example of a simple REST get request that returns a String.
	 * We also illustrate here how we can convert Java objects to JSON strings.
	 * @return - List of words as JSON
	 * @throws IOException
	 */
	public String helloJSONList() throws IOException {
		
		List<String> listOfWords = new ArrayList<String>();
		listOfWords.add("Hello");
		listOfWords.add("World!");
		
		// We can turn arbatory Java objects directly into JSON strings using
		// Jackson seralization, assuming that the Java objects are not too complex.
		String listAsJSONString = oWriter.writeValueAsString(listOfWords);
		
		return listAsJSONString;
	}
	
	@GET
	@Path("/helloWord")
	/**
	 * Here is an example of how to read parameters provided in an HTML Get request.
	 * @param Word - A word
	 * @return - A String
	 * @throws IOException
	 */
	public String helloWord(@QueryParam("Word") String Word) throws IOException {
		return "Hello "+Word;
	}
	
}
