/*
 * Netzplan.java
 * Jan Ulrich Schmitt & Dennis Juckwer, 2015 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;

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

    public void init(){
        lang = new AnimalScript("Netzplantechnik", "Jan Ulrich Schmitt & Dennis Juckwer", 800, 600);
        lang.setStepMode(true);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
        graph = (Graph)primitives.get("graph");
        //lang.addGraph(graph);
        NetzplanGraph n = new NetzplanGraph();
        List<Integer> nodesToProcess = n.getEndNodes();
        for(Integer currentNode: nodesToProcess){
        	
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