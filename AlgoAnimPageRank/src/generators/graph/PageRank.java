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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import algoanim.primitives.Circle;
import algoanim.primitives.Graph;
import algoanim.primitives.SourceCode;
import algoanim.primitives.StringMatrix;
import algoanim.primitives.Text;
import algoanim.primitives.generators.GraphGenerator;
import algoanim.primitives.generators.Language;
import algoanim.properties.*;
import algoanim.util.Coordinates;
import algoanim.util.Node;
import algoanim.util.Offset;
import algoanim.util.Timing;



/*
import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalScript;
import algoanim.properties.PolylineProperties;
import algoanim.properties.SourceCodeProperties;
import algoanim.properties.TextProperties;
import algoanim.properties.RectProperties;
import algoanim.properties.CircleProperties;
import algoanim.properties.MatrixProperties;
*/
import java.util.Hashtable;

import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalCircleGenerator;
import algoanim.animalscript.AnimalScript;
import algoanim.animalscript.AnimalStringMatrixGenerator;


///*
import java.util.Locale;
import java.awt.Color;

import algoanim.properties.SourceCodeProperties;
//*/
public class PageRank implements Generator {
    private Language lang;
    private Graph g;
    /*
    private PolylineProperties edgeProperties;
    private SourceCodeProperties sourceCodeProperties;
    private TextProperties headerTextProperties;
    private RectProperties headerRectangleProperties;
    private CircleProperties nodeColorProperties;
    private MatrixProperties resultAnimationProperties;
    */
    Circle graphCircles[];
    Text graphText[];
    
    private int adjacencymatrix[][];
    private int numberOfOutgoingEdges[];
    private ArrayList<float[]> results;
    private float initValue;
    private float difference=100.0f;
    private int iterations = 1;
    
    
    private Color color_for_lowest_PRValue;
    private SourceCodeProperties sourceCode;
    private Color nodehighlightcolor;
    private Color color_of_headertext;
    private Color color_for_highest_PRValue;
    private Color color_of_edges;
    private Color color_of_nodetext;
    //private Color boardercolor_of_nodes;
    private Color matrixfillcolor;
    private Color matrixhighlightcolor;
    private Color edgehighlightcolor;
    private Color color_of_dangling_nodes;
    
    public PageRank(){
    	//System.out.println("Hello World");
    }

    public void init(){
        lang = new AnimalScript("PageRank", "Jan Ulrich Schmitt, Dennis Juckwer", 800, 600);
        lang.setStepMode(true);
        
    }
    
    private void initalValues(int [][] adjacencymatrix){
    	    	
        results = new ArrayList<float[]>();
        iterations = 1;
    	difference = 100;
    	this.adjacencymatrix = new int [adjacencymatrix.length][adjacencymatrix.length];
    	numberOfOutgoingEdges = new int[adjacencymatrix.length];
    	initValue = 1.0f / (float)g.getSize();
    	results.add(new float[adjacencymatrix.length]);
    	for(int to = 0; to < adjacencymatrix.length; to++){
    		float[] temparray = (float[])results.get(0);
    		temparray[to] = initValue;
    		results.set(0, temparray);
    		for(int from = 0; from < adjacencymatrix.length; from++){
    			if(adjacencymatrix[from][to] != 0){
    				this.adjacencymatrix[from][to] = 1;
    				numberOfOutgoingEdges[from] +=1;
    			}
    		}
    	}
    }
    
