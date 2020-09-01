package me.DevTec.TheAPI.Utils.TheAPIUtils.Command;

import org.bukkit.command.CommandSender;

import me.DevTec.TheAPI.TheAPI;
import me.DevTec.TheAPI.Utils.StringUtils;
import me.DevTec.TheAPI.Utils.DataKeeper.User;

public class TAC_User {

	public TAC_User(CommandSender s, String[] args) {
		if(args.length==1) {
			TheAPI.msg("&e/TheAPI User <NAME/UUID> Set <key> <value>", s);
			TheAPI.msg("&e/TheAPI User <NAME/UUID> Get <key>", s);
			TheAPI.msg("&e/TheAPI User <NAME/UUID> Exists <key>", s);
			TheAPI.msg("&e/TheAPI User <NAME/UUID> Keys [key]", s);
			TheAPI.msg("&e/TheAPI User <NAME/UUID> Reload", s);
			return;
		}
		
		if(args.length==2) {
			if(!TheAPI.existsUser(args[1]) && args[1].equals("*")) {
				TheAPI.msg("&eUser &6"+args[1]+" &edoesn't exist.", s);
				return;
			}
		}
		
		if(args[3].equalsIgnoreCase("reload")) {
			if(args[1].equals("*")) {
				TheAPI.msg("&eReloading cached users..", s);
				for(User u : TheAPI.getCachedUsers())
				u.getData().reload(u.getData().getFile());
				TheAPI.msg("&eReload of cached users finished.", s);
				return;
			}
			TheAPI.msg("&eReloading user &6"+args[1]+"&e..", s);
			TheAPI.getUser(args[1]).getData().reload(TheAPI.getUser(args[1]).getData().getFile());
			TheAPI.msg("&eReload of user &6"+args[1]+"&e finished.", s);
			return;
		}
		
		if(args[3].equalsIgnoreCase("exists")) {
			if(args[1].equals("*")) {
				TheAPI.msg("&eThis operation isn't allowed.", s);
			}
			if(args.length==4)
				TheAPI.msg("&e/TheAPI User <NAME/UUID> Exists <key>", s);
			else
			TheAPI.msg(TheAPI.getUser(args[1]).exists(args[4])+"", s);
			return;
		}
		
		if(args[3].equalsIgnoreCase("Get")) {
			if(args[1].equals("*")) {
				TheAPI.msg("&eThis operation isn't allowed.", s);
			}
			if(args.length==4)
				TheAPI.msg("&e/TheAPI User <NAME/UUID> Exists <key>", s);
			else
			TheAPI.msg(TheAPI.getUser(args[1]).get(args[4])+"", s);
			return;
		}
		
		if(args[3].equalsIgnoreCase("Set")) {
			if(args[1].equals("*")) {
				TheAPI.msg("&eThis operation isn't allowed.", s);
			}
			if(args.length==4||args.length==5)
				TheAPI.msg("&e/TheAPI User <NAME/UUID> Exists <key>", s);
			else
			TheAPI.getUser(args[1]).set(args[4], args[5]);
			TheAPI.msg("&eIn user data of &6"+args[1]+" &eset value &6"+args[4]+" &eto &6"+args[5], s);
			return;
		}
		
		if(args[3].equalsIgnoreCase("keys")) {
			if(args[1].equals("*")) {
				TheAPI.msg("&eThis operation isn't allowed.", s);
			}
			if(args.length==4)
				TheAPI.msg("&eUser &6"+args[1]+"&e has these keys: &6"+StringUtils.join(TheAPI.getUser(args[1]).getKeys(), ", "), s);
			else
			TheAPI.msg("&eUser &6"+args[1]+"&e has these keys: &6"+StringUtils.join(TheAPI.getUser(args[1]).getKeys(args[4]), ", "), s);
			return;
		}
	}

}
