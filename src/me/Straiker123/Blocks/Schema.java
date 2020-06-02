package me.Straiker123.Blocks;

import me.Straiker123.ConfigAPI;
import me.Straiker123.Position;
import me.Straiker123.Abstract.AbstractSchemate;
import me.Straiker123.Scheduler.Tasker;

public class Schema implements AbstractSchemate {
	private final Schemate schem;
	private final Runnable onFinish;
	private boolean cancel;
	public Schema(Runnable onFinish, Schemate schemate) {
		schem=schemate;
		this.onFinish=onFinish;
		new Tasker() {
			public void run() {
			}}.runAsync();
	}
	@Override
	public void paste(Position position) {
		cancel=false;
		ConfigAPI c = schem.getFile();
			new Tasker() {
				int id = 0;
				boolean done = false;
				public void run() {
					if(cancel) {
						cancel();
						return;
					}
					for(int i = 0; i < 1000; ++i) {
						if(c.exists(""+id)) {
					String[] s = c.getString(""+id).split("/!!!/");
					Position pos = Position.fromString(s[0]);
					if(schem.isSetStandingPosition())
					pos.add(position.getX(),position.getY(),position.getZ());
					BlockSave.fromString(schem.getFile().getString(s[1])).load(pos);
					}else {
						done=true;
						break;
					}
					}
					if(done) {
						cancel();
						if(onFinish!=null)
							onFinish.run();
					}
				}
			}.repeatingAsync(0, 5);
	}
	@Override
	public boolean canBeCancelled() {
		return true; //this is running task, it can be cancelled
	}
	@Override
	public void cancel() {
		cancel=true;
	}

}
