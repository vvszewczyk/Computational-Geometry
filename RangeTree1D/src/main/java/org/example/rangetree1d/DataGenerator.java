package org.example.rangetree1d;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGenerator
{
    private static final int COUNT = 45;
    private static final int NUMBER_RANGE = 1000; //<-NUMBER_RANGE/2, NUMBER_RANGE/2>

    public static List<List<Integer>> generateDataSets(int numberOfSets)
    {
        List<List<Integer>> dataSets = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < numberOfSets; i++)
        {
            List<Integer> dataSet = new ArrayList<>();

            for (int j = 0; j < COUNT; j++)
            {
                int point = random.nextInt(NUMBER_RANGE) - NUMBER_RANGE / 2;
                dataSet.add(point);
            }

            dataSets.add(dataSet);
        }

        return dataSets;
    }
}