    private boolean isDanglingNode(int i){
    	if(numberOfOutgoingEdges[i] == 0){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
    	
        /*
    	edgeProperties = (PolylineProperties)props.getPropertiesByName("edgeProperties");
        sourceCodeProperties = (SourceCodeProperties)props.getPropertiesByName("sourceCodeProperties");
        headerTextProperties = (TextProperties)props.getPropertiesByName("headerTextProperties");
        headerRectangleProperties = (RectProperties)props.getPropertiesByName("headerRectangleProperties");
        nodeColorProperties = (CircleProperties)props.getPropertiesByName("nodeColorProperties");
        resultAnimationProperties = (MatrixProperties)props.getPropertiesByName("resultAnimationProperties");
    	*/
    	
    	//TODO: MatrixHighlightColor, MatrixFillColor
        color_for_lowest_PRValue = (Color)primitives.get("color_for_lowest_PRValue");
        sourceCode = (SourceCodeProperties)props.getPropertiesByName("sourceCode");
        nodehighlightcolor = (Color)primitives.get("nodehighlightcolor");
        color_of_headertext = (Color)primitives.get("color_of_headertext");
        color_for_highest_PRValue = (Color)primitives.get("color_for_highest_PRValue");
        color_of_edges = (Color)primitives.get("color_of_edges");
        color_of_nodetext = (Color)primitives.get("color_of_nodetext");
        //boardercolor_of_nodes = (Color)primitives.get("boardercolor_of_nodes");
        matrixfillcolor = (Color)primitives.get("matrixfillcolor");
        matrixhighlightcolor = (Color)primitives.get("matrixhighlightcolor");
        edgehighlightcolor = (Color)primitives.get("edgehighlightcolor");
        color_of_dangling_nodes = (Color)primitives.get("color_of_dangling_nodes");
    	setHeader();
    	setInformationText();
    	g = (Graph)primitives.get("graph");
    	initalValues(g.getAdjacencyMatrix());
    	PageRankGraph p = setupGraph(nodehighlightcolor, color_of_edges,color_of_nodetext);
    	p.setAllDangingEdgeBaseColor(color_of_dangling_nodes);
        //PageRankCalculator prc = new PageRankCalculator(g.getAdjacencyMatrix());
        SourceCode src = setSourceCode(sourceCode);
        StringMatrix smat = setupMatrix(700,250, initValue);
        StringMatrix actMat = setupMatrix(700, 400, 0.0f);
        Text lastText = setCounter(700, 210, "Die PageRank-Werte nach der Initialisierung:");
        Text currentText = setCounter(700, 360, "Die PageRank-Werte von Iteration 1:");
        //Text actualValueText = setCounter(700, 490, "");
        //actualValueText.changeColor(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE, null, null);
        Text formulaV = setCounter(50, 400, "");
        Text formulaC = setCounter(50, 450, "");
        lang.nextStep();
        src.unhighlight(0);
        
        //float minDelta = 0.001f;
        //int i = 0;
        //System.out.println("iteration "+i+":\n"+prc.toString()+"\n");
		while(difference > 0.01){ // Counter fuer Iterationen
			formulaV.setText("Manhattan-Distanz zwischen letzter und vorletzter Iteration: " + new DecimalFormat("#.#####").format(difference) , null, null);
			formulaC.setText("", null, null);
			iterations += 1;
			src.highlight(1);
			lang.nextStep();
			src.unhighlight(1);
			float[] currentResults = new float[adjacencymatrix.length];
			src.highlight(2);
			lang.nextStep();
			src.unhighlight(2);
			formulaV.setText("", null, null);

			//results.add(new float[adjacencymatrix.length]);
			for(int to = 0; to < adjacencymatrix.length; to++){
				String fV = "PR(" + g.getNodeLabel(to) +") = (1-d)/|G| "; 
				formulaV.setText(fV, null, null);
				currentResults[to] = 0.15f / adjacencymatrix.length;
				String fC = "PR(" + g.getNodeLabel(to) + ") = 0.15/" + g.getSize() + " = " + new DecimalFormat("#.#####").format(currentResults[to]);
				formulaC.setText(fC, null, null);
				p.highlightNode(to);
				actMat.highlightCell(0, to, null, null);
				src.highlight(3);
				
				//actualValueText.setText("(1-d)/|G| : " + new DecimalFormat("#.#####").format(currentResults[to]), null, null);
				actMat.put(1, to, new DecimalFormat("#.#####").format(currentResults[to]), null, null);
				//p.setNodeFillColor(to, colorLin(color_for_lowest_PRValue, color_for_highest_PRValue, (float)0.15/g.getSize(), (float)1, currentResults[to]));
				p.setNodeSize(to, this.calcNodeSize(currentResults[to], p.getmaxRadius(), p.getminRadius(), g));
				lang.nextStep();
				//actualValueText.setText("", null, null);
				src.unhighlight(3);
				src.highlight(4);
				lang.nextStep();
				src.unhighlight(4);
				float[] predecValues = (float[]) results.get(results.size()-1);
				for(int from = 0; from < adjacencymatrix.length; from++){
					if(adjacencymatrix[from][to] == 1){
						fV = "PR(" + g.getNodeLabel(to) + ") = PR(" +  g.getNodeLabel(to) + ") + d * PR(" + g.getNodeLabel(from) + ")/"  + "outgoing edges from " + g.getNodeLabel(from) + " ";
						formulaV.setText(fV, null, null);
						float tempResult = currentResults[to];
						currentResults[to] = (float) (currentResults[to] + 0.85f* (predecValues[from]/numberOfOutgoingEdges[from]));
						fC = "PR(" + g.getNodeLabel(to) + ") = " + new DecimalFormat("#.#####").format(tempResult)  + " + 0.85 * " + new DecimalFormat("#.#####").format(results.get(results.size()-1)[from]) + "/" + numberOfOutgoingEdges[from] + " = " + new DecimalFormat("#.#####").format(currentResults[to]) ;
						formulaC.setText(fC, null, null);
						src.highlight(5);
						p.highlightEdge(from, to, null, null);
						
						p.setNodeSize(to, this.calcNodeSize(currentResults[to], p.getmaxRadius(), p.getminRadius(), g));
						float [] lastValues = (float []) results.get(iterations - 2);
						//float tempVal = lastValues[from];
//						smat.highlightCell(1, from, null, null);
						smat.highlightElem(1, from, null, null);
						//actualValueText.setText("PR of p in the last step: " + new DecimalFormat("#.#####").format(lastValues[from]), null, null);
						actMat.put(1, to, new DecimalFormat("#.#####").format(currentResults[to]), null, null);
						//p.setNodeFillColor(to, colorLin(color_for_lowest_PRValue, color_for_highest_PRValue, (float)0.15/g.getSize(), (float)1, currentResults[to]));
						lang.nextStep();
						//actualValueText.setText("", null, null);
//						smat.unhighlightCell(1, from, null, null);
						smat.unhighlightElem(1, from, null, null);
						src.unhighlight(5);
						p.unhighlightEdge(from, to, null, null);
						//lang.nextStep();
						
					}
				}
				src.highlight(6);
				lang.nextStep();
				src.unhighlight(6);
	
				for(Integer dangNode : p.getAllDanglingNodeNrs())
				{
					src.highlight(7);
					smat.highlightElem(0, dangNode, null, null);
					fV = "PR( " + g.getNodeLabel(to)+ ") = PR(" + g.getNodeLabel(to) + ") + d * 1/|G| ";
					formulaV.setText(fV, null, null);
					float tempResult = currentResults[to];
					currentResults[to] = (float) (currentResults[to] + 0.85f * (predecValues[dangNode]/adjacencymatrix.length));
					fC = "PR(" + g.getNodeLabel(to) + ") = " + new DecimalFormat("#.#####").format(tempResult) + " +  0.85 * 1/" + g.getSize() + " = " + new DecimalFormat("#.#####").format(currentResults[to]);
					formulaC.setText(fC, null, null);
					p.setNodeHighlightColor(dangNode, color_of_dangling_nodes);
					p.highlightNode(dangNode);
					p.showEdge(dangNode, to);
					p.hideEdge(to, dangNode);
					p.setNodeSize(to, this.calcNodeSize(currentResults[to], p.getmaxRadius(), p.getminRadius(), g));
					actMat.put(1, to, new DecimalFormat("#.#####").format(currentResults[to]), null, null);
					p.setNodeFillColor(to, colorLin(color_for_lowest_PRValue, color_for_highest_PRValue, (float)0.15/g.getSize(), (float)1, currentResults[to]));
					lang.nextStep();
					src.unhighlight(7);
					p.unhighlightNode(dangNode);
					p.setNodeHighlightColor(dangNode, nodehighlightcolor);
					p.showEdge(to, dangNode);
					p.hideEdge(dangNode, to);
					smat.unhighlightElem(0, dangNode, null, null);
				}
				
				p.unhighlightNode(to);
				actMat.unhighlightCell(0, to, null, null);
				//smat.unhighlightCell(0, to, null, null);
			}
			
			for(int i = 0; i< adjacencymatrix.length; i++){
				smat.put(1, i, new DecimalFormat("#.#####").format(currentResults[i]), null, null);
				actMat.put(1,i,"0.0",null,null);
			}
			results.add(currentResults);
			difference = getDifference((float[])results.get(results.size()-2), (float[])results.get(results.size()-1));
			//iterations += 1;
			lastText.setText("Die Werte von Iteration " + (iterations -1) + ":" , null, null);
			currentText.setText("Die Werte von Iteration " + iterations + ":", null, null);
		}
        
        
        System.out.println(lang.toString());
		return lang.toString();
    }
        
    
    private int calcNodeSize(float prValue, int max, int min, Graph g){
    	float minPRValue = (float)0.15/(float)(g.getAdjacencyMatrix().length);
    	float newSize = ((prValue-(float)minPRValue)/(float)(1.0f - minPRValue)) * ((float)(max - min)) + (float)min;
    	return (int) newSize;
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
    
   /*
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
    	p.unhighlightNode(1);*/
    	
    	/*
    	p.highlightEdge(3,1, null, null);
    	p.setEdgeHighlightColor(3, 1, Color.BLUE);
    	p.setNodeSize(1, 20);
    	lang.nextStep();
    	p.unhighlightEdge(3, 1, null, null);
    	p.setEdgeBaseColor(3, 1, Color.GRAY);
    	
    	
    }*/
    
    private void setHeader(){
    	TextProperties headerProps = new TextProperties();
    	headerProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,Font.BOLD, 24));
    	//headerProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, color_of_headertext);
    	headerProps.set(AnimationPropertiesKeys.COLOR_PROPERTY, edgehighlightcolor);
    	lang.newText(new Coordinates(20,30), "Der PageRank-Algorithmus", "header", null, headerProps);
    }
    
