package eu.eyan.timecounter;


public class TimeCounter {
	private long myStart;

	public void start(){
		this.myStart = System.currentTimeMillis();
	}
	
	public String end(){
		long ms = System.currentTimeMillis()-myStart;
		float seconds = ((float)(ms/1000));
		return ""+seconds+"s";
	}
	
}
