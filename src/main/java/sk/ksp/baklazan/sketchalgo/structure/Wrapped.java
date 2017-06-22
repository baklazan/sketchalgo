package sk.ksp.baklazan.sketchalgo.structure;


public class Wrapped<T>
{
	protected T wrapped;
	
	public Wrapped()
	{
		wrapped = null;
	}
	
	public Wrapped(T t)
	{
		wrapped = t;
	}
	
	public T get()
	{
		return wrapped;
	}
	
	public void set(T val)
	{
		wrapped = val;
	}
}