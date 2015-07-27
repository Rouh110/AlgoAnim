package generators.misc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import algoanim.primitives.StringMatrix;

public class NetzplanGraph {
	
	public enum CellID{EarliestStartTime, LatestStartTime, EarliestEndTime, LatesteEndTime, ProcessTime, Name};
	
	HashMap<Integer, NetzplanNode> nodes = new HashMap<Integer, NetzplanNode>();
	
	public void setProcessTime(int id, int time)
	{
		//TODO: implement
	}
	
	public int getProcessTime(int id)
	{
		return -1;
		//TODO: implement
	}
	
	public void setEarliestStartTime(int id, int time)
	{
		//TODO: implement
	}
	
	public int getEarliestStartTime(int id)
	{
		return -1;
		//TODO: implement
	}
	
	public void setLatestStartTime(int id, int time)
	{
		//TODO: implement
	}
	
	public int getLatestStartTime(int id)
	{
		return -1;
		//TODO: implement
	}
	
	public void setEarliestEndTime(int id, int time)
	{
		//TODO: implement
	}
	
	public int getEarliestEndTime(int id)
	{
		return -1;
		//TODO: implement
	}
	
	public void setLatestEndTime(int id, int time)
	{
		//TODO: implement
	}
	
	public int getLatestEndTime(int id)
	{
		return -1;
		//TODO: implement
	}
	
	public void setName(int id, String name)
	{
		//TODO: implement
	}
	
	public String getName(int id, String name)
	{
		return "";
		//TODO: implement
	}
	
	public List<Integer> getSuccessors(int id)
	{
		return new LinkedList<Integer>();
		//TODO: implement
	}
	
	public List<Integer> getPredecessor(int id)
	{
		return new LinkedList<Integer>();
		//TODO: implement
	}
	
	public boolean isStartNode(int id)
	{
		return false;
		//TODO: implement
	}
	
	public boolean isEndNode(int id)
	{
		return false;
		//TODO: implement
	}
	
	public List<Integer> getEndNodes()
	{
		return new LinkedList<Integer>();
		//TODO: implement
	}
	
	public List<Integer> getStartNodes()
	{
		return new LinkedList<Integer>();
		//TODO: implement
	} 
	
	
	public void highlightCell(int id, CellID cell)
	{
		//TODO: implement
	}
	
	public void unhighlightCell(int id, CellID cell)
	{
		//TODO: implement
	}
	
	public void highlightEdge(int from, int to)
	{
		//TODO: implement
	}
	
	public void unHighlightEdge(int from, int to)
	{
		//TODO: implement
	}
	
	public boolean hasValidEntry(int id, CellID cell)
	{
		return false;
		//TODO: implement
	}
	
	private NetzplanEdge getNetzplanEdge(int from, int to)
	{
		NetzplanNode node = getNetzplanNode(from);
		
		if(node != null)
		{
			if(node.edges.containsKey(to))
			{
				return node.edges.get(to);
			}
		}
		
		return null;
	}
	
	private NetzplanNode getNetzplanNode(int id)
	{
		if(nodes.containsKey(id))
		{
			return nodes.get(id);
		}else
		{
			return null;
		}
	}
	
	protected class NetzplanNode
	{
		List<Integer> predecessors = new LinkedList<Integer>();
		List<Integer> sucsessors = new LinkedList<Integer>();	
		HashMap<Integer, NetzplanEdge> edges = new HashMap<Integer, NetzplanEdge>();
		StringMatrix values = null;
	}
	
	protected class NetzplanEdge
	{
		
	}

}
