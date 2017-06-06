/*
 * CMSC 335 Project 1 
 * by Kolby Kauffman
 */



import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SeaPortsProgram extends JFrame {
	static final long serialVersionUID = 123L;

	World world; // Initiates and runs on all created objects

	HashMap<Integer, Ship> hms = new HashMap<Integer, Ship>();
	HashMap<Integer, Dock> hmd = new HashMap<Integer, Dock>();
	HashMap<Integer, SeaPort> hmp = new HashMap<Integer, SeaPort>();
	private JButton sortButton;
	String[] sortingStrings = { "weight", "length", "width", "draft" };;

	JTextArea jta = new JTextArea();
	JComboBox<String> jcb;
	JTextField jtf;
	Scanner scin;
	File selectedFile;

	public SeaPortsProgram() {

		setTitle("Sea Ports");
		setSize(800, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		JScrollPane jsp = new JScrollPane(jta);
		add(jsp, BorderLayout.CENTER);

		JButton jbr = new JButton("Read");
		;
		JButton jbs = new JButton("Search");
		sortButton = new JButton("Sort");
		JLabel jls = new JLabel("Search Target");

		final JTextField jtf = new JTextField(10);

		final JComboBox<String> jcb = new JComboBox<String>();
		jcb.addItem("Index");
		jcb.addItem("Skill");
		jcb.addItem("Name");

		final JComboBox<String> jcbsort = new JComboBox<String>(sortingStrings);

		JPanel jp = new JPanel();
		jp.add(jbr);
		jp.add(jls);
		jp.add(jtf);
		jp.add(jcb);
		jp.add(jbs);
		jp.add(jcbsort);
		jp.add(sortButton);
		add(jp, BorderLayout.PAGE_START);

		readFile();

		validate();

		sortButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jta.append("\n\n--------------------------Sorted data ------------------------------------\n");
				String sortInput = jcbsort.getSelectedItem().toString();
				String resultText = sortElement(sortInput);
				jta.append(resultText);
			}
		});

		jbr.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				readFile();

			}
		});

		jbs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				search((String) jcb.getSelectedItem(), jtf.getText());
			}
		});

	} // end no-parameter constructor

	/***
	 * Sort the data
	 * 
	 * @param sortInput
	 * @return
	 */
	private String sortElement(String sortInput) {
		String result = "";
		ArrayList<Ship> ships = new ArrayList<>();
		
		for (SeaPort msp : world.ports) {
			for (Ship ms : msp.ships) {
				ships.add(ms);
			}
		}
		
		if (sortInput.equals("weight")) {
			Collections.sort(ships, new ShipComp("width"));
		} else if (sortInput.equals("Width")) {
			Collections.sort(ships, new ShipComp("Width"));
		} else if (sortInput.equals("length")) {
			Collections.sort(ships, new ShipComp("length"));
		} else if (sortInput.equals("draft")) {
			Collections.sort(ships, new ShipComp("draft"));
		}

		for (Ship ship : ships) {
			result += ship.toString() + "\n";
		}
		return result;
	}

	public void readFile() {
		JFileChooser jfc = new JFileChooser(".");
		int result = jfc.showOpenDialog(new JFrame());
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				scin = new Scanner(new FileReader(jfc.getSelectedFile()));
			} catch (FileNotFoundException e) {
				JOptionPane.showMessageDialog(null, "File Not Found.");
			}
		}
		world = new World(scin);
		
		
		
		//Add SeaPort
		for (SeaPort msp : world.ports) {
			hmp.put(msp.index, msp);
		}
		
		
		//Add ship
		for (SeaPort msp : world.ports) {
			for (Ship ms : msp.ships) {
				hms.put(ms.index, ms);
			}
		}
		
		//dd Dock
		for (SeaPort msp : world.ports) {
			for (Dock md : msp.docks) {
				hmd.put(md.index, md);
			}
		}
		jta.setText(world.toString());
	} // end method readFile

	public void search(String type, String target) {
		jta.append("\n\nSearch for " + type.toLowerCase() + " " + target
				+ ": \n");
		jta.append("\t" + world.search(type, target));
	} // end method search

	public static void main(String[] args) {
		SeaPortsProgram sp = new SeaPortsProgram();
	} // end main
} // end class SeaPortsProgram

