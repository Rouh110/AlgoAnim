/*
 * Netzplan.java
 * Jan Ulrich Schmitt & Dennis Juckwer, 2015 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import algoanim.primitives.Graph;
import algoanim.primitives.generators.Language;

import java.util.Hashtable;

import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;

public class Netzplan implements Generator {
    private Language lang;
    private Graph graph;
    NetzplanGraph n;

    public void init(){
        lang = new AnimalScript("Netzplantechnik", "Jan Ulrich Schmitt & Dennis Juckwer", 800, 600);
        lang.setStepMode(true);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        graph = (Graph)primitives.get("graph");
        //lang.addGraph(graph);
        n = new NetzplanGraph((AnimalScript)lang, graph);
        
        if(n.hasLoops())
        {
        	//TODO: Message about invalid graph
        	return lang.toString();
        }
        
        List<Integer> nodesToProcess = n.getEndNodes();
        for(Integer currentNode: nodesToProcess){
        	/*
        	n.setEarliestStartTime(currentNode, Integer.MAX_VALUE);
        	n.setEarliestEndTime(currentNode, Integer.MAX_VALUE);
        	List<Integer> predecessors = n.getPredecessor(currentNode);
        	for(Integer currentPredecessor: predecessors){
        		if(n.hasValidEntry(currentPredecessor, NetzplanGraph.CellID.EarliestEndTime)==false){
        			calculateValue(currentPredecessor);
        		}
        	}
        	for(Integer currentPredecessor: predecessors){
        		if(n.getEarliestStartTime(currentPredecessor) < n.getEarliestStartTime(currentNode)){
        			n.setEarliestStartTime(currentNode, n.getEarliestEndTime(currentPredecessor));
        			n.setEarliestEndTime(currentNode, n.getEarliestStartTime(currentNode) + n.getProcessTime(currentNode));
        		}
        	}*/
        	this.calculateFirstDirection(currentNode);
        	
        }
        
        nodesToProcess = n.getStartNodes();
        for(Integer currentNode: nodesToProcess){
        	this.calculateSecondDirection(currentNode);
        }
        /*
        List<Integer> nodesToProcess = n.getStartNodes();
        for(Integer currentNode: nodesToProcess){
        	n.setEarliestEndTime(currentNode, Integer.MAX_VALUE);
        	n.setEarliestStartTime(currentNode, Integer.MAX_VALUE);
        	List<Integer> currentPredecessors = n.getPredecessor(currentNode);
        	if(currentPredecessors.isEmpty()){
        		n.setEarliestStartTime(currentNode, 0);
        		n.setLatestEndTime(currentNode, n.getProcessTime(currentNode));
        	}else{
        		for(Integer actualPredecessor: currentPredecessors){
        			if(n.getEarliestEndTime(actualPredecessor) < n.getEarliestStartTime(currentNode)){
        				n.setEarliestStartTime(currentNode, n.getEarliestEndTime(actualPredecessor));
        				n.setEarliestEndTime(currentNode, n.getEarliestStartTime(currentNode) + n.getProcessTime(currentNode));
        			}
        		}
        	}
        	
        	
        }*/
        
        
        n.setAllNodeBaseColor(Color.blue);
        n.setAllEdgeBaseColor(Color.blue);
        n.highlightCell(0, NetzplanGraph.CellID.Name);
        n.setNodeHighlightColor(1,Color.red);
        n.highlightCell(1, NetzplanGraph.CellID.Name);
        
        return lang.toString();
    }
    
    

	private void calculateFirstDirection(Integer node){
    	List<Integer> predecessors = n.getPredecessors(node);
    	if(n.isStartNode(node)){
    		n.setEarliestStartTime(node, 0);
    		n.setEarliestEndTime(node, n.getProcessTime(node));
    	}
    	
    	for(Integer currentPredecessor: predecessors){
    		if(n.hasValidEntry(currentPredecessor, NetzplanGraph.CellID.EarliestEndTime) == false){
    			calculateFirstDirection(currentPredecessor);
    		}
    	}
    	for(Integer currentPredecessor: predecessors){
    		if(n.hasValidEntry(node, NetzplanGraph.CellID.EarliestEndTime)==false ||n.getEarliestEndTime(currentPredecessor) > n.getEarliestStartTime(node)){
    			n.setEarliestStartTime(node, n.getEarliestEndTime(currentPredecessor));
    			n.setEarliestEndTime(node, n.getEarliestStartTime(node) + n.getProcessTime(node));
    		}
    	}
    	
    }
	
	private void calculateSecondDirection(Integer node) {
		List<Integer> successors = n.getSuccessors(node);
    	if(n.isEndNode(node)){
    		n.setLatestStartTime(node, n.getEarliestStartTime(node));
    		n.setLatestEndTime(node, n.getEarliestEndTime(node));
    	}
    	
    	for(Integer currentSuccessor: successors){
    		if(n.hasValidEntry(currentSuccessor, NetzplanGraph.CellID.LatestEndTime) == false){
    			calculateSecondDirection(currentSuccessor);
    		}
    	}
    	
    	for(Integer currentSuccessor: successors){
    		if(n.hasValidEntry(node, NetzplanGraph.CellID.LatestEndTime)==false || n.getLatestStartTime(currentSuccessor)< n.getLatestEndTime(node)){
    			n.setLatestStartTime(node, n.getLatestStartTime(currentSuccessor)- n.getProcessTime(node));
    			n.setLatestEndTime(node, n.getLatestStartTime(node)+ n.getProcessTime(node));
    		}
    	}
		
	}
    
    

    public String getName() {
        return "Netzplantechnik";
    }

    public String getAlgorithmName() {
        return "Netzplantechnik";
    }

    public String getAnimationAuthor() {
        return "Jan Ulrich Schmitt & Dennis Juckwer";
    }

    public String getDescription(){
        return "Berechnung der fruehesten und spaetesten Anfangs- und Endzeitpunkte einzelner Arbeitsschritte, "
 +"\n"
 +"sowie Identifikation kritischer Pfade. ";
    }

    public String getCodeExample(){
        return "Code1"
 +"\n"
 +"Code2"
 +"\n"
 +"Code3";
    }

    public String getFileExtension(){
        return "asu";
    }

    public Locale getContentLocale() {
        return Locale.GERMAN;
    }

    public GeneratorType getGeneratorType() {
        return new GeneratorType(GeneratorType.GENERATOR_TYPE_MORE);
    }

    public String getOutputLanguage() {
        return Generator.PSEUDO_CODE_OUTPUT;
    }

}