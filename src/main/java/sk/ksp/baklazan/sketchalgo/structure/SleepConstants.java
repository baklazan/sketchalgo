package sk.ksp.baklazan.sketchalgo.structure;

public class SleepConstants
{
	public static final int GET_BATCH = 0;
	public static final int GET_INSTANT = 1;
	public static final int GET_SILENT = 2;
	public int sleepGet, sleepSet, sleepAdd, sleepRemove;
	public int getType;
	public SleepConstants(int sleepAdd, int sleepGet, int sleepRemove, int sleepSet, int getType)
	{
		this.sleepAdd = sleepAdd;
		this.sleepGet = sleepGet;
		this.sleepRemove = sleepRemove;
		this.sleepSet = sleepSet;
		this.getType = getType;
	}
}