package org.firstinspires.ftc.teamcode.menu;

public class DoubleMenuItem extends MenuItem
{
    private double value = 0.0;

    public void increase()
    {
        value += 1.0;
    }

    public void decrease()
    {
        value += 1.0;
    }

    public DoubleMenuItem(int index)
    {
        super(index);
        this.value = 0.0;
    }

    public String toString()
    {
        return String.valueOf(value);
    }
}
