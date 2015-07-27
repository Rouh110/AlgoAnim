package generators.misc;

import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import algoanim.primitives.Polyline;
import algoanim.primitives.StringMatrix;
import algoanim.util.Coordinates;

public class NetzplanGraph {
	
	public enum CellID{EarliestStartTime, LatestStartTime, EarliestEndTime, LatestEndTime, ProcessTime, Name};
	
	HashMap<Integer, NetzplanNode> nodes = new HashMap<Integer, NetzplanNode>();
	
	String invalidChar = "-";
	
	int estColumn = 1;
	int estRow = 0;
	int lstColumn = 1;
	int lstRow = 1;
	int eetColumn = 2;
	int eetRow = 0;
	int letColumn = 2;
	int letRow = 1;
	int ptColumn = 0;
	int ptRow = 1;
	int nameColumn = 0;
	int nameRow = 0;
	
	public void setProcessTime(int id, int time)
	{
		this.setEntry(id, CellID.ProcessTime, String.valueOf(time));
	}
	
	public int getProcessTime(int id)
	{
		int result = -1;
		try
		{
			result = Integer.valueOf(this.getEntry(id, CellID.ProcessTime));
		}catch(NumberFormatException e)
		{
			result = -1;
		}
		return result;
	}
	
	public void setEarliestStartTime(int id, int time)
	{
		this.setEntry(id, CellID.EarliestStartTime, String.valueOf(time));
	}
	
	public int getEarliestStartTime(int id)
	{
		int result = -1;
		try
		{
			result = Integer.valueOf(this.getEntry(id, CellID.EarliestStartTime));
		}catch(NumberFormatException e)
		{
			result = -1;
		}
		return result;
	}
	
	public void setLatestStartTime(int id, int time)
	{
		this.setEntry(id, CellID.LatestStartTime, String.valueOf(time));
	}
	
	public int getLatestStartTime(int id)
	{
		int result = -1;
		try
		{
			result = Integer.valueOf(this.getEntry(id, CellID.LatestStartTime));
		}catch(NumberFormatException e)
		{
			result = -1;
		}
		return result;
	}
	
	public void setEarliestEndTime(int id, int time)
	{
		this.setEntry(id, CellID.EarliestEndTime, String.valueOf(time));
	}
	
	public int getEarliestEndTime(int id)
	{
		int result = -1;
		try
		{
			result = Integer.valueOf(this.getEntry(id, CellID.EarliestEndTime));
		}catch(NumberFormatException e)
		{
			result = -1;
		}
		return result;
	}
	
	public void setLatestEndTime(int id, int time)
	{
		this.setEntry(id, CellID.LatestEndTime, String.valueOf(time));
	}
	
	public int getLatestEndTime(int id)
	{
		int result = -1;
		try
		{
			result = Integer.valueOf(this.getEntry(id, CellID.LatestEndTime));
		}catch(NumberFormatException e)
		{
			result = -1;
		}
		return result;
	}
	
	public void setName(int id, String name)
	{
		this.setEntry(id, CellID.Name, name);
	}
	
	public String getName(int id, String name)
	{
		return this.getEntry(id, CellID.Name);
	}
	
	public List<Integer> getSuccessors(int id)
	{
		if(hasNode(id))
		{
			return getNetzplanNode(id).sucsessors;
		}
		return new LinkedList<Integer>();
	}
	
	public List<Integer> getPredecessors(int id)
	{
		if(hasNode(id))
		{
			return getNetzplanNode(id).predecessors;
		}
		return new LinkedList<Integer>();
	}
	
	public boolean isStartNode(int id)
	{
		if(hasNode(id))
		{
			
			return getNetzplanNode(id).predecessors.size() == 0;
		}
		return false;
	}
	
	public boolean isEndNode(int id)
	{
		if(hasNode(id))
		{
			
			return getNetzplanNode(id).sucsessors.size() == 0;
		}
		return false;
	}
	
	public List<Integer> getEndNodes()
	{
		List<Integer> endNodes =  new LinkedList<Integer>();
		
		for(Integer nodeId : nodes.keySet())
		{
			if(isEndNode(nodeId))
			{
				endNodes.add(nodeId);
			}
		}
		
		return endNodes;
	}
	
