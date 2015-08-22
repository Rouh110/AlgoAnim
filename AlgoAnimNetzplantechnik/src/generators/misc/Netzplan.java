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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import algoanim.primitives.Graph;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.generators.Language;
import algoanim.properties.AnimationPropertiesKeys;
import algoanim.properties.CounterProperties;
import algoanim.properties.MatrixProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.util.Coordinates;

import java.util.Hashtable;

import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;
import algoanim.animalscript.AnimalStringMatrixGenerator;
import algoanim.counter.model.TwoValueCounter;
import algoanim.counter.view.TwoValueView;

//%Die Gewichte der Kanten representieren die Prozesszeit von ausgehenden Knoten.
//%Achte deshalb darauf, dass alle Ausgehendne Kanten von einem Knoten die gleiche Gewichtung hat.
//%Um die Prozesszeit von den Endkoten festzulegen wird weiterer Dummy-Knoten benötigt.
//%Dieser Knoten wird im Graphen nicht angezeigt und nicht beachtet.
public class Netzplan implements Generator {
    private Language lang;
    private Graph graph;
    NetzplanGraph n;
    SourceCode src1;
    SourceCode src2;
    private SourceCodeProperties sourceCodeStyle;
    StringMatrix smat;
    LinkedList<Integer> critcalPathNodes;
    
    
    String qg01 = "firstDirectionQuestions";
    String qg02 = "secondDirectionQuestions";
    String qg03 = "criticalPathQuestion";
    String qg04 = "delayQuestion";

    public void init(){
        lang = new AnimalScript("Netzplantechnik", "Jan Ulrich Schmitt & Dennis Juckwer", 800, 600);        
        lang.setStepMode(true);
        lang.setInteractionType(Language.INTERACTION_TYPE_AVINTERACTION);
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {

        graph = (Graph)primitives.get("graph");
        sourceCodeStyle = (SourceCodeProperties) props.getPropertiesByName("SourcecodeStyle");
        Color edgeColor = (Color)primitives.get("EdgeColor");
        Color edgeHighlightColor = (Color)primitives.get("EdgeHighlightColor");
        Color headerColor = (Color)primitives.get("headerColor");
        MatrixProperties matrixProperties = (MatrixProperties) props.getPropertiesByName("NodeStyle");
        
        setupQuestions();
        
        setHeader(headerColor);
        setInformationText();
        src1 = setSourceCodeForward();    
        n = new NetzplanGraph((AnimalScript)lang, graph,matrixProperties);
        n.setAllEdgeBaseColor(edgeColor);
        n.setAllEdgeHightlightColor(edgeHighlightColor);
        src1.highlight(0);
        
        // Zähler Anfang
        AnimalStringMatrixGenerator matrixGenerator = new AnimalStringMatrixGenerator((AnimalScript) lang);
		MatrixProperties matProp = new MatrixProperties();
		String[][] strValues = new String[2][3];
		for(int i = 0; i < 3; i++){
    		strValues[0][i] = "-1";
    		strValues[1][i] = "-1";
    	}
		
		smat = new StringMatrix(matrixGenerator,
				new Coordinates(700, 490), strValues, "Values", null,
				matProp);
		smat.hide();
        TwoValueCounter counter = lang.newCounter(smat);
        CounterProperties cp = new CounterProperties();
        cp.set(AnimationPropertiesKeys.FILLED_PROPERTY, true);
        cp.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.BLUE);
        TwoValueView view = lang.newCounterView(counter,
        		new Coordinates(700, 490), cp, true, true);
        //Zähler Ende
        
        lang.nextStep("Beginn der Rückwärtsrechnung");
       
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
        lang.nextStep("Wechsel Rückwärts- zu Vorwärtsrechnung");
        
        
        algorithmChange.hide();
        src2 = this.setSourceCodeBackward();
        src2.highlight(2);
        lang.nextStep("Beginn der Vorwärtsrechnung");
        
        src2.unhighlight(2);
        for(Integer currentNode: nodesToProcess){
        	src2.highlight(3);
        	this.calculateSecondDirection(currentNode);
        }
        
        
        
        
        this.startCriticalPathQuestion(n);
       
          
        lang.nextStep();
        src2.hide();
        SourceCode criticalPathText = setCriticicalPathInformation();
        for(Integer currentNode : n.getStartNodes()){
        	this.drawCriticalPath(currentNode);
        }
        lang.nextStep("Darstellung kritischer Pfad");
        criticalPathText.hide();
        n.hideGraph();
        SourceCode endInformation = this.showEndText(counter);
        
        
        startDelayQuestion(n);

        
        
        lang.finalizeGeneration();

        return lang.toString();
    }
    
    

