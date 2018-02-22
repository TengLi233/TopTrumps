
package commandline;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import commandline.vo.CMDGameModelCard;
import commandline.vo.CMDGameModelPlayer;
import commandline.function.CMDDatabase;
import commandline.function.CMDLog;
import ult.Utility;
import java.sql.*;

/**
 * This class 是MVC结构中的Control。负责处理玩游戏（菜单选1）的各种操作。
 * @author yifeng sun, feiguang cao
 */
public class CMDGameControl {
	private String gameText;
	/**用户玩家的id（命令行模式， 固定为0）*/
	public static final int USER = 0;
	/**玩家的总数为5*/
	private int playerNum;

	// the number of total cards in the file
	public static final int CARDS_NUM = 40;
	/**包含牌的数据的文件名*/
	public static final String FILE_NAME = "StarCitizenDeck.txt";
	private int drawCnt = 0;
	private int roundCnt = 0;
	
	/**输入流对象，用于读取文件输入以及用户输入*/
	private Scanner in;
	/**储存所有玩家对象的容器
	 * 因为会有玩家退出， 所以该容器的长度应可变
	 */
	private ArrayList<CMDGameModelPlayer> players; 
	/**储存所有卡牌对象的容器的容器（总数为40张）*/
	private CMDGameModelCard[] cards;
	
	/**第一回合标志位，如果是第一回会有对应的初始化操作*/
	private boolean isFirstRound;
	/**上一局游戏的赢家id*/
	private int lastWinner;
	// active player of the round
	private int currentPlayer;
	/**上一局游戏赢家的id在容器中的索引*/
	private int lastWinnerIndex;
	/**平局标志位*/
	private boolean drawFlag;
	private int[] winnerArr;
	
	/**
	 * 暂存每一回合所有玩家的出牌的容器
	 * 由于玩家数的变动，出牌存储的容量也不应固定
	 * 该容器每局会被清空
	 */
	private ArrayList<CMDGameModelCard> cardsPerRound;
	
	/**
	 * 如果有平局，将该局所有打出过的卡牌放入一个新的容器
	 * 由于玩家数变动，平局的储存也不应为固定
	 * 该容器不会每局被清空
	 */
	private ArrayList<CMDGameModelCard> cardsAfterDraw;
	
	private CMDLog testLog;
	
	private CMDDatabase db;
	/**
	 * CMDGameControl的构造器：
	 * 负责对该类的各个参数初始化
	 * 包括创建玩家对象， 卡牌对象，洗牌，以及发牌等
	 */
	public CMDGameControl(CMDLog testLog, CMDDatabase db, int playerNum) {
		this.playerNum = playerNum;
		this.db = db;		
		
		// testLog is for -t commend. will record game information.
		this.testLog = testLog;
		
		/**为卡牌对象的容器初始化（40张卡牌）*/
		this.cards = new CMDGameModelCard[CMDGameModelPlayer.CARD_NUM];
		/**为玩家容器初始化，长度不固定（）*/
		this.players = new ArrayList<CMDGameModelPlayer>();
		
		/** player joined the game */
		for(int i = 0; i < playerNum; i++) {
			this.players.add(new CMDGameModelPlayer(i));
			System.out.println("player " + i + " loading......");
		}
		
		
		/**为成员变量初始化*/
		this.isFirstRound = true;
		this.drawFlag = false;
		this.cardsPerRound = new ArrayList<CMDGameModelCard>();
		this.cardsAfterDraw = new ArrayList<CMDGameModelCard>();
		this.winnerArr = new int[this.playerNum];
		
		/**读取卡牌数据并创建初始牌组*/	
		this.readFile();
		System.out.println("[System]: Shuffling Cards Complete.");
		/**洗牌并发牌*/
		this.dealing();
		System.out.println("[System]: Dealing Cards.");
		
		
	}
	
	public CMDGameControl(CMDLog testLog, CMDDatabase db) {
		this(testLog, db, 5);
	}
	
	
	/**
	 * 该函数负责读取卡牌数据， 创建卡牌对象并放入容器中
	 */
	public void readFile() {
		try {
			this.in = new Scanner(new FileInputStream(FILE_NAME));
			in.useDelimiter("[ ]+|\n");
			
			//将第一行的数据跳过（无效的数据）
			in.nextLine();
			
			//为所有卡牌对象初始化并创建id
			int cnt = 0;
			String tempRecords = "---------------------------------------------------\r\noriginal Cards:  \r\n";
			while(in.hasNext()) {
				this.cards[cnt] = new CMDGameModelCard(cnt,in.next(), in.nextInt(),in.nextInt(),
														in.nextInt(),in.nextInt(),in.nextInt());
				// tests require "The contents of the complete deck once it has been read in and constructed"
				tempRecords += "Card["+ cards[cnt].getId() + "]" + "description["+ cards[cnt].getDescription() + "]\r\n";
				cnt++;
			}
			this.testLog.record(tempRecords);
			
		} catch (FileNotFoundException e) {
			System.err.println("File " + FILE_NAME + " not found.");
			e.printStackTrace();
		} finally {
			in.close();
		}
	}
	
