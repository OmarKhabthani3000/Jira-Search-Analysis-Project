package org.foo

@Audited
@Entity
public class SampleEntity{
  
  private Integer id;
  private String someValue;
  
  @Id
  public Integer getId(){
   	return id;
  }

  private void setId(Integer id){
    this.id = id;
  }
  
  public String getSomeValue(){
     return someValue;
  }

  public void setSomeValue(String value){
  	this.someValue = value;
  }

}