package vinnydgf.deanphysics.bukkit.core.bound.mathf;


public class Vector2 implements Vector<Vector2> {

	public final static Vector2 X = new Vector2(1, 0);
	public final static Vector2 Y = new Vector2(0, 1);
	public final static Vector2 Zero = new Vector2(0, 0);

	public float x;
	public float y;

	public Vector2 () {
	}

	public Vector2 (float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2 (Vector2 v) {
		set(v);
	}

	@Override
	public Vector2 cpy () {
		return new Vector2(this);
	}

	public float len (float x, float y) {
		return (float)Math.sqrt(x * x + y * y);
	}


	public float len2 (float x, float y) {
		return x * x + y * y;
	}


	@Override
	public Vector2 set (Vector2 v) {
		x = v.x;
		y = v.y;
		return this;
	}

	public Vector2 set (float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public Vector2 sub (float x, float y) {
		this.x -= x;
		this.y -= y;
		return this;
	}


	@Override
	public Vector2 add (Vector2 v) {
		x += v.x;
		y += v.y;
		return this;
	}

	public Vector2 add (float x, float y) {
		this.x += x;
		this.y += y;
		return this;
	}

	public float dot (float x1, float y1, float x2, float y2) {
		return x1 * x2 + y1 * y2;
	}

	public float dot (float ox, float oy) {
		return x * ox + y * oy;
	}

	public Vector2 scl (float x, float y) {
		this.x *= x;
		this.y *= y;
		return this;
	}


	public float distance(float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return (float)Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public float distance(Vector2 vector2) {
		final float x_d = vector2.x - this.x;
		final float y_d = vector2.y - this.y;
		return (float) Math.sqrt(x_d * x_d + y_d * y_d);
	}

	public float dst2 (float x1, float y1, float x2, float y2) {
		final float x_d = x2 - x1;
		final float y_d = y2 - y1;
		return x_d * x_d + y_d * y_d;
	}

	public float dst2 (float x, float y) {
		final float x_d = x - this.x;
		final float y_d = y - this.y;
		return x_d * x_d + y_d * y_d;
	}


	

	
	
	
	
	@Override
	public Vector2 lerp (Vector2 target, float alpha) {
		final float invAlpha = 1.0f - alpha;
		this.x = (x * invAlpha) + (target.x * alpha);
		this.y = (y * invAlpha) + (target.y * alpha);
		return this;
	}

	@Override
	public Vector2 interpolate (Vector2 target, float alpha, Interpolation interpolation) {
		return lerp(target, interpolation.apply(alpha));
	}

	@Override
	public Vector2 nor() {
		return null;
	}

}