	private void calculateFirstDirection(Integer node){

    	src1.highlight(5);
    	n.highlightNode(node);
    	lang.nextStep("Aufruf Rückwärtsrechnung Knoten " + graph.getNodeLabel(node));
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
			smat.put(0, 0, "-1", null, null);
			lang.nextStep();
			src1.unhighlight(7);
			src1.unhighlight(6);
			src1.highlight(8);
    		n.setEarliestEndTime(node, n.getProcessTime(node));
    		smat.put(0, 0, "-1", null, null);
    		smat.getElement(0, 0);
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
        			if(n.hasValidEntry(node, NetzplanGraph.CellID.EarliestEndTime)==true){
        				smat.getElement(0, 0);
        			}
        			n.highlightEdge(currentPredecessor, node);
        			src1.highlight(14);
        			lang.nextStep();
        			n.setEarliestStartTime(node, n.getEarliestEndTime(currentPredecessor));
        			smat.getElement(0, 0);
        			smat.put(0, 0, "-1", null, null);
        			src1.highlight(15);
        			src1.unhighlight(14);
        			lang.nextStep();
        			src1.unhighlight(15);
        			src1.highlight(16);
        			n.setEarliestEndTime(node, n.getEarliestStartTime(node) + n.getProcessTime(node));
        			smat.getElement(0, 0);
        			smat.put(0, 0, "-1", null, null);
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
    	lang.nextStep("Aufruf Vorwärtsrechnung Knoten " + graph.getNodeLabel(node));
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
    		smat.put(0, 0, "-1", null, null);
    		smat.getElement(0, 0);
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
        			if(n.hasValidEntry(node, NetzplanGraph.CellID.LatestEndTime)==true){
        				smat.getElement(0, 0);
        			}
        			n.highlightEdge(node, currentSuccessor);
        			src2.highlight(14);
        			lang.nextStep();
        			n.setLatestStartTime(node, n.getLatestStartTime(currentSuccessor)- n.getProcessTime(node));
        			smat.put(0, 0, "-1", null, null);
        			smat.getElement(0, 0);
        			src2.highlight(15);
        			src2.unhighlight(14);
        			lang.nextStep();
          			src2.unhighlight(15);
        			src2.highlight(16);
        			n.setLatestEndTime(node, n.getLatestStartTime(node)+ n.getProcessTime(node));
        			smat.put(0, 0, "-1", null, null);
        			smat.getElement(0, 0);
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
	
	
	
    private void setHeader(Color headerColor){
    	TextProperties headerProps = new TextProperties();
    	headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.BOLD, 24));
    	headerProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, headerColor);
    	lang.newText(new Coordinates(20,30), "Die Netzplantechnik", "header", null, headerProps);

    	
    }
	
	private void setInformationText(){
    	SourceCodeProperties infoProps = new SourceCodeProperties();
    	infoProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 20));
    	SourceCode infoText = lang.newSourceCode(new Coordinates(20,60), "InfoText", null, infoProps);
    	
    	infoText.addCodeLine("Bei der Netzplantechnik handelt es sich um eine Methode, welche im Rahmen der Terminplanung bzw. des", "Line0", 0, null);
    	infoText.addCodeLine("Projektmanagements zum Einsatz kommt. Das Ziel besteht darin die Mindestdauer eines Projektes auf Basis", "Line1", 0, null);
    	infoText.addCodeLine("der einzelnen Arbeitsvorgänge und ihrer Beziehungen untereinander zu bestimmen. Die Beziehungen der  ", "Line2", 0, null);
    	infoText.addCodeLine("einzelnen Vorgänge werden dabei in Form eines gerichteten Graphen dargestellt.", "Line3", 0, null);
    	infoText.addCodeLine("Neben der minimalen Gesamtdauer, welche das zu untersuchende Projekt im Idealfall benötigt, werden", "Line4", 0, null);
    	infoText.addCodeLine("zudem für jeden Arbeitsvorgang sogenannte Pufferzeiten ermittelt. Diese geben an in welchem Ausmaß", "Line5", 0, null);
    	infoText.addCodeLine("Verzögerungen eines Arbeitsvorganges möglich sind, ohne dass sie sich negativ auf die Gesamtdauer des", "Line6", 0, null);
    	infoText.addCodeLine("Projektes auszuwirken.", "Line7", 0, null);
    	infoText.addCodeLine("", "Line8", 0, null);
    	infoText.addCodeLine("Es ist zu beachten, dass die Beziehungen zwischen den Arbeitsvorgaengen eindeutig zu definieren sind.", "Line9", 0, null);
    	infoText.addCodeLine("Zyklen sind daher nicht zulässig!", "Line10", 0, null);


    	lang.nextStep("Einleitung");
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
    	SourceCodeProperties sProb = new  SourceCodeProperties();
        sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font (Font.SANS_SERIF,Font.BOLD, 16));
        sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.RED);
        //sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
    	SourceCode infoText = lang.newSourceCode(new Coordinates(700,50), "InfoText", null, sProb);
        infoText.addCodeLine("Achtung es beginnt nun der Zweite Teil", "line1", 0, null);
        infoText.addCodeLine("des Verfahrens! Der Algorithmus fährt mit", "line2", 0, null);
        infoText.addCodeLine("der Vorwärtsrechnung fort!", "line3", 0, null);
        return infoText;
    
    }
    
    private SourceCode setCriticicalPathInformation(){
    	
    	SourceCodeProperties sProb = new  SourceCodeProperties();
        sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font (Font.SANS_SERIF,Font.BOLD, 16));
        sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
        //sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        SourceCode infoText = lang.newSourceCode(new Coordinates(700,50), "InfoText", null, sProb);
        infoText.addCodeLine("Der kritische Pfad wird nun durch die", "line1", 0, null);
        infoText.addCodeLine("hervorgehobenen Kanten repräsentiert", "line2", 0, null);
        infoText.addCodeLine("auf ihm befinden sich alle Vorgänge,.", "line3", 0, null);
        infoText.addCodeLine("deren Verzögerung eine Verzögerung des", "line4", 0, null);
        infoText.addCodeLine("gesamten Vorhabens verursacht!", "line5", 0, null);
        return infoText;
    
    }
    
	private SourceCode showEndText(TwoValueCounter counter) {
    	//int actualCount = iterations - 1;
		SourceCodeProperties infoProps = new SourceCodeProperties();
    	infoProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 14));
    	SourceCode endText = lang.newSourceCode(new Coordinates(20,100), "InfoText", null, infoProps);
    	endText.addCodeLine("Informationen zu dem zuvor angzeigten Ablauf des Algorithmus:", "Line0", 0, null);
    	endText.addCodeLine("", "Line1", 0, null);
    	endText.addCodeLine("Anzahl Schreibzugriffe: " + counter.getAssigments(), "Line3", 0, null);
    	endText.addCodeLine("Anzahl Lesezugriffe: " + counter.getAccess(), "Line4", 0, null);
    	String criticalPathString = "";
    	for(Integer currentNode: critcalPathNodes){
    		criticalPathString = criticalPathString + " " + n.getName(currentNode);
    	}
    	
    	endText.addCodeLine("Knoten auf kritischem Pfad: " + criticalPathString, "Line 5", 0, null);

    	return endText;
	}
    
	private void startCriticalPathQuestion(NetzplanGraph npg)
	{
		 critcalPathNodes = new LinkedList<Integer>(); // habe es zu gloabelen Vairable gemacht, um auf die Knoten im Pfad zuzuggreifen zu koennen
	         
	     for(Integer currentNode:npg.getStartNodes()){
	      	this.getCriticalPath(currentNode,critcalPathNodes);
	     }
	        
		 MultipleSelectionQuestionModel question = new MultipleSelectionQuestionModel("Kritischer Pfad");
		 question.setPrompt("Welche Knoten gehören alles zu einem kritischen Pfad? (Es kann mehr als einen kritischen Pfad geben.)");
	     question.setGroupID(qg03);
	       
	        
	     for(Integer currentNode : npg.getAllNodes())
	     {
	      	if(critcalPathNodes.contains(currentNode))
        	{
	      		question.addAnswer(npg.getName(currentNode), 5,npg.getName(currentNode) + " gehört zu einem kritischen Pfad.\n");
	        	}else
	        	{
	        		question.addAnswer(npg.getName(currentNode), -5,npg.getName(currentNode) + " gehört nicht zu einem kritischen Pfad.\n");
	        	}
	        	
	     }
	     
	     lang.addMSQuestion(question);
	}
	
	private void startDelayQuestion(NetzplanGraph npg)
	{
		int node;
		int delay;
		
		Random rg = new Random();
		
		List<Integer> nodes = npg.getAllNodes();
		
		if(nodes.size() == 0)
		{
			return;
		}else if(nodes.size() == 1)
		{
			node = nodes.get(0);
		}else
		{
			Iterator<Integer> nodesIt = nodes.iterator();
			
			while(nodesIt.hasNext())
			{
				if(npg.isEndNode(nodesIt.next()))
				{
					nodesIt.remove();
				}
			}
			
			node = nodes.get(rg.nextInt(nodes.size()));
		}
		
		delay = npg.getLatestStartTime(node)- npg.getLatestStartTime(node);
		
		int maxDelay = delay+4;
		int minDelay = 1;
		
		delay = rg.nextInt(maxDelay-minDelay)+minDelay;
		
		
		startDelayQuestion(npg, node, delay);
	}
	
	private void startDelayQuestion(NetzplanGraph npg, int node, int delay)
	{
		String nodeLabel =  npg.getName(node);
		int answer = getDelay(npg, node, delay);
		
		FillInBlanksQuestionModel question = new FillInBlanksQuestionModel("Delay Question");
		question.setPrompt("Angenommen der Startzeitpunkt von Knoten "+ nodeLabel+" verzögert sich um "+delay+" Einheiten, um wie viel verzögert sich maximal die Fertigstellung des Endproduktes?");
		question.setGroupID(qg04);
		
		question.addAnswer(""+answer, 5, answer+" war richtig.");
		
		lang.addFIBQuestion(question);
		
	}
	
	private int getDelay(NetzplanGraph npg, int node, int delay)
	{
		if(npg.isEndNode(node))
		{
			return delay;
		}
		
		int currentDelay = delay - (npg.getLatestStartTime(node)- npg.getEarliestStartTime(node));
		
		if(currentDelay <= 0)
		{
			return 0;
		}
		int successorDelay = 0;
		for(int successor : npg.getSuccessors(node))
		{
			int tmp = getDelay(npg, successor, currentDelay);
			if(tmp > successorDelay)
			{
				successorDelay = tmp;
			}
		}
		
		return successorDelay;
			
	}
	
	private int calculateEST(NetzplanGraph npg, int nodeId)
	{
		if(npg.isStartNode(nodeId))
		{
			return 0;
		}
		int est = 0;
		for(int predecessor: npg.getPredecessors(nodeId))
		{
			int tmp = calculateEST(npg, predecessor)+ npg.getProcessTime(predecessor);		
			if(tmp > est)
				est = tmp;
		}
		
		return est;
	}
	
	
	private int calculateLST(NetzplanGraph npg, int nodeId)
	{
		
		if(npg.isEndNode(nodeId))
		{
			return npg.getEarliestStartTime(nodeId);
		}
		
		int lst = Integer.MAX_VALUE;
		for(int successor: npg.getSuccessors(nodeId))
		{
			int tmp = calculateLST(npg, successor)-npg.getProcessTime(nodeId);
			
			if(tmp < lst)
			{
				lst = tmp;
			}
		}
		
		return lst;
	}
	
    
    private void setupQuestions()
    {

    	QuestionGroupModel model;
      
        model = new QuestionGroupModel(qg01, 1);
        model.setNumberOfRepeats(2);
        lang.addQuestionGroup(model);
        
        model = new QuestionGroupModel(qg02, 1);
        model.setNumberOfRepeats(2);
        lang.addQuestionGroup(model);
        
        model = new QuestionGroupModel(qg03, 1);
        model.setNumberOfRepeats(1);
        lang.addQuestionGroup(model);
        
        model = new QuestionGroupModel(qg04, 1);
        model.setNumberOfRepeats(1);
        lang.addQuestionGroup(model);
        
        
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
        return "Bei der Netzplantechnik handelt es sich um eine Methode, welche im Rahmen der Terminplanung bzw. des "
 +"Projektmanagements zum Einsatz kommt. Das Ziel besteht darin die Mindestdauer eines Projektes auf Basis "
 +"der einzelnen Arbeitsvorgänge und ihrer Beziehungen untereinander zu bestimmen. Die Beziehungen der "
 +"einzelnen Vorgänge werden dabei in Form eines gerichteten Graphen dargestellt. \n"
 +"Neben der minimalen Gesamtdauer, welche das zu untersuchende Projekt im Idealfall benötigt, werden "
 +"zudem für jeden Arbeitsvorgang sogenannte Pufferzeiten ermittelt. Diese geben an in welchem Ausmaß "
 +"Verzögerungen eines Arbeitsvorganges möglicht sind, ohne dass sie sich negativ auf die Gesamtdauer des "
 +"Projektes auswirken."
 +"\n \n"
 +"Es ist zu beachten, dass die Beziehungen zwischen den Arbeitsvorgängen eindeutig zu definieren sind. "
 +"Zyklen sind daher nicht zulässig!";
    }

    public String getCodeExample(){
        return
 "01. For all nodes without outgoing edges do \n"
 +"02.     calcualteFirstDirection(node) \n"
 +"03. For all nodes without ingoing edges do \n"
 +"04.     calculateSecondDirection(node) \n"
 +"\n"
 +"05. calculateFirstDirection(node) \n"
 +"06.     if node has no ingoing edges do: \n"
 +"07.         EarliestStartTime of node = 0 \n"
 +"08.         EarliestEndTime of node = EarliestStartTime of Node + ProcessTime of node \n"
 +"09.     if node has no ingoing edges do: \n"
 +"10.         for each predecessor of node do: \n"
 +"11.             if EarliestStartTime of Predecessor has not been set do: \n"
 +"12.                 calculateFirstDirection(currentPredecessor) \n"
 +"13.     for each predecessor of node do: \n"
 +"14.         if EearliestStartTime of node has not been set or EarliestEndTime of predecessor > EarliestStartTime of node \n"
 +"15.             EarliestStartTime of node = EarliestEndTime of predecessor \n"
 +"16.             EarliestEndTime of node = EarliestStartTime of node + ProcessTime of node \n"
 +"\n"
 +"17. calculateSecondDirection(node) \n"
 +"18.     if node has no outgoing edges do: \n"
 +"19.         LatestStartTime of node = EarliestStartTime of node \n"
 +"20.         LatestEndTime of node = EarliestEndTime of node \n"
 +"21.     if node has outgoing edges do: \n"
 +"22.         for each successor of node do: \n"
 +"23.             if LatestStartTime of successor has not been set do: \n"
 +"24.                 calculateSecondDirection(currentSuccessor) \n"
 +"25.     for each successor of node do: \n"
 +"26.         if LatestStartTime of node has not been set or LatestStartTime of successor < LatestEndTime of node: \n"
 +"27.             LatestStartTime of node = LatestStartTime of successor - ProcessTime of node \n"
 +"28.             LatestEndTime of node = LatestStartTime of node + ProcessTime of node \n";
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