package org.firstinspires.ftc.teamcode.menu;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

public class MenuManager
{
    private ElapsedTime debouncer = null;

    private MenuItem[] menuItems;
    private int selectedItem = 0;

    public MenuManager()
    {
        debouncer = new ElapsedTime();
        debouncer.reset();
    }

    public void onUpdate(Gamepad gp1, Gamepad gp2)
    {
        final boolean inputtable = (debouncer.milliseconds() > 250);

        final int START_DELAY = 0, ADVANCED = 1;

        if (inputtable)
        {
            if (gp1.dpad_up || gp2.dpad_up)
            {
                debouncer.reset();
                selectedItem--;
            }

            else if (gp1.dpad_down || gp2.dpad_down)
            {
                debouncer.reset();
                selectedItem++;
            }

            //clamp to permissible range
            selectedItem = Math.max(START_DELAY, Math.min(ADVANCED, selectedItem));

            ////////////////////////////////////////////////

            if (gp1.dpad_left || gp2.dpad_left)
            {
                debouncer.reset();
                menuItems[selectedItem].decrease();
            }

            else if (gp1.dpad_right || gp2.dpad_right)
            {
                debouncer.reset();
                menuItems[selectedItem].increase();
            }
        }
    }
}
