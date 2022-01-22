package com.yqj.algorithm.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortTest {

    // 1.冒泡排序
    public static void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0 ; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = tmp;
                }
            }
        }
    }

    // 2.选择排序
    public static void selectionSort(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            int min = i;
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < arr[min]) {
                    min = j;
                }
            }
            int tmp = arr[i];
            arr[i] = arr[min];
            arr[min] = tmp;
        }
    }

    // 3.插入排序
    public static void insertSort(int[] arr) {
        for (int i = 1; i < arr.length; i++) {
            for (int j = i; j > 0; j--) {
                if (arr[j] < arr[j - 1]) {
                    int tmp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = tmp;
                }
            }
        }
    }

    // 4.快速排序
    public static void quickSort(int[] arr,int low,int high) {
        if (low >= high) return;
        int start = low;
        int end = high;
        int key = arr[start];
        while (start < end) {
            while (start < end && arr[end] >= key) end--;
            if (start < end) arr[start++] = arr[end];
            while (start < end && arr[start] <= key) start++;
            if (start < end) arr[end--] = arr[start];
        }
        arr[start] = key;
        quickSort(arr,low,start - 1);
        quickSort(arr,start + 1,high);
    }

    // 5.堆排序
    public static void heapSort(int[] arr) {
        // 创建堆
        for (int i = (arr.length - 1) / 2; i >= 0; i--) {
            // 从第一个非叶子结点从下至上，从右至左调整结构
            adjustHeap(arr,i,arr.length);
        }
        // 调整堆结构+交换堆顶元素与末尾元素
        for (int i = arr.length - 1; i > 0 ; i--) {
            // 将堆顶元素与末尾元素进行交换
            int tmp = arr[i];
            arr[i] = arr[0];
            arr[0] = tmp;
            // 重新对堆进行调整
            adjustHeap(arr,0,i);
        }
    }
    // 调整堆
    private static void adjustHeap(int[] arr,int parent,int length) {
        int tmp = arr[parent];
        // 左孩子
        int lChild = parent * 2 + 1;
        while (lChild < length) {
            // 右孩子
            int rChild = lChild + 1;
            // 如果有右孩子结点，并且右孩子结点的值大于左孩子结点，则选取右孩子结点
            if (rChild < length && arr[rChild] > arr[lChild]) {
                lChild++;
            }
            // 如果父结点的值已经大于孩子结点的值，则直接结束
            if (tmp > arr[lChild]) break;
            // 把孩子结点的值赋给父结点
            arr[parent] = arr[lChild];
            // 选取孩子结点的左孩子结点,继续向下筛选
            parent = lChild;
            lChild = parent * 2 + 1;
        }
        arr[parent] = tmp;
    }

    // 6.归并排序
    public static void mergeSort(int[] arr,int low,int high) {
        int mid = (low + high) / 2;
        if (low < high) {
            mergeSort(arr,low,mid);
            mergeSort(arr,mid + 1,high);
            // 左右归并
            merge(arr,low,mid,high);
        }
    }
    //归并
    private static void merge(int[] arr,int low,int mid,int high) {
        int[] tmpArr = new int[high - low + 1];
        int i = low;
        int j = mid + 1;
        int k = 0;
        // 把较小的数先移到新数组中
        while (i <= mid && j <= high) {
            if (arr[i] < arr[j]) {
                tmpArr[k++] = arr[i++];
            } else {
                tmpArr[k++] = arr[j++];
            }
        }
        // 把左边剩余的数移入数组
        while (i <= mid) tmpArr[k++] = arr[i++];
        // 把右边边剩余的数移入数组
        while (j <= high) tmpArr[k++] = arr[j++];
        // 把新数组中的数覆盖arr数组
        for (int x = 0; x < tmpArr.length; x++) {
            arr[x + low] = tmpArr[x];
        }
    }

    // 7.希尔排序（特殊的插入排序）
    // 将数组列在一个表中并对列分别进行插入排序，重复这过程。最后整个表就只有一列，进行最后一次插入排序
    public static void shellSort(int[] arr) {
        int n = arr.length;
        // 首次步长
        int gap = n / 2;
        while (gap > 0) {
            for (int i = gap; i < n; i++) {
                int j = i;
                while (j >= gap && arr[j - gap] > arr[j]) {
                    int tmp = arr[j];
                    arr[j] = arr[j - gap];
                    arr[j - gap] = tmp;
                    j -= gap;
                }
            }
            gap /= 2;
        }
    }

    // 8.计数排序
    public static void countSort(int[] arr) {
        // 获取数组的最大值，数组所有值都在0 - max之间
        int max = arr[0];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        // 创建一个max+1大小的数组用于表示从0 - max所有数字的重复次数
        int[] countArr = new int[max + 1];
        // 创建临时拷贝的数组
        int[] tmpArr = new int[arr.length];
        System.arraycopy(arr,0,tmpArr,0,arr.length);
        // 因为countArr的下标代表arr中的数字，而值代表arr中元素的出现次数，所以countArr[arr[i]]++
        for (int i = 0; i < arr.length; i++) {
            countArr[arr[i]]++;
        }
        // 将countArr中的每一个元素变成与前一个元素相加的和
        for (int i = 1; i < countArr.length; i++) {
            countArr[i] += countArr[i - 1];
        }
        // 将tmpArr中i位置的的元素放到arr中的--countArr[tmpArr[i]]位置
        // 例如i等于1时，temp[i]值时6，countArray[6]的值是6，也就代表6这个元素前面有5个元素小于小于它，
        // 那么6应该放在array数组的第6个位置也就是array[5]
        for (int i = 0; i < tmpArr.length; i++) {
            arr[--countArr[tmpArr[i]]] = tmpArr[i];
        }
    }

    // 9.桶排序
    public static void bucketSort(int[] arr) {
        // 计算最大值与最小值
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            max = Math.max(max,arr[i]);
            min = Math.min(min,arr[i]);
        }
        // 计算桶的数量
        // +1 是因为算上 0 号桶
        int bucketNum = (max - min) / arr.length + 1;
        List<List<Integer>> buckets = new ArrayList<>(bucketNum);
        for (int i = 0; i < bucketNum; i++) {
            buckets.add(new ArrayList<>());
        }
        // 将每个元素放入桶
        for (int i = 0; i < arr.length; i++) {
            int num = (arr[i] - min) / arr.length; //放桶的位置计算 0～bucketNum-1
            buckets.get(num).add(arr[i]);
        }
        // 对每个桶进行排序
        for (int i = 0; i < bucketNum; i++) {
            Collections.sort(buckets.get(i));
        }
        // 将桶中的元素赋值到原序列
        int index = 0;
        for (int i = 0; i < buckets.size(); i++) {
            for (int j = 0; j < buckets.get(i).size(); j++) {
                arr[index++] = buckets.get(i).get(j);
            }
        }
    }

    // 10.基数排序
    public static void redixSort(int[] arr,int threshold) {
        int length = arr.length;
        // 代表位数对应的数：1,10,100...
        int n = 1;
        // 排序桶用于保存每次排序后的结果，这一位上排序结果相同的数字放在同一个桶里
        int[][] buckets = new int[10][length];
        //用于保存每个桶里有多少个数字
        int[] order = new int[10];
        while (n < threshold) {
            // 将数组array里的每个数字放在相应的桶里
            for (int item : arr) {
                int digit = (item / n) % 10;
                buckets[digit][order[digit]++] = item;
            }
            // 将前一个循环生成的桶里的数据覆盖到原数组中用于保存这一位的排序结果
            int index = 0;
            for (int i = 0; i < 10; i++) {
                // 这个桶里有数据，从上到下遍历这个桶并将数据保存到原数组中
                if (order[i] != 0) {
                    for (int j = 0; j < order[i]; j++) {
                        arr[index++] = buckets[i][j];
                    }
                }
                // 将桶里计数器置0，用于下一次位排序
                order[i] = 0;
            }
            n *= 10;
        }

    }


    private static void print(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    public static void main(String[] args) {
        int[] arr = new int[]{12,121,143,12,11,5,5,21,1};
//        bubbleSort(arr);
//        selectionSort(arr);
//        insertSort(arr);
//        quickSort(arr,0,arr.length - 1);
        heapSort(arr);
//        mergeSort(arr,0,arr.length - 1);
//        shellSort(arr);
//        countSort(arr);
//        bucketSort(arr);
//        redixSort(arr,1000);
        print(arr);
    }
}
