
public class Tuple { 
  private String key; 
  private int value;
  private String title;
  
  public Tuple(String key, int value, String title) { 
    this.key = key; 
    this.value = value; 
    this.title = title;
  } 
  
  public String getKey() {
	  return this.key;
  }
  
  public int getValue() {
	  return this.value;
  }
  
  public String getTitle() {
	  return this.title;
  }
  
  public void setKey(String newKey) {
	  this.key = newKey;
  }
  
  public void setValue(int newValue) {
	  this.value = newValue;
  }
  
  public void setTitle(String title) {
	  this.title = title;
  }
  
} 