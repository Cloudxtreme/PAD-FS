package mcsn.pad;

import java.io.Serializable;

public class Pair implements Serializable{

	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final Serializable left;
	  private final String right;

	  public Pair(Serializable left, String right) {
	    this.left = left;
	    this.right = right;
	  }

	  public Serializable getLeft() { return left; }
	  public String getRight() { return right; }

	
	}
