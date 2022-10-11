package vinnydgf.deanphysics.common.core.extensions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.CollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btCollisionAlgorithm;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcherInfo;
import com.badlogic.gdx.physics.bullet.collision.btManifoldResult;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.utils.GdxNativesLoader;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.ChunkSection;
import vinnydgf.deanphysics.DeanPhysics;
import vinnydgf.deanphysics.bukkit.core.bound.AABB;
import vinnydgf.deanphysics.common.core.extensions.libgdx.corpse.Corpse;

public class JBullet {


	
	static {
		GdxNativesLoader.load();
		Bullet.init();
	}

	private final DeanPhysics plugin;
	
	private btDefaultCollisionConfiguration COLLISION_CONFIG;
	
	private btCollisionDispatcher DISPATCHER;
	private btDbvtBroadphase BROADPHASE;
	private btSequentialImpulseConstraintSolver SOLVER;
	
	
	private btDiscreteDynamicsWorld DYNAMIC_WORLD;
	
	public static Set<Corpse> corpse = new HashSet<>();
	
	private long lastSim = System.nanoTime();

	//= new btCollisionDispatcher(collisionConfig);
	
	
	//= new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfig);

	
	
	public JBullet (final DeanPhysics plugin) {
		this.plugin = plugin;
		
		this.COLLISION_CONFIG = new btDefaultCollisionConfiguration();
		this.DISPATCHER = new btCollisionDispatcher(COLLISION_CONFIG);
		this.BROADPHASE = new btDbvtBroadphase();
		this.SOLVER = new btSequentialImpulseConstraintSolver();
		
		
		this.DYNAMIC_WORLD = new btDiscreteDynamicsWorld(
				DISPATCHER,
				BROADPHASE, 
				SOLVER, 
				COLLISION_CONFIG);
		
		this.DYNAMIC_WORLD.setGravity(new Vector3(0f, -9.8f, 0f));
	}
	
	
	public btDiscreteDynamicsWorld dynamicWorld() { return this.DYNAMIC_WORLD; }
	
	public void step() {
        long now = System.nanoTime();
        float delta = (float)(now - this.lastSim) / (float) TimeUnit.SECONDS.toNanos(1L);   
        DYNAMIC_WORLD.stepSimulation(delta, 100);
        Set<Corpse> corpses = new HashSet<>(corpse);
        corpses.forEach(corpse -> {
        	if (corpse.remove) {
        		JBullet.corpse.remove(corpse);
        		return;
        	}
        	corpse.tick(delta);
        });
        lastSim = now;
	}
	
	
	public void addCorpse(final Corpse corpse) {
		JBullet.corpse.add(corpse);
	}
	
	
	
	public boolean checkCollision(btCollisionObject obj0, btCollisionObject obj1) {
		CollisionObjectWrapper co0 = new CollisionObjectWrapper(obj0);
		CollisionObjectWrapper co1 = new CollisionObjectWrapper(obj1);
		
		btCollisionAlgorithm algorithm = DISPATCHER.findAlgorithm(co0.wrapper, co1.wrapper);

		btDispatcherInfo info = new btDispatcherInfo();
		btManifoldResult result = new btManifoldResult(co0.wrapper, co1.wrapper);
		
		algorithm.processCollision(co0.wrapper, co1.wrapper, info, result);

		boolean r = result.getPersistentManifold().getNumContacts() > 0;

		DISPATCHER.freeCollisionAlgorithm(algorithm.getCPointer());
		result.dispose();
		info.dispose();
		co1.dispose();
		co0.dispose();
		
		return r;
	}
	public void removeCorpse(final Corpse corpse) {
		JBullet.corpse.remove(corpse);
	}
	
	
	
	public Set<Corpse> getCorpses() {
		return new HashSet<>(corpse);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
