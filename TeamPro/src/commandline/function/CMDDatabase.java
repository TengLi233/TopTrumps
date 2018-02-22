package commandline.function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * this class contains all operations about database 
 * @author 2278853c
 *
 */
public class CMDDatabase {
	//
	public boolean status;
	private String logInfo, user, pwd; 
	private Connection conn = null;
	private Statement smt = null;
	private ResultSet rs;
	
	public CMDDatabase () {
		this.logInfo = "jdbc:postgresql://yacata.dcs.gla.ac.uk:5432/m_17_2278853c";
		this.user = "m_17_2278853c";
		this.pwd = "2278853c";
//		this.logInfo = "jdbc:mysql://45.78.59.136:3306/2278853";
//		this.user = "2278853c";
//		this.pwd = "10214358og";
		
		this.connect();
	}

	/**
	 * create a connection to school's postgres database.
	 * 
	 */
	private void connect() {
		//
		try {
			conn = DriverManager.getConnection(logInfo, user, pwd);
			smt = conn.createStatement();
			if (conn != null) {
				System.out.println("[System:] Postgres Connection successful");
				status = true;
			} else {
				System.err.println("Failed to make connection!");
				status = false;
			}
		} catch (SQLException e) {
			System.err.println("Connection Failed!");
			e.printStackTrace();
			status = false;
		}
		
	}
	
	/**
	 * get Number of games played overall
	 * @return the integer type number, or -1 if failed.
	 */
	public int getGameNumber() {
		int result = -1;
		try {
			this.rs = smt.executeQuery("SELECT count(id) FROM tp.gamerecord");
			if(this.rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Query is Failed!");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * get How many times the computer has won
	 * @return the integer type number, or -1 if failed.
	 */
	public int getAIWonNumber() {
		int result = -1;
		try {
			this.rs = smt.executeQuery("SELECT count(id) FROM tp.gamerecord WHERE winner IN (SELECT id FROM tp.player WHERE isAI = true)");
			if(this.rs.next()) {
				result =  rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Query is Failed!");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * get How many times the human has won 
	 * @return the integer type number, or -1 if failed.
	 */
	public int getUserWonNumber() {
		int result = -1;
		try {
			this.rs = smt.executeQuery("SELECT count(id) FROM tp.gamerecord WHERE winner = (SELECT id FROM tp.player WHERE isAI = false)");
			if(this.rs.next()) {
				result =  rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Query is Failed!");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * get The average number of draws
	 * @return the double type number, or -1 if failed.
	 */
	public double getAvgDraws() {
		double result = -1.0, sum = 0.0;
		int  count = 0;
		try {
			this.rs = smt.executeQuery("SELECT SUM(drawCnt) FROM tp.gamerecord");
			if(this.rs.next()) {
				sum = this.rs.getDouble(1) ;
			}
			this.rs = smt.executeQuery("SELECT COUNT(id) FROM tp.gamerecord");
			if(this.rs.next()) {
				count = this.rs.getInt(1);
				result = sum / count;
			}
			
		} catch (SQLException e) {
			System.err.println("Query is Failed!");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * get The largest number of rounds played in a single game 
	 * @return
	 */
	public int getLargestRoundNumber() {
		int result = -1;
		try {
			this.rs = smt.executeQuery("SELECT MAX(drawCnt) FROM tp.gamerecord");
			if(this.rs.next())
				result = this.rs.getInt(1);
		} catch (SQLException e) {
			System.err.println("Query is Failed!");
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * upload this game's information to database
	 * @param draws How many draws were there
	 * @param winner Who won the game
	 * @param rounds How many rounds were played in the game
	 * @param playerWinRounds How many rounds did each player win
	 */
	public boolean uploadRocord(int draws, int winner, int rounds, int[] playerWinRounds) {
		int gameid = 1, row = 0;
		boolean state = true;
		try {
			this.rs = smt.executeQuery("select count(id) from tp.gamerecord");
			if(this.rs.next()) {
				gameid =  rs.getInt(1) + 1;
			} else {
				state = false;
				System.err.println("upload is failed! because cannot generate gameid");
			}
			row = smt.executeUpdate("INSERT INTO tp.gamerecord VALUES ("+gameid+","+winner+","+draws+","+rounds+")");
			if (row == 0)
				state = false;
			for(int i = 0; i < playerWinRounds.length; i++) {	
				row = smt.executeUpdate("INSERT INTO tp.gamePlayerRecord VALUES ("+gameid+","+i+","+playerWinRounds[i]+")");
				if (row == 0)
					state = false;
			}
			
			
		} catch (SQLException e) {
			System.err.println("Upload is Failed!");
			e.printStackTrace();
		}
		return state;
	}
	
	
	/**
	 * close the database connection
	 */
	public void pgClose() {
		try {
			conn.close();
			System.out.println("Connection closed");
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Connection could not be closed ¨C SQLexception");
		}
		
	}
	
}
