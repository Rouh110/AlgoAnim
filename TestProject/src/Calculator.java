
public class Calculator {
int t1 = 5;
float t2 = (float) 2.3;

float value = t1 - t2;

public void ausgabe(){
	System.out.println(value);
}

public static void main(String args[]){
	Calculator c = new Calculator();
	c.ausgabe();
}

}
