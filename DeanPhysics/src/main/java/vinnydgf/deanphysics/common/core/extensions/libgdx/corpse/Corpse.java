package vinnydgf.deanphysics.common.core.extensions.libgdx.corpse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.CollisionConstants;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.Chunk;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.World;
import vinnydgf.deanphysics.DeanPhysics;
import vinnydgf.deanphysics.bukkit.core.bound.AABB;

public class Corpse {

	private DeanPhysics plugin;
	
	
	private World world;
	private Location location;
	public ArmorStand armorStand;
	private EntityArmorStand entityArmorStand;
	
	private Matrix4 TRANSFORM = new Matrix4();
	private Vector3 BOX_INERTIA = Vector3.Zero;
	private btCollisionShape BOX_COLLISION;
	
	

	private btDefaultMotionState MOTION_STATE;
	private btRigidBodyConstructionInfo INFO;
	
	private btRigidBody BODY;
	
	private static Map<Chunk, Set<btRigidBody>> chunk = new HashMap<>();
	private ItemStack stack;
	Vector3 boxInertia = new Vector3(0f, 0f, 0f);

	
	private ChunkMesh chunkMesh;
	
	private float life = 20f;
	public boolean remove = false;
	
	private float sizeH = 0.625f / 2f;
	btBoxShape boxCollision = new btBoxShape(new Vector3(sizeH, sizeH, sizeH));

//	btBoxShape boxCollision = new btBoxShape(new Vector3(0.1625f, 0.25f, 0.1625f));

