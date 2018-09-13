package davserver.repository;

public abstract class Collection extends Resource {

	public abstract Resource getChild(String name);
	
}
