package vinnydgf.deanphysics.bukkit.listener;

import java.util.ListIterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.badlogic.gdx.math.Vector3;

import vinnydgf.deanphysics.DeanPhysics;
import vinnydgf.deanphysics.common.core.extensions.JBullet;
import vinnydgf.deanphysics.common.core.extensions.libgdx.corpse.Corpse;

import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityExplodeEvent;

public class PlayerListener implements Listener {

	private final DeanPhysics plugin;
	
	public PlayerListener (final DeanPhysics plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void PlayerInteractEvet (final PlayerInteractEvent event) {
		final Player player = event.getPlayer();
		final Location eye = player.getEyeLocation();
		final Vector direction = eye.getDirection().normalize();
		final ItemStack item = event.getItem();
		final Action action = event.getAction();
		
		if (event.getHand().equals(EquipmentSlot.OFF_HAND)) return;
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (!(item.getType() == Material.POTATO_ITEM)) return;
			
			Corpse corpse = new Corpse(plugin, eye, new ItemStack(Material.GOLD_BLOCK));
			Vector3 velocity = new Vector3(
					(float) direction.getX(),
					(float) direction.getY(),
					(float) direction.getZ()
				);
			velocity.nor();
			velocity.scl(20);
			corpse.body().setLinearVelocity(velocity);
			//corpse.body().setLinearVelocity(velocity);
			
			plugin.core().addCorpse(corpse);
			
		}
	}
	@EventHandler
	public void explode(final EntityExplodeEvent e) {
		Location loc = e.getLocation();
		plugin.core().getCorpses().forEach(it -> {
			Location l = it.armorStand.getLocation();
			if (loc.distanceSquared(l) < e.getYield() * e.getYield() * e.getYield()) {
				Vector3 vec = new Vector3(
						(float) (loc.getX() - l.getX()),
						(float) (loc.getY() - l.getY()),
						(float) (loc.getZ() - l.getZ()));
				vec.nor();
				vec.scl(-e.getYield() * 30);
				it.body().setLinearVelocity(vec);
			}
		});
		
		
		// parte dos blocos normais
		
		ListIterator<Block> ite = e.blockList().listIterator();
		while (ite.hasNext()) {
			Block it = ite.next();
			if (it.getType() == Material.TNT) continue;

			Location l = it.getLocation().add(0.5, 0.5, 0.5);
			Corpse corpse = new Corpse(plugin, l, new ItemStack(it.getType()));
			JBullet.corpse.add(corpse);
			Vector3 vec = new Vector3(
					(float) (loc.getX() - l.getX()),
					(float) (loc.getY() - l.getY()),
					(float) (loc.getZ() - l.getZ()));
			vec.nor();
			vec.scl(-e.getYield() * 30);
			corpse.body().setLinearVelocity(vec);
			it.setType(Material.AIR);
			ite.remove();
		}
	}
	
}