	public Corpse (final DeanPhysics plugin) {
		this.plugin = plugin;
	}
	public Corpse (final DeanPhysics plugin, final Location location, final ItemStack stack) {
		this.plugin = plugin;
		this.location = location;
		
        location.setYaw(0f);
        location.setPitch(0f);
        location.add(0.0, -1.75, 0.0);
		
		this.stack = stack;
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.sendMessage("start location: " + location.toString());
		});

		spawnArmorstand();
		
		this.TRANSFORM.idt();
		this.TRANSFORM.setTranslation(new Vector3(
				
				// 1.77750003338 eye location

				((float) location.getX()),
				((float) location.getY() + 1.77750003338f),
				((float) location.getZ())
	        		
			));
		
	//	final Vector3 boxCollision = new Vector3(2f, 2f, 2f);
		
	//	this.BOX_COLLISION = boxCollision;

		boxCollision.calculateLocalInertia(30f, boxInertia);

	//	this.BOX_COLLISION = new btBoxShape(boxCollision);
		this.MOTION_STATE = new btDefaultMotionState(TRANSFORM);
		this.INFO = new btRigidBody.btRigidBodyConstructionInfo(
				//30f
				30f, MOTION_STATE, 
				boxCollision,
				boxInertia);
		
		INFO.setAdditionalDamping(true);
		
		this.BODY = new btRigidBody(INFO);
		plugin.core().dynamicWorld().addRigidBody(BODY);
		INFO.dispose();
	}
	
	public btRigidBody body() { return this.BODY; }
	
	public void kill() {
		if (BODY.isDisposed()) return;
		plugin.core().dynamicWorld().removeRigidBody(BODY);
	}
	
	
	public Chunk section(float x, float y, float z) {
		final BlockPosition position = entityArmorStand.getChunkCoordinates();
		return world.getChunkAtWorldCoords(position);
	}
	
	@SuppressWarnings("deprecation")
	public void actionBar(final String message) {
		Bukkit.getOnlinePlayers().forEach(player -> {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
		});
	}
	
	public void tick(float delta) {
		final BlockPosition position = entityArmorStand.getChunkCoordinates();
		final Chunk chunk = world.getChunkAtWorldCoords(position);
    //	plugin.sendMessage(location.toString());
    	
	//	actionBar(" x: " + location.getX() + " y: " + location.getY() + " z: " + location.getZ());
		final btTriangleMesh meshTerrain = new btTriangleMesh();
		meshTerrain.preallocateVertices(16*16*16);
		
		Set<AABB> axisMesh = new HashSet<>();
	    
				tick();
//		tickLegacy();
/*		boolean unempty = false;

		int tcx = chunk.locX << 4;
		int tcz = chunk.locZ << 4;
			
		for (int x = tcx; x <= (tcx | 15); x++) {
			for (int y = 0; y <= 256; y++) {
				for (int z = tcz; z <= (tcz | 15); z++) {
							
					final org.bukkit.block.Block block = block(new BlockPosition(x, y, z));
					if (block == null) continue;
					if (block.isLiquid() || block.isEmpty()) continue;

					AABB aabb = new AABB(plugin, block);
					axisMesh.add(aabb);

					final float minX = aabb.min().x,	maxX = aabb.max().x,
								minY = aabb.min().y,	maxY = aabb.max().y,
								minZ = aabb.min().z,	maxZ = aabb.max().z;
													
					// NORTH
					meshTerrain.addTriangle(new Vector3(maxX, maxY, minZ), new Vector3(maxX, minY, minZ), new Vector3(minX, minY, minZ));
					meshTerrain.addTriangle(new Vector3(minX, maxY, minZ), new Vector3(maxX, minY, minZ), new Vector3(minX, minY, minZ));
									
					// SOUTH
					meshTerrain.addTriangle(new Vector3(maxX, maxY, maxZ), new Vector3(maxX, minY, maxZ), new Vector3(minX, minY, maxZ));
					meshTerrain.addTriangle(new Vector3(minX, maxY, maxZ), new Vector3(maxX, minY, maxZ), new Vector3(minX, minY, maxZ));

					// WEST
					meshTerrain.addTriangle(new Vector3(minX, maxY, maxZ), new Vector3(minX, minY, maxZ), new Vector3(minX, minY, minZ));
					meshTerrain.addTriangle(new Vector3(minX, maxY, minZ), new Vector3(minX, minY, maxZ), new Vector3(minX, minY, minZ));
			
					// EAST
					meshTerrain.addTriangle(new Vector3(maxX, maxY, maxZ), new Vector3(maxX, minY, maxZ), new Vector3(maxX, minY, minZ));
					meshTerrain.addTriangle(new Vector3(maxX, maxY, minZ), new Vector3(maxX, minY, maxZ), new Vector3(maxX, minY, minZ));

					unempty = true;
				}
			}
		} 
		if (unempty) {
			this.chunkMesh = new ChunkMesh(plugin.core(), chunk, axisMesh, meshTerrain);

		}*/
		
		
		this.BODY.getMotionState().getWorldTransform(this.TRANSFORM);
		Quaternion quaternion = new Quaternion();
		quaternion = this.TRANSFORM.getRotation(quaternion);
		EulerAngle eulerAngle = quatToEul(quaternion);
		
	//	actionBar("x:" + eulerAngle.getX() + " y:" + eulerAngle.getY() + " z: " + eulerAngle.getZ());
		this.armorStand.setHeadPose(eulerAngle);
		
		final Vector3 origin = TRANSFORM.getTranslation(new Vector3());
		
	    location.setX(Double(origin.x));
	    location.setY(Double(origin.y) - 1.77750003338f + 0.05f);
	    location.setZ(Double(origin.z));
	    location.add(
	    		-Math.sin(eulerAngle.getZ() * 0.625f / 2),
	    		-Math.cos(eulerAngle.getX() * 0.625f / 2 - Math.cos(eulerAngle.getZ() * 0.625f / 2)), 
	    		-Math.sin(eulerAngle.getX() * 0.625f / 2));
	    
	    armorStand.teleport(location);
	    
	    this.life -= delta;

	    if (location.getY() < -10 || location.getY() > 256 || armorStand.isDead() || life <= 0) {
	    
	    	Corpse.chunk.forEach((ch, set) -> {
	    		set.forEach(body -> {
	    			plugin.core().dynamicWorld().removeRigidBody(body);
	    			
	    		});
	    		
	    	});
	    	Corpse.chunk.clear();
	    	this.armorStand.remove();
	    	plugin.core().dynamicWorld().removeRigidBody(BODY);
	    	remove = true;
	    }
			
	}
	
	
	private btBoxShape boxStaticCollision = new btBoxShape(new Vector3(1.0f, 1.0f, 1.0f));

	
	
	public static class IntRef {
		public int offset;
	}
	
	private List<btRigidBody> pool = new ArrayList<>();

	private Set<Location> visited = new HashSet<>();
	
	
	public void tick() {
		final BlockPosition position = entityArmorStand.getChunkCoordinates();
		final Chunk chunk = world.getChunkAtWorldCoords(position);
		
		if (!Corpse.chunk.containsKey(chunk)) {
			Set<btRigidBody> axisMesh = new HashSet<>();
		    
			boolean unempty = false;

			int tcx = chunk.locX << 4;
			int tcz = chunk.locZ << 4;
				
			for (int x = tcx; x <= (tcx | 15); x++) {
				for (int y = 0; y <= 256; y++) {
					for (int z = tcz; z <= (tcz | 15); z++) {
								
						final org.bukkit.block.Block block = block(new BlockPosition(x, y, z));
						if (block == null) continue;
						if (block.isLiquid() || block.isEmpty()) continue;

						unempty = true;
						AABB aabb = new AABB(plugin, block);
						
						Matrix4 transform = new Matrix4();
						transform.idt();
						transform.setTranslation(
								aabb.getCenterX() + .5f,
								aabb.getCenterY() + .5f,
								aabb.getCenterZ() + .5f);
									
						btRigidBodyConstructionInfo info; 
						info = new btRigidBody.btRigidBodyConstructionInfo(0f, null, boxStaticCollision, Vector3.Zero);
						btRigidBody body = new btRigidBody(info);
						body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
						body.setWorldTransform(transform);
						body.setActivationState(0);
						plugin.core().dynamicWorld().addRigidBody(body);
						axisMesh.add(body);
					}
				}
			}
			
			if (!unempty) return;
			Corpse.chunk.put(chunk, axisMesh);
			
		}
		Corpse.chunk.get(chunk).forEach(body -> {
			
			if (remove) return;
/*			if (plugin.core().checkCollision(BODY, body)) {
				
				
				
				Vector3 veloc = body.getLinearVelocity();
				veloc.x = 0 - veloc.x;
				veloc.y = 0 - veloc.y;
				veloc.z = 0 - veloc.z;
				
				
				veloc.scl(2);
				body.setLinearVelocity(veloc);


				
			}*/

			
/*			if (BODY.isDisposed()) return;
			if (body ) return;
			if (plugin.core().checkCollision(BODY, body)) {*/
				
				
				
				
				
				
			
		});
		
	}
	