    private void setInformationText(){
    	/*TextProperties infoProps = new TextProperties();
    	infoProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 20));
    	Text infoText = lang.newText(new Coordinates(20,100),getDescription() , "infoText", null, infoProps);
    	lang.nextStep();
    	infoText.hide();
    	*/
    	SourceCodeProperties infoProps = new SourceCodeProperties();
    	infoProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.BOLD, 20));
    	SourceCode infoText = lang.newSourceCode(new Coordinates(20,100), "InfoText", null, infoProps);
    	infoText.addCodeLine("Der PageRank-Algorithmus ist ein Algorithmus zur Bewertung von Knoten in einem Netzwerk.", "Line0", 0, null);
    	infoText.addCodeLine("Larry Page und Sergei Brin entwickelten ihn an der Stanford University zur Bewertung von", "Line1", 0, null);
    	infoText.addCodeLine("Webseiten im Rahmen ihrer mittlerweile weltweit bekannnten Suchmaschine Google. Das Bewertungsprinzip", "Line2", 0, null);
    	infoText.addCodeLine("sieht dabei vor, dass das Gewicht einer Seite umso groesser ist, je mehr andere Seiten auf sie verweisen.", "Line3", 0, null);
    	infoText.addCodeLine("Der Effekt wird dabei von dem Gewicht der auf diese Seite verweisenden Seiten verstaertk.", "Line4", 0, null);
    	infoText.addCodeLine("", "Line5", 0, null);
    	infoText.addCodeLine("Eine moegliche Interpretation des PageRanks liefert das sogenannte Random Surfer Modell. Im Rahmen dieses", "Line6", 0, null);
    	infoText.addCodeLine("Modells repraesentiert der PageRank eines Knotens bzw. einer Webseite (bei einer Normierung der Summe der PageRanks auf 1) die", "Line7", 0, null);
    	infoText.addCodeLine("Wahrscheinlichkeit mit der sich ein sogenannter Zufallssurfer auf einer bestimmten Webseite befindet. Hierbei gilt, dass", "Line8", 0, null);
    	infoText.addCodeLine("der Zufallssurfer mit einer Wahrscheinlichkeit von d den Links auf der Webseite folgt, auf der er sich gerade befindet.", "Line9", 0, null);
    	infoText.addCodeLine("Mit einer Wahrscheinlichkeit von 1-d ruft er manuell in seinem Browser eine der anderen Webseiten auf.", "Line10", 0, null);
    	lang.nextStep();
    	infoText.hide();
    }
    
    private Text setCounter(int x, int y, String Text){

    	TextProperties counterProps = new TextProperties();
    	counterProps.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF, Font.PLAIN, 16));
    	return lang.newText(new Coordinates(x, y), Text, "Counter", null, counterProps);
    	
    }
    
    private SourceCode setSourceCode(SourceCodeProperties sProb){
//    	SourceCodeProperties sProb = new  SourceCodeProperties();
//        sProb.set(AnimationPropertiesKeys.FONT_PROPERTY, new Font(Font.SANS_SERIF,
//                Font.PLAIN, 12));
//        sProb.set(AnimationPropertiesKeys.COLOR_PROPERTY, Color.BLUE);
//        sProb.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, Color.RED);
        
        SourceCode src = lang.newSourceCode(new Coordinates(700, 50), "SourceCode", null, sProb);
        src.addCodeLine("1. PageRank (Graph G, dampingfactor d)", "Code0", 0, null);
        src.addCodeLine("2. while PageRank Values change signifficantly", "Code1", 0, null);
        src.addCodeLine("3.     for all nodes in G do", "Code2", 0, null);
        src.addCodeLine("4.         PageRank of actual node n <- (1-d)/|G|", "Code3", 0, null);
        src.addCodeLine("5.         for each predecessor p of actual node n do", "Code4", 0, null);
        src.addCodeLine("6.             PR of n <- PR of n + d*((PR of p in the last step)/outgoing edges from p)", "Code5", 0, null);
        src.addCodeLine("7.         for each dangling node dn do", "Code6", 0, null);
        src.addCodeLine("8.             PR of n <- PR of n + d * (1/|G|)", "Code7", 0, null);
        src.highlight(0);
        return src;
    }
    
    private PageRankGraph setupGraph(Color nodehighlightcolor,Color color_of_edges,Color color_of_nodetext){
    	//Graph graph = (Graph)primitives.get("graph");
    	GraphProperties gProps = new GraphProperties("graphprop");
        gProps.set(AnimationPropertiesKeys.FILL_PROPERTY, Color.WHITE);
        gProps.set(AnimationPropertiesKeys.HIGHLIGHTCOLOR_PROPERTY, nodehighlightcolor);
        gProps.set(AnimationPropertiesKeys.DIRECTED_PROPERTY, true);
        gProps.set(AnimationPropertiesKeys.DEPTH_PROPERTY,0);
        g = lang.addGraph(g, null, gProps);
        g.hide();
        PageRankGraph p = new PageRankGraph(g,lang);
        p.setAllNodeHighlightColor(nodehighlightcolor);
        p.setAllEdgesBaseColor(color_of_edges);
        p.setAllTextColor(color_of_nodetext);
        p.setAllEdgesHighlightColor(edgehighlightcolor);
        return  p;
    }
    
    private StringMatrix setupMatrix(int x, int y, float initValue){
    	
    	AnimalStringMatrixGenerator matrixGenerator = new AnimalStringMatrixGenerator(
				(AnimalScript) lang);
    	
        //String[][] strValues = {{"Hallo", "und",},{"Ciao","!"}};
    	String[][] strValues = new String[2][g.getSize()];
    	//String[][] strValues = new String[2][2];
    	//float initValue =  0.3f;

    	for(int i = 0; i < g.getSize(); i++){
    		strValues[0][i] = g.getNodeLabel(i);
    		strValues[1][i] = "" + initValue;
    	}
    	
        MatrixProperties matProp = new MatrixProperties();
        matProp.set(AnimationPropertiesKeys.GRID_STYLE_PROPERTY, "table");
        //matProp.set(AnimationPropertiesKeys.FILLED_PROPERTY, false);
        matProp.set(AnimationPropertiesKeys.CELL_HEIGHT_PROPERTY, 30);
        matProp.set(AnimationPropertiesKeys.CELL_WIDTH_PROPERTY, 80);
        StringMatrix smat = new StringMatrix(matrixGenerator,
				new Coordinates(x, y), strValues, "Matrix", null,
				matProp);
        
        //StringMatrix smat = lang.newStringMatrix(new Coordinates(x, y), strValues, "Matrix", null, matProp);
    	return smat;
    }
    
    private float getDifference(float[] lastValues, float[] actualValues){
    	float difference = 0.0f;
    	for(int i = 0; i < lastValues.length; i++){
    		difference += Math.abs((Math.abs(lastValues[i]) - Math.abs(actualValues[i])));
    	}
    	return difference;
    }
    
    
    /*
    private void visualizeIteration(PageRankCalculator prc, PageRankGraph p, SourceCode  src, StringMatrix smat){
    	src.highlight(1);
    	lang.nextStep();
    	src.unhighlight(1);
    	//lang.nextStep();
    	src.highlight(2);
    	lang.nextStep();
    	src.unhighlight(2);

    	int[][] adjacencyMatrix = g.getAdjacencyMatrix();
    	for(int to = 0; to < prc.getCurrentValues().length; to++){
    		boolean highlightedEdges = false;
        	//src.highlight(2);
        	//lang.nextStep();
        	//src.unhighlight(2);
        	//lang.nextStep();
    		p.highlightNode(to);
    		//lang.nextStep();
    		src.highlight(3);
    		lang.nextStep();
    		src.unhighlight(3);
    		src.highlight(4);
    		lang.nextStep();
    		src.unhighlight(4);
    		for(int from = 0; from < prc.getCurrentValues().length; from++){
    			if(adjacencyMatrix[from][to] != 0){
    				src.highlight(5);
    				p.highlightEdge(from, to, null, null);
    				lang.nextStep();
    				//src.unhighlight(5);
    				//lang.nextStep();
    				highlightedEdges = true;
    			}
    		}
    		src.unhighlight(5);
    		//p.unhighlightNode(to);
    		if(highlightedEdges){
    			for(int from = 0; from < prc.getCurrentValues().length; from++){
    				if(adjacencyMatrix[from][to]!=0){
    					p.unhighlightEdge(from, to, null, null);
    				}
    			}
    			//p.unhighlightNode(to);
    			//lang.nextStep();
    		}
    		src.highlight(6);
    		lang.nextStep();
    		src.unhighlight(6);
    		for(int i = 0; i < g.getSize(); i++){
    			if(prc.outcomingEdges[i]==0){
    				src.highlight(7);
    				if(p.nodes[i].isHighlighted){
    					p.setNodeHighlightColor(i, color_of_dangling_nodes);
    					lang.nextStep();
    					p.setNodeHighlightColor(i, nodehighlightcolor);
    					src.unhighlight(7);
    				}else{
    					Color tempColor = p.nodes[i].fillColor;
    					p.setNodeFillColor(i, color_of_dangling_nodes);
    					lang.nextStep();
    					p.setNodeFillColor(i, tempColor);
    					src.unhighlight(7);
    				}
    			}
    		}
    		
    		//src.highlight(7);
    		//lang.nextStep();
    		//src.unhighlight(7);
    		p.unhighlightNode(to);
    		//lang.nextStep();
    		
    	}
    	
    	for(int nodes = 0; nodes < prc.getCurrentValues().length; nodes++){
    		p.setNodeSize(nodes, this.calcNodeSize(prc.getCurrentValues()[nodes], p.getmaxRadius(), p.getminRadius(), g));
    		p.setNodeFillColor(nodes, colorLin(color_for_lowest_PRValue, color_for_highest_PRValue, (float)0.15/g.getSize(), (float)1, prc.getCurrentValues()[nodes]));
    	}
    	//lang.nextStep();
 
  
    }
    */
    
    

}