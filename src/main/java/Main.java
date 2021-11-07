import me.brennan.footlocker.FootLocker;

/**
 * @author Brennan / skateboard
 * @since 11/7/2021
 **/
public class Main {

    public static void main(String[] args) {
        try {
            FootLocker.INSTANCE.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
