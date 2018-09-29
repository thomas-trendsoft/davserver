package davserver.utils;

public class Pair<T,L> {

	private T key;
	
	private L value;
	
	public Pair(T key,L val) {
		this.key   = key;
		this.value = val;
	}

	public T getKey() {
		return key;
	}

	public void setKey(T key) {
		this.key = key;
	}

	public L getValue() {
		return value;
	}

	public void setValue(L value) {
		this.value = value;
	}
	
}
