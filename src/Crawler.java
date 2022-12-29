import java.net.MalformedURLException;

public class Crawler {
    public static final String ERROR = "usage: java Crawler <URL><depth><numThreads>"; // выводим ошибку если длина массива не равно 3
    public static void main(String[] args){
        if(args.length != 3){
            System.out.println(ERROR);
            return;
        }
        int s; // ссылка
        int mD; // максимальная глубина
        try{
            mD = Integer.parseInt(args[1]); // переделываем из строк в числа
            s = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.out.println(ERROR);
            return;
        }
        URLDepthPair first; // создаем первую пару
        try{
            first = new URLDepthPair(args[0], 0); // записываем ссылку и глубину
        } catch (MalformedURLException e) {
            System.out.println(ERROR); // выводит ошибку если ссылка неправильная
            return;
        }
        URLPool p = new URLPool(); // пул
        p.push(first);
        Thread[] th = new Thread[s]; // многопоточность, массив объектов, управляющие потоками
        for (int i = 0; i < s; i++){
            CrawlerTask task = new CrawlerTask(p, mD); // добавляем потоки
            th[i] = new Thread(task);
            th[i].start(); //запустили потоки
        }
        while (p.getNumWaiters() != s) { // основной поток ждет, когда завершатся другие
            try {
                Thread.sleep(500); // время ожидания
            } catch (InterruptedException e) {
            }
        }
        for (int i=0; i < s; i++) {
            th[i].stop(); // остановка всех потоков
        }
        System.out.println("Обработка завершена");// вывод результата
        for (URLDepthPair pr: p.getClosedLinks()) {
            System.out.println(pr);
        }
    }
}
