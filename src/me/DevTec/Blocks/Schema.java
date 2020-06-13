package me.DevTec.Blocks;

import me.DevTec.ConfigAPI;
import me.DevTec.Abstract.AbstractSchemate;
import me.DevTec.Other.Position;
import me.DevTec.Scheduler.Tasker;

public class Schema implements AbstractSchemate {
	private final Schemate schem;
	private final Runnable onFinish;
	private boolean cancel;
	public Schema(Runnable onFinish, Schemate schemate) {
		schem=schemate;
		this.onFinish=onFinish;
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
					String[] s = c.getString(""+id).split("/!_!/");
					Position pos = Position.fromString(s[0]);
					pos.setWorld(position.getWorld());
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