class Port {

} // end class Port

class Thing implements Comparable<Thing> {

	int index;
	String name;
	int parent;

	public Thing() {
		name = "";
		index = 0;
		parent = 0;
	} // end no parameter constructor

	public Thing(Scanner sc) {
		if (sc.hasNext()) {
			name = sc.next();
		}
		if (sc.hasNextInt()) {
			index = sc.nextInt();
		}
		if (sc.hasNextInt()) {
			parent = sc.nextInt();
		}
	} // end Scanner constructor

	public String toString() {
		String st = name + " " + index;
		return st;
	} // end toString method

	@Override
	public int compareTo(Thing o) {
		// TODO Auto-generated method stub
		return 0;
	}
} // end class Thing

class World extends Thing {
	ArrayList<SeaPort> ports = new ArrayList<SeaPort>();
	PortTime time = new PortTime();

	public World(Scanner scin) {
		super();
		while (scin.hasNextLine()) {
			process(scin.nextLine());
		}
	}

	public String toString() {
		String st = "\n\n------- The World -------";
		if (ports.size() == 0) {
			return st;
		}
		for (SeaPort msp : ports) {
			st += msp;
		}
		return st;
	}

	public void process(String st) {
		Scanner sc = new Scanner(st);
		if (!sc.hasNext()) {
			return;
		}
		switch (sc.next()) {
		case "port":
			addPort(sc);
			break;
		case "dock":
			addDock(sc);
			break;
		case "pship":
			addPassengerShip(sc);
			break;
		case "cship":
			addCargoShip(sc);
			break;
		case "person":
			addPerson(sc);
			break;
		case "job":
			addJob(sc);
			break;
		default:
			break;
		}
	} // end process

	public void addPort(Scanner sc) {
		ports.add(new SeaPort(sc));
	}

	public void addDock(Scanner sc) {
		Dock tempDock = new Dock(sc);
		for (SeaPort msp : ports) {
			if (msp.index == tempDock.parent) {
				msp.docks.add(tempDock);
			}
		}
	}

	public void addPassengerShip(Scanner sc) {
		PassengerShip tempPShip = new PassengerShip(sc);
		assignShip(tempPShip);
	} // end method addPassengerShip

	public void addCargoShip(Scanner sc) {
		CargoShip tempCShip = new CargoShip(sc);
		assignShip(tempCShip);
	} // end method addCargoShip

	public void addPerson(Scanner sc) {
		Person tempPerson = new Person(sc);
		for (SeaPort msp : ports) {
			if (msp.index == tempPerson.parent) {
				msp.person.add(tempPerson);
			}
		}
	} // end method addPerson

	public void addJob(Scanner sc) {
		Job tempJob = new Job(sc);
		//
		// Implement in Projects 3 and 4
		//
	} // end method addJob

	public SeaPort getSeaPortByIndex(int x) {
		for (SeaPort msp : ports) {
			if (msp.index == x) {
				return msp;
			}
		}
		return null;
	} // end method getSeaPortByIndex

	public Dock getDockByIndex(int x) {
		for (SeaPort msp : ports) {
			for (Dock md : msp.docks) {
				if (md.index == x) {
					return md;
				}
			}
		}
		return null;
	} // end method getDockByIndex

	public Ship getShipByIndex(int x, java.util.HashMap<Integer, Ship> hms) {
		 return hms.get(x);
	} // end method getShipByIndex

	public Person getPersonByIndex(int x) {
		for (SeaPort msp : ports) {
			for (Person mp : msp.person) {
				if (mp.index == x) {
					return mp;
				}
			}
		}
		return null;
	} // end method getPersonByIndex

