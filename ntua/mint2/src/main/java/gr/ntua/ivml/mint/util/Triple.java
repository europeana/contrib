package gr.ntua.ivml.mint.util;

public class Triple<U,V,W> {
	public U _1;
	public V _2;
	public W _3;
	public Triple( U u, V v, W w) {
		this._1 = u;
		this._2 = v;
		this._3 = w;
	}

	public String toString() {
		return "[" + _1 + ", " + _2 +  _3 + "]";
	}
}
