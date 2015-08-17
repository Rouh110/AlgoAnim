package generators.graph;

import java.awt.Color;
import java.awt.Font;
import java.util.LinkedList;
import java.util.List;

import algoanim.animalscript.AnimalCircleGenerator;
import algoanim.animalscript.AnimalScript;
import algoanim.primitives.Circle;
import algoanim.primitives.Graph;
import algoanim.primitives.generators.GraphGenerator;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.CircleProperties;
import algoanim.properties.GraphProperties;
import algoanim.properties.PolylineProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;
import algoanim.util.DisplayOptions;
import algoanim.util.MsTiming;
import algoanim.util.Node;
import algoanim.util.Timing;
import algoanim.primitives.*;

public class PageRankGraph{

	private Graph graph;
	
	PageRankNode nodes[];
	int[][] adjacencyMatrix;
	PageRankEdge[][] edgeMatrix;
	Language lang;
	
	int minRadius = 15;
	int maxRadius = 80;
	
	int drawDeph = 3;
	int textDrawDepth = 0;
	int edgeDrawDepth = 1;
	int nodeDrawDepth = 2;
	
	
	public PageRankGraph(Graph graph, Language lang) {
		this.graph = graph;
		adjacencyMatrix = graph.getAdjacencyMatrix();
		edgeMatrix = new PageRankEdge[adjacencyMatrix.length][adjacencyMatrix[0].length];
		this.lang = lang;
		init();
	}
	
	public int getminRadius(){
		return minRadius;
	}
	
	public int getmaxRadius(){
		return maxRadius;
	}
	
	protected int getNodeDrawDepth()
	{
		return nodeDrawDepth+drawDeph;
	}
	protected int getTextDrawDepth()
	{
		return textDrawDepth+drawDeph;
	}
	protected int getEdgeDrawDepth()
	{
		return edgeDrawDepth+drawDeph;
	}
	
	private void init()
	{
		// setup nodes
		nodes = new PageRankNode[graph.getSize()];
		for(int i = 0; i < graph.getSize(); i++)
		{
			
			nodes[i] = new PageRankNode();
			
			CircleProperties cp = getCircleProperties(null);
			nodes[i].circle = lang.newCircle(graph.getNode(i),minRadius , graph.getNodeLabel(i), null, cp);
			nodes[i].radius = minRadius;
			//nodes[i].circle.moveTo("C", "translate", nodes[i].circle.getCenter(), null, null);
			TextProperties tp = getTextProperties();
			nodes[i].text = lang.newText(new Coordinates(((Coordinates)nodes[i].circle.getCenter()).getX(),((Coordinates)nodes[i].circle.getCenter()).getY()), graph.getNodeLabel(i), graph.getNodeLabel(i)+" text", null, tp);	
			nodes[i].text.moveTo("C", "translate", nodes[i].circle.getCenter(), null, null);
			nodes[i].text.moveBy("translate", -4, -8, null, null);
		}
		
		// set up normal edges
		for(int to = 0; to < graph.getSize(); to++)
		{
			for(int from = 0; from < graph.getSize(); from++)
			{
				if(adjacencyMatrix[from][to] != 0)
				{
					PageRankEdge edge = new PageRankEdge();				
					
					Coordinates lineNodes[] = getEdgeCoordinates(nodes[from], nodes[to]);
					edge.line = createLine(lineNodes);			
					edge.from = lineNodes[0];
					edge.to = lineNodes[1];
					
					edgeMatrix[from][to] = edge;
				}
			}
		}
	    
		// set up dangling Edges
		
		List<Integer> danglingNodes = getAllDanglingNodeNrs();
		
		for(Integer dNodeNr : danglingNodes)
		{
			for(int to = 0; to < nodes.length; to++)
			{
				PageRankEdge dEdge = new PageRankEdge();
				
				dEdge.isDanglingEdge = true;
				Coordinates lineNodes[] = getEdgeCoordinates(nodes[dNodeNr], nodes[to]);
				dEdge.line = createLine(lineNodes);			
				dEdge.from = lineNodes[0];
				dEdge.to = lineNodes[1];
				edgeMatrix[dNodeNr][to] = dEdge;
				
				hideEdge(dNodeNr, to);
			}
		}
		
	}

	protected Polyline createLine(Node lineNodes[])
	{
		
		PolylineProperties pp = getPolyLineProperties(null);
		return lang.newPolyline(lineNodes, "edge", null, pp);
	}
	
	public void setNodeText(int nodeNr, String text){
		PageRankNode prn = nodes[nodeNr];
		prn.text.setText(text, null, null);
		
	}
	