	public void assignShip(Ship ms) {
		Dock md = getDockByIndex(ms.parent);
		if (md == null) {
			getSeaPortByIndex(ms.parent).ships.add(ms);
			getSeaPortByIndex(ms.parent).que.add(ms);
		} else {
			md.ship = ms;
			getSeaPortByIndex(md.parent).ships.add(ms);
		}
	} // end method assignShip

	public String search(String type, String target) {
		String st = "";
		switch (type) {
		case "Name":
			st += searchName(target);
			break;
		case "Index":
			// check if int
			try {
				st += searchIndex(Integer.parseInt(target));
			} catch (NumberFormatException e) {
				st += "Not a valid search target for Index";
			}
			break;
		case "Skill":
			st += searchType(target);
			break;
		default:
			break;
		}
		return st;
	} // end class search

	public String searchName(String target) {
		for (SeaPort msp : ports) {
			if (msp.name.equals(target)) {
				return msp.toString();
			}
			for (Dock md : msp.docks) {
				if (md.name.equals(target)) {
					return md.toString();
				}
			}
			for (Ship ms : msp.ships) {
				if (ms.name.equals(target)) {
					return ms.toString();
				}
			}
			for (Person mp : msp.person) {
				if (mp.name.equals(target)) {
					return mp.toString();
				}
			}
		}
		return "Target Not Found";
	} // end class searchName

	public String searchIndex(int target) {
		for (SeaPort msp : ports) {
			if (msp.index == target) {
				return msp.toString();
			}
			for (Dock md : msp.docks) {
				if (md.index == target) {
					return md.toString();
				}
			}
			for (Ship ms : msp.ships) {
				if (ms.index == target) {
					return ms.toString();
				}
			}
			for (Person mp : msp.person) {
				if (mp.index == target) {
					return mp.toString();
				}
			}
		}
		return "Target Not Found";
	} // end class searchIndex

	public String searchType(String target) {
		String st = "";
		for (SeaPort msp : ports) {
			for (Person mp : msp.person) {
				if (mp.skill.equals(target)) {
					st += mp.toString() + '\n';
				}
			}
		}
		if (st == "") {
			return "Target Not Found";
		}
		return st;
	}

	// CMSC 335
	// Sorting :-

} // end class World

class SeaPort extends Thing {
	ArrayList<Dock> docks = new ArrayList<Dock>(); // the list of docks at the
													// port
	ArrayList<Ship> que = new ArrayList<Ship>(); // the list of ships waiting to
													// dock
	ArrayList<Ship> ships = new ArrayList<Ship>(); // the list of all the ships
													// at this port
	ArrayList<Person> person = new ArrayList<Person>(); // people with skills at
														// this port

	public SeaPort(Scanner sc) {
		super(sc);
	}

	public String toString() {
		String st = "\n\nSeaPort: " + super.toString() + '\n';
		for (Dock md : docks) {
			st += "\n" + md;
		}
		st += "\n\n --- List of all ships in que:";
		for (Ship ms : que) {
			st += "\n  > " + ms;
		}
		st += "\n\n --- List of all ships:";
		for (Ship ms : ships) {
			st += "\n  > " + ms;
		}
		st += "\n\n --- List of all person:";
		for (Person mp : person) {
			st += "\n  > " + mp;
		}
		return st;
	} // end method toString
} // end class SeaPort

class Dock extends Thing {
	Ship ship;

	public Dock(Scanner sc) {
		super(sc);
	}

	public String toString() {
		String st = "Dock: " + super.toString();
		if (ship == null) {
			return st;
		}
		st += "\n  " + ship;
		return st;
	} // end method toString
} // end class Dock

class Ship extends Thing {
	PortTime arrivalTime = new PortTime();
	private PortTime dockTime = new PortTime();
	private double draft;
	private double length;
	private double weight;
	private double width;
	ArrayList<Job> jobs = new ArrayList<Job>();