	/**
	 * 该函数负责洗牌跟发牌
	 * 通过调用shuffle()函数洗牌后，通过遍历玩家容器为玩家发牌(每位玩家初始的卡牌数量为8)
	 */
	public void dealing() {
		/**洗牌*/
		this.shuffle();
		
		/**为每位玩家发牌*/
//		for(int i = 0; i < this.playerNum; i++) {
//			while(cardIndex < 40 && this.players.get(i).size() < CMDGameModelPlayer.INIT) {
//				this.players.get(i).addCard(this.cards[cardIndex++]);
//			};
//		}
		int cardIndex = 0;
		int k = Utility.random(this.players.size());
		while(cardIndex < 40) {
			this.players.get(k++ % this.players.size()).addCard(this.cards[cardIndex++]);
		}
		
		/**
		 * tests require the following
		 * "The contents of the user抯 deck and the computer抯 deck(s) once they have been allocated.
		 * Be sure to indicate which the user抯 deck is and which the computer抯 deck(s) is. "
		 */
		String tempRecords = "---------------------------------\r\nHuman player's deck\r\n";
		for(int j = 0; j < this.players.get(0).size(); j++) {
			tempRecords += "cards["+ this.players.get(0).getCard(j).getId() + "]\r\n";
		}
		for(int i = 1; i < this.playerNum; i++) {
			//System.out.println("-----" + this.players.get(i).size());
			tempRecords += "Computer Player["+i+"]'s decks\r\n";
			for(int j = 0; j < this.players.get(i).size(); j++) {
				tempRecords += "cards["+ this.players.get(i).getCard(j).getId() + "]\r\n";
			}
		}
		this.testLog.record(tempRecords);
	}
	
	/**
	 * 该函数负责洗牌
	 */
	private void shuffle() {
		
		/**每张牌跟一个随机的位置交换，实现随机洗牌*/
		for(int i = 0; i < CMDGameModelPlayer.CARD_NUM; i++) {
			Utility.swap(this.cards, i, Utility.random(40));
		}
		
		// tests require "The contents of the complete deck after it has been shuffled "
		this.testLog.record("---------------------------------------------------\r\nshuffled Cards\r\n");
		;
		for (int cnt = 0; cnt < CARDS_NUM; cnt++) {
			this.testLog.record("Card["+ cards[cnt].getId() + "]" + "description["+ cards[cnt].getDescription() + "]\r\n");
		}
		
	}
	
