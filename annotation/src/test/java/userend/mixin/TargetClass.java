package userend.mixin;

public class TargetClass {
    private final String secretMessage;

    public TargetClass(String secretMessage) {
        this.secretMessage = secretMessage;
    }

    public void doStuff() {
        System.out.println("This class did stuff it's stuff.");
    }
}