	public Ship(Scanner sc) {
		super(sc);
		if (sc.hasNextDouble()) {
			setWeight(sc.nextDouble());
		}
		if (sc.hasNextDouble()) {
			setLength(sc.nextDouble());
		}
		if (sc.hasNextDouble()) {
			setWidth(sc.nextDouble());
		}
		if (sc.hasNextDouble()) {
			setDraft(sc.nextDouble());
		}
	} // end Scanner constructor

	public String toString() {
		String st = "Ship: " + super.toString();
		return st;
	} // end method toString

	/**
	 * @return the draft
	 */
	public double getDraft() {
		return draft;
	}

	/**
	 * @param draft
	 *            the draft to set
	 */
	public void setDraft(double draft) {
		this.draft = draft;
	}

	/**
	 * @return the length
	 */
	public double getLength() {
		return length;
	}

	/**
	 * @param length
	 *            the length to set
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * @return the dockTime
	 */
	public PortTime getDockTime() {
		return dockTime;
	}

	/**
	 * @param dockTime
	 *            the dockTime to set
	 */
	public void setDockTime(PortTime dockTime) {
		this.dockTime = dockTime;
	}

	/**
	 * @return the weight
	 */
	public double getWeight() {
		return weight;
	}

	/**
	 * @param weight
	 *            the weight to set
	 */
	public void setWeight(double weight) {
		this.weight = weight;
	}

	/**
	 * @return the width
	 */
	public double getWidth() {
		return width;
	}

	/**
	 * @param width
	 *            the width to set
	 */
	public void setWidth(double width) {
		this.width = width;
	}
} // end class Ship

class PassengerShip extends Ship {
	int numberOfOccupiedRooms;
	int numberOfPassengers;
	int numberOfRooms;

	public PassengerShip(Scanner sc) {
		super(sc);
		if (sc.hasNextInt()) {
			numberOfPassengers = sc.nextInt();
		}
		if (sc.hasNextInt()) {
			numberOfRooms = sc.nextInt();
		}
		if (sc.hasNextInt()) {
			numberOfOccupiedRooms = sc.nextInt();
		}
	} // end Scanner Constructor

	public String toString() {
		String st = "Passenger ship: " + super.toString();
		if (jobs.size() == 0) {
			return st;
		}
		for (Job mj : jobs) {
			st += "\n    - " + mj;
		}
		return st;
	} // end method toString
} // end class PassengerShip

class CargoShip extends Ship {
	double cargoValue;
	double cargoVolume;
	double cargoWeight;

	public CargoShip(Scanner sc) {
		super(sc);
		if (sc.hasNextDouble()) {
			cargoWeight = sc.nextDouble();
		}
		if (sc.hasNextDouble()) {
			cargoVolume = sc.nextDouble();
		}
		if (sc.hasNextDouble()) {
			cargoValue = sc.nextDouble();
		}
	} // end Scanner Constructor

	public String toString() {
		String st = "Cargo ship: " + super.toString();
		if (jobs.size() == 0) {
			return st;
		}
		for (Job mj : jobs) {
			st += "\n   - " + mj;
		}
		return st;
	} // end method toString
} // end class CargoShip

class Person extends Thing {
	String skill;

	public Person(Scanner sc) {
		super(sc);
		if (sc.hasNext()) {
			skill = sc.next();
		}
	} // end Scanner constructor

	public String toString() {
		String st = "Person: " + super.toString() + " " + skill;
		return st;
	} // end method toString
} // end class Person

class Job extends Thing {
	double duration;
	ArrayList<String> requirements = new ArrayList<String>(); // should be some
																// of the skills
																// of the
																// persons

	public Job(Scanner sc) {
		super(sc);
		if (sc.hasNextDouble()) {
			duration = sc.nextDouble();
		}
		while (sc.hasNext()) {
			String st = sc.next();
			for (String mr : requirements) { // check if repeat of skill
				if (mr.equals(st)) {
					st = null;
				}
			}
			if (st != null) {
				requirements.add(st);
			}
		}
	} // end Scanner constructor

	public String toString() {
		String st = "Job: " + super.toString() + " " + requirements;
		return st;
	} // end method toString
} // end class Job

class PortTime {
	int time;
} // end class PortTime