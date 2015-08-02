/*
 * Netzplan.java
 * Jan Ulrich Schmitt & Dennis Juckwer, 2015 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import algoanim.primitives.Graph;
import algoanim.primitives.SourceCode;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;

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
        setHeader();
        setInformationText();
        this.setSourceCode();
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
        for(Integer currentNode:n.getStartNodes()){
        	this.drawCriticalPath(currentNode);
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
        
        return lang.toString();
    }
    
    

	private void calculateFirstDirection(Integer node){
		List<Integer> predecessors = n.getPredecessors(node);;
		if(n.isStartNode(node)){
    		lang.nextStep();
    		n.setEarliestStartTime(node, 0);
    		lang.nextStep();
    		n.setEarliestEndTime(node, n.getProcessTime(node));
    		//lang.nextStep();
    	}else{
        	for(Integer currentPredecessor: predecessors){
        		if(n.hasValidEntry(currentPredecessor, NetzplanGraph.CellID.EarliestEndTime) == false){
        			calculateFirstDirection(currentPredecessor);
        		}
        	}
        	for(Integer currentPredecessor: predecessors){
        		if(n.hasValidEntry(node, NetzplanGraph.CellID.EarliestEndTime)==false ||n.getEarliestEndTime(currentPredecessor) > n.getEarliestStartTime(node)){
        			lang.nextStep();
        			n.setEarliestStartTime(node, n.getEarliestEndTime(currentPredecessor));
        			lang.nextStep();
        			n.setEarliestEndTime(node, n.getEarliestStartTime(node) + n.getProcessTime(node));
        		}
        	}
    		
    	}
    	

    	
    }
	
	private void calculateSecondDirection(Integer node) {
		List<Integer> successors = n.getSuccessors(node);
    	if(n.isEndNode(node)){
    		lang.nextStep();
    		n.setLatestStartTime(node, n.getEarliestStartTime(node));
    		lang.nextStep();
    		n.setLatestEndTime(node, n.getEarliestEndTime(node));
    	}
    	
    	for(Integer currentSuccessor: successors){
    		if(n.hasValidEntry(currentSuccessor, NetzplanGraph.CellID.LatestEndTime) == false){
    			calculateSecondDirection(currentSuccessor);
    		}
    	}
    	
    	for(Integer currentSuccessor: successors){
    		if(n.hasValidEntry(node, NetzplanGraph.CellID.LatestEndTime)==false || n.getLatestStartTime(currentSuccessor)< n.getLatestEndTime(node)){
    			lang.nextStep();
    			n.setLatestStartTime(node, n.getLatestStartTime(currentSuccessor)- n.getProcessTime(node));
    			lang.nextStep();
    			n.setLatestEndTime(node, n.getLatestStartTime(node)+ n.getProcessTime(node));
    		}
    	}
		
	}
	
//	private void drawCriticalPath(){
//		LinkedList<Integer> nodesToProcess = new LinkedList<Integer>();
//		nodesToProcess.addAll(n.getEndNodes());
//		Integer actualNode;
//		while(nodesToProcess.isEmpty()==false){
//			actualNode = nodesToProcess.pop();
//			if(n.getEarliestStartTime(actualNode) == n.getLatestStartTime(actualNode)){
//				for(Integer actualPredecessor: n.getPredecessors(actualNode)){
//					lang.nextStep();
//					n.highlightEdge(actualPredecessor, actualNode);
//					nodesToProcess.add(actualPredecessor);
//				}
//				
//			}
//		}
//	
//	}
	
	private boolean drawCriticalPath(Integer actualNode){
		LinkedList<Integer> currentSuccessors = new LinkedList<Integer>();
		currentSuccessors.addAll(n.getSuccessors(actualNode));
		boolean isCriticalStep = false;
		for(Integer actualSuccessor: currentSuccessors){
			if(n.getEarliestStartTime(actualNode) == n.getLatestStartTime(actualNode) && (n.isEndNode(actualSuccessor)||drawCriticalPath(actualSuccessor) )){
				//lang.nextStep();
				n.highlightEdge(actualNode, actualSuccessor);
				isCriticalStep = true;
			}
			
		}
				
		
		return isCriticalStep;
	}
	
    private void setHeader(){
    	TextProperties headerProps = new TextProperties();
    	headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.BOLD, 24));
    	//headerProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, edgehighlightcolor);
    	headerProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
    	lang.newText(new Coordinates(20,30), "Die Netzplantechnik", "header", null, headerProps);
    }
	
	private void setInformationText(){
    	SourceCodeProperties infoProps = new SourceCodeProperties();
    	infoProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 20));
    	SourceCode infoText = lang.newSourceCode(new Coordinates(20,100), "InfoText", null, infoProps);
    	
    	infoText.addCodeLine("Bei der Netzplantechnik handelt es sich um eine Methode, welche im Rahmen der Terminplanung bzw. des", "Line0", 0, null);
    	infoText.addCodeLine("des Projektmanagements zum Einsatz kommt. Das Ziel besteht darin, die Dauer eines Projektes auf Basis ", "Line1", 0, null);
    	infoText.addCodeLine("der einzelnen Arbeitsvorgaenge und ihrer Beziehungen untereinander zu bestimmen. Die Beziehungen der  ", "Line13", 0, null);
    	infoText.addCodeLine("einzelnen Vorgange werden dabei in Form eines gerichteten Graphen dargestellt. Dabei ist zu beachten,", "Line2", 0, null);
    	infoText.addCodeLine("dass die Beziehungen zwischen den Arbeitsvorgaengen eindeutig zu definieren sind, weshalb Zyklen ", "Line3", 0, null);
    	infoText.addCodeLine("nicht zulaessig sind.", "Line4", 0, null);
    	infoText.addCodeLine("Neben der minimalen Gesamtdauer, welche das zu untersuchende Projekt im Idealfall benoetigt, werden ", "Line5", 0, null);
    	infoText.addCodeLine("zudem fuer jeden Arbeitsvorgang sogenannte Puffer ermittelt, welche angeben in welchem Ausmass ", "Line6", 0, null);
    	infoText.addCodeLine("Verzoegerungen eines Arbeitsvorganges moeglich sind, ohne dass sie sich negativ auf die Gesamtdauer", "Line7", 0, null);
    	infoText.addCodeLine("des Projektes auszuwirken.", "Line8", 0, null);
    	lang.nextStep();
    	infoText.hide();
    }
	
    private SourceCode setSourceCode(){
    	SourceCodeProperties sProb = new  SourceCodeProperties();
        sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,
                Font.PLAIN, 12));
        sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
        sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        
        SourceCode src = lang.newSourceCode(new Coordinates(700, 50), "SourceCode", null, sProb);
        src.addCodeLine("01. For all nodes without outgoing edges do", "Code0", 0, null);
        src.addCodeLine("02.     calculateFirstDirection(node)", "Code1", 0, null);
        src.addCodeLine("03. For all nodes withoud ingoing edges do", "Code2", 0, null);
        src.addCodeLine("04.     calculateSecendDirection(node)", "Code3", 0, null);
        src.addCodeLine("", "Code4", 0, null);
        src.addCodeLine("05. calculateFirstDirection(node)", "Code5", 0, null);
        src.addCodeLine("06.     if node has no ingoing edges do", "Code6", 0, null);
        src.addCodeLine("07.         EarliestStartTime of node = 0", "Code7", 0, null);
        src.addCodeLine("08.          EarliestEndTime of node = EarliestStartTime of Node + ProcessTime of node", "Code8", 0, null);
        src.addCodeLine("09.     if node has ingoing edges do:", "Code9", 0, null);
        src.addCodeLine("10.         for each predecessor of node do", "Code10", 0, null);
        src.addCodeLine("11.             if EarliestStartTime of Predecessor has not been set do", "Code11", 0, null);
        src.addCodeLine("12.                 calculateFirstDirection(currentPredecessor)", "Code12", 0, null);
        src.addCodeLine("13.     for each predecessor of node do:", "Code13", 0, null);
        src.addCodeLine("14.         if EarliestStartTime of node has not been set or EarliestEndTime of predecssor > EarliestStartTime of node", "Code14", 0, null);
        src.addCodeLine("15.             EarliestStartTime of node = EarliestEndTime of Predecessor", "Code15", 0, null);
        src.addCodeLine("16.             EarliestEndTime  of node = EarliestStartTime of node + ProcessTime of node", "Code16", 0, null);
        src.addCodeLine("", "Code17", 0, null);
        src.addCodeLine("17. calculateSecondDirection(node)", "Code18", 0, null);
        src.addCodeLine("18.     if node has no outgoing edges do", "Code19", 0, null);
        src.addCodeLine("19.          LatestStartTime of node = EarliestStartTime of Node", "Code20", 0, null);
        src.addCodeLine("20.          LatestEndTime of node = EearliestEndTime of Node", "Code21", 0, null);
        src.addCodeLine("21.     if node has outgoing edges do:", "Code22", 0, null);
        src.addCodeLine("22.         for each successor of node do", "Code23", 0, null);
        src.addCodeLine("23.             if LatestStartTime of Successor has not been set do", "Code24", 0, null);
        src.addCodeLine("24.                 calculateSecondDirection(currentSuccessor)", "Code25", 0, null);
        src.addCodeLine("25.     for each successor of node do:", "Code26", 0, null);
        src.addCodeLine("26.         if LatestStartTime of node has not been set or LatestStartTime of successor < LatestEndTime of node", "Code27", 0, null);
        src.addCodeLine("27.             LatestStartTime of node = LatestStartTime of Successor - ProcessTime of node", "Code28", 0, null);
        src.addCodeLine("28.             LatestEndTime of node = LatestStartTime of node + ProcessTime of node", "Code29", 0, null);
        src.highlight(0);
        return src;
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