/*Name: RadixSort
Authors: Rory Abraham and Parth Bansal
Date Due: 12/20/17
Description: Sorts an array of 1,000,000 random integers using radix sort
        For a simplier demonstration, uncomment lines 164-172, 196-197, 214-220
        and insert a parameter value of 9999 into nextInt() on line 28*/


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

public class RadixSort {
    //generate a random list of 1,000,000 integers
    private static int[] generateRandomList()
    {
        //create random number generator
        Random random = new Random();

        //initialize the array
        int[] list = new int[1000000];

        //fill the list with 1,000,000 random integers
        for(int i=0;i<1000000;i++)
        {
            //generate random integers
            int randInt = random.nextInt();
            list[i] = randInt;
        }
        return list;
    }

    //helper method returns digit at a specified decimal place
        // 1==1's place; 2==10's place; 3===100's place...
    private static int getDecimalPlaceValue(int num, int decimalPlace)
    {
        //convert the int into a String
        String numString = Integer.toString(num);

        //try to get the charAt given decimal place "reading from right to left"
        try{
            char digit = numString.charAt(numString.length()-(decimalPlace));
            //don't count negative symbol for now
            if(digit == '-')
            {
                return 0;
            }
            return Character.getNumericValue(digit);
        }
        //this ensures that the hundreds place of 45 is 0, or that the ten-thousands place of 9687 is 0
        catch (IndexOutOfBoundsException e)
        {
            return 0;
        }
    }

    //helper method returns max radius of the largest number in our array
        //this method is pretty unnecessary, because more-or-less it will return 10 every time
    private static int getMaxRadix(int[] array)
    {
        int max = 1;
        //loop through each num in our array
        for(int i: array)
        {
            //convert int to String
            String s = Integer.toString(i);

            //remove minus sign from negative ints
            String k = s.replaceAll("[^0-9]","");

            //find max radix
            int maxRadix = k.length();
            if(maxRadix>max)
            {
                max=maxRadix;
            }
        }
        return max;
    }

    //decimalPlace=1: 1's place, 2: 10's place, 3: 100's place, 4: 1000's place, etc...
    //public static int[] radixSort(int[] unsorted, int decimalPlace)
    private static int[] radixSort(int[] unsorted, int decimalPlace)
    {
        //holding variable for radix position
        //int decimalPlace;

        //holding variable for max Radix position
        int maxRadix = getMaxRadix(unsorted);

        //initialize an array of "buckets"
            //each bucket is a Queue so we can use bucket.add() and bucket.poll()
        Queue<Integer>[] bucketArray = new Queue[10];

        //fill the arrays with lists
        for(int i=0; i<bucketArray.length; i++)
        {
            bucketArray[i] = new LinkedList<>();
        }

        //for each number in our unsorted list
        for(int j=0; j<unsorted.length; j++)
        {
            //examine the digit in the specified decimal place
            //add the number into the bucket that corresponds to the examined digit
            switch(getDecimalPlaceValue(unsorted[j], decimalPlace))
            {
                case 0:
                    bucketArray[0].add(unsorted[j]);
                    break;
                case 1:
                    bucketArray[1].add(unsorted[j]);
                    break;
                case 2:
                    bucketArray[2].add(unsorted[j]);
                    break;
                case 3:
                    bucketArray[3].add(unsorted[j]);
                    break;
                case 4:
                    bucketArray[4].add(unsorted[j]);
                    break;
                case 5:
                    bucketArray[5].add(unsorted[j]);
                    break;
                case 6:
                    bucketArray[6].add(unsorted[j]);
                    break;
                case 7:
                    bucketArray[7].add(unsorted[j]);
                    break;
                case 8:
                    bucketArray[8].add(unsorted[j]);
                    break;
                case 9:
                    bucketArray[9].add(unsorted[j]);
                    break;
            }
        }

        //create an arrayList to load buckets back into
        Queue<Integer> sortedList = new LinkedList<>();

        //for each bucket, going from 0 to 9,
        for(Queue<Integer> bucket: bucketArray)
        {
            //while bucket is not empty
            while(!bucket.isEmpty())
            {
                //unload buckets back into an int[]
                sortedList.add(bucket.poll());
            }
        }


        //convert Queue<Integer> to int[]
        int[] sorted = new int[sortedList.size()];
        for(int i=0; i<sorted.length; i++)
        {
            sorted[i] = sortedList.poll();
        }

        //print the array after each pass of radix sort
        // System.out.println(decimalPlace+"th Pass:");
        //for(int n: sorted)
        {
            //System.out.print(n + ", ");
        }


        //System.out.print("\n");
        //System.out.print("\n");

        //increment decimalPlace (move from 1's place to 10's, 1,000's to 10,000's, etc...)
        decimalPlace++;

        //if decimalPlace is not greater than 10 (billion's place)
        if (decimalPlace <= maxRadix)
        {
            //recursively call radixSort with the more sorted array
            return radixSort(sorted,decimalPlace);
        }
        else
        {
            return sorted;
        }
    }

    //method returns true if array is sorted from smallest to largest, false if not
    private static boolean testRadixSort(int[] sorted)
    {
        System.out.println("Testing Radix Sort:");
        System.out.print("sorted? == ");

        //for each element of the array
        //for(int n=0; n<sorted.length; n++)
        //System.out.print(sorted[n] + " ");

        for(int n=0; n<(sorted.length-1); n++)
        {
            //if value at index n+1 < value at index n
            if(sorted[n+1] < sorted[n])
            {
                //array is not sorted
                return false;
            }
        }
        //else array is properly sorted
        return true;
    }

    public static void main(String[] args){
        int[] unsorted = generateRandomList();
        //System.out.println("Unsorted List:");
        //for(int i: unsorted)
        {
            //System.out.print(i+", ");
        }

        //System.out.println("\n");

        //split the array based on positive and negative values
        ArrayList<Integer> negList = new ArrayList<>();
        ArrayList<Integer> posList = new ArrayList<>();
        for(int i: unsorted) {
            if (i >= 0) {
                posList.add(i);
            }
            else{
                negList.add(i);
            }
        }

        int[] negArray = new int[negList.size()];
        int[] posArray = new int[posList.size()];
        //convert lists to arrays
        for(int i = 0; i<negArray.length; i++)
        {
            negArray[i] = negList.get(i);
        }
        for(int i = 0; i<posArray.length; i++)
        {
            posArray[i] = posList.get(i);
        }

        //radix sort both arrays
        int[] sortedNegArray = radixSort(negArray,1);
        int[] sortedPosArray = radixSort(posArray,1);

        //recombine arrays
        int[] sorted = new int[sortedNegArray.length + sortedPosArray.length];
        //holding variable for index
        int j = 0;
        //step backwards through negatives so value with greatest absolute value gets added first
        for(int i = sortedNegArray.length-1; i >= 0; i--)
        {
            sorted[j] = sortedNegArray[i];
            j++;
        }
        for(int i = 0; i<sortedPosArray.length; i++)
        {
            sorted[j] = sortedPosArray[i];
            j++;
        }

        //TESTING getDecimalPlaceValue
            //System.out.println(getDecimalPlaceValue(-97801,6));

        //test radix sort
        System.out.println(testRadixSort(sorted));
    }
}
