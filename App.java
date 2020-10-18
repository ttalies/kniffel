import java.util.*;
import java.util.Arrays;

class Tuple<K, V> {
    public final K key;
    public final V value;
    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }
}

class Language {
    static String ones = "Einer";
    static String twos = "Zweier";
    static String threes = "Dreier";
    static String fours = "Vierer";
    static String fives = "Fünfer";
    static String sixes = "Sechser";

    static String threeEquals = "Drei Gleiche";
    static String fourEquals = "Vier Gleiche";
    static String fullHouse = "Volles Haus";
    static String smallStreet = "Kleine Straße";
    static String bigStreet = "Große Straße";
    static String kniffel = "Kniffel";
    static String chance = "Chance";

    static String horizontalRuler = "-----------------------";
    static String dashedRuler     = "- - - - - - - - - - - -";
    static String doubleRuler     = "=======================";

    static String partialSumPart1 = "Teilsumme Teil 1: ";
    static String sumPart1 = "Summe Teil 1: ";
    static String sumPart2 = "Summe Teil 2: ";
    static String bonus = "Bonus Teil 1: ";
    static String totalSum = "Gesamtsumme : ";

    static String dice = "Würfel";

    Language(){
        // only language definition
    }
}    

class KniffelSheet {
    String[] part1 = {Language.ones, Language.twos, Language.threes, Language.fours, Language.fives, Language.sixes};
    String[] part2 = {Language.threeEquals, Language.fourEquals, Language.fullHouse, Language.smallStreet, Language.bigStreet, Language.kniffel, Language.chance};    
}

class Dice {
    Integer count;
    boolean roll;
    Random r = new Random();

    public Dice() {
        this.count = 0;
        this.roll = true;
    }

    public void roll() {
        if (this.roll) {            
            this.count = r.nextInt(6) + 1;            
        }
        else {
            // Roll not permitted
        }
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public boolean getRollState() {
        return this.roll;
    }

    public void setRollState(boolean state) {
        this.roll = state;
    }
}

class DiceDeck {
    Dice[] dice = new Dice[5];

    public DiceDeck() {
        for (int d = 0; d<5; d++) {
            dice[d] = new Dice();
        }
    }
}

class Goal {
    Tuple<String, Integer> set;
}

class Sheet {
    ArrayList<Goal> game = new ArrayList<>();
}

class Player {
    String name;

    Sheet sheet;
    Integer sumPart1 = 0;
    Integer partSumPart1 = 0;
    Integer sumPart2 = 0;
    Integer bonusPart1 = 0;
    Integer sum = 0;

    DiceDeck deck = new DiceDeck();
}

class Players {
    ArrayList<Player> player = new ArrayList<>();
}

class Kniffel {
    private static int addPlayer(Players actualPlayers, String name) {
        Integer currentSize = actualPlayers.player.size();
        Player player = new Player();
        player.name = name;
        player.sheet = new Sheet();        
        actualPlayers.player.add(currentSize, player);        
        return actualPlayers.player.size();
    }
        
    private static Integer[] dicesToArray(Players actualPlayers, Integer playerNumber) {
        Integer[] dices = new Integer[actualPlayers.player.get(playerNumber).deck.dice.length];
        for (int k = 0; k<actualPlayers.player.get(playerNumber).deck.dice.length; k++) {
            dices[k] = actualPlayers.player.get(playerNumber).deck.dice[k].getCount();
        }
        return dices;
    }

    private static Boolean countEqualDices(Players actualPlayers, Integer playerNumber, Integer amount) {
        Integer[] dices = dicesToArray(actualPlayers, playerNumber);
        Integer count = 0;
        for (int c = 1; c < 7; c++) {
            Integer tempCount = 0;
            for (int d = 0; d < 5; d++) {
                if (dices[d] == c) tempCount++;
            }
            if (count < amount ) count = tempCount;
        }
        Boolean valid = false;
        if (count > amount - 1) valid = true;
        return valid;
    }