	public void setTextColor(int nodeNr, Color textColor)
	{
		if(textColor != null)
		{
			nodes[nodeNr].text.changeColor("color", textColor, null, null);
		}
	}
	
	public void setAllTextColor(Color textColor)
	{
		for(int i = 0; i < nodes.length; i++)
		{
			setTextColor(i, textColor);
		}
	}
	public void highlightNode(int nodeNr)
	{
		PageRankNode prn = nodes[nodeNr];
		
		if(prn.isHighlighted)
			return;
		
		prn.isHighlighted = true;
		
		
		prn.circle.changeColor(AnimalScript.COLORCHANGE_FILLCOLOR, prn.highlightColor, null, null);
		
		//CircleProperties cp = getCircleProperties(prn);
		//prn.circle.hide();
		//prn.circle = lang.newCircle(prn.circle.getCenter(),prn.circle.getRadius() , prn.circle.getName(), null, cp);
		
	}
	
	public void unhighlightNode(int nodeNr)
	{
		PageRankNode prn = nodes[nodeNr];
		
		if(!prn.isHighlighted)
			return;
	
		prn.isHighlighted = false;
		
		prn.circle.changeColor(AnimalScript.COLORCHANGE_FILLCOLOR, prn.fillColor, null, null);
//		CircleProperties cp = getCircleProperties(prn);
//		prn.circle.hide();
//		prn.circle = lang.newCircle(prn.circle.getCenter(),prn.circle.getRadius() , prn.circle.getName(), null, cp);
		
	}
	
	
	
	public void highlightEdge(int from, int to, Timing offset, Timing duration)
	{
		PageRankEdge edge = edgeMatrix[from][to];
		
		if(edge != null)
		{
			if(!edge.isHighlighted)
			{
				edge.line.changeColor("color", edge.highlightColor, offset, duration);
				edge.isHighlighted = true;
			}
		}
	}
	
	public void unhighlightEdge(int from, int to, Timing offset, Timing duration)
	{
		PageRankEdge edge = edgeMatrix[from][to];
		
		if(edge != null)
		{
			if(edge.isHighlighted)
			{
				edge.line.changeColor("color", edge.baseColor, offset, duration);
				edge.isHighlighted = false;
			}
		}
	}
	
	//TODO hide graph completely
	public void hideGraph(){
		
	}
	
	public void setEdgeBaseColor(int from, int to, Color newColor)
	{
		if(newColor == null)
			return;
		
		PageRankEdge edge = edgeMatrix[from][to];
		
		if(edge == null)
			return;
		
		edge.baseColor = newColor;
		
		if(!edge.isHighlighted)
		{
			edge.isHighlighted = true;
			unhighlightEdge(from, to, null,null);
		}
	}
	
	public void setAllEdgesBaseColor(Color edgeBaseColor)
	{
		setAllEdgesBaseColor(edgeBaseColor, false);
			
	}
	
	public void setAllEdgesBaseColor(Color edgeBaseColor, boolean setDanglingEdgesColor)
	{
		for(int from = 0; from < nodes.length; from++)
		{
			for(int to = 0; to < nodes.length; to ++)
			{
				if(setDanglingEdgesColor || adjacencyMatrix[from][to] != 0)
				{
					setEdgeBaseColor(from,to,edgeBaseColor);
				}
				
			}
		}
			
	}
	
	public void setAllDangingEdgeBaseColor(Color edgeBaseColor)
	{
		PageRankEdge edge = null;
		for(int from = 0; from < nodes.length; from++)
		{
			for(int to = 0; to < nodes.length; to ++)
			{
				edge = edgeMatrix[from][to];
				if(edge != null && edge.isDanglingEdge)
				{
					setEdgeBaseColor(from,to,edgeBaseColor);
				}
			}
		}
	}
	
	public void setEdgeHighlightColor(int from, int to, Color newColor )
	{
		if(newColor == null)
			return;
		
		PageRankEdge edge = edgeMatrix[from][to];
		
		if(edge == null)
			return;
		
		edge.highlightColor = newColor;
		
		if(edge.isHighlighted)
		{
			edge.isHighlighted = false;
			highlightEdge(from, to, null,null);
		}
		
	}
	
	
	
	public void setAllEdgesHighlightColor(Color edgeHighlightColor)
	{
		setAllEdgesHighlightColor(edgeHighlightColor, false);
			
	}
	
