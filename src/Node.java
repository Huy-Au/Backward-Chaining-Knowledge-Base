public class Node {
	public String name;
	public boolean value;
	public Node next;
	public Node prev;
	public Node andNext;
	public Node andPrev;
	
	public Node(String input){
		name = input;
		value = false;
		next = null;
		prev = null;
		andNext = null;
		andPrev = null;
	}
	
	public void print(){
		System.out.println(name);	
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		Node temp = (Node)obj;
		return temp.name.equals(name);
//		return super.equals(obj);
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return name.intern().hashCode();
	}
	
	

}
