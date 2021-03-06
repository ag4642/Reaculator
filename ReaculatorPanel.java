 //Reaculator 
 //Pranav Balan, Ashvarya Garg, Aditya Ponukumati, Anirudh Ryali
   import javax.swing.*;
   import java.awt.*;
   import java.awt.event.*;
   import java.util.*;
   import java.io.*;
   public class ReaculatorPanel extends JPanel
   {
      private JLabel label, label1, label2, label3, label4, label5, label6, label7, label8, label9; 		 
      private JTextField box, box1, box2, box3, box4, box5, box6;    
      private static final double Rjoules = 8.3145;
      private static final double Ratmos = 0.0821;
      private static final double faraday = 96500;
      private String[] reactants, products, split;
      private ArrayList<Integer> output;
      private PrintStream ps;
   
      public ReaculatorPanel()
      {
         setLayout(new GridLayout(13,1));
         setBackground(Color.CYAN); 
         
         box = new JTextField("Enter Chemical Equation", 1);
         add(box, BorderLayout.CENTER);
         
         box1 = new JTextField("Enter Q Value", 1);
         add(box1, BorderLayout.CENTER);
         
         box2 = new JTextField("Enter Temperature in Kelvin", 1);
         add(box2, BorderLayout.CENTER); 
         
         box4 = new JTextField("Enter Van't Hoff Factor", 1);
         add(box4, BorderLayout.CENTER);
         
         box5 = new JTextField("Enter Molar Concentration", 1);
         add(box5,BorderLayout.CENTER); 
         
         JButton button1 = new JButton("Reaculate");
         button1.setPreferredSize(new Dimension(200, 100));
         button1.addActionListener(new Listener());
         add(button1, BorderLayout.CENTER);    
      	 
         label9 = new JLabel("Balanced Chemical Equation");
         label9.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label9.setForeground(Color.red);
         add(label9);
           	
         label = new JLabel("Delta H�rxn");
         label.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label.setForeground(Color.black);
         add(label);
         
         label1 = new JLabel("Delta S�rxn");
         label1.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label1.setForeground(Color.black);
         add(label1);
         
         label2 = new JLabel("Delta G�rxn");
         label2.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label2.setForeground(Color.black);
         add(label2);
         
           
         label4 = new JLabel("Keq at 298 Kelvin");
         label4.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label4.setForeground(Color.black);
         add(label4);
         
         label5 = new JLabel("Delta G");
         label5.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label5.setForeground(Color.black);
         add(label5);   
           
         label8 = new JLabel("Osmotic Pressure");
         label8.setFont(new Font("Sans_Serif", Font.BOLD , 30));
         label8.setForeground(Color.black);
         add(label8); 
          
      
      }
      private class Listener implements ActionListener
      {
         public void actionPerformed(ActionEvent e)
         { //try {
            String equation = box.getText();
            String[] separate = equation.split("=>");
            String[] reactant = separate[0].split("\\+");
            String[] product = separate[1].split("\\+");  
            String balanced = balance(equation);
            label9.setText("Balanced Chemical Equation: " + balanced);
            double h = 0;
            double ss = 0;
            try {
               h = dHcalc(reactant, product, output); 
            } 
               catch (IOException easdf) {
                  System.out.println("WTF"); 
                  System.exit(0);
               }
            try {
               ss = dScalc(reactant, product, output); 
            } 
               catch (IOException easdf) {
                  System.out.println("WTF"); 
                  System.exit(0);
               }  
            label.setText("Delta H�rxn: " + h + " kJ/mol");    
            label1.setText("Delta S�rxn: " + ss + " J/mol*K");
            double g = GHTS(h, (double)(Integer.parseInt(box2.getText())) , ss);
            label2.setText("Delta G�rxn: " + g + " kJ/mol"); 
            double k = 0.0;
            k =  GnRTlnK(g, (double)Integer.parseInt(box2.getText()));
            label4.setText("Keq at 298 Kelvin: " + k + "");
            double gg = 0.0;
            gg = GGRTlnQ(g, (double)Integer.parseInt(box2.getText()), Double.parseDouble(box1.getText()));
            label5.setText("Delta G: " + gg + " kJ/mol");
            double o = 0.0;
            o = PiIMRT((double)Integer.parseInt(box4.getText()), Double.parseDouble(box5.getText()), Double.parseDouble(box2.getText()));
            label8.setText("Osmotic Pressure: " + o + " atm"); 
            try{
               ps = new PrintStream(new FileOutputStream(new File("equilibrium.txt")));
            }
               catch(FileNotFoundException file){
                  System.exit(0);
               }
            String kk = "";
            kk = equilibrium(reactant, product, h);
            ps.println(kk);
            ps.close();
            
                 //} catch(Exception asdlfkj) {
         	//	int asdf = 0;
         	//}   
         }
      }
    
          //Calculation of enthalpy change using products - reactants
      public static double dHcalc(String[] reactants, String[] products, ArrayList<Integer> coefficients) throws IOException
      {
         Scanner q;
         double product = 0;
         double reactant = 0;
         int counter = 0;
      	
         for(String e : products)
         {
            q = new Scanner(new File("thermo_cut.txt"));
            while(q.hasNextLine())
            {
               String[] temp = q.nextLine().split(" ");
               if(temp[0].trim().equals(e.trim()))
               {
                  product += (coefficients.get(counter + reactants.length)) * Double.parseDouble(temp[1]);
                  break;
               }	
            }
            
            counter++;
         }
         counter = 0;
         for(String e : reactants)
         {
            q = new Scanner(new File("thermo_cut.txt"));
            while(q.hasNextLine())
            {
               String[] temp = q.nextLine().split(" ");
               if(temp[0].trim().equals(e.trim()))
               {
                  reactant += coefficients.get(counter) * Double.parseDouble(temp[1]);
                  break;
               }	
            }
            
            counter++;
         }
      	
         return product - reactant;
      }
      
   
   	//Calculation of change in entropy via products - reactants
      public static double dScalc(String[] reactants, String[] products, ArrayList<Integer> coefficients) throws IOException
      {
         Scanner q;
         double product = 0;
         double reactant = 0;
         int counter = 0;
      	
         for(String e : products)
         {
            q = new Scanner(new File("thermo_cut.txt"));
            while(q.hasNextLine())
            {
               String[] temp = q.nextLine().split(" ");
               if(temp[0].trim().equals(e.trim()))
               {
                  product += (coefficients.get(counter + reactants.length)) * Double.parseDouble(temp[2]);
                  break;
               }	
            }
            
            counter++;
         }
         counter = 0;
         for(String e : reactants)
         {
            q = new Scanner(new File("thermo_cut.txt"));
            while(q.hasNextLine())
            {
               String[] temp = q.nextLine().split(" ");
               if(temp[0].trim().equals(e.trim()))
               {
                  reactant += coefficients.get(counter) * Double.parseDouble(temp[2]);
                  break;
               }	
            }
            
            counter++;
         }
      	
         return product - reactant;
      }
   
   //Calculation of dG via dH + TdS
      public static double GHTS(double dH, double T, double dS)
      {
         return dH - T * dS/1000;
      }
   
   //Calculation of K via dG = -RTln(K)
      public static double GnRTlnK(double dG, double T)
      {
         double convertR = Rjoules / 1000; //Conversion of R from kJ to J
         return Math.pow(Math.E, dG / (-1 * convertR * T));
      }
   
   //Calculation of E via dG = -nFE
      public static double GnNFE(double dG, double N)
      {
         return -1 * (dG / (N * faraday));
      }
   
   //Calculation of pi via pi = iMRT
      public static double PiIMRT(double I, double M, double T)
      {
         return I * M * Ratmos * T;
      }
   //Calculation of E via E = E - RT/NF * ln(Q)
      public static double EnsERTNFlnQ(double E, double Q, double T, double N)
      {
         return E - ((Rjoules * T) / (N * faraday)) * Math.log(Q);
      }
      public static String equilibrium(String[] reactants, String[] products, double dH)
      {
         String presponse = "";
         String response = "";
         for(String e : reactants)
         {
            presponse = "";
            for(int i = 0; i < products.length; ++i)
            {
               if(products.length == 1)
                  presponse += products[i].trim() + ".\n";
               else if(i == products.length - 1)
                  presponse += products[i].trim() + ".\n";
               else
                  presponse += products[i].trim() + ", ";
            }
            
            if(e.contains("(s)") || e.contains("(diamond)") || e.contains("(graphite)") || e.contains("(l)"))
            {
               response += " Addition of " + e + "causes no change in equilibrium.\n";
               continue;
            }
            else
            {
               if(reactants.length == 1)
               {
                  response += " Addition of " + e + "causes a decrease in nothing.";
                  System.out.println();
               }
               else
               {
                  response += " Addition of " + e + "causes a decrease in ";
                  System.out.println();
                  for(int i = 0; i < reactants.length; ++i)
                  {
                     if(reactants[i].trim().equals(e.trim()))
                     {
                        if(i == reactants.length - 1)
                        {
                           response = response.substring(0, response.length() - 1);
                           response += ".\n";
                        }
                        continue;
                     }
                     else if(i == reactants.length - 1)
                        response += reactants[i].trim() + ".\n";
                     else
                        response += reactants[i].trim() + ", ";
                  }
               }
               response += " Addition of " + e + "causes an increase in " + presponse;
            }
         } 
         for(String e : products)
         {
            presponse = "";
            for(int i = 0; i < reactants.length; ++i)
            {
               if(reactants.length == 1)
                  presponse += reactants[i].trim() + ".\n";
               else if(i == reactants.length - 1)
                  presponse += reactants[i].trim() + ".\n";
               else
                  presponse += reactants[i].trim() + ", ";
            }
            
            if(e.contains("(s)") || e.contains("(diamond)") || e.contains("(graphite)") || e.contains("(l)"))
            {
               response += " Addition of" + e + " causes no change in equilibrium.\n";
               System.out.println();
               continue;
            }
            else
            {
               if(products.length == 1)
                  response += " Addition of " + e + "causes a decrease in nothing.";
               else
               {
                  response += " Addition of " + e + "causes a decrease in ";
                  for(int i = 0; i < products.length; ++i)
                  {
                     if(products[i].trim().equals(e.trim()))
                     {
                        if(i == products.length - 1)
                        {
                           response = response.substring(0, response.length() - 1);
                           response += ".\n";
                        }
                        continue;
                     }
                     else if(i == products.length - 1)
                        response += products[i].trim() + ".\n";
                     else
                        response += products[i].trim() + ", ";
                  }
               }
               response += "Addition of" + e + " causes an increase in " + presponse;
            }
         } 
      
         if(dH > 0)
         {
            response += " Increasing temperature causes an decrease in concentration of " + presponse;
            System.out.println();
         }
         else
            response += " Increasing temperature causes an increase in " + presponse;
            
         return response;
      }
   
   	//Calculation of dG via dG = dG + RTln(Q)
      public static double GGRTlnQ(double dG, double T, double Q)
      {
         return dG + (Rjoules / 1000) * T * Math.log(Q);
      }
      public String balance(String sem)
      {
         split = (sem).split(" ");
         String[] inp = new String[split.length/2 + (split.length%2)+1];
         int numToAdd = 0;
         int IMPIND = 0;
         int reac = 0; int prod= 0;
         for(int i = 0; i < split.length; i++) {
            if(split[i].equals("=>")) {
               numToAdd = 1;
               reac = i/2 + 1;
               inp[i/2 +1] = "1234567890";
               IMPIND = i/2 + 1;
            }
            if(i%2 == 0)
               inp[i/2+numToAdd] = split[i];
         }
         prod = inp.length - reac - 1;
         reactants = new String[reac];
         products = new String[prod];
         for(int i = 0; i < reac; i++) reactants[i] = inp[i];
         for(int i = 0; i < prod; i++) products[i] = inp[inp.length - i - 1];
         inp = trimThewittyCoefs(inp);
         String[] inp1= trimThehairyHair(inp);
         ArrayList<ArrayList<Element>> eles = findElements(inp1, IMPIND);
      //Negative numbers in the matrix mean those atoms come from the products
      //Each row represents a different element
         ArrayList<ArrayList<Double>> input = getTheMothersMatrix(eles);
         output = rref(input);
         ArrayList<Integer> subOut = new ArrayList<Integer>();
         for(int i: output) subOut.add(i);
         String fin = "";
         if(output != null)
         {
            fin = applyCoefs(subOut, inp);
            return fin;
         }     
         else {
            return new String("UNSOLVABLE EQUATION");
         }
      }
      //Puts the correct coefficients properly to the equation
      public static String applyCoefs(ArrayList<Integer> coefs, String[] mols) {
         coefs.add(Arrays.asList(mols).indexOf("1234567890"), 1);
         String out = "";
         if(coefs.get(0) > 1) out = out + coefs.get(0);
         out = out + mols[0];
         for(int i = 1; i < mols.length; i++) {
            if(mols[i-1].equals("1234567890")) {
               out = out + " => ";
            } 
            else {
               if(mols[i].equals("1234567890")) 
                  continue; 
               out = out + " + ";
            }
            if(coefs.get(i) > 1) out = out + coefs.get(i);
            out = out + mols[i];
         }
         return out;
      }
   //Trims any heading coefficients
      public static String[] trimThewittyCoefs(String[] inp) {
         String[] nums = ("1234567890").split("");
         for(int ind = 0; ind < inp.length; ind++) {
            String s = inp[ind];
            if(s.equals("1234567890")) 
               continue;
            int i = 0;
            while(contains(nums, s.substring(i,i+1))) i++;
            s = s.substring(i, s.length());
            inp[ind] = s;
         }
         return inp;
      }
   
   //Trims the state at the end
      public static String[] trimThehairyHair(String[] inp) {
         String[] output = new String[inp.length];
         for(int ind = 0; ind < inp.length; ind++) {
            String s = inp[ind];
            if(s == "1234567890") {
               output[ind] = s;
               continue;
            }
            int num = 3;
            if (s.substring(s.length() - 2, s.length() - 1).equals("q")) num = 4;
            output[ind] = s.substring(0,s.length() - num);
         }
         return output;
      }
   //Makes the equation into a matrix
      public static ArrayList<ArrayList<Double>> getTheMothersMatrix(ArrayList<ArrayList<Element>> input) {
         HashSet<String> used = new HashSet<String>();
         ArrayList<ArrayList<Double>> output = new ArrayList<ArrayList<Double>>();
         while(true) {
            String ele = "";
            for(ArrayList<Element> e: input) {
               for(Element e1: e) {
                  if(!used.contains(e1.name)) {
                     ele = e1.name;
                     used.add(ele);
                     break;
                  }
               }
               if (!ele.equals("")) 
                  break;
            }
            if (ele.equals("")) 
               break;
            ArrayList<Double> sub = new ArrayList<Double>();
            for(ArrayList<Element> e: input) {
               int num = 0;
               for(Element e1: e) {
                  if (e1.name.equals(ele)) {
                     num = e1.num;
                     break;
                  }
               }
               sub.add((double)num);
            }
            output.add(sub);
         }
         return output;
      }
   
   //Finds the different elements in the reaction
      public static ArrayList<ArrayList<Element>> findElements(String[] input, int IMPIND) {
         ArrayList<ArrayList<Element>> output = new ArrayList<ArrayList<Element>>();
         for(String s: input) {
            ArrayList<Element> a = helper(s);
            if(a != null) output.add(a);
         }
         for(int i = IMPIND; i < output.size(); i++) {
            ArrayList<Element> eles = output.get(i);
            for(Element e: eles) {
               e.num = e.num*-1;
            }
         }
         for(int i = 0; i < output.size(); i++) {
            ArrayList<Element> elses = output.get(i);
            TreeMap<String, Integer> map = new TreeMap<String, Integer>();
            Set<String> set = new HashSet<String>();
            for(Element e: elses) {
               int prevNum = 0;
               if(set.contains(e.name)) {
                  prevNum = map.get(e.name);
               }
               map.put(e.name, e.num + prevNum);
            }
            elses = new ArrayList<Element>();
            for(String name: map.keySet())
               elses.add(new Element(name, map.get(name)));
         }
         return output;
      }
   
      public static ArrayList<Element> helper(String s) {
         if(s.equals("1234567890")) 
            return null;
         String[] lower = ("abcdefghijklmnopqrstuvwxyz").split("");
         String[] nums = ("123456789").split("");
         ArrayList<Element> a = new ArrayList<Element>();
         String prev = ""; int num = 0;
         for(int i = 0; i < s.length(); i++) {
            String sub = s.substring(i,i+1);
            if(contains(lower, sub)) {
               prev = prev + sub;
               continue;
            }
            if(contains(nums, sub)) {
               num = 10*num + Integer.parseInt(sub);
               continue;
            }
            if (!prev.equals("")) {
               if(num == 0) num = 1;
               a.add(new Element(prev,num));
            }
            if(sub.equals("(")) {
               prev = "";
               int thenum = 0;
               int j = 0;
               for(j = 0; j < s.length(); j++) 
                  if(s.substring(j, j+1).equals(")")) 
                     break;
               ArrayList<Element> sublist = helper(s.substring(i+1,j));
               j = j+1;
               int k = 0;
               for(k = j; k < s.length(); k++) 
                  if(!Arrays.asList(nums).contains(s.substring(k, k+1))) 
                     break;
               i = k;
               thenum = Integer.parseInt(s.substring(j, k));
               for(Element e: sublist) {
                  e.num *= thenum;
                  a.add(e);
               }
               continue;
            }
            prev = sub;
            num = 0;
         }
         if(!prev.equals("")) {
            if(num == 0) num = 1;
            a.add(new Element(prev, num));
         }
         return a;
      }
   
   //Checks if b is in String array a
      public static boolean contains(String[] a, String str) {
         for(String s: a)
            if(s.equals(str))
               return true;
         return false;
      }
   //Row reduces to find the null space of the matrix involving the equations
      public static ArrayList<Integer> rref(ArrayList<ArrayList<Double>> input) {
         try {
            HashSet<Integer> set = new HashSet<Integer>();
            for(int col = 0; col < Math.min(input.size(), input.get(0).size()); col++) {  //Goes through each molecule (NOT ATOM)
               double piv = 0; int r = -1;
               for(int row = 0; row < input.size(); row++) { //Finds the pivot in that column
                  if (input.get(row).get(col) != 0 && !set.contains(row)) { //Makes sure pivot is not in a row already used
                     piv = input.get(row).get(col);
                     r = row;
                     set.add(row);
                     break;
                  }
               }
               if(piv == 0) 
                  continue;
               for(int c = 0; c < input.get(0).size(); c++) { //Reduces every element in the row
                  input.get(r).set(c, input.get(r).get(c)/(piv*1.0));
               }
               piv = 1;
               for(int row = 0; row < input.size(); row++){ //Reduces every other row
                  if(row == r) 
                     continue;
                  double first = (double)(input.get(row).get(col));
                  if(first == 0.0) 
                     continue;
                  for(int c = 0; c < input.get(0).size(); c++){
                     input.get(row).set(c, input.get(row).get(c) - first * input.get(r).get(c));
                  }
               }
            }
            for(int row = 0; row < input.size(); row++) {
               for(int col = 0; col < input.get(0).size(); col++) {
                  if(Math.abs(input.get(row).get(col)) < 0.0001) input.get(row).set(col, 0.0);
                  input.get(row).set(col, (double)(Math.round(input.get(row).get(col)*1000000.0))/1000000.0);
               }
            }
            ArrayList<ArrayList<Double>> input2 = new ArrayList<ArrayList<Double>>();
            for(int row = 0; row < input.size(); row++) {
               boolean zero = true;
               for(int col = 0; col < input.get(0).size(); col++) {
                  if(input.get(row).get(col) != 0.0) {
                     zero = false; 
                     break;
                  }
               }
               if(!zero) input2.add(input.get(row));
            }
            input = input2;
            ArrayList<Double> output = new ArrayList<Double>(); //Gets the answers for each element
            for(int col = 0; col < input.size(); col++) output.add(0.0);
            for(int row = 0; row < input.size(); row++) {
               double sum = 0.0;
               for(int col = input.size(); col < input.get(0).size(); col++) {
                  sum -= input.get(row).get(col);
               }
               output.set(input.get(row).indexOf(1.0), sum);
            }
            for(int i = 0; i < input.get(0).size() - input.size(); i++)
               output.add(1.0);
            for(Double d: output) { //Makes all the doubles into whole numbers
               int a = findMult(d);
               for(int ind = 0; ind < output.size(); ind++) output.set(ind, output.get(ind) * a);
            }
            ArrayList<Integer> o = new ArrayList<Integer>();
            for(double d: output) {
               o.add((int)d);
            }
            return o;
         } 
            catch(Exception co) { 
               return null;
            }
      }
   
   //Checks if the double is a whole number
      public static boolean checkIfInt(double d) {
         if(Math.abs(d - (double)(Math.round(d))) < 0.0001) 
            return true;
         return false;
      }
   
   //Finds the number to make the double a whole number
      public static int findMult(double d) {
         for(int i = 1; i <= 100; i++) {
            if(checkIfInt(d*i)) 
               return i;
         }
         System.out.println("FIND MULT ERROR WITH NUMBER " + d);
         System.exit(0);
         return 0;
      }
   }
   class Element {
      public String name;
      public int num;
      public Element(String n, int i) {
         name = n; num = i;
      }
      public String toString() {
         return num + name;
      }
   //Gives estimated changes in equilibrium based on addition of chemicals
   }