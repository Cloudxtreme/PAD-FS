package mcsn.pad;

import java.io.Serializable;

public class Pair{

	  private final Serializable left;
	  private final String right;

	  public Pair(Serializable left, String right) {
	    this.left = left;
	    this.right = right;
	  }

	  public Serializable getLeft() { return left; }
	  public String getRight() { return right; }

	
	}