/*	
    set {_v} to velocity of projectile
    set {_dir} to event.getHitBlockFace()
    add passengers of passengers of passengers of projectile to {_pass::*}
    set metadata "bounce" of {_pass::1} to "%{_dir}%"
    set {_e} to event.getHitBlock()
    drawDot particle "blockcrack", material {_e}, center location of projectile, visibleRange 32, pulseDelay 0, keepFor 5 ticks
    play sound "grenades.he_bounce" with volume 1 at projectile
    set {_dx} to abs({_dir}.getModX())
    set {_dy} to abs({_dir}.getModY())
    set {_dz} to abs({_dir}.getModZ())
                
    if max({_dx}, {_dy}, and {_dz}) is {_dx}:
        set x component of {_v} to 0 - x component of {_v}
    else if max({_dx}, {_dy}, and {_dz}) is {_dz}:
        set z component of {_v} to 0 - z component of {_v}
    else:
        set y component of {_v} to 0 - y component of {_v}
                    
    spawn egg at projectile
    set velocity of last spawned egg to {_v} ** vector(0.5, 0.5, 0.5)
    set metadata "bounces" of last spawned egg to (metadata "bounces" of projectile + 1) ? 1 
        
    loop passengers of projectile:
        make loop-value ride last spawned egg
	*/
	
	public void tickLegacy() {
		
	    IntRef intref = new IntRef();
	    intref.offset = 0;
		for (int y = -2; y < 2; y++) {
			for (int z = -2; z < 2; z++) {
				for (int x = -2; x < 2; x++) {
					Location loc = location.clone().add(x, y, z);
					Block b = loc.getBlock();
					Location bloc = b.getLocation(loc);
					if (b.getType() == Material.AIR || !b.getType().isOccluding() || visited.contains(bloc)) {
						continue;
					}
					visited.add(bloc);
					Matrix4 transform = new Matrix4();
					transform.idt();
					transform.setTranslation(
					loc.getBlockX() + .5f,
					loc.getBlockY() + .5f,
					loc.getBlockZ() + .5f);
								
								
					if (intref.offset >= pool.size()) {
						btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(0f, null, boxStaticCollision, new Vector3(0f, 0f, 0f));
						btRigidBody body = new btRigidBody(info);
						body.setActivationState(0);
						body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
						pool.add(body);
					}
								
					btRigidBody body = pool.get(intref.offset);
					body.setWorldTransform(transform);
					plugin.core().dynamicWorld().removeRigidBody(body);
					plugin.core().dynamicWorld().addRigidBody(body);
					body.setActivationState(0);
					intref.offset++;

				}
			}
		}
		
        for (int i = intref.offset; i < this.pool.size(); i++) {
        	final btRigidBody body = this.pool.get(i);
/*        	if (plugin.core().checkCollision(BODY, body)) {
        		plugin.sendMessage("atingiu alguem");
        	}*/
        	this.pool.get(i).setActivationState(CollisionConstants.DISABLE_SIMULATION);
		}
        visited.clear();
		
	}
	
	private double Double(float value) {
		return Double.valueOf(Float.valueOf(value).toString()).doubleValue();
	}
	private org.bukkit.block.Block block(final BlockPosition blockPosition) {
		return new Location(world.getWorld(), blockPosition.getX(), blockPosition.getY(), blockPosition.getZ()).getBlock();
	}
	
	
	private void spawnArmorstand() {
		this.armorStand = location.getWorld().spawn(location, ArmorStand.class);
		this.world = ((CraftWorld)location.getWorld()).getHandle();
		this.entityArmorStand = ((CraftArmorStand)armorStand).getHandle();
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setHelmet(stack);
	}
	
	public EulerAngle quaternionToEuler(final Quaternion quaternion) {
	    double sqw = quaternion.w * quaternion.w;
	    double sqx = quaternion.x * quaternion.x;
	    double sqy = quaternion.y * quaternion.y;
	    double sqz = quaternion.z * quaternion.z;
	    
	    double unit = sqx + sqy + sqz + sqw;
	    double test = quaternion.x * quaternion.w - quaternion.y * quaternion.z;
	    
	    EulerAngle euler = new EulerAngle(0, 0, 0);
	    if (test > 0.4995f * unit)
	    {
	        euler.setY(2 * MathUtils.atan2(quaternion.y, quaternion.x));
	        euler.setX(MathUtils.PI2);
	        euler.setZ(0);
	    }
	    if (test < -0.4995f * unit)
	    {
	        euler.setY(-2 * MathUtils.atan2(quaternion.y, quaternion.x));
	        euler.setX(MathUtils.PI2);
	        euler.setZ(0);
	        return euler;
	    }
	    // Yaw
	    euler.setY(MathUtils.atan2(2 * quaternion.w * quaternion.y + 2 * quaternion.z * quaternion.x,
	        1 - 2 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y)));
	    // Pitch
	    euler.setX(MathUtils.asin(2 * (quaternion.w * quaternion.x - quaternion.y * quaternion.z)));
	    // Roll
	    euler.setZ(MathUtils.atan2(2 * quaternion.w * quaternion.z + 2 * quaternion.x * quaternion.y,
	        1 - 2 * (quaternion.z * quaternion.z + quaternion.x * quaternion.x)));
	    return euler;
	}
	
	
	public EulerAngle quatToEul(Quaternion q) {
	    float sqw = q.w * q.w;
	    float sqx = q.x * q.x;
	    float sqy = q.y * q.y;
	    float sqz = q.z * q.z;
	    float unit = sqx + sqy + sqz + sqw;
	    float test = q.x * q.y + q.z * q.w;
	    

	    if (test > 0.499 * unit) return new EulerAngle(Math.PI / 2, 2 * Math.atan2(Double(q.x), Double(q.w)), 0.0);
	    if (test < -0.499 * unit)  return new EulerAngle(-Math.PI / 2, -2 * Math.atan2(Double(q.x), Double(q.w)), 0.0);
	    
	    return new EulerAngle(Math.atan2(Double((2 * q.y * q.w - 2 * q.x * q.z)),
	                    Double((sqx - sqy - sqz + sqw))
	            ),
	            -Math.atan2(Double( (2 * q.x * q.w - 2 * q.y * q.z)),
	                    Double((-sqx + sqy - sqz + sqw))
	            ),
	            -Math.asin(Double((2 * test / unit)))
	    );
	}
}
		