	public List<Integer> getStartNodes()
	{
		List<Integer> startNodes =  new LinkedList<Integer>();
		
		for(Integer nodeId : nodes.keySet())
		{
			if(isStartNode(nodeId))
			{
				startNodes.add(nodeId);
			}
		}
		
		return startNodes;
	} 
	
	
	public void highlightCell(int id, CellID cell)
	{
		if(hasNode(id))
		{
			NetzplanNode node = getNetzplanNode(id);
			
			switch(cell)
			{
			case EarliestStartTime:
				node.values.highlightCell(estRow, estColumn, null, null);
				break;
			case LatestStartTime:
				node.values.highlightCell(lstRow, lstColumn, null, null);
				break;
			case EarliestEndTime:
				node.values.highlightCell(eetRow, eetColumn, null, null);
				break;
			case LatestEndTime:
				node.values.highlightCell(letRow, letColumn, null, null);
				break;
			case ProcessTime:
				node.values.highlightCell(ptRow, ptColumn, null, null);
				break;
			case Name:
				node.values.highlightCell(nameRow, nameColumn, null, null);
				break;
			}
		}
	}
	
	public void unhighlightCell(int id, CellID cell)
	{
		if(hasNode(id))
		{
			NetzplanNode node = getNetzplanNode(id);
			
			switch(cell)
			{
			case EarliestStartTime:
				node.values.unhighlightCell(estRow, estColumn, null, null);
				break;
			case LatestStartTime:
				node.values.unhighlightCell(lstRow, lstColumn, null, null);
				break;
			case EarliestEndTime:
				node.values.unhighlightCell(eetRow, eetColumn, null, null);
				break;
			case LatestEndTime:
				node.values.unhighlightCell(letRow, letColumn, null, null);
				break;
			case ProcessTime:
				node.values.unhighlightCell(ptRow, ptColumn, null, null);
				break;
			case Name:
				node.values.unhighlightCell(nameRow, nameColumn, null, null);
				break;
			}
		}
	}
	
	public void highlightEdge(int from, int to)
	{
		if(hasEdge(from, to))
		{
			NetzplanEdge edge = getNetzplanEdge(from, to);
			if(!edge.isHighlighted)
			{
				edge.line.changeColor("color", edge.highlightColor, null, null);
				edge.isHighlighted = true;
			}
		}
		
	}
	
	public void unHighlightEdge(int from, int to)
	{
		if(hasEdge(from, to))
		{
			NetzplanEdge edge = getNetzplanEdge(from, to);
			if(edge.isHighlighted)
			{
				edge.line.changeColor("color", edge.baseColor, null, null);
				edge.isHighlighted = false;
			}
		}
	}
	
	public boolean hasValidEntry(int id, CellID cell)
	{
		
		if(getEntry(id, cell).equals(invalidChar) || getEntry(id, cell) == null)
		{
			return false;
		}
		
		return true;
	}
	
	
	public boolean hasNode(int id)
	{
		return nodes.containsKey(id);
	}
	
	public boolean hasEdge(int from, int to)
	{
		if(hasNode(from))
		{
			NetzplanNode node = getNetzplanNode(from);
			
			return node.edges.containsKey(to);
		}
		
		return false;
	}
	
	private String getEntry(int id, CellID cell)
	{
		NetzplanNode node = getNetzplanNode(id);
		
		if(node != null)
		{
			switch(cell)
			{
			case EarliestStartTime:
				return node.values.getElement(estRow, estColumn);
			case LatestStartTime:
				return node.values.getElement(lstRow, lstColumn);
			case EarliestEndTime:
				return node.values.getElement(eetRow, eetColumn);
			case LatestEndTime:
				return node.values.getElement(letRow, letColumn);
			case ProcessTime:
				return node.values.getElement(ptRow, ptColumn);
			case Name:
				return node.values.getElement(nameRow, nameColumn);
			}
		} 
		
		return invalidChar;
		
	}
	
	private boolean setEntry(int id, CellID cell, String entry)
	{
		NetzplanNode node = getNetzplanNode(id);
		
		if(node != null)
		{
			switch(cell)
			{
			case EarliestStartTime:
				node.values.put(estRow, estColumn, entry, null, null);
				return true;
			case LatestStartTime:
				node.values.put(lstRow, lstColumn, entry, null, null);
				return true;
			case EarliestEndTime:
				node.values.put(eetRow, eetColumn, entry, null, null);
				return true;
			case LatestEndTime:
				node.values.put(letRow, letColumn, entry, null, null);
				return true;
			case ProcessTime:
				node.values.put(ptRow, ptColumn, entry, null, null);
				return true;
			case Name:
				node.values.put(nameRow, nameColumn, entry, null, null);
				return true;
			}
		} 
		
		return false;
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
		public Polyline line = null;
		public Color highlightColor = Color.GREEN;
		public Color baseColor = Color.BLACK;
		public boolean isHighlighted = false;
		public Coordinates from;
		public Coordinates to;
	}

}