	public void setAllEdgesHighlightColor(Color edgeHighlightColor,boolean setDanglingEdgesHColor )
	{
		for(int from = 0; from < nodes.length; from++)
		{
			for(int to = 0; to < nodes.length; to ++)
			{
				if(setDanglingEdgesHColor || adjacencyMatrix[from][to] != 0)
				{
					setEdgeHighlightColor(from,to,edgeHighlightColor);
				}
			}
		}
			
	}
	
	public void setAllDanglingEdgesHighlightColor(Color edgeHighlightColor)
	{
		PageRankEdge edge = null;
		for(int from = 0; from < nodes.length; from++)
		{
			for(int to = 0; to < nodes.length; to ++)
			{
				edge = edgeMatrix[from][to];
				if(edge != null && edge.isDanglingEdge)
				{
					setEdgeHighlightColor(from,to,edgeHighlightColor);
				}
			}
		}

	}
	
	public void showEdge(int from, int to)
	{
		PageRankEdge edge = edgeMatrix[from][to];
		
		if(edge != null)
		{
			edge.line.show();
		}
		
	}
	
	public void hideEdge(int from, int to)
	{
		PageRankEdge edge = edgeMatrix[from][to];
		
		if(edge != null)
		{
			edge.line.hide();
		}
	}
	
	public void showAllDanglingEdges()
	{
		PageRankEdge edge = null;
		for(int from = 0; from < nodes.length; from++)
		{
			for(int to = 0; to < nodes.length; to ++)
			{
				edge = edgeMatrix[from][to];
				if(edge != null && edge.isDanglingEdge)
				{
					showEdge(from, to);
				}
			}
		}
	}
	
	public void hideAllDanglingEdges()
	{
		PageRankEdge edge = null;
		for(int from = 0; from < nodes.length; from++)
		{
			for(int to = 0; to < nodes.length; to ++)
			{
				edge = edgeMatrix[from][to];
				if(edge != null && edge.isDanglingEdge)
				{
					hideEdge(from, to);
				}
			}
		}
	}
	
	public void setNodeFillColor(int nodeNr, Color newColor)
	{
		if(newColor == null)
			return;
		
		PageRankNode node = nodes[nodeNr];
		
		node.fillColor = newColor;
		
		if(!node.isHighlighted)
		{
			node.isHighlighted = true;
			unhighlightNode(nodeNr);
		}
	}
	
	
	public void setNodeHighlightColor(int nodeNr, Color newColor)
	{
		if(newColor == null)
			return;
		
		PageRankNode node = nodes[nodeNr];
		
		node.highlightColor = newColor;
		
		if(node.isHighlighted)
		{
			node.isHighlighted = false;
			highlightNode(nodeNr);
		}
	}
	
	public void setAllNodeHighlightColor(Color highlightColor)
	{
		for(int i = 0; i < nodes.length; i++)
		{
			setNodeHighlightColor(i, highlightColor);
		}
	}
	
	public void setNodeSize(int nodeNr, int newRadius)
	{
		Timing duration = new MsTiming(500);
		PageRankNode prn = nodes[nodeNr];
		
		if(newRadius < minRadius)
		{
			newRadius = minRadius;
		}else if(newRadius > maxRadius)
		{
			newRadius = maxRadius;
		}
		
		prn.circle.moveBy("translateRadius", newRadius-prn.radius, newRadius-prn.radius, null,duration);
		prn.radius = newRadius;
//		CircleProperties cp = getCircleProperties(prn);
//		prn.circle.hide();
//		prn.circle = lang.newCircle(prn.circle.getCenter(),newRadius , prn.circle.getName(), null, cp);
//		
		updateEdgesForNode(nodeNr,duration);
	
	}
	
	public boolean isDanglingNode(int nodeNr)
	{
		for(int to = 0; to < nodes.length; ++to)
		{
			if(adjacencyMatrix[nodeNr][to] != 0)
			{
				return false;
			}
		}
		
		return true;
	}
	
	public List<Integer> getAllDanglingNodeNrs()
	{
		LinkedList<Integer> list = new LinkedList<Integer>();
		
		for(int i = 0; i < nodes.length; ++i)
		{
			if(isDanglingNode(i))
			{
				list.add(i);
			}
		}
		
		return list;
	}
	
	protected void updateEdgesForNode(int nodeNr, Timing duration)
	{
		for(int to = 0; to < graph.getSize(); to++)
		{
			if(edgeMatrix[nodeNr][to] != null)
			{
				updateEdge(edgeMatrix[nodeNr][to], nodes[nodeNr], nodes[to],duration);
			}
			
			if(edgeMatrix[to][nodeNr] != null)
			{
				updateEdge(edgeMatrix[to][nodeNr], nodes[to], nodes[nodeNr],duration);
			}
								
		}
	}
	
