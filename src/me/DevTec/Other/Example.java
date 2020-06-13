package me.DevTec.Other;

import org.bukkit.entity.Player;

import me.DevTec.ScoreboardAPI;
import me.DevTec.TheAPI;
import me.DevTec.Scheduler.Tasker;

public class Example {
	private boolean created;
	private ScoreboardAPI sb;
	public Example(Player s) {
		sb=new ScoreboardAPI(s,ScoreboardType.PACKETS);
	}
	
	public void scoreboard() {
		if(created)return;
		created=true;
		sb.setDisplayName("Name");
		sb.setLine(0, "Normal thing");
		
		new Tasker() {
			public void run() {
				if(runTimes()==200||!created) { //task run 200 times or is destroyed
					remove();
					cancel();
				}
				sb.setLine(1, "Animated:"+TheAPI.generateRandomInt(100));
			}
		}.repeatingTimes(0, 2, 200); //sync task
	}
	
	public void remove() {
		if(!created)return; //if isn't created, return
		created=false; //is created {
		sb.destroy(); //destroy scoreboard
	}
	
}
