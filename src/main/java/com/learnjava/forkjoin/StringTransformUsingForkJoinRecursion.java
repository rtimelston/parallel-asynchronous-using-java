package com.learnjava.forkjoin;

import com.learnjava.util.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import static com.learnjava.util.CommonUtil.delay;
import static com.learnjava.util.CommonUtil.stopWatch;
import static com.learnjava.util.LoggerUtil.log;

/**
 * Uses the ForkJoin pattern to recursively divide and conquer the task operations for each element in a list.
 *
 * This parallelizes the tasks, but the code is complex and hard to understand. Superior to this approach is
 * using the parallel streams API.
 */
public class StringTransformUsingForkJoinRecursion extends RecursiveTask<List<String>> {

    private List<String> inputList;

    public StringTransformUsingForkJoinRecursion(List<String> inputList) {
        this.inputList = inputList;
    }

    public static void main(String[] args) {

        stopWatch.start();
        List<String> names = DataSet.namesList();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        log("names : "+ names);

        StringTransformUsingForkJoinRecursion stringTransformUsingForkJoinRecursion =
                new StringTransformUsingForkJoinRecursion(names);
        List<String> resultList = forkJoinPool.invoke(stringTransformUsingForkJoinRecursion);

        stopWatch.stop();
        log("Final Result : "+ resultList);
        log("Total Time Taken : "+ stopWatch.getTime());
    }


    private static String addNameLengthTransform(String name) {
        delay(500);
        return name.length()+" - "+name ;
    }

    @Override
    protected List<String> compute() {
        // If the list can't be further split
        if(inputList.size() <= 1) {
            // Do the transformation
            List<String> resultList = new ArrayList<>();
            inputList.forEach(name -> resultList.add(addNameLengthTransform(name)));
            return resultList;
        }

        // Otherwise recursively fork and join
        int midpoint = inputList.size() / 2;
        // Start forking the elements in the left-hand side of the input list.
        ForkJoinTask<List<String>> leftInputTask =
                new StringTransformUsingForkJoinRecursion(this.inputList.subList(0, midpoint)).fork();
        // The input list will now be the right side of the original input list
        this.inputList = this.inputList.subList(midpoint, this.inputList.size());
        List<String> rightResult = compute(); // recurse with the right side of the list
        List<String> leftResult = leftInputTask.join();
        leftResult.addAll(rightResult);

        return leftResult;
    }
}
