package generators.graph;

import java.awt.Color;
import java.awt.Font;

import algoanim.animalscript.AnimalCircleGenerator;
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
import algoanim.util.Node;
import algoanim.primitives.*;

public class PageRankGraph{

	private Graph graph;
	
	PageRankNode nodes[];
	int[][] adjacencyMatrix;
	PageRankEdge[][] edgeMatrix;
	Language lang;
	
	int minRadius = 15;
	int maxRadius = 80;
	
	
	public PageRankGraph(Graph graph, Language lang) {
		this.graph = graph;
		adjacencyMatrix = graph.getAdjacencyMatrix();
		edgeMatrix = new PageRankEdge[adjacencyMatrix.length][adjacencyMatrix[0].length];
		this.lang = lang;
		init();
	}
	
	private void init()
	{
		nodes = new PageRankNode[graph.getSize()];
		for(int i = 0; i < graph.getSize(); i++)
		{
			nodes[i] = new PageRankNode();
			
			CircleProperties cp = new CircleProperties();
    		cp.set(AnimationPropertiesKeys.FILLED_PROPERTY,true);
    		//cp.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY,nodes[i].highlightColor);
    		cp.set(AnimationPropertiesKeys.FILL_PROPERTY,nodes[i].fillColor);
			nodes[i].circle = lang.newCircle(graph.getNode(i),minRadius , graph.getNodeLabel(i), null, cp);
			TextProperties tp = new TextProperties();
	    	tp.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.PLAIN, 8));
			nodes[i].text = lang.newText(nodes[i].circle.getCenter(), graph.getNodeLabel(i), graph.getNodeLabel(i)+" text", null, tp);
			
		}
		
		
		for(int to = 0; to < graph.getSize(); to++)
		{
			for(int from = 0; from < graph.getSize(); from++)
			{
				if(adjacencyMatrix[from][to] != 0)
				{
					PageRankEdge edge = new PageRankEdge();				
					edge.line = createLine(nodes[from], nodes[to]);
					edgeMatrix[from][to] = edge;
				}
			}
		}
	    
	}

	protected Polyline createLine(PageRankNode from, PageRankNode to)
	{
		Coordinates c1 = ((Coordinates)from.circle.getCenter());
		Coordinates c2 = ((Coordinates)to.circle.getCenter());
		
		System.out.println("jooooooooooooooo "+c1.getX()+""+c1.getY());
		
		int vecx = c2.getX()-c1.getX();
		int vecy = c2.getY()-c1.getY();
		
		double length =  Math.sqrt(vecx*vecx+vecy*vecy);
		
		if(length <= from.circle.getRadius() + to.circle.getRadius())
		{
			//TODO: do something other when nodes are overlapping
		}
		
		int fromX = c1.getX() + (int)((float)vecx/(float)length * (float)from.circle.getRadius());
		int fromY = c1.getY() +(int)((float)vecy/(float)length * (float)from.circle.getRadius());
		
		int toX = c1.getX() + (int)((float)vecx/(float)length * (float)(length - to.circle.getRadius()));
		int toY = c1.getY() +(int)((float)vecy/(float)length * (float)(length - from.circle.getRadius()));
		
		Node lineNodes[] = {new Coordinates(fromX,fromY),new Coordinates(toX,toY)};
		
		PolylineProperties pp = new PolylineProperties();
		pp.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.black);
		pp.set(AnimationPropertiesKeys.FWARROW_PROPERTY, true);
		return lang.newPolyline(lineNodes, "edge", null, pp);
	}
	
	protected class PageRankNode
	{
		public Text text = null;
		public Circle circle = null;
		public Color highlightColor = Color.GREEN;
		public Color fillColor = Color.WHITE;
		public boolean isHighlighted = false;
	}
	
	protected class PageRankEdge
	{
		public Polyline line = null;
		public Color highlightColor = Color.GREEN;
		public Color base = Color.BLACK;
	}
}