/*	
		public PBlock(Grenade plugin, Location location, ItemStack stack) {
		this(plugin, location);
		
		headItem = stack;
		//headItem = new ItemStack(block.getType());
		
		location.setYaw(0f);
		location.setPitch(0f);
		location.add(0.0, -1.8, 0.0);
		stand = location.getWorld().spawn(location, ArmorStand.class);
		stand.setGravity(false);
		stand.setVisible(false);
		stand.setHelmet(headItem);
		

		transform.idt();
		transform.setTranslation(new Vector3(
				Float(location.getX()),
				Float(location.getY() + 1.8f),
				Float(location.getZ())
	        		
			));
		btDefaultMotionState motionState = new btDefaultMotionState(transform);
		btRigidBodyConstructionInfo info = new btRigidBody.btRigidBodyConstructionInfo(30f, motionState, plugin.boxCollision, plugin.boxInertia);
		info.setAdditionalDamping(true);
		body = new btRigidBody(info);
		info.dispose();
	
	public EulerAngle quaternionToEuler(final Quaternion quaternion) {
	    double sqw = quaternion.w * quaternion.w;
	    double sqx = quaternion.x * quaternion.x;
	    double sqy = quaternion.y * quaternion.y;
	    double sqz = quaternion.z * quaternion.z;
	    
	    double unit = sqx + sqy + sqz + sqw;
	    double test = quaternion.x * quaternion.w - quaternion.y * quaternion.z;
	    
	    EulerAngle euler = new EulerAngle(0, 0, 0);
	    if (test > 0.4995f * unit)
	    {
	        euler.setY(2 * MathUtils.atan2(quaternion.y, quaternion.x));
	        euler.setX(MathUtils.PI2);
	        euler.setZ(0);
	    }
	    if (test < -0.4995f * unit)
	    {
	        euler.setY(-2 * MathUtils.atan2(quaternion.y, quaternion.x));
	        euler.setX(MathUtils.PI2);
	        euler.setZ(0);
	        return euler;
	    }
	    // Yaw
	    euler.setY(MathUtils.atan2(2 * quaternion.w * quaternion.y + 2 * quaternion.z * quaternion.x,
	        1 - 2 * (quaternion.x * quaternion.x + quaternion.y * quaternion.y)));
	    // Pitch
	    euler.setX(MathUtils.asin(2 * (quaternion.w * quaternion.x - quaternion.y * quaternion.z)));
	    // Roll
	    euler.setZ(MathUtils.atan2(2 * quaternion.w * quaternion.z + 2 * quaternion.x * quaternion.y,
	        1 - 2 * (quaternion.z * quaternion.z + quaternion.x * quaternion.x)));
	    return euler;
	}
	
}*/
