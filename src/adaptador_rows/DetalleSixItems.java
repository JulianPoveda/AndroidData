package adaptador_rows;

public class DetalleSixItems {
	protected String 	item1;
    protected String 	item2;
    protected String 	item3;
    protected String 	item4;
    protected String 	item5;
    protected String 	item6;
    
    protected long 		Id;

    public DetalleSixItems(String _item1, String _item2, String _item3, String _item4, String _item5, String _item6){
        super();
        this.item1 	= _item1;
        this.item2 	= _item2;
        this.item3 	= _item3;
        this.item4 	= _item4;
        this.item5 	= _item5;
        this.item6 	= _item6;
    }

	public String getItem1() {
		return item1;
	}

	public void setItem1(String item1) {
		this.item1 = item1;
	}

	public String getItem2() {
		return item2;
	}

	public void setItem2(String item2) {
		this.item2 = item2;
	}

	public String getItem3() {
		return item3;
	}

	public void setItem3(String item3) {
		this.item3 = item3;
	}

	public String getItem4() {
		return item4;
	}

	public void setItem4(String item4) {
		this.item4 = item4;
	}

	public String getItem5() {
		return item5;
	}

	public void setItem5(String item5) {
		this.item5 = item5;
	}

	public String getItem6() {
		return item6;
	}

	public void setItem6(String item6) {
		this.item6 = item6;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}
    
    
    
    
}
