package ru.grigoriev;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class Main {
    private static final BlockingQueue<String> maxA = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> maxB = new ArrayBlockingQueue<>(100);
    private static final BlockingQueue<String> maxC = new ArrayBlockingQueue<>(100);
    private static final int numberOfTexts = 10_000;
    private static final int lengthText = 100_000;

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < numberOfTexts; i++) {
                String str;
                try {
                    str = generateText("abc", lengthText);
                    maxA.put(str);
                    maxB.put(str);
                    maxC.put(str);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        new Thread(() -> findMaxCharacterIntoStrings(maxA, 'a')).start();
        new Thread(() -> findMaxCharacterIntoStrings(maxB, 'b')).start();
        new Thread(() -> findMaxCharacterIntoStrings(maxC, 'c')).start();
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    public static void findMaxCharacterIntoStrings(BlockingQueue<String> blockingQueue, char forSearch) {
        int max = 0;
        String maxCharacter = "";
        for (int i = 0; i < numberOfTexts; i++) {
            String tempStr = "";
            int tempNum = 0;
            try {
                tempStr = blockingQueue.take();
                for (int j = 0; j < lengthText; j++) {
                    if (tempStr.charAt(j) == forSearch) {
                        tempNum++;
                    }
                }
                if (tempNum > max) {
                    max = tempNum;
                    maxCharacter = tempStr.substring(0, 20);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.printf("Текст с максимальным количесвтом символов \"%s\" - %s..., кол-во символов - %d\n", forSearch, maxCharacter, max);
    }
}