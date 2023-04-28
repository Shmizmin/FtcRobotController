package org.firstinspires.ftc.teamcode.menu;

public class BooleanMenuItem extends MenuItem
{
    private boolean value;

    public void increase()
    {
        value = true;
    }

    public void decrease()
    {
        value = false;
    }

    public BooleanMenuItem(int index)
    {
        super(index);
        this.value = false;
    }

    public String toString()
    {
        return (value ? "true" : "false");
    }
}
