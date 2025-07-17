/* © SRSoftware 2025 */
package de.srsoftware.tools;

/**
 * This class provides a container for two arbitrary objects
 * @param <A> class of the first object
 * @param <B> class of the second object
 */
public class Tuple<A,B>{
	/** direct access to the first object  **/ public final A a;
	/** direct access to the second object **/ public final B b;

	/**
	 * create a new tuple containing two objects a and b
	 * @param a the first object
	 * @param b the second object
	 */
	public Tuple(A a, B b){
		this.a = a;
		this.b = b;
	}

	/**
	 * convenience function to create a new tuple
	 * @param a the first object
	 * @param b the second object
	 * @return the Tuple holding a and b
	 * @param <A> class of the first object
	 * @param <B> class of the second object
	 */
	public static <A, B> Tuple<A,B> of(A a, B b) {
		return new Tuple<>(a,b);
	}
}