    private static int getValidDiceSum(Players actualPlayers, Integer playerNumber, String goalName) {
        Integer goalValue = 0;

        // alle Würfel aufsummieren
        if (goalName.equals(Language.chance)) {
            for (int d = 0; d < 5; d++) {
                goalValue = goalValue + actualPlayers.player.get(playerNumber).deck.dice[d].getCount();
            }
        }
        
        // Finde 5 gleiche Würfel, wenn ja 50 Punkte
        if (goalName.equals(Language.kniffel)) {
            Integer[] dices = dicesToArray(actualPlayers, playerNumber);            
            Boolean valid = Arrays.asList(dices).stream().allMatch(t -> t.equals(dices[1]));
            if (valid.equals(true)) goalValue = 50;      
            else goalValue = 0;
        }
        
        // Finde 3 gleiche Würfel und addiere alle Augenzahlen    
        if (goalName.equals(Language.threeEquals)) {                                                      
            Boolean valid = countEqualDices(actualPlayers, playerNumber, 3);                        
            if (valid.equals(true)) {
                for (int d = 0; d < 5; d++) {
                    goalValue = goalValue + actualPlayers.player.get(playerNumber).deck.dice[d].getCount();
                }   
            } else goalValue = 0;
        }

        // Finde 4 gleiche Würfel und addiere alle Augenzahlen  
        if (goalName.equals(Language.fourEquals)) {                      
            Boolean valid = countEqualDices(actualPlayers, playerNumber, 4);            
            if (valid.equals(true)) {            
                for (int d = 0; d < 5; d++) {
                    goalValue = goalValue + actualPlayers.player.get(playerNumber).deck.dice[d].getCount();
                }
            } else goalValue = 0;
        }

        // Finde ein Paar und einen Drilling, wenn ja, 25 Punkte
        if (goalName.equals(Language.fullHouse)) {            
            Boolean valid = false;
            Boolean valid1 = false; // Pair of three exists
            Boolean valid2 = false; // Pair of two exists
            
            // wenn es ein Kniffel ist, ist es auch ein Full House
            Integer[] dices = dicesToArray(actualPlayers, playerNumber);
            valid = Arrays.asList(dices).stream().allMatch(t -> t.equals(dices[1]));

            if (Boolean.FALSE.equals(valid)) {
                Integer count = 0;
                Integer pairOfThree = 0;
                
                // prüfe ob es einen Drilling gibt
                for (int c = 1; c < 7; c++) {
                    Integer tempCount = 0;
                    for (int d = 0; d < 5; d++) if (dices[d] == c) tempCount++;
                    if (count < 3 ) {
                        count = tempCount;
                        pairOfThree = c;
                    }                
                }
                if (count > 2) valid1 = true;

                // gibt es noch ein weiteres Paar
                if (Boolean.TRUE.equals(valid1)) {
                    for (int c = 1; c < 7; c++) {
                        if (c != pairOfThree) {
                            Integer tempCount = 0;
                            for (int d = 0; d < 5; d++) if (dices[d] == c) tempCount++;
                            if (count < 3 ) count = tempCount;       
                        }
                    }
                    if (count > 1) valid2 = true;
                }

                if ((valid1) && (valid2)) valid = true;
            }
            if (valid.equals(true)) goalValue = 25;                
            else goalValue = 0;
        }

        // Finde 4 aufeinander folgende Würfel, wenn ja 30 Punkte
        if (goalName.equals(Language.smallStreet)) {
            Integer[] dices = dicesToArray(actualPlayers, playerNumber);
            Arrays.sort(dices);     

            Boolean valid1 = true; // small street within first 4 dices
            Boolean valid2 = true; // small street within last 4 dices
            
            // prüfe die ersten 4 Würfel
            for (int i = 1; i < dices.length -1; i++) {
                int dice1 = dices[i-1];
                int dice2 = dices[i];
                if ( dice2 != dice1 + 1) valid1 = false;
            }

            // prüfe die letzten 4 Würfel
            for (int i = 2; i < dices.length; i++) {
                int dice1 = dices[i-1];
                int dice2 = dices[i];
                if ( dice2 != dice1 + 1) valid2 = false;
            }

            if ((valid1.equals(true)) || (valid2.equals(true))) goalValue = 30;   
            else goalValue = 0;
        }
        
        // Finde 5 aufeinander folgende Würfel, wenn ja 40 Punkte
        if (goalName.equals(Language.bigStreet)) {
            Integer[] dices = dicesToArray(actualPlayers, playerNumber);
            Arrays.sort(dices);            
            Boolean valid = true;
            
            for (int i = 1; i < dices.length; i++) {
                int dice1 = dices[i-1];
                int dice2 = dices[i];
                if ( dice2 != dice1 + 1) valid = false;
            }
            
            if (valid.equals(true)) goalValue = 40;
            else goalValue = 0;
        }

        Integer goalCounter = 0;
        if (goalName.equals(Language.ones)) goalCounter = 1;
        if (goalName.equals(Language.twos)) goalCounter = 2;
        if (goalName.equals(Language.threes)) goalCounter = 3;
        if (goalName.equals(Language.fours)) goalCounter = 4;
        if (goalName.equals(Language.fives)) goalCounter = 5;
        if (goalName.equals(Language.sixes)) goalCounter = 6;
        
        if (goalCounter > 0) {         
            for (int d = 0; d < 5; d++) {
                if (actualPlayers.player.get(playerNumber).deck.dice[d].getCount() == goalCounter) {
                    goalValue = goalValue + actualPlayers.player.get(playerNumber).deck.dice[d].getCount();
                }
            }      
        }
        return goalValue;
    }

