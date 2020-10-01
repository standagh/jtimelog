package cz.hatua.jtimelog.cats;

class CatGroup {
	String group;
	
	CatGroup(String g) {
		group = g;
	}
	
	public String getGroup() {
		return group;
	}
	
	@Override
	public int hashCode() {
		return group.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof CatGroup && this.equals((CatGroup)obj);
	}
	
	private boolean equals(CatGroup cg) {
		return this.group.equals(cg.getGroup());
	}
	
	@Override
	public String toString() {
		return group;
	}
}
