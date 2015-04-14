package com.dataCollected;

/**
 *  Class for collected data per application, when it is sending/receiving packets from the network
 * @author troglodito22
 * 
 */

public class DataCollectedApp {

	// Variables collected, when monitoring
	private long time;
	private int app_name;
	private int num_process;
	private long packet_size_tx;
	private long packet_size_rx;
	private long packet_counter_tx;
	private long packet_counter_rx;
	private int packet_type_tx;
	private int packet_type_rx;
	private int isForeground;
	
	/**
	 * Contructor
	 */
	public DataCollectedApp(){
		super();
		this.time = 0;
		this.app_name = 0;
		this.num_process = 0;
		this.packet_size_tx = -1;
		this.packet_size_tx = -1;
		this.packet_counter_tx = -1;
		this.packet_counter_rx = -1;
		this.packet_type_tx = PacketType.NORMAL;
		this.packet_type_rx = PacketType.NORMAL;
		this.isForeground = 0;
	}
	/**
	 * Constructor with paramaters
	 * @param time
	 * @param app_name
	 * @param packet_size
	 * @param packet_counter
	 * @param packet_type
	 * @param isForeground
	 */
	public DataCollectedApp(long time, int app_name, int num_proc,
			long packet_size_tx, long packet_size_rx, 
			long packet_counter_tx, long packet_counter_rx, 
			int packet_type_tx, int packet_type_rx, int isForeground) {
		this.time = time;
		this.app_name = app_name;
		this.num_process = num_proc;
		this.packet_size_tx = packet_size_tx;
		this.packet_size_tx = packet_size_rx;
		this.packet_counter_tx = packet_counter_tx;
		this.packet_counter_rx = packet_counter_rx;
		this.packet_type_tx = packet_type_tx;
		this.packet_type_rx = packet_type_rx;
		this.isForeground = isForeground;
	}
	
	public void setTime(long time){
		this.time = time;
	}
	
	public void setAppName(int app_name){
		this.app_name = app_name;
	}
	
	public void setNumProcess(int num_proc){
		this.num_process = num_proc;
	}
	
	public void setPacketSizeTx(long packet_size){
		this.packet_size_tx = packet_size;
	}
	
	public void setPacketSizeRx(long packet_size){
		this.packet_size_rx = packet_size;
	}
	
	public void setPacketCounterTx(long packet_counter){
		this.packet_counter_tx = packet_counter;
	}
	
	public void setPacketCounterRx(long packet_counter){
		this.packet_counter_rx = packet_counter;
	}
	
	public void setPacketTypeTx(int packet_type){
		this.packet_type_tx = packet_type;
	}
	
	public void setPacketTypeRx(int packet_type){
		this.packet_type_rx = packet_type;
	}
	
	public void setIsForeground(int isForeground){
		this.isForeground = isForeground;
	}
	
	/**
	 * Get methods
	 * @return
	 */
	public long getTime(){
		return this.time;
	}
	
	public int getAppName(){
		return this.app_name;
	}
	
	public int getNumProcess(){
		return this.num_process;
	}

	public double getPacketSizeTx(){
		return this.packet_size_tx;
	}
	
	public double getPacketSizeRx(){
		return this.packet_size_rx;
	}
	
	public double getPacketCounterTx(){
		return this.packet_counter_tx;
	}
	
	public double getPacketCounterRx(){
		return this.packet_counter_rx;
	}
	
	public int getPacketTypeTx(){
		return this.packet_type_tx;
	}
	
	public int getPacketTypeRx(){
		return this.packet_type_rx;
	}
	
	public int getIsForeground(){
		return this.isForeground;
	}
	
	public static class Application{
		
		public static final int FACEBOOK = 0;
		public static final int WHATSAPP = 1;
		public static final int SKYPE = 2;
		public static final int GMAIL = 3;
		public static final int TWITTER = 4;
		
		public static String[] applicationProcess = {"com.facebook", "com.whatsapp",
													"com.skype", "com.gmail", 
													"com.twitter" };
				
		public static String[] applicationName = { "facebook", "whatsapp",
												"skype", "gmail",
												"twitter" };
	}
	
	public static class PacketType{
		public static final int NORMAL = 1;
	}
}