    private static boolean addGoal(Players actualPlayers, Integer playerNumber, String goalName) {
        Boolean keyExists = false;
        Integer goalValue = 0;
        
        goalValue = getValidDiceSum(actualPlayers, playerNumber, goalName);        

        for (int i = 0; i < actualPlayers.player.get(playerNumber).sheet.game.size(); i++) {
            if (actualPlayers.player.get(playerNumber).sheet.game.get(0).set.key.equals(goalName)) keyExists = true;
        }        
        
        if (Boolean.TRUE.equals(keyExists)) return false; // Wurf existiert
        else {
            Goal newGoal = new Goal();
            newGoal.set = new Tuple<>(goalName, goalValue);        
            Sheet oldSheet = actualPlayers.player.get(playerNumber).sheet;
            oldSheet.game.add(newGoal);
            actualPlayers.player.get(playerNumber).sheet = oldSheet;
            return true;
        }
    }

    private static int[] scoreTable(Players actualPlayers, Integer playerNumber, boolean log) {
        String[] listPart1 = new KniffelSheet().part1;
        String[] listPart2 = new KniffelSheet().part2;

        int[] table = new int[listPart1.length + listPart2.length + 5];

        if (log) {
            System.out.println(actualPlayers.player.get(playerNumber).name);
            System.out.println(Language.horizontalRuler);
        }

        for (int j=0; j < listPart1.length; j++) {
            if (log) System.out.print(listPart1[j] + " : ");
            for (int i = 0; i < actualPlayers.player.get(playerNumber).sheet.game.size(); i++) {                
                if (listPart1[j].equals(actualPlayers.player.get(playerNumber).sheet.game.get(i).set.key)) {
                    if (log) System.out.print(actualPlayers.player.get(playerNumber).sheet.game.get(i).set.value);  
                    table[j] = actualPlayers.player.get(playerNumber).sheet.game.get(i).set.value;      
                }
                else {
                    // Not found
                }
            }
            if (log) System.out.println("");
        }

        if (log) {
            System.out.println(Language.horizontalRuler);
            System.out.println(Language.partialSumPart1 + actualPlayers.player.get(playerNumber).partSumPart1);
            System.out.println(Language.bonus + actualPlayers.player.get(playerNumber).bonusPart1);
            System.out.println(Language.doubleRuler);
            System.out.println(Language.sumPart1 + actualPlayers.player.get(playerNumber).sumPart1);
            System.out.println(Language.dashedRuler);
        }
        table[listPart1.length + 1] = actualPlayers.player.get(playerNumber).partSumPart1;
        table[listPart1.length + 2] = actualPlayers.player.get(playerNumber).bonusPart1;
        table[listPart1.length + 3] = actualPlayers.player.get(playerNumber).sumPart1;

        for (int j=0; j < listPart2.length; j++) {
            if (log) System.out.print(listPart2[j] + " : ");
            for (int i = 0; i < actualPlayers.player.get(playerNumber).sheet.game.size(); i++) {                
                if (listPart2[j].equals(actualPlayers.player.get(playerNumber).sheet.game.get(i).set.key)) {
                    if (log) System.out.print(actualPlayers.player.get(playerNumber).sheet.game.get(i).set.value);  
                    table[listPart1.length + 3 + j] = actualPlayers.player.get(playerNumber).sheet.game.get(i).set.value;      
                } else {
                    // Not found
                }
            }
            if (log) System.out.println("");
        }
        if (log) {
            System.out.println(Language.horizontalRuler);
            System.out.println(Language.sumPart2 + actualPlayers.player.get(playerNumber).sumPart2);
            System.out.println(Language.doubleRuler);
            System.out.println(Language.totalSum + actualPlayers.player.get(playerNumber).sum);
            System.out.println("");
        }
        table[listPart1.length + 3 + listPart2.length + 1] = actualPlayers.player.get(playerNumber).sumPart2;
        table[listPart1.length + 3 + listPart2.length + 1] = actualPlayers.player.get(playerNumber).sum;

        return table;
    }

