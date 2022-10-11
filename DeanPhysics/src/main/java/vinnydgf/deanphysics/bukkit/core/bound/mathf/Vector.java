package vinnydgf.deanphysics.bukkit.core.bound.mathf;

public interface Vector<T extends Vector<T>> {
	T cpy ();

	T set (T v);

	T nor ();

	T add (T v);

	T lerp (T target, float alpha);

	T interpolate (T target, float alpha, Interpolation interpolator);

}