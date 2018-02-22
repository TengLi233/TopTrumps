package online.dwResources;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import commandline.function.CMDDatabase;
import commandline.function.CMDLog;
import commandline.vo.CMDGameModelCard;
import commandline.vo.CMDGameModelPlayer;
import ult.Utility;


public class OnlineTopTrumps {
	private String gameText;
	/**�û���ҵ�id��������ģʽ�� �̶�Ϊ0��*/
	public static final int USER = 0;
	/**��ҵ�����Ϊ5*/
	private int playerNum;
	private int attributeKey;


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
	private String currentAttribute = "";
	
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
	private CMDGameModelCard currentPlayerCard;
	
	public OnlineTopTrumps(CMDDatabase db, int playerNum) {
		this.playerNum = playerNum;
		this.db = db;		
		
		/**Ϊ���ƶ����������ʼ����40�ſ��ƣ�*/
		this.cards = new CMDGameModelCard[CMDGameModelPlayer.CARD_NUM];
		/**Ϊ���������ʼ�������Ȳ��̶�����*/
		this.players = new ArrayList<CMDGameModelPlayer>();
		
		/** player joined the game */
		for(int i = 0; i < playerNum; i++) {
			this.players.add(new CMDGameModelPlayer(i));
		}
		
		
		/**Ϊ��Ա������ʼ��*/
		this.isFirstRound = true;
		this.drawFlag = false;
		this.cardsPerRound = new ArrayList<CMDGameModelCard>();
		this.cardsAfterDraw = new ArrayList<CMDGameModelCard>();
		this.winnerArr = new int[this.playerNum];
		
		/**��ȡ�������ݲ�������ʼ����*/	
		this.readFile();
		/**ϴ�Ʋ�����*/
		this.dealing();
		
		
	}
	
	public void readFile() {
			try {
				this.in = new Scanner(new FileInputStream(FILE_NAME));
				in.useDelimiter("[ ]+|\n");
				
				//����һ�е�������������Ч�����ݣ�
				in.nextLine();
				
				//Ϊ���п��ƶ����ʼ��������id
				int cnt = 0;
				while(in.hasNext()) {
					this.cards[cnt] = new CMDGameModelCard(cnt,in.next(), in.nextInt(),in.nextInt(),
															in.nextInt(),in.nextInt(),in.nextInt());
					// tests require "The contents of the complete deck once it has been read in and constructed"
					System.out.println(this.cards[cnt].getAttribute(0) + "..." + this.cards[cnt].getAttribute(1) + "..." + this.cards[cnt].getAttribute(2) + "..."
							+ this.cards[cnt].getAttribute(3) + "..." + this.cards[cnt].getAttribute(4));
					cnt++;
				}
				
			} catch (FileNotFoundException e) {
				System.err.println("File " + FILE_NAME + " not found.");
				e.printStackTrace();
			} finally {
				in.close();
			}
	}
	
	public void dealing() {
		/**ϴ��*/
		this.shuffle();
		
		/**Ϊÿλ��ҷ���*/
		int cardIndex = 0;
		int k = Utility.random(this.players.size());
		while(cardIndex < 40) {
			this.players.get(k++ % this.players.size()).addCard(this.cards[cardIndex++]);
		}

//		for(int i = 0; i < this.players.size(); i++) {
//			System.err.println("-----" + this.players.get(i).size());
//			for(int j = 0; j < this.players.get(i).size(); j++) {
//				System.err.println("-----" + this.players.get(i).getCard(j).getId() + " " + j);
//				System.err.println("***********" + j);
//			}
//		}
		
	}
	
	private void shuffle() {
		
		/**ÿ���Ƹ�һ�������λ�ý�����ʵ�����ϴ��*/
		for(int i = 0; i < CMDGameModelPlayer.CARD_NUM; i++) {
			Utility.swap(this.cards, i, Utility.random(40));
		}	
	}
	
	public int gameWinner() {

		for(int i = 0; i < this.players.size(); i++) {
			if(this.players.get(i).hasCard()) {
				return this.players.get(i).getID();
			}
		}
		
		return -1;
	}
	
	public void prepared() {
		/** choose the first player randomly*/
		if(isFirstRound) {
			this.lastWinner = Utility.random(this.players.size());
			this.lastWinnerIndex = this.lastWinner;
			this.currentPlayer = this.lastWinner;
			this.isFirstRound = false;		
		}

		// players drop their top card to this round's common pile.
		for(int i = 0; i < this.players.size(); i++) {
			if(this.players.get(i).getID() == this.lastWinner) {
				this.currentPlayerCard = this.players.get(i).getCard();
			}
			this.cardsPerRound.add(this.players.get(i).removeCard());
		}
		for(int i = 0; i < this.players.size(); i++) {
			System.out.println("****" + this.cardsPerRound.get(i));
		}
		
	}
	
