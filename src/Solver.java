import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Solver {
	LinkedList<Node> head;
	List<String> knowledgeBase;

	public static void main(String args[]) {
		Solver eg = new Solver();
		while(true){
			System.out.println();
			System.out.println("Type any character to search KB or hit @ to terminate");
			Scanner reader = new Scanner(System.in);
			String input = reader.next();
			if(input.equals("@")){
				break;
			}
			eg.Ask(input);
		}
		System.out.println("Program terminated");
	}

	public Solver() {
		knowledgeBase = getUserInput();
	}

	public void Ask(String input) {
		head = new LinkedList<Node>();
		dfsBuild(input.charAt(0));

	}

	public void dfsBuild(char input) {
		ArrayList<String> results = resultsFromKB(input);
		for (String element : results) {
			System.out.println(element);
		}

		buildInitial(results);
		for (Node ele : head) {
			ele.print();
		}
		
		boolean solved = false;
		int count = 0;
		while(!head.isEmpty() && !solved){
			System.out.println((count+1) + "th iteration");
			Node temp = head.peek();
			ArrayList<String> res = resultsFromKB(temp.name.charAt(0));
			solved = expandAndDelete(res, temp);
			count++;
		}
		System.out.println("---------------------------------------------");
		System.out.println("Final Result");
		if(solved){
			System.out.println("KB|-" + input + ": True");
		} else{
			System.out.println("KB|-" + input + ": False");
		}
	}

	public void deepPrint(Node input) {
		if (input.andNext == null) {
			input.print();
		}
		while (input.andNext != null) {
			input.print();
			input = input.andNext;
			if (input.andNext == null) {
				input.print();
			}
		}
	}

	public boolean expandAndDelete(ArrayList<String> input, Node parent) {
		// how many cases? ^bcd (DONE), b>d(DONE), vqwb(DONE), b (DONE)
		System.out.print("Inside expandFunction: ");
		parent.print();
		Node newParent = parent.andNext;

		// All element in conjunctive statement needs to be proven true
		// In this case, first element is found to be true. Information is no
		// redundant, remove element
		// from conjunctive statement.
		// Loop is not necessary, used to short circuit function

		for (int i = 0; i < input.size(); i++) {
			String instruction = input.get(0);
			if (instruction.length() == 1 && instruction.equals(parent.name)) {
				head.add(1, newParent);
				if (head.peek().andNext == null) {
					System.out.println("TRUE");
					return true;
				}
				head.pop();
				return false;
			}
		}
		
		// Expand AND and OR Node. And nodes will build via andNext pointer
		// whilst OR nodes will be built using next pointer
		for (int i = 0; i < input.size(); i++) {
			String instruction = input.get(i);
			Node expandedNode = buildNode(instruction);
			
			if (instruction.length() == 3){
				Node appendingNode = expandedNode;
				appendingNode.andNext = newParent;
				head.add(i+1, appendingNode);
			}

			if (instruction.charAt(0) == '^') {
				Node appendingNode = expandedNode;
				while (appendingNode.andNext != null) {
					appendingNode = appendingNode.andNext;
				}
				appendingNode.andNext = newParent;
				while (appendingNode.andPrev != null) {
					appendingNode = appendingNode.andPrev;
				}
				head.add(i + 1, appendingNode);
			}
			if (instruction.charAt(0) == 'v') {
				ArrayList<Node> tempHolder = new ArrayList<Node>();
				Node element = buildNode(instruction);
				while (element.next != null) {
					tempHolder.add(element);
					element = element.next;
					if (element.next == null) {
						tempHolder.add(element);
					}
				}
				for (int j = 0; j < tempHolder.size(); j++) {
					Node temp = tempHolder.get(j);
					temp.andNext = newParent;
					head.add(i + j + 1, temp);
				}

			}

		}
		// If first element of Conjunctive is proven false, entire conjunctive is false
		// Delete entire conjunctive statement from list
		head.pop();
		return false;
	}


	public ArrayList<String> resultsFromKB(char input) {
		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < knowledgeBase.size(); i++) {
			String knowledgeRow = knowledgeBase.get(i);
			char lastChar = knowledgeRow.charAt(knowledgeRow.length() - 1);
			if (lastChar == input) {
				results.add(knowledgeRow);
			}
		}
		return results;
	}

	public Node buildNode(String input) {
		if (input.charAt(0) == '^') {
			Node first = new Node("" + input.charAt(1));
			Node temp = first;
			for (int i = 2; i < input.length() - 1; i++) {
				Node newAndNeighbour = new Node("" + input.charAt(i));
				temp.andNext = newAndNeighbour;
				newAndNeighbour.andPrev = temp;
				temp = newAndNeighbour;
			}
			return first;
		} else if (input.charAt(0) == 'v') {
			Node first = new Node("" + input.charAt(1));
			Node temp = first;
			for (int i = 2; i < input.length() - 1; i++) {
				Node newOrNeighbour = new Node("" + input.charAt(i));
				temp.next = newOrNeighbour;
				newOrNeighbour.prev = temp;
				temp = newOrNeighbour;
			}
			return first;
		} else if (input.length() == 1) {
			Node first = new Node(input);
			first.value = true;
			return first;
		} else {
			// This is for cases A>B.
			Node first = new Node("" + input.charAt(0));
			return first;
		}
	}

	public void buildInitial(ArrayList<String> input) {
		for (String e : input) {
			Node element = buildNode(e);
			if (e.charAt(0) == 'v') {
				while (element.next != null) {
					head.add(element);
					element = element.next;
					if (element.next == null) {
						head.add(element);
					}
				}
			} else {
				head.add(element);
			}
		}

	}

	public boolean searchKB(char input) {
		for (int i = 0; i < knowledgeBase.size(); i++) {
			String instruction = knowledgeBase.get(i);
			if (instruction.charAt(instruction.length() - 1) == input) {
				return true;
			}
		}
		return false;
	}

	public void checkKB() {
		for (String element : knowledgeBase) {
			System.out.println(element);
		}
	}

	public static List<String> getUserInput() {
		List<String> userInstruction = new ArrayList<String>();
		System.out.println("Please input your instructions.\n" + "Press ENTER to provide KB with more information else "
				+ " to end, hit '!' followed by ENTER");
		Scanner reader = new Scanner(System.in);
		String userInput;
		do {
			userInput = reader.nextLine();
			if (validateInput(userInput)) {
				if (userInput.length() >= 5) {
					String compressed = compressInput(userInput);
					userInstruction.add(compressed);
				} else {
					if (userInput.equals("!")) {
						return userInstruction;
					}
					userInstruction.add(userInput);
				}
			} else {
				// System.out.println("Bad Validation");
			}
		} while (!userInput.equals("!"));
		reader.close();
		return userInstruction;
	}

	public static boolean validateInput(String input) {
		// A, B, C, etc always True
		if (input.length() == 1) {
			return true;
		}
		// A -> B, B -> C, etc...
		if (input.length() == 3) {
			if (input.charAt(1) == '>') {
				return true;
			} else {
				return false;
			}
		}
		// Input must always be odd length to be valid
		if (input.length() % 2 != 1) {
			return false;
		}
		// Second last char MUST be >
		if (input.charAt(input.length() - 2) != '>') {
			return false;
		}
		// Operation can only be 'v' or '^'
		char operation;
		if (input.charAt(1) == 'v' || input.charAt(1) == '^') {
			operation = input.charAt(1);
		} else {
			return false;
		}

		for (int i = 1; i <= input.length() - 4; i = i + 2) { // 4 characters
																// after the
																// last will
																// provide the
																// last AND/OR
			if (input.charAt(i) != operation) {
				return false;
			}
		}
		return true; // valid
	}

	// Converts A^B^C^D>E into compressed format ^ABCDE>
	public static String compressInput(String input) {
		StringBuilder sb = new StringBuilder();
		sb.append(input.charAt(1)); // Add the first o
		for (int i = 0; i <= input.length() - 3; i = i + 2) {
			sb.append(input.charAt(i));
		}
		sb.append(input.charAt(input.length() - 1));
		return sb.toString();
	}
}
