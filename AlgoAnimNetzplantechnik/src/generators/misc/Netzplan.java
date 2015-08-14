/*
 * Netzplan.java
 * Jan Ulrich Schmitt & Dennis Juckwer, 2015 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.misc;

import generators.framework.Generator;
import generators.framework.GeneratorType;
import interactionsupport.models.AnswerModel;
import interactionsupport.models.FillInBlanksQuestionModel;
import interactionsupport.models.MultipleChoiceQuestionModel;
import interactionsupport.models.MultipleSelectionQuestionModel;
import interactionsupport.models.QuestionGroupModel;
import interactionsupport.models.TrueFalseQuestionModel;
import interactionsupport.views.MultipleSelectionQuestionView;
import interactionsupport.views.QuestionView;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Currency;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import algoanim.primitives.Graph;
import algoanim.primitives.SourceCode;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.MatrixProperties;
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
    SourceCode src1;
    SourceCode src2;
    private TextProperties headerStyle; //headerStyle
    private SourceCodeProperties informationTextStyle;
    private SourceCodeProperties sourceCodeStyle;
    
    
    String qg01 = "firstDirectionQuestions";
    String qg02 = "secondDirectionQuestions";
    String qg03 = "finnishQuestions";

    public void init(){
        lang = new AnimalScript("Netzplantechnik", "Jan Ulrich Schmitt & Dennis Juckwer", 800, 600);        
        lang.setStepMode(true);
        lang.setInteractionType(Language.INTERACTION_TYPE_AVINTERACTION);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {

        graph = (Graph)primitives.get("graph");
        informationTextStyle = (SourceCodeProperties) props.getPropertiesByName("InformationTextStyle");
        headerStyle = (TextProperties) props.getPropertiesByName("HeaderStyle");
        sourceCodeStyle = (SourceCodeProperties) props.getPropertiesByName("SourcecodeStyle");
        Color edgeColor = (Color)primitives.get("EdgeColor");
        MatrixProperties matrixProperties = (MatrixProperties) props.getPropertiesByName("NodeStyle");
        
        setHeader();
        setInformationText();
        src1 = setSourceCodeForward();    
        n = new NetzplanGraph((AnimalScript)lang, graph,matrixProperties);
        n.setAllEdgeBaseColor(edgeColor);
        src1.highlight(0);
        //lang.nextStep();
       
        if(n.hasLoops())
        {
        	//TODO: Message about invalid graph
        	return lang.toString();
        }
        src1.unhighlight(0);
        List<Integer> nodesToProcess = n.getEndNodes();
        for(Integer currentNode: nodesToProcess){
        	/////////////src1.unhighlight(0);
        	src1.highlight(1);
        	this.calculateFirstDirection(currentNode);
        	
        }
        
        
        
        nodesToProcess = n.getStartNodes();
        src1.hide();
        SourceCode algorithmChange = setChangeAlgorithmInformation();
        lang.nextStep();
        
        
        algorithmChange.hide();
        src2 = this.setSourceCodeBackward();
        src2.highlight(2);
        lang.nextStep();
        
        src2.unhighlight(2);
        for(Integer currentNode: nodesToProcess){
        	src2.highlight(3);
        	this.calculateSecondDirection(currentNode);
        }
        src2.hide();
        
        LinkedList<Integer> critcalPathNodes = new LinkedList<Integer>();
        
        
        for(Integer currentNode:n.getStartNodes()){
        	this.getCriticalPath(currentNode,critcalPathNodes);
        }
        
        
        MultipleSelectionQuestionModel m1 = new MultipleSelectionQuestionModel("Kritischer Pfad");
        m1.setPrompt("Welche Knoten gehören alles zu einem Kritischen Pfad? (Es kann mehr als ein Kritischer Pfad geben.)");
        
        for(Integer currentNode : n.getAllNodes())
        {
        	if(critcalPathNodes.contains(currentNode))
        	{
        		m1.addAnswer(n.getName(currentNode), 5,n.getName(currentNode) + " gehört zu einem Kritischen Pfad.\n");
        	}else
        	{
        		m1.addAnswer(n.getName(currentNode), -5,n.getName(currentNode) + " gehört nicht zu einem Kritischen Pfad.\n");
        	}
        	
        }
        lang.addMSQuestion(m1);
       
        
        SourceCode criticalPathText = setCriticicalPathInformation();
        for(Integer currentNode:n.getStartNodes()){
        	this.drawCriticalPath(currentNode);
        }

        
        
        lang.finalizeGeneration();

        return lang.toString();
    }
    
    

	private void calculateFirstDirection(Integer node){

    	src1.highlight(5);
    	n.highlightNode(node);
    	lang.nextStep();
    	src1.unhighlight(12);
    	src1.unhighlight(1);
    	src1.unhighlight(5);
		List<Integer> predecessors = n.getPredecessors(node);
		if(n.isStartNode(node)){
    		src1.highlight(6);
    		lang.nextStep();
    		src1.unhighlight(6);
    		src1.highlight(7);
			n.setEarliestStartTime(node, 0);
			lang.nextStep();
			src1.unhighlight(7);
			src1.unhighlight(6);
			src1.highlight(8);
    		n.setEarliestEndTime(node, n.getProcessTime(node));
    		lang.nextStep();
    		src1.unhighlight(8);
    		

    	}else{
    		src1.highlight(9);
    		for(Integer currentPredcessor: predecessors){
    			n.highlightEdge(currentPredcessor, node);
    		}
    		lang.nextStep();
    		

    		src1.unhighlight(9);
    		src1.highlight(10);
    		lang.nextStep();
    		for(Integer currentPredecessor: predecessors){
        		for(Integer innerPredecessors: predecessors){
        			n.unHighlightEdge(innerPredecessors, node);
        		}
        		src1.unhighlight(10);
    			if(n.hasValidEntry(currentPredecessor, NetzplanGraph.CellID.EarliestEndTime) == false){
    				n.highlightEdge(currentPredecessor, node);
    				src1.highlight(11);
    				lang.nextStep();
    				n.unHighlightEdge(currentPredecessor, node);
    				src1.unhighlight(11);
        			src1.highlight(12);
        			calculateFirstDirection(currentPredecessor);
        		}
        	}
    		src1.highlight(13);
    		for(Integer innerPredecessors: predecessors){
    			n.highlightEdge(innerPredecessors, node);
    		}
    		lang.nextStep();
        	for(Integer currentPredecessor: predecessors){
        		for(Integer innerPredecessors: predecessors){
        			n.unHighlightEdge(innerPredecessors, node);
        		}
        		src1.unhighlight(13);
        		if(n.hasValidEntry(node, NetzplanGraph.CellID.EarliestEndTime)==false ||n.getEarliestEndTime(currentPredecessor) > n.getEarliestStartTime(node)){
        			n.highlightEdge(currentPredecessor, node);
        			src1.highlight(14);
        			lang.nextStep();
        			n.setEarliestStartTime(node, n.getEarliestEndTime(currentPredecessor));
        			src1.highlight(15);
        			src1.unhighlight(14);
        			lang.nextStep();
        			src1.unhighlight(15);
        			src1.highlight(16);
        			n.setEarliestEndTime(node, n.getEarliestStartTime(node) + n.getProcessTime(node));
        			lang.nextStep();
        			src1.unhighlight(16);
        			n.unHighlightEdge(currentPredecessor, node);
        		}
        	}
    		
    	}
		n.unhighlightNode(node);
    	

    	
    }
	
	private void calculateSecondDirection(Integer node) {
    	src2.highlight(5);
    	n.highlightNode(node);
    	lang.nextStep();
    	src2.unhighlight(12);
    	src2.unhighlight(3);
    	src2.unhighlight(5);
		List<Integer> successors = n.getSuccessors(node);
    	if(n.isEndNode(node)){
    		src2.highlight(6);
    		lang.nextStep();
    		src2.unhighlight(6);
    		src2.highlight(7);
    		n.setLatestStartTime(node, n.getEarliestStartTime(node));
    		lang.nextStep();
    		src2.unhighlight(7); //////////////////
			src2.unhighlight(6);
			src2.highlight(8);
    		n.setLatestEndTime(node, n.getEarliestEndTime(node));
    		lang.nextStep();
    		src2.unhighlight(8);
    	}else{
    		src2.highlight(9);
    		
    		for(Integer currentSuccessor: successors){
    			n.highlightEdge(node, currentSuccessor);
    		}
    		lang.nextStep();
    		
    		src2.unhighlight(9);
    		src2.highlight(10);
    		lang.nextStep();
        	for(Integer currentSuccessor: successors){
         		for(Integer innerSuccessors: successors){
        			n.unHighlightEdge(node, innerSuccessors);
        		}
         		src2.unhighlight(10);
        		if(n.hasValidEntry(currentSuccessor, NetzplanGraph.CellID.LatestEndTime) == false){
    				n.highlightEdge(node, currentSuccessor);
    				src2.highlight(11);
    				lang.nextStep();
    				n.unHighlightEdge(node, currentSuccessor);
    				src2.unhighlight(11);
        			src2.highlight(12);
        			calculateSecondDirection(currentSuccessor);
        		}
        	}
        	src2.highlight(13);
        	for(Integer currentSuccessor: successors){
        		for(Integer innerSuccessors: successors){
        			n.unHighlightEdge(node, innerSuccessors);
        		}
        		lang.nextStep();
        		src2.unhighlight(13);
        		if(n.hasValidEntry(node, NetzplanGraph.CellID.LatestEndTime)==false || n.getLatestStartTime(currentSuccessor)< n.getLatestEndTime(node)){
        			n.highlightEdge(node, currentSuccessor);
        			src2.highlight(14);
        			lang.nextStep();
        			n.setLatestStartTime(node, n.getLatestStartTime(currentSuccessor)- n.getProcessTime(node));
        			src2.highlight(15);
        			src2.unhighlight(14);
        			lang.nextStep();
          			src2.unhighlight(15);
        			src2.highlight(16);
        			n.setLatestEndTime(node, n.getLatestStartTime(node)+ n.getProcessTime(node));
        			lang.nextStep();
        			src2.unhighlight(16);
        			n.unHighlightEdge(node, currentSuccessor);
        		}
        	}
    	}
    	n.unhighlightNode(node);
    		
	}
	
	
	private boolean drawCriticalPath(Integer actualNode){
		LinkedList<Integer> currentSuccessors = new LinkedList<Integer>();
		currentSuccessors.addAll(n.getSuccessors(actualNode));
		boolean isCriticalStep = false;
		for(Integer actualSuccessor: currentSuccessors){
			if(n.getEarliestStartTime(actualNode) == n.getLatestStartTime(actualNode) && (n.isEndNode(actualSuccessor)||drawCriticalPath(actualSuccessor) )){
				n.highlightEdge(actualNode, actualSuccessor);
				isCriticalStep = true;
			}
			
		}
				
		
		return isCriticalStep;
	}
	
	private boolean getCriticalPath(Integer actualNode, List<Integer> criticalNodes)
	{
		LinkedList<Integer> currentSuccessors = new LinkedList<Integer>();
		currentSuccessors.addAll(n.getSuccessors(actualNode));
		boolean isCriticalStep = false;
		for(Integer actualSuccessor: currentSuccessors){
			if(n.getEarliestStartTime(actualNode) == n.getLatestStartTime(actualNode) && (n.isEndNode(actualSuccessor)||drawCriticalPath(actualSuccessor) )){
				criticalNodes.add(actualSuccessor);
				isCriticalStep = true;
			}
			
		}
		
		if(isCriticalStep)
		{
			criticalNodes.add(actualNode);
		}
				
		
		return isCriticalStep;
	}
	
	
	
    private void setHeader(){
    	//TextProperties headerProps = new TextProperties();
    	//headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.BOLD, 24));
    	//headerProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
    	lang.newText(new Coordinates(20,30), "Die Netzplantechnik", "header", null, headerStyle);

    	
    }
	
	private void setInformationText(){
    	//SourceCodeProperties infoProps = new SourceCodeProperties();
    	//infoProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 20));
    	SourceCode infoText = lang.newSourceCode(new Coordinates(20,100), "InfoText", null, informationTextStyle);
    	
    	infoText.addCodeLine("Bei der Netzplantechnik handelt es sich um eine Methode, welche im Rahmen der Terminplanung bzw. des", "Line0", 0, null);
    	infoText.addCodeLine("des Projektmanagements zum Einsatz kommt. Das Ziel besteht darin, die Dauer eines Projektes auf Basis ", "Line1", 0, null);
    	infoText.addCodeLine("der einzelnen Arbeitsvorgänge und ihrer Beziehungen untereinander zu bestimmen. Die Beziehungen der  ", "Line13", 0, null);
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
	
    private SourceCode setSourceCodeForward(){
    	//SourceCodeProperties sProb = new  SourceCodeProperties();
        //sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        //sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
        //sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        
        SourceCode src = lang.newSourceCode(new Coordinates(700, 50), "SourceCode", null, sourceCodeStyle);
        src.addCodeLine("01. For all nodes without outgoing edges do", "Code0", 0, null);
        src.addCodeLine("02.     calculateFirstDirection(node)", "Code1", 0, null);
        src.addCodeLine("03. For all nodes withoud ingoing edges do", "Code2", 0, null);
        src.addCodeLine("04.     calculateSecendDirection(node)", "Code3", 0, null);
        src.addCodeLine("", "Code4", 0, null);
        src.addCodeLine("05. calculateFirstDirection(node)", "Code5", 0, null);
        src.addCodeLine("06.     if node has no ingoing edges do", "Code6", 0, null);
        src.addCodeLine("07.         EarliestStartTime of node = 0", "Code7", 0, null);
        src.addCodeLine("08.         EarliestEndTime of node = EarliestStartTime of Node + ProcessTime of node", "Code8", 0, null);
        src.addCodeLine("09.     if node has ingoing edges do:", "Code9", 0, null);
        src.addCodeLine("10.         for each predecessor of node do", "Code10", 0, null);
        src.addCodeLine("11.             if EarliestStartTime of Predecessor has not been set do", "Code11", 0, null);
        src.addCodeLine("12.                 calculateFirstDirection(currentPredecessor)", "Code12", 0, null);
        src.addCodeLine("13.     for each predecessor of node do:", "Code13", 0, null);
        src.addCodeLine("14.         if EarliestStartTime of node has not been set or EarliestEndTime of predecssor > EarliestStartTime of node", "Code14", 0, null);
        src.addCodeLine("15.             EarliestStartTime of node = EarliestEndTime of Predecessor", "Code15", 0, null);
        src.addCodeLine("16.             EarliestEndTime  of node = EarliestStartTime of node + ProcessTime of node", "Code16", 0, null);
        return src;
    }
    
    private SourceCode setSourceCodeBackward(){
    	//SourceCodeProperties sProb = new  SourceCodeProperties();
        //sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        //sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
        //sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        
        SourceCode src = lang.newSourceCode(new Coordinates(700, 50), "SourceCode", null, sourceCodeStyle);
        src.addCodeLine("01. For all nodes without outgoing edges do", "Code0", 0, null);
        src.addCodeLine("02.     calculateFirstDirection(node)", "Code1", 0, null);
        src.addCodeLine("03. For all nodes withoud ingoing edges do", "Code2", 0, null);
        src.addCodeLine("04.     calculateSecendDirection(node)", "Code3", 0, null);
        src.addCodeLine("", "Code4", 0, null);
        src.addCodeLine("05. calculateSecondDirection(node)", "Code4", 0, null);
        src.addCodeLine("06.     if node has no outgoing edges do", "Code5", 0, null);
        src.addCodeLine("07.          LatestStartTime of node = EarliestStartTime of Node", "Code6", 0, null);
        src.addCodeLine("08.          LatestEndTime of node = EearliestEndTime of Node", "Code7", 0, null);
        src.addCodeLine("09.     if node has outgoing edges do:", "Code08", 0, null);
        src.addCodeLine("10.         for each successor of node do", "Code09", 0, null);
        src.addCodeLine("11.             if LatestStartTime of Successor has not been set do", "Code10", 0, null);
        src.addCodeLine("12.                 calculateSecondDirection(currentSuccessor)", "Code11", 0, null);
        src.addCodeLine("13.     for each successor of node do:", "Code12", 0, null);
        src.addCodeLine("14.         if LatestStartTime of node has not been set or LatestStartTime of successor < LatestEndTime of node", "Code13", 0, null);
        src.addCodeLine("15.             LatestStartTime of node = LatestStartTime of Successor - ProcessTime of node", "Code14", 0, null);
        src.addCodeLine("16.             LatestEndTime of node = LatestStartTime of node + ProcessTime of node", "Code15", 0, null);
        return src;
    }
    
    private SourceCode setChangeAlgorithmInformation(){
    	//SourceCodeProperties sProb = new  SourceCodeProperties();
        //sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font (Font.SANS_SERIF,Font.BOLD, 24));
        //sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
        //sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
    	SourceCode infoText = lang.newSourceCode(new Coordinates(700,50), "InfoText", null, informationTextStyle);
        infoText.addCodeLine("Achtung es beginnt nun der Zweite Teil", "line1", 0, null);
        infoText.addCodeLine("des Verfahrens! Der Algorithmus fährt mit", "line2", 0, null);
        infoText.addCodeLine("der Vorwärtsrechnung fort.", "line3", 0, null);
        return infoText;
    
    }
    
    private SourceCode setCriticicalPathInformation(){
    	
    	SourceCodeProperties sProb = new  SourceCodeProperties();
        sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font (Font.SANS_SERIF,Font.BOLD, 24));
        sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
        sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        SourceCode infoText = lang.newSourceCode(new Coordinates(700,50), "InfoText", null, sProb);
        infoText.addCodeLine("Der kritische Pfad wird nun durch die", "line1", 0, null);
        infoText.addCodeLine("hervorgehobenen Kanten repräsentiert", "line2", 0, null);
        infoText.addCodeLine("auf ihm befinden sich alle Vorgänge,.", "line3", 0, null);
        infoText.addCodeLine("deren Verzögerung eine Verzögerung des", "line4", 0, null);
        infoText.addCodeLine("gesamten Vorhabens verursacht!", "line5", 0, null);
        return infoText;
    
    }
    
    
    private void setupQuestions()
    {

        lang.addQuestionGroup(new QuestionGroupModel(qg01, 1));
        lang.addQuestionGroup(new QuestionGroupModel(qg02, 1));
        lang.addQuestionGroup(new QuestionGroupModel(qg03, 1));
        
        
    }
    
    
    private void startQuestion(FillInBlanksQuestionModel questionModel)
    {
    	lang.addFIBQuestion(questionModel);
    }
    
    private void startQuestion(MultipleSelectionQuestionModel questionModel)
    {
    	lang.addMSQuestion(questionModel);
    }
    private void startQuestion(MultipleChoiceQuestionModel questionModel)
    {
    	lang.addMCQuestion(questionModel);
    }
    
    private void startQuestion(TrueFalseQuestionModel questionModel)
    {
    	lang.addTFQuestion(questionModel);
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