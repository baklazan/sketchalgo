package sk.ksp.baklazan.demo.hamming;
import sk.ksp.baklazan.sketchalgo.*;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.lang.Enum.*;
import javafx.scene.shape.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.input.*;
import javafx.scene.canvas.*;
import javafx.scene.*;
import javafx.collections.*;
import javafx.beans.value.*;


public class MainTask extends Thread
{
	private Visualizer visualizer;
	private DefaultDSFactory factory;
	
	public MainTask(Canvas canvas)
	{
		super("main task thread");
		this.visualizer = new Visualizer(canvas);
		this.factory = visualizer.getFactory();
	}
	
	private boolean isPowerOfTwo(int a)
	{
		if(a == 1)return true;
		if(a % 2 == 1)return false;
		return isPowerOfTwo(a/2);
	}
	
	ArrayList<Integer> encode(ArrayList<Integer> word)
	{
		ArrayList<Integer> codeword = factory.createArrayList("codeword", true, factory.createSleepConstants(0, 0, 2000, true));
		int n = 1, k = 1;
		while(n < word.size())
		{
			n = n*2+1;
			k++;
		}
		visualizer.setAlgorithmState("Encoding word: copying information bits");
		int wind = 0;
		for(int i=1; i<=word.size() + k; i++)
		{
			if(isPowerOfTwo(i)) codeword.add(-1);
			else 
			{
				codeword.add(word.get(wind));
				wind++;
			}
		}
		
		int parityBit = 1;
		for(int i=0; i<k; i++)
		{
			visualizer.setAlgorithmState("Encoding word: computing parity bit " + parityBit);
			int sum = 1;
			for(int j=parityBit; j<=codeword.size(); j+= parityBit*2)
			{
				for(int l = j; l < Math.min(j+parityBit, codeword.size()+1); l++)
				{
					sum += (codeword.get(l-1)+2) % 2;
					sum %= 2;
				}
			}
			codeword.set(parityBit-1, sum);
			parityBit *= 2;
		}
		return codeword;
	}
	
	void induceError(ArrayList<Integer> codeword, int ind)
	{
		visualizer.setAlgorithmState("Inducing an error");
		
		codeword.set(ind, 1-codeword.get(ind));
	}
	
	ArrayList<Integer> decode(ArrayList<Integer> codeword)
	{
		ArrayList<Integer> syndrome = 
		  factory.createArrayList("error syndrome (reversed)", true, factory.createSleepConstants(1600,0,0, true));
		for(int parityBit = 1; parityBit <= codeword.size(); parityBit *= 2)
		{
			visualizer.setAlgorithmState("Decoding: controlling parity bit " + parityBit);
			int sum = 0;
			for(int i = parityBit; i <= codeword.size(); i += 2*parityBit)
			{
				for(int j=i; j < Math.min(i + parityBit, codeword.size()+1); j++)
				{
					sum += codeword.get(j-1);
					sum %= 2;
				}
			}
			syndrome.add(sum);
		}
		visualizer.setAlgorithmState("Decoding: repairing error");
		int errorIndex = 0;
		for(int i=syndrome.size()-1; i>=0; i--)
		{
			errorIndex *= 2;
			errorIndex += syndrome.get(i);
		}
		if(errorIndex > 0)
		{
			codeword.set(errorIndex-1, 1-codeword.get(errorIndex-1));
		}
		else
		{
			visualizer.setAlgorithmState("Decoding: threre was no error");
		}
		visualizer.unregister(syndrome);
		
		ArrayList<Integer> result = factory.createArrayList("decoded word", true, factory.createSleepConstants(200, 0, 0, false));
		visualizer.setAlgorithmState("Decoding: copying information bits");
		for(int i=1; i<=codeword.size(); i++)
		{
			if(!isPowerOfTwo(i)) result.add(codeword.get(i-1));
		}
		return result;
	}
	
	@Override
	public void run()
	{
		int n;
		Scanner in = new Scanner(System.in);
		n = in.nextInt();
		visualizer.setAlgorithmState("Loading word");
		ArrayList<Integer> word = factory.createArrayList("word");
		for(int i=0; i<n; i++)
		{
			word.add(new Integer(in.nextInt()));
		}
		
		ArrayList<Integer> codeword = encode(word);
		int ind = in.nextInt();
		induceError(codeword, ind);
		ArrayList<Integer> decodedWord = decode(codeword);
		visualizer.setAlgorithmState("Word decoded!");
	}
}