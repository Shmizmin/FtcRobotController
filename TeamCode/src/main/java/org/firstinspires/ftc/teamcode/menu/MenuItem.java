package org.firstinspires.ftc.teamcode.menu;

abstract public class MenuItem
{
    private int index;

    public abstract void increase();
    public abstract void decrease();

    public MenuItem(int index)
    {
        this.index = index;
    }
}
