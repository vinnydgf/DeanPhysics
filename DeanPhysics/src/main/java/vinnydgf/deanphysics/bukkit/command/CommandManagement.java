package vinnydgf.deanphysics.bukkit.command;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.PluginCommand;

import vinnydgf.deanphysics.DeanPhysics;
import vinnydgf.deanphysics.bukkit.command.executors.PhysicsCommand;



public class CommandManagement {

	private static final Set<String> ALLOWED_COMMANDS = new HashSet<>();
	private final DeanPhysics plugin;
	  
	public CommandManagement(DeanPhysics plugin) {
		this.plugin = plugin;
	}

	public boolean isAllowedCommand(String command) {
		if (command == null) throw new NullPointerException("command is marked non-null but is null"); 
		return ALLOWED_COMMANDS.contains(command.toLowerCase());
	}
 
	public void register() {
		Arrays.asList(Commands.values()).forEach(command -> { 
			try {
				final PluginCommand pluginCommand = plugin.getCommand(command.name);
				if (pluginCommand == null) {
					throw new RuntimeException("Missing command '" + command.name + "'"); 
				}
				ALLOWED_COMMANDS.add("/" + command.name);
				pluginCommand.getAliases().forEach(aliases -> ALLOWED_COMMANDS.add("/"+ aliases.toString()));
				Constructor<?> constructor = command.clasz.getConstructor(new Class[] { DeanPhysics.class });
				AbstractCommand bukkitCommand = (AbstractCommand) constructor.newInstance(new Object[] { this.plugin });
				pluginCommand.setExecutor(bukkitCommand);
				
			} catch (final Exception exception) {
				exception.printStackTrace();
			}
		});
	}
	  
	public enum Commands {
		PH("ph", PhysicsCommand.class);
		Commands(String name, Class<?> clasz) {
			this.name = name;
			this.clasz = clasz;
		}
	    
		private final String name;
		private final Class<?> clasz;
	}
}