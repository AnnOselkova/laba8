import java.util.LinkedList;
public class URLPool {
    private final LinkedList<URLDepthPair> raw = new LinkedList<>(); // список для необработанных ссылок
    private final LinkedList<URLDepthPair> finished = new LinkedList<>(); // список для обработанных ссылок

    private int waitt = 0; // хранит количество ожидающих потоков

    public synchronized URLDepthPair getPair(){ // получаем необработанную ссылку
        while( raw.size() == 0){
            waitt++;
            try{
                wait(); // заставляют поток ждать
            } catch (InterruptedException e) {
            }
            waitt--; // убираем ссылку их списка необработанных ссылок
        }
        return raw.removeFirst();
    }

    public synchronized void push(URLDepthPair pair){
        if (!raw.contains(pair)) raw.add(pair); //проверяем, есть ли такая ссылка, если нет, то добавляем
        notify(); //  выводит поток из ожидания
    }

    public void pushClosed(URLDepthPair pair){ // для обработанных ссылок
        if (!finished.contains(pair)) finished.add(pair);
    }

    public LinkedList<URLDepthPair> getClosedLinks(){ //возвращает обработанные ссылки
        return finished;
    }

    public int getNumWaiters() { // возвращаем число ожидающих потоков
        return waitt;
    }
}
