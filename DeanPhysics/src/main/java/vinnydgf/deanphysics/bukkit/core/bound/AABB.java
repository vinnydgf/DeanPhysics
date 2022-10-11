package vinnydgf.deanphysics.bukkit.core.bound;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Entity;

import com.google.common.base.Predicate;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import net.minecraft.server.v1_12_R1.BlockCauldron;
import net.minecraft.server.v1_12_R1.BlockPosition;
import net.minecraft.server.v1_12_R1.BlockStairs;
import net.minecraft.server.v1_12_R1.IBlockData;
import net.minecraft.server.v1_12_R1.WorldServer;
import vinnydgf.deanphysics.DeanPhysics;
import vinnydgf.deanphysics.bukkit.core.bound.mathf.Vector3;
import vinnydgf.deanphysics.bukkit.core.bound.mathf.collision.Ray;

public class AABB {

	private DeanPhysics plugin;
	private AxisAlignedBB aabb;
	private BlockPosition blockPos;
	public World world;
	
	
	private WorldServer worldServer;
	
	
	private Vector3 bounds[];
	private Vector3 min;
	private Vector3 max;
	private Vector3 extent;
	private net.minecraft.server.v1_12_R1.Block blockNMS;
	private List<AxisAlignedBB> aabbList = new ArrayList<>();
	double angle;
	
	private IBlockData blockData;
	
	
	public AABB (final DeanPhysics plugin, final Block block, final AxisAlignedBB aabb) {
		this.world = block.getWorld();
		this.blockPos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		this.aabb = aabb;
		this.min = new Vector3(blockPos.getX() + aabb.a, blockPos.getY() + aabb.b, blockPos.getZ() + aabb.c);
		this.max = new Vector3(blockPos.getX() + aabb.d, blockPos.getY() + aabb.e, blockPos.getZ() + aabb.f);
		this.bounds = new Vector3[] { this.min, this.max };
		this.extent = max.cpy().sub(min);
		this.angle = 0;
	}
	
	public AABB (final DeanPhysics plugin, final Vector3 min, final Vector3 max) {
		this.plugin = plugin;
		this.min = min;
		this.max = max;
		this.bounds = new Vector3[] { this.min, this.max };
	}
	
    /**
     * Create a new AABB from a given block.
     * @param block - the block.
     */
	
	public AABB (final DeanPhysics plugin, final Block block) {
		this.plugin = plugin;
		this.world = block.getWorld();
		this.worldServer = ((CraftWorld) world).getHandle();
		this.blockPos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		this.blockData = worldServer.getType(blockPos);
		this.blockNMS = blockData.getBlock();

		this.aabb = worldServer.getType(blockPos).d(worldServer, blockPos);
		this.min = new Vector3(blockPos.getX() + aabb.a, blockPos.getY() + aabb.b, blockPos.getZ() + aabb.c);
		this.max = new Vector3(blockPos.getX() + aabb.d, blockPos.getY() + aabb.e, blockPos.getZ() + aabb.f);
		this.bounds = new Vector3[] { this.min, this.max };
		
		
	}
	

	public List<AxisAlignedBB> getAABBList () {
/*		BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
		this.worldSe = ((CraftWorld) world).getHandle();
		this.blockData = craftWorld.getType(pos);
		this.blockNMS = blockData.getBlock();
		*/
		List<AxisAlignedBB> bounds = new ArrayList<>();
		if (blockNMS instanceof BlockStairs) {
			try {
				BlockStairs bs = (BlockStairs) blockNMS;
				this.blockData = bs.updateState(blockData, worldServer, blockPos);
				Class<?> c = BlockStairs.class;
				Constructor<?> cons = c.getDeclaredConstructor(IBlockData.class);
				cons.setAccessible(true);
				Object obj = cons.newInstance(blockData);
				Method method = obj.getClass().getDeclaredMethod("y", IBlockData.class);
				method.setAccessible(true);
				Object list = method.invoke(obj, blockData);
				return (List<AxisAlignedBB>) list;
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (blockNMS instanceof BlockCauldron) {
			AxisAlignedBB AABB_LEGS = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D);
			AxisAlignedBB AABB_WALL_WEST = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.125D, 1.0D, 1.0D);
			AxisAlignedBB AABB_WALL_NORTH = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.125D);
			AxisAlignedBB AABB_WALL_EAST = new AxisAlignedBB(0.875D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
		    AxisAlignedBB AABB_WALL_SOUTH = new AxisAlignedBB(0.0D, 0.0D, 0.875D, 1.0D, 1.0D, 1.0D);;
		    
		    Collections.addAll(bounds, AABB_LEGS, AABB_WALL_WEST, AABB_WALL_NORTH, AABB_WALL_EAST, AABB_WALL_SOUTH);
		    return bounds;
		}
		bounds.add(aabb);
		return bounds;
		
		
	}
	
	
	
	public Vector3 min() { return this.min; }
	
	public Vector3 max() { return this.max; }
	
	
	
    public float getWidthX() {
        return (this.max.x - this.min.x);
    }

