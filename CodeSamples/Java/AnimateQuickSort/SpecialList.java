//Name: SpecialList
//Authors: Rory Abraham and Parth Bansal
//Date: 12/19/17
//Description: A special type of list to be sorted using QuickSort. This is simply meant to make it easier to keep track
    //of the pivot, low, and high

public class SpecialList
{
    private int[] list;
    private int pivotIndex;
    private int lowIndex;
    private int highIndex;

    public SpecialList(int[] list, int pivotIndex, int lowIndex, int highIndex)
    {
        this.list = list;
        this.pivotIndex = pivotIndex;
        this.lowIndex = lowIndex;
        this.highIndex = highIndex;
    }

    public int[] getList()
    {
        return this.list;
    }
    public void setList(int[] newList)
    {
        this.list = newList;
    }

    public int valueAt(int index)
    {
        return list[index];
    }
    public void setValueAt(int index, int value)
    {
        int[] tempList = this.getList();
        tempList[index] = value;
        setList(tempList);
    }

    public int getPivotIndex()
    {
        return this.pivotIndex;
    }
    public void setPivotIndex(int index)
    {
        this.pivotIndex = index;
    }

    public int getLowIndex()
    {
        return this.lowIndex;
    }
    public void setLowIndex(int index)
    {
        this.lowIndex = index;
    }

    public int getHighIndex()
    {
        return this.highIndex;
    }
    public void setHighIndex(int index)
    {
        this.highIndex = index;
    }
}
