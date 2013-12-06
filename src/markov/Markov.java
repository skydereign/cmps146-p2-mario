package markov;

import java.io.Console;
import java.util.Hashtable;


// simple markov implementation based off of http://pcg.wikidot.com/pcg-algorithm:markov-chain
public class Markov {
    Hashtable<String, String> frequency;
    String[] keys;
    String lastStr = "1";
    int order;

    public Markov () {
        frequency = new Hashtable<String, String>();
        keys = new String[1000];
        order = 1;
    }
   
    // generates the lookup table
    public void parse (String s) {
        int divisions = s.length() - order;
        for(int i=0; i<divisions; i++) {
            String key = s.substring(i, i+order);
            String str = frequency.get(key);
           
            if(str == null) {
                str = new String();
            str += s.charAt(i+order);
            frequency.put(key, str);
            //System.out.println("putting " + str + " in [" + s.substring(i,i+order) + "]");
                keys[frequency.size()-1] = key;
            } else {
            str += s.charAt(i+order);
            frequency.put(key, str);
            //System.out.println("putting " + str + " in [" + s.substring(i,i+order) + "]");
            }
        }
        //System.out.println(frequency);
    }

//    public void generate () {
//        int start = (int) (Math.random()*frequency.size());
//        int length = 30;
//        String str = frequency.get(keys[start]);
//        str+= frequency.get(keys[(int) (Math.random()*frequency.size())]);
//        for(int i=0; i<10; i++) {
//                String substr = str.substring(str.length()-2, str.length());
//                String choices = frequency.get(substr);
//                if(choices != null) {
//                        int random = (int) (choices.length()*Math.random());
//                        str+=choices.charAt(random);
//                }
//        }
//        System.out.println("result is " + str);
//    }
    
    public String generateNext() {
        //starts at some predetermined key instead of a random one and goes one at a time
    	String str = "";
        String choices = frequency.get(lastStr);
        if(choices != null) {
        	int random = (int) (choices.length()*Math.random());
        	str += choices.charAt(random);
        }
        lastStr = str;
        return str;
    }
    
    public void setInitial(String s) {
    	lastStr = s;
    }

//    public static void main (String[] args) {
//        Console console = System.console();
//        //String input = console.readLine("Enter input: ");
//        Markov markov = new Markov();
//        markov.parse("123912983908430982371827387259834750191239112123123123123123232313111111112312313");
//        for(int i = 0; i < 12; i++) {
//        	markov.generateNext();
//        }
//    }
}
