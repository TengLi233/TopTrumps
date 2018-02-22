package commandline.function;

public class CMDStatistic {
	
	private CMDDatabase db;
	private String results;
	
	public CMDStatistic (CMDDatabase db) {
		this.db = db;
		
	}

	public void show() {
		// TODO Auto-generated method stub
		System.out.println("Total Game Number : "+ db.getGameNumber());
		System.out.println("AI players won : "+ db.getAIWonNumber());
		System.out.println("User wons : "+ db.getUserWonNumber());
		System.out.println("The largest round number of a game : "+ db.getLargestRoundNumber());
		System.out.println(String.format("Average draws of total games : %.2f", db.getAvgDraws()));
	}
	
	/**
	 * for web service only, get the game statistic fromdatabase
	 * @return a html type results.
	 */
	public String get() {
		results = "<li>Total Game Number : "+ db.getGameNumber() + "</li>" +
					"<li>AI players won : "+ db.getAIWonNumber() + "</li>" +
					"<li>User wons : "+ db.getUserWonNumber() + "</li>" +
					"<li>The largest round number of a game : "+ db.getLargestRoundNumber() + "</li>" +
					"<li>" + String.format("Average draws of total games : %.2f", db.getAvgDraws()) + "</li>";
		return results;
	}
}
