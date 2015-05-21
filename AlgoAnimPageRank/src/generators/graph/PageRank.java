/*
 * PageRank.java
 * Jan Ulrich Schmitt, Dennis Juckwer, 2015 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.graph;

import generators.framework.Generator;
import generators.framework.GeneratorType;

import java.awt.Color;
import java.awt.Font;
import java.util.Locale;

import algoanim.primitives.Circle;
import algoanim.primitives.Graph;
import algoanim.primitives.Text;
import algoanim.primitives.generators.GraphGenerator;
import algoanim.primitives.generators.Language;
import algoanim.properties.*;
import algoanim.util.Coordinates;
import algoanim.util.Node;

import java.util.Hashtable;

import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalCircleGenerator;
import algoanim.animalscript.AnimalScript;

public class PageRank implements Generator {
    private Language lang;
    private Graph g;
    
    Circle graphCircles[];
    Text graphText[];
    
    public PageRank(){
    	//System.out.println("Hello World");
    }

    public void init(){
        lang = new AnimalScript("PageRank", "Jan Ulrich Schmitt, Dennis Juckwer", 800, 600);
        lang.setStepMode(true);
        
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
    	TextProperties headerProps = new TextProperties();
    	headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.BOLD, 24));
    	lang.newText(new Coordinates(20,30), "Der PageRank-Algorithmus", "header", null, headerProps);
    	lang.nextStep();
    	
    	Graph graph = (Graph)primitives.get("graph");
    	GraphProperties gProps = new GraphProperties("graphprop");
        gProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
        gProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.GREEN);
        gProps.set(AnimationPropertiesKeys.DIRECTED_PROPERTY, true);
        gProps.set(AnimationPropertiesKeys.DEPTH_PROPERTY,10);
        Graph g = lang.addGraph(graph, null, gProps);
        lang.nextStep();
        
        setUpAdditionalGraphProperties(g);

        PageRankCalculator prc = new PageRankCalculator(graph.getAdjacencyMatrix());
        float minDelta = 0.000001f;
        int i = 0;
        System.out.println("iteration "+i+":\n"+prc.toString()+"\n");
        boolean highlightedEdges = false;
        while(prc.calcNextStep() > minDelta)
        {
        	++i;
        	
        	System.out.println("iteration "+i+":\n"+prc.toString()+"\n");
        	int[][] adjacencyMatrix = g.getAdjacencyMatrix();
        	for(int to = 0; to< prc.getCurrentValues().length; to++){
        		highlightedEdges = false;
        		g.highlightNode(to, null, null);
        		lang.nextStep();
        		for(int from = 0; from < prc.getCurrentValues().length; from++){
        			if(adjacencyMatrix[from][to]==1){
        				g.highlightEdge(from, to, null, null);
        				lang.nextStep();
        				//g.unhighlightEdge(from, to, null, null);
        				highlightedEdges = true;
        			}
        			
        		}
        		
        		if(highlightedEdges)
        		{
        			for(int from = 0; from < prc.getCurrentValues().length; from++){
            			if(adjacencyMatrix[from][to]==1){
            				g.unhighlightEdge(from, to, null, null);	
            			}
            		}
            		lang.nextStep();
        		}
        		
        		
        		//g.getProperties().set(AnimationPropertiesKeys.FILL_PROPERTY, colorLin(Color.YELLOW,Color.RED,prc.getCurrentValues()[to]));
        		g.getProperties().set(AnimationPropertiesKeys.FILL_PROPERTY,new Color(0.0f,0.0f,0.0f,0.5f));
        		lang.addGraph(g, null, gProps);
        		g.unhighlightNode(to, null, null);
        		System.out.println("");
        		//lang.nextStep();
        	}
        	
        	
        }
        

        System.out.println(lang.toString());
        return lang.toString();
    }
    
    

    public String getName() {
        return "PageRank";
    }

    public String getAlgorithmName() {
        return "PageRank";
    }

    public String getAnimationAuthor() {
        return "Jan Ulrich Schmitt, Dennis Juckwer";
    }

    public String getDescription(){
        return "Der PageRank-Algorithmus ist ein Algorithmus zur Bewertung von Knoten in einem Netzwerk."
 +" Larry Page und Sergei Brin entwickelten ihn an der Stanford University zur Bewertung von"
 +" Webseiten im Rahmen ihrer mittlerweile weltweit bekannnten Suchmaschine Google. Das Bewertungsprinzip"
 +" sieht dabei vor, dass das Gewicht einer Seite umso größer ist, je mehr andere Seiten auf sie verweisen."
 +" Der Effekt wird dabei von dem Gewicht der auf diese Seite verweisenden Seiten verstärtk."
 +"\n"
 +"\nEine moegliche Interpretation des PageRanks liefert das sogenannte Random Surfer Modell. Im Rahmen dieses"
 +" Modells repraesentiert der PageRank eines Knotens bzw. einer Webseite (bei einer Normierung der Summe der PageRanks auf 1) die"
 +" Wahrscheinlichkeit mit der sich ein sogenannter Zufallssurfer auf einer bestimmten Webseite befindet. Hierbei gilt, dass"
 + " der Zufallssurfer mit einer Wahrscheinlichkeit von d den Links auf der Webseite folgt, auf der er sich gerade befindet."
 + " Mit einer Wahrscheinlichkeit von 1-d ruft er manuell in seinem Browser eine der anderen Webseiten auf.";
    }

    public String getCodeExample(){
        return "PageRank (Graph G, dampingfactor d)"
      + "\n    while PageRank Values change signifficantly"
      + "\n        for all nodes in G do"
      + "\n            PageRank of actual node n <- (1-d)/|G|"
      + "\n        for each predecessor p of actual node n do"
      + "\n	           PR of n <- PR of n + d*((PR of p in the last step)/outgoing edges from p)"
      + "\n	       for each dangling node dn do// dangling nodes are nodes with no successors"
      + "\n 	       PR of n <- PR of n + d * (1/|G|)";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
        return Locale.GERMAN;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_GRAPH);
    }

    public String getOutputLanguage() {
        return Generator.PSEUDO_CODE_OUTPUT;
    }
    
    /////////////////////////////////////Helper Functions///////////////////////////////
   
    private Color colorLin(Color startColor, Color endColor, float percent)
    {
    	int[] color1 = new int[3];
    	color1[0] = startColor.getRed();
    	color1[1] = startColor.getGreen();
    	color1[2] = startColor.getBlue();
    	int[]color2 = new int[3];
    	color2[0] = endColor.getRed();
    	color2[1] = endColor.getGreen();
    	color2[2] = endColor.getBlue();

    	for(int i = 0; i < color1.length; i++)
    	{    		
    		color1[i] = color1[i] +(int)((float)(color2[i]-color1[i]) * percent);
    		
    		if(color1[i] > 255) color1[i] = 255;
    		if(color1[i] < 0) color1[i] = 0;
    	}
    	
    	Color result = new Color(color1[0],color1[1],color1[2]);
    	
    	return result;   
    }
    
    private Color colorLin(Color startColor, Color endColor, float minValue, float maxValue, float value)
    {
    	if(minValue == maxValue)
    		return endColor;
    	
    	return colorLin(startColor, endColor,((value-minValue)/(maxValue-minValue)));
    }
    
    private void setUpAdditionalGraphProperties(Graph g)
    {
    	PageRankGraph p = new PageRankGraph(g,lang);
    	p.setNodeText(3, "Hallo");
    	p.setNodeSize(3, 100);
    	p.setNodeFillColor(3, Color.CYAN);
    	lang.nextStep();
    	p.setNodeSize(3, 1);
    	p.highlightNode(1);
    	p.setNodeFillColor(1, Color.BLUE);
    	p.setNodeHighlightColor(1, Color.RED);
    	lang.nextStep();
    	p.unhighlightNode(1);
    	
    	p.highlightEdge(3,1, null, null);
    	p.setEdgeHighlightColor(3, 1, Color.BLUE);
    	p.setNodeSize(1, 20);
    	lang.nextStep();
    	p.unhighlightEdge(3, 1, null, null);
    	p.setEdgeBaseColor(3, 1, Color.GRAY);
    	
    	
    }

}