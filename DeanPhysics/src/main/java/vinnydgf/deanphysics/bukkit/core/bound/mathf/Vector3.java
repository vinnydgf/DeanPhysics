package vinnydgf.deanphysics.bukkit.core.bound.mathf;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sun.webkit.plugin.Plugin;


public class Vector3 implements Vector<Vector3> {

	public float x;
	public float y;
	public float z;

	public final static Vector3 X = new Vector3(1, 0, 0);
	public final static Vector3 Y = new Vector3(0, 1, 0);
	public final static Vector3 Z = new Vector3(0, 0, 1);
	public final static Vector3 Zero = new Vector3(0, 0, 0);

	public Vector3 () {
	}
	
	public Vector3(final org.bukkit.util.Vector vector) {
		this.set((float) vector.getX(), (float) vector.getY(), (float) vector.getZ());
	}

	public Vector3 (final Location local) {
		this.set((float) local.getX(), (float) local.getY(), (float) local.getZ());
	}
	public org.bukkit.util.Vector asBukkitVector() {
		return new org.bukkit.util.Vector(x, y, z);
	}
	
	public Location asBukkitLocation(final World world) {
		return new Location(world, x, y, z);
	}
	
	public static Vector3 fromLocation(final Location loc) {
		 return new Vector3(loc.getX(), loc.getY(), loc.getZ());
	}
	
	
	public Vector3 (double x, double y, double z) {
		this.set((float) x, (float) y, (float) z);
	}
	public Vector3 (float x, float y, float z) {
		this.set(x, y, z);
	}
	

	public Vector3 (final Vector3 vector) {
		this.set(vector);
	}
	public Vector3 (final float[] values) {
		this.set(values[0], values[1], values[2]);
	}

	public Vector3 (final Vector2 vector, float z) {
		this.set(vector.x, vector.y, z);
	}

	public Vector3 set (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}

	
	public float sqrMagnitude() {
		return (float) Math.sqrt(Vector3.dot(this, this));
	}
	@Override
	public Vector3 set (final Vector3 vector) {
		return this.set(vector.x, vector.y, vector.z);
	}

	
	public Vector3 set (final float[] values) {
		return this.set(values[0], values[1], values[2]);
	}

	public Vector3 set (final Vector2 vector, float z) {
		return this.set(vector.x, vector.y, z);
	}


	
	@Override
	public Vector3 add (final Vector3 vector) {
		return this.add(vector.x, vector.y, vector.z);
	}

	public Vector3 add (float x, float y, float z) {
		return this.set(this.x + x, this.y + y, this.z + z);
	}

	public Vector3 add (float values) {
		return this.set(this.x + values, this.y + values, this.z + values);
	}

	

	@Override
	public Vector3 lerp (final Vector3 target, float alpha) {
		x += alpha * (target.x - x);
		y += alpha * (target.y - y);
		z += alpha * (target.z - z);
		return this;
	}
	
	public Vector3 slerp (final Vector3 target, float alpha) {
		final float dot = dot(target);
		// If the inputs are too close for comfort, simply linearly interpolate.
		if (dot > 0.9995 || dot < -0.9995) return lerp(target, alpha);

		// theta0 = angle between input vectors
		final float theta0 = (float)Math.acos(dot);
		// theta = angle between this vector and result
		final float theta = theta0 * alpha;

		final float st = (float)Math.sin(theta);
		final float tx = target.x - x * dot;
		final float ty = target.y - y * dot;
		final float tz = target.z - z * dot;
		final float l2 = tx * tx + ty * ty + tz * tz;
		final float dl = st * ((l2 < 0.0001f) ? 1f : 1f / (float)Math.sqrt(l2));

		return scl((float)Math.cos(theta)).add(tx * dl, ty * dl, tz * dl).nor();
	}
	
	public float dot (final Vector3 vector) {
		return x * vector.x + y * vector.y + z * vector.z;
	}
	

	@Override
	public Vector3 interpolate (Vector3 target, float alpha, Interpolation interpolator) {
		return lerp(target, interpolator.apply(0f, 1f, alpha));
	}

	public float dot (float ox, float oy) {
		return x * ox + y * oy;
	}
	
	public static float dot (float x1, float y1, float z1, float x2, float y2, float z2) {
		return x1 * x2 + y1 * y2 + z1 * z2;
	}
	
	public static float dot (Vector3 vec1, Vector3 vec2) {
		return vec1.x * vec2.x + vec1.y * vec2.y + vec1.z * vec2.z;
	}
	
	
	public Vector3 sub (final Vector3 a_vec) {
		return this.sub(a_vec.x, a_vec.y, a_vec.z);
	}
	
	public Vector3 sub (float x, float y, float z) {
		return this.set(this.x - x, this.y - y, this.z - z);
	}
	
	public Vector3 sub (float value) {
		return this.set(this.x - value, this.y - value, this.z - value);
	}

	
	public Vector3 scl (float scalar) {
		return this.set(this.x * scalar, this.y * scalar, this.z * scalar);
	}
	
	public Vector3 scl (Vector3 scalar) {
		return this.set(this.x * scalar.x, this.y * scalar.y, this.z * scalar.z);
	}
	public Vector3 scl (float x, float y, float z) {
		return this.set(this.x * x, this.y * y, this.z * z);
	}

	public Vector3 divide(float divis) {
		return this.set(this.x / divis, this.y / divis, this.z / divis);
	}
	
	
    public float minComponent() { return Math.min(Math.min(x, y), z); }

    /**
     * Gets the largest component of this vector.
     * @return The component.
     */
    public float maxComponent() { return Math.max(Math.max(x, y), z); }
	
    public Vector3 step(Vector3 o) {
        return new Vector3(
            o.x < x ? 0 : 1,
            o.y < y ? 0 : 1,
            o.z < z ? 0 : 1
        );
    }
    
    public Vector3 sign() {
        return new Vector3(Math.signum(x), Math.signum(y), Math.signum(z));
    }
	 public Vector3 abs() { return new Vector3(Math.abs(x), Math.abs(y), Math.abs(z)); }
	/** Multiplies this vector by a scalar
	 * @return This vector for chaining */
	public Vector3 scl (float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}
	
	public Vector3 neg() { return new Vector3(-x, -y, -z); }
	
	public Vector3 midpoint (Vector3 o) {
		return new Vector3(
	                (x + o.x) / 2,
	                (y + o.y) / 2,
	                (z + o.z) / 2
	        );
	}
	
	public String toString() {
		return " X: " + x + " Y: " + y + " Z: " + z;
	}

	public float len2 () {
		return x * x + y * y + z * z;
	}
	
	public Vector3 cpy() {
		return new Vector3(this);
	}
	
	
	public Vector3 reciprocal() {
		return new Vector3(1 / x, 1 / y, 1 / z);
	}

	public Vector3 rotateY(double ang) {
		double cos = Math.cos(ang);
		double sin = Math.sin(ang);
		return new Vector3(
				cos * x + sin * z,
				y,
				-sin * x + cos * z
				);
	}

	@Override
	public Vector3 nor() {
		final float len2 = this.len2();
		if (len2 == 0f || len2 == 1f) return this;
		return this.scl(1f / (float)Math.sqrt(len2));
	}

}