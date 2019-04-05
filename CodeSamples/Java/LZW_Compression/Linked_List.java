/** a singly-linked list */
public class Linked_List
{
    private Node head;
    private int length;

    //constructor to initialize an empty list
    public Linked_List() {}

    public void add(short newData)
    {
        final Node f = head;
        final Node newNode = new Node(newData, f);

        head = newNode;
        length++;
    }

    public boolean contains(short element)
    {
        for(Node n = head; n != null; n = n.next)
        {
            if(element == n.data)
                return true;
        }
        return false;
    }

    private class Node
    {
        short data;
        Node next;

        private Node(short data, Node next)
        {
            this.data = data;
            this.next = next;
        }

        @Override
        public String toString()
            { return Short.toString(data); }
    }

    //getters and setters
    public Node getHead()
        { return head; }
    public void setHead(Node head)
        {this.head = head; }
    public int getLength()
        { return length; }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for(Node n = head; n != null; n = n.next) {
            builder.append(n.data);
            builder.append(" -> ");
        }
        return builder.toString();
    }
}
