package it.torvergata.mp.pmcom.entity;

public class Category {
		private int id;
		private String name;
		
		
		public Category(int i,String n){
			id=i;
			name=n;	
		}
		
		public int getId() {
			return id;
		}
		public void setId(int id) {
			this.id = id;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
}