    /**
     * Gets the width of the bounding box in the z direction.
     *
     * @return the width in the z direction
     */
    public float getWidthZ() {
        return (this.max.z - this.min.z);
    }
	
    public float getCenterX() {
        return (this.min.x + this.getWidthX() * 0.5f);
    }

    /**
     * Gets the y coordinate of the center of the bounding box.
     *
     * @return the center's y coordinate
     */
    public float getCenterY() {
        return (this.min.y + this.getHeight() * 0.5f);
    }

    /**
     * Gets the z coordinate of the center of the bounding box.
     *
     * @return the center's z coordinate
     */
    public float getCenterZ() {
        return (this.min.z + this.getWidthZ() * 0.5f);
    }

    /**
     * Gets the center of the bounding box.
     *
     * @return the center
     */
    public Vector3 getCenter() {
        return new Vector3(this.getCenterX(), this.getCenterY(), this.getCenterZ());
    }
	
	public float getHeight() {
        return (this.max.y - this.min.y);
    }
	public void translate(final Vector3 pos) {
	    this.min = this.min.add(pos);
	    this.max = this.max.add(pos);
	}
	
	public List<Entity> getEntitiesInside(Entity entity, Predicate<? super Entity> predicate) {
		net.minecraft.server.v1_12_R1.World nmsWorld = (net.minecraft.server.v1_12_R1.World) world;
		return null;
	}
	
	public AxisAlignedBB axisAlignedBB() { return this.aabb; }
	
	public Vector3 intersectRay(final Ray ray, float minDist, float maxDist) {
		final Vector3 invDir = ray.invDir();
		

        boolean signDirX = invDir.x < 0;
        boolean signDirY = invDir.y < 0;
        boolean signDirZ = invDir.z < 0;
        
        
        Vector3 bbox = signDirX ? max : min;
        double tmin = (bbox.x - ray.origin().x) * invDir.x;
        bbox = signDirX ? min : max;
        double tmax = (bbox.x - ray.origin().x) * invDir.x;
        bbox = signDirY ? max : min;
        double tymin = (bbox.y - ray.origin().y) * invDir.y;
        bbox = signDirY ? min : max;
        double tymax = (bbox.y - ray.origin().y) * invDir.y;
        
        if ((tmin > tymax ) || (tymin > tmax)) { return null; }
        
        if (tymin > tmin) { tmin = tymin; }
        
        if (tymax < tmax) { tmax = tymax; }
        
        if (tymax < tmax) { tmax = tymax; }
        
        bbox = signDirZ ? max : min;
        double tzmin = (bbox.z - ray.origin().z) * invDir.z;
        bbox = signDirZ ? min : max;
        double tzmax = (bbox.z - ray.origin().z) * invDir.z;
        
        if ((tmin > tzmax) || (tzmin > tmax)) { return null; }
        
        if (tmin > tmin) { tmin = tzmin; }
        
        if (tzmax < tmax) { tmax = tzmax; }
       
        if ((tmin < maxDist) && (tmax > minDist)) {
        	return ray.getPointAtDistance((float) tmin);
        }
        
        return null;
	}
	
	
	
	
	public Vector3 inAABB(final Ray ray) {
		final Vector3 direction = ray.direction();
		final Vector3 origin = ray.origin();
		final Vector3 dirInv = ray.invDir().scl(1);
		float tMin, min,
			  tMax, max;
		
		int b[] = {(dirInv.x < 0) ? 1 : 0, 
					 (dirInv.y < 0) ? 1 : 0, 
					 (dirInv.z < 0) ? 1 : 0};

		// x axis
		tMin = (bounds[b[0]].x - ray.origin().x) * dirInv.x;
		tMax = (bounds[1 - b[0]].x - ray.origin().x) * dirInv.x;

		// y axis
		min = (bounds[b[1]].y - ray.origin().y) * dirInv.y;
		max = (bounds[1 - b[1]].y - ray.origin().y) * dirInv.y;
		
		if(max < tMin || min > tMax) return null; //if it criss crosses, its a miss
		if(min > tMin) tMin = min; //Get the greatest min
		if(max < tMax) tMax = max; //Get the smallest max
		
		
		// z axis

		min =  (bounds[b[2]].z - ray.origin().z) * dirInv.z;
		max =  (bounds[1 - b[2]].z - ray.origin().z) * dirInv.z;
		
		if(max < tMin || min > tMax) return null; //if criss crosses, its a miss
		if(min > tMin) tMin = min; //Get the greatest min
		if(max < tMax) tMax = max; //Get the smallest max

	//	ret direction.cpy().scl(tMin).add(origin); //with the shortist distance from start of ray, calc intersection

		
		// short e far
		//	Location nearLoc = direction.cpy().scl(tMin).add(origin).asBukkitLocation(world);
		//	Location farLoc = direction.cpy().scl(tMax).add(origin).asBukkitLocation(world);
	//	DeanGuns.LOCATIONS.add(nearLoc);
	//	DeanGuns.LOCATIONS.add(farLoc);

		return direction.cpy().scl(tMax).add(origin); //with the shortist distance from start of ray, calc intersection
		
	}
	
}