
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
 * This class ��MVC�ṹ�е�Control������������Ϸ���˵�ѡ1���ĸ��ֲ�����
 * @author yifeng sun, feiguang cao
 */
public class CMDGameControl {
	private String gameText;
	/**�û���ҵ�id��������ģʽ�� �̶�Ϊ0��*/
	public static final int USER = 0;
	/**��ҵ�����Ϊ5*/
	private int playerNum;

	// the number of total cards in the file
	public static final int CARDS_NUM = 40;
	/**�����Ƶ����ݵ��ļ���*/
	public static final String FILE_NAME = "StarCitizenDeck.txt";
	private int drawCnt = 0;
	private int roundCnt = 0;
	
	/**�������������ڶ�ȡ�ļ������Լ��û�����*/
	private Scanner in;
	/**����������Ҷ��������
	 * ��Ϊ��������˳��� ���Ը������ĳ���Ӧ�ɱ�
	 */
	private ArrayList<CMDGameModelPlayer> players; 
	/**�������п��ƶ��������������������Ϊ40�ţ�*/
	private CMDGameModelCard[] cards;
	
	/**��һ�غϱ�־λ������ǵ�һ�ػ��ж�Ӧ�ĳ�ʼ������*/
	private boolean isFirstRound;
	/**��һ����Ϸ��Ӯ��id*/
	private int lastWinner;
	// active player of the round
	private int currentPlayer;
	/**��һ����ϷӮ�ҵ�id�������е�����*/
	private int lastWinnerIndex;
	/**ƽ�ֱ�־λ*/
	private boolean drawFlag;
	private int[] winnerArr;
	
	/**
	 * �ݴ�ÿһ�غ�������ҵĳ��Ƶ�����
	 * ����������ı䶯�����ƴ洢������Ҳ��Ӧ�̶�
	 * ������ÿ�ֻᱻ���
	 */
	private ArrayList<CMDGameModelCard> cardsPerRound;
	
	/**
	 * �����ƽ�֣����þ����д�����Ŀ��Ʒ���һ���µ�����
	 * ����������䶯��ƽ�ֵĴ���Ҳ��ӦΪ�̶�
	 * ����������ÿ�ֱ����
	 */
	private ArrayList<CMDGameModelCard> cardsAfterDraw;
	
	private CMDLog testLog;
	
	private CMDDatabase db;
	/**
	 * CMDGameControl�Ĺ�������
	 * ����Ը���ĸ���������ʼ��
	 * ����������Ҷ��� ���ƶ���ϴ�ƣ��Լ����Ƶ�
	 */
	public CMDGameControl(CMDLog testLog, CMDDatabase db, int playerNum) {
		this.playerNum = playerNum;
		this.db = db;		
		
		// testLog is for -t commend. will record game information.
		this.testLog = testLog;
		
		/**Ϊ���ƶ����������ʼ����40�ſ��ƣ�*/
		this.cards = new CMDGameModelCard[CMDGameModelPlayer.CARD_NUM];
		/**Ϊ���������ʼ�������Ȳ��̶�����*/
		this.players = new ArrayList<CMDGameModelPlayer>();
		
		/** player joined the game */
		for(int i = 0; i < playerNum; i++) {
			this.players.add(new CMDGameModelPlayer(i));
			System.out.println("player " + i + " loading......");
		}
		
		
		/**Ϊ��Ա������ʼ��*/
		this.isFirstRound = true;
		this.drawFlag = false;
		this.cardsPerRound = new ArrayList<CMDGameModelCard>();
		this.cardsAfterDraw = new ArrayList<CMDGameModelCard>();
		this.winnerArr = new int[this.playerNum];
		
		/**��ȡ�������ݲ�������ʼ����*/	
		this.readFile();
		System.out.println("[System]: Shuffling Cards Complete.");
		/**ϴ�Ʋ�����*/
		this.dealing();
		System.out.println("[System]: Dealing Cards.");
		
		
	}
	
	public CMDGameControl(CMDLog testLog, CMDDatabase db) {
		this(testLog, db, 5);
	}
	
	
	/**
	 * �ú��������ȡ�������ݣ� �������ƶ��󲢷���������
	 */
	public void readFile() {
		try {
			this.in = new Scanner(new FileInputStream(FILE_NAME));
			in.useDelimiter("[ ]+|\n");
			
			//����һ�е�������������Ч�����ݣ�
			in.nextLine();
			
			//Ϊ���п��ƶ����ʼ��������id
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
	 * �ú�������ϴ�Ƹ�����
	 * ͨ������shuffle()����ϴ�ƺ�ͨ�������������Ϊ��ҷ���(ÿλ��ҳ�ʼ�Ŀ�������Ϊ8)
	 */
	public void dealing() {
		/**ϴ��*/
		this.shuffle();
		
		/**Ϊÿλ��ҷ���*/
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
		 * "The contents of the user�s deck and the computer�s deck(s) once they have been allocated.
		 * Be sure to indicate which the user�s deck is and which the computer�s deck(s) is. "
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
	 * �ú�������ϴ��
	 */
	private void shuffle() {
		
		/**ÿ���Ƹ�һ�������λ�ý�����ʵ�����ϴ��*/
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
	 * ����ÿ���غϵ���Ϸ����
	 * �Լ���Ϸ�Ľ���
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
			
			// tests require "The contents of the current cards in play (the cards from the top of the user�s deck and the computer�s deck(s))"
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