    private static void sumParts(Players actualPlayers, Integer playerNumber) {        
        String[] listPart1 = new KniffelSheet().part1;
        String[] listPart2 = new KniffelSheet().part2;
        
        Integer part1 = actualPlayers.player.get(playerNumber).partSumPart1;
        Integer part2 = actualPlayers.player.get(playerNumber).sumPart2;
        
        for (int i = 0; i < actualPlayers.player.get(playerNumber).sheet.game.size(); i++) {
            String key = actualPlayers.player.get(playerNumber).sheet.game.get(i).set.key;
            if (Arrays.asList(listPart1).contains(key)) {
                part1 += actualPlayers.player.get(playerNumber).sheet.game.get(i).set.value;
            } else {
                // Not found
            }
            if (Arrays.asList(listPart2).contains(key)) {
                part2 += actualPlayers.player.get(playerNumber).sheet.game.get(i).set.value;
            } else {
                // Not found
            }
        }
        actualPlayers.player.get(playerNumber).partSumPart1 = part1;
        actualPlayers.player.get(playerNumber).sumPart2 = part2;

        // Calculate bonus
        if (part1 > 62 ) actualPlayers.player.get(playerNumber).bonusPart1 = 35;

        actualPlayers.player.get(playerNumber).sumPart1 = part1 + actualPlayers.player.get(playerNumber).bonusPart1;
        actualPlayers.player.get(playerNumber).sum = part1 + actualPlayers.player.get(playerNumber).bonusPart1 + part2;
    }

    public static void main(String[] args) {
        Players knifflers = new Players();
        int[] table = new int[25];
        
        addPlayer(knifflers, "Jarvis");
    
        System.out.println("Würfle zufällig:");
        System.out.println(Language.horizontalRuler);
        for (int d = 0; d < 5; d++) {
            knifflers.player.get(0).deck.dice[d].roll();
            System.out.println(Language.dice + d + " : " + knifflers.player.get(0).deck.dice[d].getCount());   
        }

        knifflers.player.get(0).deck.dice[2].setRollState(false);
        System.out.println("Würfle zufällig, aber Würfel 3 ist blockiert:");
        System.out.println(Language.horizontalRuler);
        for (int d = 0; d < 5; d++) {
            knifflers.player.get(0).deck.dice[d].roll();
            System.out.println(Language.dice + d + " : " + knifflers.player.get(0).deck.dice[d].getCount());   
        }

        knifflers.player.get(0).deck.dice[4].setRollState(false);
        System.out.println("Würfle erneut zufällig, aber Würfel 3 und 5 sind blockiert:");
        System.out.println(Language.horizontalRuler);
        for (int d = 0; d < 5; d++) {
            knifflers.player.get(0).deck.dice[d].roll();
            System.out.println(Language.dice + d + " : " + knifflers.player.get(0).deck.dice[d].getCount());   
        }

        System.out.println("");
        System.out.println("Nun bauen wir uns die Würfel, wie wir sie brauchen:");
        System.out.println(Language.horizontalRuler);
        System.out.println("");

        knifflers.player.get(0).deck.dice[0].setCount(3);
        knifflers.player.get(0).deck.dice[1].setCount(3);
        knifflers.player.get(0).deck.dice[2].setCount(3);
        knifflers.player.get(0).deck.dice[3].setCount(3);
        knifflers.player.get(0).deck.dice[4].setCount(3);

        for (int d = 0; d < 5; d++) {            
            System.out.println(Language.dice + d + " : " + knifflers.player.get(0).deck.dice[d].getCount());   
        }

        System.out.println("");
        System.out.println("Dieser Wurf könnte eines dieser Ergbnisse sein:");
        System.out.println(Language.doubleRuler);
        System.out.println("");

        addGoal(knifflers, 0, Language.ones);
        addGoal(knifflers, 0, Language.twos);
        addGoal(knifflers, 0, Language.threes);
        addGoal(knifflers, 0, Language.fours);
        addGoal(knifflers, 0, Language.fives);
        addGoal(knifflers, 0, Language.sixes);
        addGoal(knifflers, 0, Language.threeEquals);
        addGoal(knifflers, 0, Language.fourEquals);
        addGoal(knifflers, 0, Language.fullHouse);
        addGoal(knifflers, 0, Language.smallStreet);
        addGoal(knifflers, 0, Language.bigStreet);
        addGoal(knifflers, 0, Language.kniffel);
        addGoal(knifflers, 0, Language.chance);        

        for (int x = 0; x < knifflers.player.size(); x++) {
            sumParts(knifflers, x);
            table = scoreTable(knifflers, x, true);             
        }       

        System.out.println("Ergebnisliste zur Weiterverarbeitung:");
        System.out.println(Language.dashedRuler);
        System.out.println(Arrays.toString(table));
    }
}