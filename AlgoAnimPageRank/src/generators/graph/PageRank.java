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

import algoanim.primitives.Graph;
import algoanim.primitives.generators.GraphGenerator;
import algoanim.primitives.generators.Language;
import algoanim.properties.*;
import algoanim.util.Coordinates;
import algoanim.util.Node;

import java.util.Hashtable;

import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;

public class PageRank implements Generator {
    private Language lang;
    private Graph g;
    
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
        Graph g = lang.addGraph(graph, null, gProps);
        lang.nextStep();

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
        return "1: procedure PageRank(G, iteration) . G: inlink file, iteration: # of iteration"
 +"\n"
 +"2: d ? 0.85 . damping factor: 0.85"
 +"\n"
 +"3: oh ? G . get outlink count hash from G"
 +"\n"
 +"4: ih ? G . get inlink hash from G"
 +"\n"
 +"5: N ? G . get # of pages from G"
 +"\n"
 +"6: for all p in the graph do"
 +"\n"
 +"7: opg[p] ? 1"
 +"\n"
 +"N"
 +"\n"
 +". initialize PageRank"
 +"\n"
 +"8: end for"
 +"\n"
 +"9: while iteration > 0 do"
 +"\n"
 +"10: dp ? 0"
 +"\n"
 +"11: for all p that has no out-links do"
 +"\n"
 +"12: dp ? dp + d ?"
 +"\n"
 +"opg[p]"
 +"\n"
 +"N"
 +"\n"
 +". get PageRank from pages without out-links"
 +"\n"
 +"13: end for"
 +"\n"
 +"14: for all p in the graph do"
 +"\n"
 +"15: npg[p] ? dp +"
 +"\n"
 +"1?d"
 +"\n"
 +"N"
 +"\n"
 +". get PageRank from random jump"
 +"\n"
 +"16: for all ip in ih[p] do"
 +"\n"
 +"17: npg[p] ? npg[p] + d?opg[ip]"
 +"\n"
 +"oh[ip]"
 +"\n"
 +". get PageRank from inlinks"
 +"\n"
 +"18: end for"
 +"\n"
 +"19: end for"
 +"\n"
 +"20: opg ? npg . update PageRank"
 +"\n"
 +"21: iteration ? iteration ? 1"
 +"\n"
 +"22: end while"
 +"\n"
 +"23: end procedure";
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

}