	protected void updateEdge(PageRankEdge edge, PageRankNode from, PageRankNode to, Timing duration)
	{
		Coordinates lineNodes[] = getEdgeCoordinates(from, to);
//		edge.line.moveTo("C","translateNodes 1",lineNodes[0] ,null, null);
//		edge.line.moveTo("C","translateNodes 2",lineNodes[1] ,null, null);
		edge.line.moveBy("translateNodes 1", lineNodes[0].getX()-edge.from.getX(),lineNodes[0].getY()-edge.from.getY(), null, duration);
		edge.line.moveBy("translateNodes 2", lineNodes[1].getX()-edge.to.getX(),lineNodes[1].getY()-edge.to.getY(), null, duration);
		
//		PolylineProperties pp = getPolyLineProperties(edge);
//		
//		edge.line.hide();
//		edge.line = lang.newPolyline(lineNodes, "edge", null, pp); 
		edge.from = lineNodes[0];
		edge.to = lineNodes[1];
	}
	
	protected Coordinates[] getEdgeCoordinates(PageRankNode from, PageRankNode to)
	{
		Coordinates c1 = ((Coordinates)from.circle.getCenter());
		Coordinates c2 = ((Coordinates)to.circle.getCenter());
				
		int vecx = c2.getX()-c1.getX();
		int vecy = c2.getY()-c1.getY();
		
		double length =  Math.sqrt(vecx*vecx+vecy*vecy);
		
		if(length <= from.radius + to.radius)
		{
			//TODO: do something other when nodes are overlapping
		}
		
		int fromX = c1.getX() + (int)((float)vecx/(float)length * (float)from.radius);
		int fromY = c1.getY() +(int)((float)vecy/(float)length * (float)from.radius);
		
		int toX = c1.getX() + (int)((float)vecx/(float)length * (float)(length - to.radius));
		int toY = c1.getY() +(int)((float)vecy/(float)length * (float)(length - to.radius));
		
		Coordinates result[] = {new Coordinates(fromX, fromY),new Coordinates(toX, toY)};
		
		return result;
		
	}
	
	protected CircleProperties getCircleProperties(PageRankNode node)
	{
		CircleProperties cp = new CircleProperties();
		cp.set(AnimationPropertiesKeys.FILLED_PROPERTY,true);
		cp.set(AnimationPropertiesKeys.DEPTH_PROPERTY,getNodeDrawDepth());
		
		if(node != null)
		{
			if(node.isHighlighted)
			{
				cp.set(AnimationPropertiesKeys.FILL_PROPERTY,node.highlightColor);
			}else
			{
				cp.set(AnimationPropertiesKeys.FILL_PROPERTY,node.fillColor);
			}
		}else
		{
			cp.set(AnimationPropertiesKeys.FILL_PROPERTY,Color.WHITE);
		}
		
		return cp;
	}
	
	protected TextProperties getTextProperties()
	{
		TextProperties tp = new TextProperties();
    	tp.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.PLAIN, 12));
    	tp.set(AnimationPropertiesKeys.DEPTH_PROPERTY,getTextDrawDepth());
    	return tp;
	}
	
	protected PolylineProperties getPolyLineProperties(PageRankEdge edge)
	{
		PolylineProperties pp = new PolylineProperties();
		pp.set(AnimationPropertiesKeys.FWARROW_PROPERTY, true);
		pp.set(AnimationPropertiesKeys.DEPTH_PROPERTY, getEdgeDrawDepth());
		
		if(edge != null)
		{
			if(edge.isHighlighted)
			{
				pp.set(AnimationPropertiesKeys.COLOR_PROPERTY, edge.highlightColor);
			}else
			{
				pp.set(AnimationPropertiesKeys.COLOR_PROPERTY, edge.baseColor);
			}
		}else
		{
			pp.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLACK);
		}
		
		return pp;
	}
	
	protected class PageRankNode
	{
		public Text text = null;
		public Circle circle = null;
		public int radius = minRadius;
		public Color highlightColor = Color.GREEN;
		public Color fillColor = Color.WHITE;
		public boolean isHighlighted = false;
	}
	
	protected class PageRankEdge
	{
		public Polyline line = null;
		public Color highlightColor = Color.GREEN;
		public Color baseColor = Color.BLACK;
		public boolean isHighlighted = false;
		public Coordinates from;
		public Coordinates to;
		public boolean isDanglingEdge = false;
	}
	
	
}