	/**
	 * 负责每个回合的游戏进程
	 * 以及游戏的结束
	 */
	public void newRound() {
		/**
		 * tell player which number is him or her.
		 */
		System.out.println("[System]: You are player NO.0 .");
		
		/** keep running till the game end*/
		while(true) {
			// this round's number.
			roundCnt++;
			System.out.println("//////////// Round " + roundCnt + " ////////////");
			
			/** choose the first player randomly*/
			if(isFirstRound) {
				this.lastWinner = Utility.random(5);
				this.lastWinnerIndex = this.lastWinner;
				this.currentPlayer = this.lastWinner;
				this.isFirstRound = false;		
			}

			
			// get this round's player numbers (who is still in the game).
			int[] id = new int[this.players.size()];
			for(int i = 0; i < this.players.size(); i++) {
				id[i] = this.players.get(i).getID();
			}
						
			/**
			 * print this round's player and human plaer's top card details.
			 */
			if (this.drawFlag)
				System.out.println("The current player is No." + this.currentPlayer + " player!\n");
			else
				System.out.println(this.isFirstRound ? ("The game is start with No." + this.currentPlayer + " player!\n") : ("The current player is No." + this.currentPlayer+ " player!\n") );
			
			System.out.println("[System]: Details of your top card ");
			System.out.println("          " + String.format("%-12s%-6s%-7s%-7s%-11s%-7s", 
												"Description", "Size", 
												"Speed", "Range", 
												"Firepower", "Cargo"));
			
			// tests require "The contents of the current cards in play (the cards from the top of the user抯 deck and the computer抯 deck(s))"
			this.testLog.record("---------------------------------------------------\r\n//////////////Round["+ roundCnt +"]/////////////\r\n");
			this.testLog.record("human player[0]'s current card is ["+ this.players.get(USER).getCard().getId() +"]\r\n");
			for (int i = 1; i < id.length; i++)
				this.testLog.record("computer player["+ i +"]'s current card is ["+ this.players.get(i).getCard().getId() +"]\r\n");
			
			// players drop their top card to this round's common pile.
			int key = -1;
			for(int i = 0; i < this.players.size(); i++) {
				this.cardsPerRound.add(this.players.get(i).removeCard());
			}

			// print this round's player and human plaer's top card details.
			System.out.println("          " + String.format("%-12s%-6d%-7d%-7d%-11d%-7d", 
					this.cardsPerRound.get(USER).getDescription(),this.cardsPerRound.get(USER).getSize(), 
					this.cardsPerRound.get(USER).getSpeed(), this.cardsPerRound.get(USER).getRange(), 
					this.cardsPerRound.get(USER).getFirepower(), this.cardsPerRound.get(USER).getCargo()));
			
			// if this round is human's round, then ask him or her what category he or she selected.
			if(this.currentPlayer == USER) {
				this.in = new Scanner(System.in);
				do{
					System.out.print("[Your turn]: Please enter an Attribute for comparing:\n"
							+ "0.Size.\n"
							+ "1.Speed.\n" 
							+ "2.Range.\n"  
							+ "3.Firepower.\n" 
							+ "4.Cargo.\n" + ">>>");
					
					key = in.nextInt();
				}while(!(key >= 0 && key < 5));
			} else {
				key = this.chooseBestCategory(id);
				System.out.println("key " + key);
			}
			
			// print this round's category
			String msg = "[System]: The attribute to compare this round is : ";
			switch(key) {
				case 0: msg += "Size"; break;
				case 1: msg += "Speed"; break;
				case 2: msg += "Range"; break;
				case 3: msg += "Firepower"; break;
				case 4: msg += "Cargo"; break;
			}
			System.out.println(msg + ".");
			
			// id is players, key is category, the first one is this round's cards.
			this.getWinner(this.cardsPerRound, key, id);
			
			
			/** if the game is end, the print the winner. */
			if(isEnd()) {
				System.out.println("[System]: The winner of this game is No." + this.gameWinner() + " player.");
				db.uploadRocord(drawCnt, this.gameWinner(), this.roundCnt, this.winnerArr);
				
				// tests require "The winner of the game "
				this.testLog.record("------------------------------\r\nThe winner of this game is No." + this.gameWinner() + " player.\r\n");
				
				// back to main manu
				return;
			}
			
			this.getCards();
			this.playerOut();
			
			// for tests only "The contents of each deck after a round"
			String tempRecord = "--------------------------------------\r\n after this round, the deck of players:\r\n";
			for(int i = 0; i < this.players.size(); i++) {
				System.out.println("The player[" + this.players.get(i).getID() + "] has " + this.players.get(i).size() + " cards.");
				for(int j = 0; j < this.players.get(i).size(); j++) {
					tempRecord += "The player[" + this.players.get(i).getID() + "] has card[" +this.players.get(i).getCard(j).getId()+ "]\r\n";
				}
			}
			this.testLog.record(tempRecord);
			
		}
	}
	
