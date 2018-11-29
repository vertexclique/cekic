package org.tubs.epoc.SMFF.Utility;

import java.util.Random;

// code taken from http://forums.sun.com/thread.jspa?forumID=31&threadID=5221000

/**
 * Random number generator that returns the an index in the bounds of mass.size(). Indices are generated with
 * probability according to masses in mass[] array. This generator can be used for random picking from an array/list,
 * etc.
 */
public class RandomSelection {
	private int c[]; // binary choice tree - kept heap ordered in array
	private int n;
	private Random rnd;

	/**
	 * Constructor to initialize a RandomSelection class which will later be used to select an index randomly complying
	 * with the weights passed.
	 * <p>
	* Usage: int[] e = {19, 27, -1, 4}; 
	 * 				int m = {1, 2, 3 ,4}; 
	 * 				RandomSelection r = new RandomSelection(m, seed); 
	 * 				for(int i = 0; i<10; i++){ System.out.println( e[r.nextIndex()]) };
	 * 
	 * @param mass
	 *          [] - probability distribution of indices (size must be >= 2)
	 */
	public RandomSelection(int mass[]) {
		this(mass, new Random().nextLong());
	}

	/**
	 * Constructor to initialize a RandomSelection class which will later be used to select an index randomly complying
	 * with the weights passed.
	 * <p>
	 * Usage: int[] e = {19, 27, -1, 4}; 
	 * 				int m = {1, 2, 3 ,4}; 
	 * 				int seed = 5l;
	 * 				RandomSelection r = new RandomSelection(m, seed); 
	 * 				for(int i = 0; i<10; i++){ System.out.println( e[r.nextIndex()]) };
	 * 
	 * @param mass
	 *          [] - probability distribution of indices (size must be >= 2)
	 * @param seed
	 *          seed to be used for class {@link java.util.Random Random} to set its seed.
	 */
	public RandomSelection(int mass[], long seed) {
		rnd = new Random(seed);
		n = mass.length;
		c = new int[n];
		for (int i = n - 1; i > 0; i--) { // first calculate total mass from leaves to top
			int k = 2 * i; // k is left child, k+1 is right
			c[i] = get(k, mass) + get(k + 1, mass); // c is sum of mass of two children.
		}

		// since we aren't using c[0] for anything, we will keep total mass there
		c[0] = c[1]; // c[1] was the total mass of entire tree.

		// Now we fixup internal nodes to be only hold mass of the right child
		for (int i = 1; i < n; i++) {
			c[i] -= get(2 * i + 1, mass);
		}
	}

	// helper routine - lets us treat c and mass as one large array.
	private int get(int i, int[] mass) {
		return (i < n) ? c[i] : mass[i - n];
	}

	/**
	 * Returns next Index with the distribution in this.
	 * 
	 * @return the next index.
	 */
	public int nextIndex() {
		int r = rnd.nextInt(c[0]); // r is uniformly distributed across total mass
		return reduce(r); // r reduces to a single index, this function simplifies testing
	}

	private int reduce(int r) {
		int k = 1;
		while (k < n) {
			int m = c[k];
			k *= 2;
			if (r >= m) {
				r -= m;
				k++;
			}
		}
		return k - n;
	}
}
