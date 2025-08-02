package org.hibernate.test.selectjoin;



/**
 * @author Scott Russell
 */

public class Animal {

    private int animalId;
    private Zoo zoo;
    private Zoo zoo2;
    
    public int getAnimalId() {
        return animalId;
    }
    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }
    
    public Zoo getZoo() {
        return zoo;
    }
    public void setZoo(Zoo zoo) {
        this.zoo = zoo;
    }
    
    public Zoo getZoo2() {
        return zoo2;
    }
    public void setZoo2(Zoo zoo2) {
        this.zoo2 = zoo2;
    }

}