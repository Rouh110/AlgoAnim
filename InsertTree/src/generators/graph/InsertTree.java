/*
 * ttdf.java
 * Team31, 2015 for the Animal project at TU Darmstadt.
 * Copying this file for educational purposes is permitted without further authorization.
 */
package generators.graph;

import generators.framework.Generator;
import generators.framework.GeneratorType;

import java.util.Locale;

import algoanim.primitives.*;
import algoanim.primitives.generators.*;

import java.util.Hashtable;

import generators.framework.properties.AnimationPropertiesContainer;
import algoanim.animalscript.AnimalGraphGenerator;
import algoanim.animalscript.AnimalScript;

public class InsertTree implements Generator {
    private Language lang;
    private String graph;

    public InsertTree(){
    	lang = new AnimalScript("InsertTree", "Team31", 800, 600);
    	lang.setStepMode(true);
    }
    
    public InsertTree(Language lang){
    	this.lang = lang;
    	lang.setStepMode(true);
    }
    
    public void init(){
        lang = new AnimalScript("InsertTree", "Team31", 800, 600);
        
        AnimalGraphGenerator gGen = new AnimalGraphGenerator((AnimalScript) lang);
        System.out.println(lang);
        this.generate(null, null);
        
    }

    public String generate(AnimationPropertiesContainer props,Hashtable<String, Object> primitives) {
    	Graph graph = (Graph) primitives.get("graph");
        return lang.toString();
    }

    public String getName() {
        return "InsertTree";
    }

    public String getAlgorithmName() {
        return "InsertTree";
    }

    public String getAnimationAuthor() {
        return "Team31";
    }

    public String getDescription(){
        return "Ein Suchbaum ordnet Elemente in einer Baumstruktur an. Das Ziels, das damit"
 +"\n"
 +"verfolgt wird besteht darin diese Elemente so systematisch anzuordnen."
 +"\n"
 +"dass sie schnell gefunden werden koennen (O (log n)). Ist der betrachtete."
 +"\n"
 +"Knoten bspw. eine Zahl, so sind alle Kindknoten links von diesem kleiner als."
 +"\n"
 +"dieser Knoten und alle Kinder rechts groesser oder gleich groß, wie dieser Knoten.";
    }

    public String getCodeExample(){
        return "1. Beginne bei der Wurzel"
 +"\n"
 +"2. Vergleiche aktuelles Element mit gesuchtem Element "
 +"\n"
 +"3. wenne aktuelle Element gleich dem gesuchten Element -> Gebe Element als gefunden zurueck"
 +"\n"
 +"4. Wenn aktuelles Elemeng groesser als gesuchtes Element"
 +"\n"
 +"   4.1 wenn linker Nachfolger existiert, mache ihn zum aktuelle Element und gehe zu Schritt 2"
 +"\n"
 +"   4.2 Algorithmus terminiert und gesuchtes Element wurde nicht gefunden"
 +"\n"
 +"5. wenn rechter Nachfolger existiert mache ihn zum aktuellen Element und gehe zu Schritt 2"
 +"\n"
 +"6. Algorithmus terminiert und gesuchtes Element wurde nicht gefunden";
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