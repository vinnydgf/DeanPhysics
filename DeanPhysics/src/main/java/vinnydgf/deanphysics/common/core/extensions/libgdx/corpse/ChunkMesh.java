package vinnydgf.deanphysics.common.core.extensions.libgdx.corpse;

import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody.btRigidBodyConstructionInfo;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btQuaternion;
import com.badlogic.gdx.physics.bullet.linearmath.btTransform;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

import net.minecraft.server.v1_12_R1.Chunk;
import vinnydgf.deanphysics.bukkit.core.bound.AABB;
import vinnydgf.deanphysics.common.core.extensions.JBullet;

public class ChunkMesh {

	private final JBullet core;
	
	private final btCollisionShape collisionShapeTerrain;
	private final btRigidBodyConstructionInfo info;
	private final btRigidBody body;
	
	private final Chunk chunk;
	public Set<AABB> aabb = new HashSet<>();
	
	
	
	public ChunkMesh(final JBullet core, Chunk chunk, Set<AABB> aabb, btTriangleMesh meshTerrain) {
		this.core = core;
		this.chunk = chunk;
		this.aabb = aabb;
		
		btCollisionShape shape = new btConvexHullShape();
		shape = new btBvhTriangleMeshShape(meshTerrain, true);
		
		int tcx = (chunk.locX << 4);
		int tcz = (chunk.locZ << 4);
		
		this.collisionShapeTerrain = shape;
	//	this.collisionShapeTerrain = new btBvhTriangleMeshShape(meshTerrain, true);
		
		Matrix4 transform = new Matrix4();
		
		transform.idt();
		transform.setTranslation(new Vector3(tcx + 8f, 64, tcz + 8f));
		btDefaultMotionState motionState = new btDefaultMotionState(transform);
		
		
		this.info = new btRigidBody.btRigidBodyConstructionInfo(0f, motionState, collisionShapeTerrain, Vector3.Zero);
		this.body = new btRigidBody(info);
		body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
	//	body.setWorldTransform(transform);

		body.setActivationState(0);
		
		core.dynamicWorld().addRigidBody(body);
		
	}
	
	
	public void build() {
		body.setActivationState(0);
		body.setCollisionFlags(body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_STATIC_OBJECT);
		core.dynamicWorld().addRigidBody(body);
		
		
	}

	public Chunk chunk() { return chunk; }
	
	public btRigidBody body() { return this.body; }
}
