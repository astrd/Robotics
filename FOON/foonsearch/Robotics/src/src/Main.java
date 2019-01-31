package src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import src.Main.Foon;

public class Main {
	static CopyOnWriteArrayList<Foon> foons = new CopyOnWriteArrayList<Foon>();

	public static void main(String[] args) {
		
		retrival("files/goals_2.txt", "files/kitchens_new_2.txt", "files/FOON_22.6.2018.txt");

	}

	public static void retrival(String goalfile, String kitchenfile, String foon) {
		ArrayList<String> goal = parseStates(goalfile);
		ArrayList<String> kitchen = parseStates(kitchenfile);

		ArrayList<Foon> treesol = new ArrayList<>();
		CopyOnWriteArrayList<Foon> foons = parse(foon);
		CopyOnWriteArrayList<Foon> sol = new CopyOnWriteArrayList<Foon>();
		Queue<String> pend = new LinkedList<>();

		for (String st : goal) {

			if (kitchen.contains(st)) {
				kitchen.add(st);

			} else {
				pend.add(st);
			}
		}
		if (kitchen.isEmpty()) {
			System.out.println("No items in kitchen");
		}
		int n = 0;
		while (!pend.isEmpty()) {

			for (Foon fu : foons) {
				String p = pend.peek();
				n++;

				if (fu.getOutputStates().contains(p)) {
					sol.add(fu);
					pend.remove(p);
				}

				if (!sol.isEmpty()) {
					for (Foon s : sol) {
						boolean contain = true;
						for (String is : s.getInputStates()) {
							if (!kitchen.contains(is)) {
								contain = false;
							}
							if (contain) {
								treesol.add(s);
							} else {
								kitchen.addAll(s.getInputStates());
							}
						}
					}
				} else {

					pend.add(p);
				}
			}
			if (n > 100000) {
				break;
			}
			for (String st : goal) {
				if (kitchen.contains(st)) {				
					break;
				}
			}
		}

		Writer writer = null;
		ArrayList<Foon> aux = new ArrayList<>();
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("FOONSEARCH.txt"), "utf-8"));
			if (treesol.isEmpty()) {
				writer.write("TREE NOT FOUND"+"\n"+"GOALS TO ACCOMPLISH:"+"\n");
				for (String st : goal) {
					writer.write(st);				
				}

			} else {
				for (Foon f : treesol) {
					if (!aux.contains(f)) {
						writer.write(f.toPrint());
						writer.write("//");
						aux.add(f);
					}

				}

			}

		} catch (IOException ex) {
			System.out.println("input exception");

		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				System.out.println("writing exception");
			}
		}

	}

	public static void merge(String filemain, String filesec) {
		CopyOnWriteArrayList<Foon> fnews = parse(filemain);
		foons = parse(filesec);

		for (Foon fu : fnews) {
			if (!foons.isEmpty()) {
				for (Foon fn : foons) {
					if (fu.toString().equals(fn.toString())) {
						break;
					}
				}
				foons.add(fu);
			} else {
				foons.add(fu);
			}
		}
		Writer writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("FOON.txt"), "utf-8"));
			for (Foon f : foons) {
				writer.write(f.toPrint());
				

			}

		} catch (IOException ex) {
			// Report
		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
				/* ignore */}
		}

	}

	public static ArrayList<String> parseStates(String filename) {
		ArrayList<String> states = new ArrayList<String>();
		try {
			@SuppressWarnings("resource")
			Scanner sc = new Scanner(new File(filename));
			Scanner in = sc.useDelimiter("//");

			while (in.hasNext()) {
				String[] parr = in.next().split("O");
				for (String p : parr) {
					if (!p.isEmpty()) {
						states.add("O" + p.split("\n")[0].split("\t")[0] + "\n" + p.split("\n")[1].split("\t")[0]);
					}

				}

			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (String s : states) {
			// System.out.println("states: "+s);
		}
		return states;

	}

	@SuppressWarnings("finally")
	public static CopyOnWriteArrayList<Foon> parse(String filename) {
		CopyOnWriteArrayList<Foon> foons = new CopyOnWriteArrayList<Foon>();
		try {

			@SuppressWarnings("resource")
			Scanner sc = new Scanner(new File(filename));
			Scanner in = sc.useDelimiter("//");

			Foon fu = null;
			while (in.hasNext()) {

				String parr = in.next();
				String par[] = parr.split("\n");

				ArrayList<String> os = new ArrayList<>();
				ArrayList<String> so = new ArrayList<>();
				ArrayList<String> ingredients = new ArrayList<>();
				String motion = "s";
				for (int i = 0; i < par.length; i++) {
					String code = par[i];
					if (code.startsWith("O")) {
						os.add(code);

					} else if (code.startsWith("S")) {

						so.add(code);

					} else if (code.startsWith("M")) {
						motion = code;

					}
				}
				fu = new Foon(os, so, motion, ingredients, parr);

				foons.add(fu);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			return foons;
		}

	}

	public static class Foon {
		ArrayList<String> os = new ArrayList<>();
		ArrayList<String> so = new ArrayList<>();
		String motion;
		List<String> ingredients;
		String parr;

		public Foon(ArrayList<String> os, ArrayList<String> so, String motion, List<String> ingredients, String parr) {
			this.os = os;
			this.so = so;
			this.motion = motion;
			this.ingredients = ingredients;
			this.parr = parr;
		}

		public String toString() {
			return "Functional Unit:" + getOs() + " " + getSo() + " " + motion.split("\t")[0];
		}

		public String toPrint() {
			return getParr();
		}

		public String getOs() {
			String states = "";
			Collections.sort(this.os);
			for (String s : this.os) {
				states = states + " " + s.split("\t")[0];
			}
			return states;
		}

		public String getParr() {

			return this.parr;
		}

		public void setOs(ArrayList<String> os) {
			this.os = os;
		}

		public String getSo() {
			String states = "";
			Collections.sort(this.so);
			for (String s : this.so) {
				states = states + " " + s.split("\t")[0];
			}
			return states;
		}

		public void setSo(ArrayList<String> so) {
			this.so = so;
		}

		public String getMotion() {
			return motion;
		}

		public void setMotion(String motion) {
			this.motion = motion;
		}

		public List<String> getIngredients() {
			return ingredients;
		}

		public void setIngredients(List<String> ingredients) {
			this.ingredients = ingredients;
		}

		public ArrayList<String> getInputStates() {
			ArrayList<String> states = new ArrayList<String>();
			String[] lines = this.parr.split("M");
			String[] parr = lines[0].split("O");
			for (String p : parr) {
				if (!p.isEmpty() && !p.equals("\n")) {
					states.add("O" + p.split("\n")[0].split("\t")[0] + "\n" + p.split("\n")[1].split("\t")[0]);
				}
			}
			return states;

		}

		public ArrayList<String> getOutputStates() {
			ArrayList<String> states = new ArrayList<String>();
			String[] lines = this.parr.split("M");
			String[] output = lines[lines.length - 1].split("\n");

			String out = "";
			int count = 0;
			for (String l : output) {
				if (count > 0) {
					out = out + l + "\n";
				}
				count++;
			}
			String[] parr = out.split("O");
			for (String p : parr) {

				if (!p.isEmpty()) {
					states.add("O" + p.split("\n")[0].split("\t")[0] + "\n" + p.split("\n")[1].split("\t")[0]);
				}

			}
			return states;
		}

	}
}