	public CMDGameModelCard getCurrentPlayerCard() {
		return currentPlayerCard;
	}

	public void result() {
		
		// id is players, key is category, the first one is this round's cards.
		this.getWinner();
		System.err.println("....." + this.cardsPerRound.size());
		
		
		/** if the game is end, the print the winner. */
		if(isEnd()) {
			db.uploadRocord(drawCnt, this.gameWinner(), this.roundCnt, this.winnerArr);
			
			// back to main manu
			return;
		}
		
		this.getCards();
		this.playerOut();
	}
	
	public void getCards() {
		System.err.println("[System]: This round's end details");  //Getting Card--------------------------
		int cardCnt = this.cardsAfterDraw.size() + this.cardsPerRound.size();
		if(this.drawFlag) {
			this.cardsAfterDraw.addAll(this.cardsPerRound);
			// tests require "The contents of the communal pile when cards are added or removed from it "
				
		} else {
			//System.out.println("          The size of card per round : " + this.cardsPerRound.size());
			//System.out.println("          The size of card after draw : " + this.cardsAfterDraw.size());
			System.err.println("          Players left : " + this.players.size());
			if(this.cardsAfterDraw.size() > 0) {
				// player collects his win cards
				this.players.get(this.lastWinnerIndex).addCard(this.cardsAfterDraw, 0);
				
				// clear the common pile
				this.cardsAfterDraw.clear();
			}
			this.players.get(this.lastWinnerIndex).addCard(this.cardsPerRound, 0);
		}
		this.cardsPerRound.clear();
		System.err.println(this.drawFlag ? ("[results]: " + cardCnt + (cardCnt > 1 ? " cards are " : " card is ") + " moved to common pile.")  : ("[Results]: No." + this.lastWinner + " player get " + cardCnt + (cardCnt > 1 ? "cards" : "card")));
	}
	
	public ArrayList<CMDGameModelPlayer> getPlayers() {
		return players;
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
			System.out.println("--------------" + this.players.get(i).getID());
			if(this.players.get(i).hasCard()) {
				valid++;
			}
		}
		
		return valid==1 || valid==0;
	}
	
	public void getWinner() {
		// print everyone's card value of category.
		// tests require "The category selected and corresponding values when a user or computer selects a category"
		
		// which card's value is the biggest
		int max = Utility.max(this.cardsPerRound, this.attributeKey);
		System.err.println("max is .........." + max);
		System.err.println("Player is .........." + this.players.size());
		System.err.println("cardsPerRound is .........." + this.cardsPerRound.size());
		
		this.drawFlag = this.isDraw(this.cardsPerRound, this.attributeKey, max);
		
		if(!this.drawFlag) {
			this.lastWinner = this.players.get(max).getID();
			System.err.println(this.winnerArr.length + " *******");
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
	
	public CMDGameModelCard getUserCard() {
		return this.cardsPerRound.get(USER);
	}
	
	public int getRoundCnt() {
		return roundCnt;
	}
	public int getLastWinner() {
		return lastWinner;
	}

	public String getGameText() {
		return gameText;
	}

	public int getPlayerNum() {
		return playerNum;
	}

	public int getDrawCnt() {
		return drawCnt;
	}


	public boolean isFirstRound() {
		return isFirstRound;
	}

	public int getCurrentPlayer() {
		return currentPlayer;
	}

	public int getLastWinnerIndex() {
		return lastWinnerIndex;
	}

	public boolean getDrawFlag() {
		return drawFlag;
	}

	public int[] getWinnerArr() {
		return winnerArr;
	}

	public ArrayList<CMDGameModelCard> getCardsPerRound() {
		return cardsPerRound;
	}

	public ArrayList<CMDGameModelCard> getCardsAfterDraw() {
		return cardsAfterDraw;
	}

	public CMDLog getTestLog() {
		return testLog;
	}

	public String getCurrentAttribute() {
		return currentAttribute;
	}



	public void setAttributeKey(int attributeKey) {
		this.attributeKey = attributeKey;
	}

	public void setRoundCnt(int roundCnt) {
		this.roundCnt = roundCnt;
	}

	public int getAttributeKey() {
		return attributeKey;
	}

	public void setCurrentAttribute(String currentAttribute) {
		this.currentAttribute = currentAttribute;
	}
	
	public boolean isUserOut() {
		for(int i = 0; i < this.players.size(); i++) {
			CMDGameModelPlayer temp = this.players.get(i);
			if(temp.getID() == USER) {
				return false;
			}
		}
		
		return true;
	}
	

}
