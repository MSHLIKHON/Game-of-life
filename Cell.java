public class Cell {

    private boolean alive;
    private int age;

    public Cell(boolean alive) {
        this.alive = alive;
        if (alive) {
            this.age = 1;
        } else {
            this.age = 0;
        }
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive() {
        if (this.alive == false) {
            this.alive = true;
            this.age = 1;
        }
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        if (age >= 0) {
            this.age = age;
        }
    }

    public void reset() {
        this.alive = false;
        this.age = 0;
    }
}