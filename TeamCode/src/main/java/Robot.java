import com.qualcomm.robotcore.eventloop.opmode.OpMode;

public class Robot {
    public static Robot R = null;
    public static OpMode O = null;

    public Robot(OpMode opmode) {
        R = this;
        O = opmode;
    }
}
