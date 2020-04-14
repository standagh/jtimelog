package cz.hatua.jtimelog.cats;

class CatGroup {
	String group;
	
	CatGroup(String g) {
		group = g;
	}
	
	// needs to override hashcode a equals
	
	@Override
	public String toString() {
		return group;
	}
}
