package vinnydgf.deanphysics.common.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.CollisionConstants;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

import vinnydgf.deanphysics.DeanPhysics;


public class Grenades {
	
	private final DeanPhysics plugin;
	
	private Matrix4 transform = new Matrix4();
	
//	public btRigidBody body;
	public Projectile proj;
	public Player player;
	public ArmorStand stand;
	
	@SuppressWarnings("deprecation")
	public Grenades(final DeanPhysics plugin, final Player player) {

		this.plugin = plugin; 
		this.player = player;
		
		
/*		//final Location hand = getHandlocation(25);
		final World world = hand.getWorld();

		this.proj = world.spawn(hand, Egg.class);
		proj.setVelocity(hand.getDirection().multiply(1));
		
		final Slime slime = spawnSlime(hand);
		proj.setPassenger(slime);
		this.stand = spawnArmorStand(hand);
		slime.setPassenger(stand);
		
		final Location loc = player.getLocation();
		final Vector v = loc.getDirection().multiply(1);
		final Vector3 vec = new Vector3(F(v.getX()), F(v.getY()), F(v.getZ()));
		vec.nor();
		
		
		transform.idt();
		transform.setTranslation(new Vector3(F(hand.getX()), F(hand.getY() + 1.8f), F(hand.getZ())));*/
		
/*		btDefaultMotionState motionState = new btDefaultMotionState(transform);
		btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(30f, motionState, plugin.boxCollision, plugin.boxInertia);
		
		info.setAdditionalDamping(true);
		body = new btRigidBody(info);
		info.dispose();
		plugin.dynamicsWorld.addRigidBody(body);*/

		
	//	body.setLinearVelocity(vec);
		                                                                                           
	}

	public void kill() {
	//	if (body.isDisposed()) return;
		stand.remove();
/*		plugin.dynamicsWorld.removeRigidBody(body);*/
	}

	public Slime spawnSlime(final Location local) {
		final Slime slime = local.getWorld().spawn(local, Slime.class);
		slime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, false));
		slime.setSize(1);
		slime.setSize(-5);
		slime.setRemoveWhenFarAway(false);
		slime.setSilent(true);
		return slime;
	}
	
	public ArmorStand spawnArmorStand(final Location local) {
		final ArmorStand armor = local.getWorld().spawn(local, ArmorStand.class);
		armor.setHelmet(new ItemStack(Material.GOLD_BLOCK));
		armor.setVisible(false);
		return armor;
	}

/*	private Location getHandlocation(final double degrees) {
        final Location eye = player.getEyeLocation();
        final Vector direc = eye.getDirection();
   //     return eye.add(Util.rotateVectorAroundY(direc, degrees));
	}*/

	public static class IntRef {
		public int offset;
	}

	public List<btRigidBody> pool = new ArrayList<>();
	public Set<Location> visited = new HashSet<>();
	
	public void stepSimulation(final float delta) {
		
/*	    IntRef intref = new IntRef();
	    intref.offset = 0;
	    if (!body.isActive()) { 
	    	Bukkit.broadcastMessage("O corpo está inativo");
	    	return;
	    }
	    for (int y = -2; y < 2; y++) {
	    	for (int z = -2; z < 2; z++) {
	    		for (int x = -2; x < 2; x++) {
	    			Location loc = proj.getLocation().clone().add(x, y, z);
	    			Block b = loc.getBlock();
	    			Location bloc = b.getLocation(loc);
	    			if (b.getType() == Material.AIR || !b.getType().isOccluding() || visited.contains(bloc)) continue;
	    			
					visited.add(bloc);
					Matrix4 transform = new Matrix4();
					transform.idt();
					transform.setTranslation(
							loc.getBlockX() + .5f,
							loc.getBlockY() + .5f,
							loc.getBlockZ() + .5f);
								
					if (intref.offset >= pool.size()) {

						btBoxShape shape = plugin.boxCollision;
						btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(0f, null, plugin.boxCollision, new Vector3(0f, 0f, 0f));
						btRigidBody body = new btRigidBody(info);
						body.setActivationState(0);
						body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
						pool.add(body);
					}
								
					btRigidBody body = pool.get(intref.offset);
					body.setWorldTransform(transform);
					plugin.dynamicsWorld.removeRigidBody(body);
					plugin.dynamicsWorld.addRigidBody(body);
					body.setActivationState(0);
					intref.offset++;
				}
			}
	    }
		
        for (int i = intref.offset; i < this.pool.size(); i++) {
        	this.pool.get(i).setActivationState(CollisionConstants.DISABLE_SIMULATION);
		}
        DeanPhysics.players.forEach((k, v) -> {
        	btRigidBody body = v;
        	Player p = k;
        	Location loc = p.getLocation();
        	Matrix4 t = body.getCenterOfMassTransform();
        	t.setTranslation(new Vector3(
        			F(loc.getX()),
        			F(loc.getY() + 0.9f),
        			F(loc.getZ())
        			));
        	body.setCenterOfMassTransform(t);
        	body.setActivationState(CollisionConstants.ACTIVE_TAG);
        });
        plugin.dynamicsWorld.stepSimulation(delta, 100);
        visited.clear();
	*/
	}
	
	private Float F(double doubleValue) {
	    return (float) doubleValue;
	}
	
	public void tick(final float delta) {
/*	    this.body.getMotionState().getWorldTransform(this.transform);
	    Quaternion rot = new Quaternion();
	    this.transform.getRotation(rot);*/
	//    Bukkit.broadcastMessage("Rotação em Quaternation X:" + rot.x + " Y: " + rot.y + " Z: " + rot.z + " W: " + rot.w);
	   // EulerAngle euler = Util.quaternationToEuler(rot);
	    
	   // stand.setHeadPose(euler);
	}
}