	/**
	 * the end of a round, players draw their cards when a draw happens, or winner collect his cards.
	 */
	public void getCards() {
		System.out.println("[System]: This round's end details");  //Getting Card--------------------------
		int cardCnt = this.cardsAfterDraw.size() + this.cardsPerRound.size();
		if(this.drawFlag) {
			this.cardsAfterDraw.addAll(this.cardsPerRound);
			// tests require "The contents of the communal pile when cards are added or removed from it "
			String tempRecord = "---------------------------------\r\n";
			for(int i = 0; i < this.cardsAfterDraw.size();i++) {
				tempRecord += "card[" + this.cardsAfterDraw.get(i).getId() + "] is added to common pile\r\n";
			}
			this.testLog.record(tempRecord);
				
		} else {
			//System.out.println("          The size of card per round : " + this.cardsPerRound.size());
			//System.out.println("          The size of card after draw : " + this.cardsAfterDraw.size());
			System.out.println("          Players left : " + this.players.size());
			if(this.cardsAfterDraw.size() > 0) {
				// player collects his win cards
				this.players.get(this.lastWinnerIndex).addCard(this.cardsAfterDraw, 0);
				
				// tests require "The contents of the communal pile when cards are added or removed from it "
				String tempRecord = "---------------------------------\r\n";
				for(int i = 0; i < this.cardsAfterDraw.size();i++) {
					tempRecord += "card[" + this.cardsAfterDraw.get(i).getId() + "] is removed from common pile by player["+this.lastWinner+"]\r\n";
				}
				this.testLog.record(tempRecord);
				
				// clear the common pile
				this.cardsAfterDraw.clear();
			}
			this.players.get(this.lastWinnerIndex).addCard(this.cardsPerRound, 0);
		}
		this.cardsPerRound.clear();
		System.out.println(this.drawFlag ? ("[results]: " + cardCnt + (cardCnt > 1 ? " cards are " : " card is ") + " moved to common pile.")  : ("[Results]: No." + this.lastWinner + " player get " + cardCnt + (cardCnt > 1 ? "cards" : "card")));
	}
	
	public void getWinner(ArrayList<CMDGameModelCard> arr, int key, int[] ids) {
		// print everyone's card value of category.
		// tests require "The category selected and corresponding values when a user or computer selects a category"
		String msg = "";
		switch(key) {
		case 0: msg = "Size"; break;
		case 1: msg = "Speed"; break;
		case 2: msg = "Range"; break;
		case 3: msg = "Firepower"; break;
		case 4: msg = "Cargo"; break;
	}
		String tempRecords = "the category selected is "+ msg +" .\r\n";
		for(int i = 0; i < arr.size(); i++) {
			System.out.println("          The attribute of player[" + ids[i] + "]'s card is " + arr.get(i).getAttribute(key));
			tempRecords += "The value of player[" + ids[i] + "]'s card is " + arr.get(i).getAttribute(key) + "\r\n";
		}
		this.testLog.record(tempRecords);
		
		// which card's value is the biggest
		int max = Utility.max(arr, key);
		
		this.drawFlag = this.isDraw(this.cardsPerRound, key, max);
		
		if(!this.drawFlag) {
			this.lastWinner = ids[max];
			this.winnerArr[this.lastWinner]++;
			this.lastWinnerIndex = max;
		} else {
			this.drawCnt++;
		}
		
		this.currentPlayer = this.lastWinner;  // if this round is draw, then current player will not change. (problem happened when two draw come together)
		System.out.println(!this.drawFlag ? 
				"[Results]: The winner of this round is No." + this.lastWinner + " player." :
				"[Results]: This round is Draw ");
		
	}
	
	public boolean isDraw(ArrayList<CMDGameModelCard> arr, int key, int max) {
		for(int i = 0; i < arr.size(); i++) {
			if(i != max && arr.get(i).getAttribute(key) == arr.get(max).getAttribute(key)) {
				return true;
			}
		}
		return false;
	}
	
	public void playerOut() {
		for(int i = this.players.size() - 1; i >= 0; i--) {
			if(!this.players.get(i).hasCard()) {
				System.out.println("[Player warning]: No." + this.players.get(i).getID() + " player is out of card!");
				this.players.remove(i);
			}
		}
	}

	public boolean isEnd() {
		int valid = 0;
		for(int i = 0; i < this.players.size(); i++) {
			if(this.players.get(i).hasCard()) {
				valid++;
			}
		}
		
		return valid==1 || valid==0;
	}
	
	public int gameWinner() {
		for(int i = 0; i < this.players.size(); i++) {
			if(this.players.get(i).hasCard()) {
				return this.players.get(i).getID();
			}
		}
		
		return -1;
	}
	
	public int chooseBestCategory(int[] playersThisRound) {
		int[] cate = new int[5];
		int max = 0, maxIndex = -1, playerIndex = -1;
		for(int i = 0; i < playersThisRound.length; i++) {
			if (playersThisRound[i] == lastWinner)
				playerIndex = i;
		}
		
		for(int i = 0; i < 5; i++) {
			cate[i] = this.cardsPerRound.get(playerIndex).getAttribute(i);
			System.out.println("         ------cate value---("+ cate[i] +")   ");
		}
		for(int k = 0; k < 5; k++) {
			if (cate[k] >= max) {
				max = cate[k];
				maxIndex = k;
			}
		}
		System.out.println("                      max value   ("+ max +")   index " + maxIndex);
		return maxIndex;
	}
	
}
