package vinnydgf.deanphysics;


import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.GdxNativesLoader;

import vinnydgf.deanphysics.bukkit.command.CommandManagement;
import vinnydgf.deanphysics.bukkit.listener.PlayerListener;
import vinnydgf.deanphysics.common.core.extensions.JBullet;
import vinnydgf.deanphysics.common.management.GunManagement;


public class DeanPhysics extends JavaPlugin implements Listener {

	private PluginManager manager;
	final float armorBlockSize = 0.220f;
	final float armorBlockSizeH = armorBlockSize / 2;
	
	// tested
	
	
/*	btDefaultCollisionConfiguration collisionConfig = new btDefaultCollisionConfiguration();
	btCollisionDispatcher dispatcher = new btCollisionDispatcher(collisionConfig);
	
	btDbvtBroadphase broadphase = new btDbvtBroadphase();
	btSequentialImpulseConstraintSolver solver = new btSequentialImpulseConstraintSolver();
	public btDiscreteDynamicsWorld dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);
	
	public btBoxShape boxCollision = new btBoxShape(new Vector3(armorBlockSizeH, 0.325f, armorBlockSizeH));
	public Vector3 boxInertia = new Vector3(0f, 0f, 0f);
	public btBoxShape boxStaticCollision = new btBoxShape(new Vector3(1.0f, 1.0f, 1.0f));
	btBoxShape playerCollision = new btBoxShape(new Vector3(.15f, 0.9f, .15f));*/
	
	
	
	private CommandManagement commandManagement;

	
	public static Map<Player, btRigidBody> players = new HashMap<>();
	
	long lastSim = System.nanoTime();
	
	private GunManagement management;
	//private TPS_60 thread;
	boolean trackExplosions = true;
	
	private JBullet core;
/*	static {
		GdxNativesLoader.load();
		Bullet.init();
	}
	*/
	public static DeanPhysics instance() {
		return JavaPlugin.getPlugin(DeanPhysics.class);
	}
    private void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) manager.registerEvents(listener, this);
    }
   
    public JBullet core() {
    	return this.core;
    }
	public void onEnable() {
		this.commandManagement = new CommandManagement(this);
		this.commandManagement.register();
		this.core = new JBullet(this);
		manager = this.getServer().getPluginManager();
		this.management = new GunManagement(this);
/*	    this.thread = new TPS_60();
	    thread.start();*/
/*		dynamicsWorld.setGravity(new Vector3(0f, -10f, 0f));
		boxCollision.calculateLocalInertia(30f, boxInertia);*/
		
		registerListeners(new PlayerListener(this));
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		trackExplosions = config.getBoolean("explosions");
		
		getServer().getScheduler().runTaskTimer(this, () -> {
			
			core.step();
			
		}, 0, 1); 
		
	//	registerListeners(new PlayerListener(this), new EntityListener(this));
	}
	
	public void sendMessage(final String message) {
		Bukkit.getConsoleSender().sendMessage("§7[" + getName() + "]§r " + message);
	}

	public void onDisable() {
	//	thread.stop();
	}
	public GunManagement getManagement() {
		return this.management;
	}

}
