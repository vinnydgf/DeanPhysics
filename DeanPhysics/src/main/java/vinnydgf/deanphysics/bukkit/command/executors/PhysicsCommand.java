package vinnydgf.deanphysics.bukkit.command.executors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import vinnydgf.deanphysics.DeanPhysics;
import vinnydgf.deanphysics.bukkit.command.AbstractCommand;
import vinnydgf.deanphysics.common.core.extensions.libgdx.corpse.Corpse;


public class PhysicsCommand extends AbstractCommand {



	private final DeanPhysics plugin;
	
	public PhysicsCommand(DeanPhysics plugin) {
		super(plugin, "ph");
		this.plugin = plugin;

	}


	@Override
	protected void perform(final CommandSender sender, final String cmd, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("§cO console não tem permissão para executar este comando"); return;
		}
		final Player player = (Player) sender;
		if (args.length == 0) {
			player.sendMessage("debug:");
			return;
		}
		final String subcom = args[0].toLowerCase();
		switch (subcom) {
			case "debug":
			//	Corpse.update(player);
				break;
			default:
				break;
		}
				
	}
}