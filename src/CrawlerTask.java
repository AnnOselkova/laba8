import java.util.LinkedList;
import java.net.*;
import java.io.*;

public class CrawlerTask implements Runnable{ // на основе класса можно создать поток
    private static final String URL_HTTP = "http://";
    private static final String URL_HREF = "<a href=";
    private final URLPool p;// храним ссылку
    private int mD; // храним максимальную глубину
    public CrawlerTask(URLPool p, int mD){ //конструктор класса, передаем максимальную глубину и ссылку к пулу
        this.p = p;
        this.mD = mD;
    }

    private LinkedList<URLDepthPair> read(URLDepthPair pair){
        Socket socket; //создаем сокет
        try{
            socket = new Socket(pair.getUrl().getHost(), 80);
        } catch (UnknownHostException e) {
            System.out.println("Ошибка неизвестный хост " + pair.getUrl().getHost());
            return null;
        } catch (IOException e) {
            System.out.println("Ошибка ввода вывода при создании сокета " + pair.getUrl().getHost());
            return null;
        }
        try{
            socket.setSoTimeout(1000); // время сколько сокет может отпправлять пакет данных
        } catch (SocketException e) {
            System.out.println("Ошибка установки таймаута сокета " + pair.getUrl().getHost());
            return null;
        }
        BufferedReader en ; // входной поток
        PrintWriter ex; // выходной поток
        try{
            en = new BufferedReader(new InputStreamReader(socket.getInputStream())); //поток на вход
            ex = new PrintWriter(socket.getOutputStream(),true); // на выход
        } catch (IOException e) {
            System.out.println("Ошибка создания потоков ввода вывода " + pair.getUrl().getHost());
            return null;
        }
        ex.println("GET " + pair.getUrl().getPath()+" HTTP/1.1"); //строка на запрос
        ex.println("Host: " + pair.getUrl().getHost());
        ex.println("Connection: close");
        ex.println();
        LinkedList<URLDepthPair> l = new LinkedList<>(); //массив для ссылок
        String ln;
        try {
            while ((ln = en.readLine()) != null){ // построчно получаем ответ
                if (!ln.contains(URL_HREF)) continue; // если нет href не обрабатываем
                int start = ln.indexOf(URL_HTTP); // смотри на наличие http
                int end = ln.indexOf("\"", start); // смотрим на наличие ковычки
                if (start == -1 || end == -1) continue;//не обрабатываем, если нет
                URLDepthPair newP; // создаем новую URL пару, если получилось создать, то добавляем ссылку в список
                try{
                    newP = new URLDepthPair(ln.substring(start,end), pair.getDepth() + 1);
                } catch (MalformedURLException e) {
                    continue;
                }
                l.add(newP);
            }
        } catch (IOException e) {
            System.out.println("Ошибка: получения Html документа: " + pair.getUrl().getHost());
            return null;
        }
        try {
            socket.close();
        } catch (IOException e) {
            System.out.println("Ошибка: закрытия сокета: " + pair.getUrl().getHost());
            return null;
        }
        return l;// возвращает список обработанных ссылок

    }
    @Override
    public void run() { // получаем ссылку, если не соответсвуюем идем дальше, получаем ссылку отправляем в список необработанных, старую добавляем в список обработанныйх и идем дальше
        while (true) {
            URLDepthPair coup = p.getPair(); //получили из списка необработанных
            if (coup.getDepth() > mD) continue;
            LinkedList<URLDepthPair> links = read(coup);
            if (links == null) continue;
            for (URLDepthPair elem: links){
                p.push(elem);
            }
            p.pushClosed(coup);
        }


    }
}
