package vinnydgf.deanphysics.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import vinnydgf.deanphysics.DeanPhysics;


public abstract class AbstractCommand implements CommandExecutor {
	
	
	protected final DeanPhysics plugin;
	private final String permission;
	  
	public AbstractCommand(DeanPhysics plugin, String command) {
		this.plugin = plugin;
		this.permission = command;
		if (command == null) throw new NullPointerException("command is marked non-null but is null"); 
	}
	  
	public boolean onCommand(CommandSender sender, Command cmd, String lb, String[] args) {
		if (!sender.hasPermission(this.permission)) {
			sender.sendMessage("§cVocê não tem permissão para executar este comando");
			return true;
		} 
		plugin.getServer().getScheduler().runTaskAsynchronously((Plugin) plugin, () -> {
			try {
	            perform(sender, lb, args);
			} catch (final Exception exception) {
				exception.printStackTrace();
			} 
		});
		return true;
	}
	  
	protected abstract void perform(CommandSender sender, String cmd, String[] args);
}