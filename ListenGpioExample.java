import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import java.util.*;
import java.io.*;

/**
 * This example code demonstrates how to setup a listener
 * for GPIO pin state changes on the Raspberry Pi.
 *
 * @author Robert Savage
 */
public class ListenGpioExample {

    private static int i = 0;
    private static Date when = new Date();

    public static void main(String args[]) throws InterruptedException {
        System.out.println("<--Pi4J--> GPIO Listen Example ... started.");

        // create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // provision gpio pin #02 as an input pin with its internal pull down resistor enabled
        final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);

        // set shutdown state for this input pin
        myButton.setShutdownOptions(true);

        // create and register gpio pin listener
        myButton.addListener(new GpioPinListenerDigital() {
            @SuppressWarnings("deprecation")
			@Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
                synchronized (this) {
                		Date now = new Date();
                		Date whenPlus = new Date();
                		whenPlus.setTime(when.getTime() + 5000);
//                		System.out.println(event.getState() == PinState.HIGH);
//                		System.out.println(now);
//                		System.out.println(whenPlus);
//                		System.out.println(now.after(whenPlus));
                		
                		if ((event.getState() == PinState.HIGH) && now.after(whenPlus)) {
//                    			System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
					long delta = now.getTime() - when.getTime();
                			when = now;
                			i++;
                			if (i>8) i=1;
                			try {
						if ((delta > 118900 && delta < 121100) || (delta > 118900 + 120000 && delta < 121100 + 120000) 
							|| (delta > 118900 + 240000 && delta < 121100 + 240000) || (delta > 118900 + 360000 && delta < 121100 + 360000)){
							System.out.println(now + ": Noise, " + delta);
						} else {
                					String str = "omxplayer /home/pi/0" + i + ".mp3";
                					System.out.println(now + ": Playing " + str + ", delta = " + delta);
                					Runtime.getRuntime().exec(str);
						}
                			} catch (IOException e) {
                				System.out.println(e.getMessage());
                			}
                		}
                }
            }

        });

        System.out.println(" ... complete the GPIO #07 circuit and see the listener feedback here in the console.");

        // keep program running until user aborts (CTRL-C)
        while(true) {
            Thread.sleep(500);
        }

        // stop all GPIO activity/threads by shutting down the GPIO controller
        // (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
        // gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
    }
}

