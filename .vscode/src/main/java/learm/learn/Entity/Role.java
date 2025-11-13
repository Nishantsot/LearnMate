package learm.learn.Entity;


public enum Role {
     ADMIN,
    TUTOR,
    STUDENT;
    @Override
    public String toString() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}